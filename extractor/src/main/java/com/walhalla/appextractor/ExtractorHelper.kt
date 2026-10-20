package com.walhalla.appextractor

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

import com.walhalla.appextractor.DownloadProgressExample.RBCWrapper
import com.walhalla.appextractor.core.RBCWrapperDelegate
import com.walhalla.appextractor.interactors.SimpleMeta
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.model.ViewModel
import com.walhalla.appextractor.utils.ExtractorUtils

import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.e
import com.walhalla.ui.DLog.handleException
import com.walhalla.appextractor.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Collections

class ExtractorHelper @OptIn(DelicateCoroutinesApi::class) constructor(
//    threadExecutor: Executor,
//    mainThread: MainThread,

    var mThreadExecutor: CoroutineScope = CoroutineScope(Dispatchers.Default),
    var mMainThread: CoroutineScope = GlobalScope,


    private val mView: Callback
)
//: AbstractInteractor(mThreadExecutor, mMainThread)
{
    //Отстук в фрагмент
    interface Callback {
        //void successExtracted(File fileName);
        fun rbcProgressCallback(
            position: Int, totalFileSize: Int,  //DownloadProgressExample.RBCWrapper rbc,
            file: File, fileSize: String, progress: Double
        )

        fun failureExtracted(resId: Int)

        fun makeSnackBar(file: File)

        fun printOutput(viewModel: ViewModel)

        fun successExtracted(mFile: Map<SimpleMeta, List<File>>)

        fun makeStorageLocalProgressBar(totalFileSize: Int)

        fun hideProgressBar(size: Int)

        fun extractOnePleaseDialog(noneExtracted: List<PackageMeta>)
    }

    //stat
    private var totalFileSize = 0
    private var index = 0



    /**
     * adb shell pm path com.google.android.googlequicksearchbox
     * package:/data/app/~~XvwzoLgWg1hKVrv_u3N92w==/com.google.android.googlequicksearchbox-32c8b9oQOZ6snbKgvYGoNw==/base.apk
     * package:/data/app/~~XvwzoLgWg1hKVrv_u3N92w==/com.google.android.googlequicksearchbox-32c8b9oQOZ6snbKgvYGoNw==/split_config.xxhdpi.apk
     *
     *
     * -rw-r--r-- 1 system system  199573360 2022-11-19 14:05 base.apk
     * -rw-r--r-- 1 system system     123138 2022-11-19 14:05 base.dm
     * drwxr-xr-x 3 system system       4096 2022-11-19 14:05 lib
     * drwxrwx--x 3 system install      4096 2022-11-19 14:05 oat
     * -rw-r--r-- 1 system system    3275913 2022-11-19 14:05 split_config.xxhdpi.apk
     */
    /**
     * App-private Downloads — always writable without storage permission.
     * Used when public Downloads / MediaStore fails (common on Android TV).
     */
    private fun appExtractDir(context: Context): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: File(context.filesDir, Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /**
     * [Util.get_out_filename] is `/pkg_vN.apk` for string concat with EXTERNAL_STORAGE.
     * [File](parent, child) treats a leading `/` as **absolute** and ignores parent —
     * that becomes `/pkg_vN.apk` (needs root). Strip for File/MediaStore display names.
     */
    private fun relativeOutName(meta: PackageMeta): String =
        Util.get_out_filename(meta).trimStart('/', '\\')

    @Throws(Exception::class)
    private fun extractBaseApkWithoutRoot(context: Context, meta: PackageMeta): File {
        val newApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        val src = File(meta.sourceDir)
        val fileName = relativeOutName(meta)

        // Q+: prefer MediaStore Downloads (phone). TV often returns null insert →
        // fall back to app-external storage (same helper for phone+TV, no fork).
        if (newApi) {
            try {
                copyApkFileToDownloads(context.contentResolver, src, fileName)
                return File(Troubleshooting.defLocation(), fileName)
            } catch (ex: Exception) {
                d("MediaStore/Downloads extract failed, app storage fallback: ${ex.message}")
                var out = File(appExtractDir(context), fileName)
                out = Util.buildDstPath(out)
                if (out.exists()) out.delete()
                try {
                    copyFileOld(context, src, out, out.name)
                } catch (ex2: Exception) {
                    println("@@@@@@@@@@@@@@" + ex2.message + " " + ex2.javaClass.simpleName)
                    throw Exception(ex2.message)
                }
                if (!out.exists() || out.length() == 0L) {
                    println("cannot extract file [no root]")
                    throw Exception("cannot extract file [no root]")
                }
                return out
            }
        }

        var out = File(Troubleshooting.defLocation(), fileName)
        out = Util.buildDstPath(out)
        if (out.exists()) {
            out.delete()
        }
        try {
            if (out.exists() && out.isFile && out.length() == src.length()) {
                println("skip file...")
            } else {
                copyFileOld(context, src, out, out.name)
            }
        } catch (ex: Exception) {
            // Pre-Q public Downloads may be blocked — same app-storage fallback.
            d("Public Downloads extract failed, app storage fallback: ${ex.message}")
            out = File(appExtractDir(context), fileName)
            out = Util.buildDstPath(out)
            if (out.exists()) out.delete()
            try {
                copyFileOld(context, src, out, out.name)
            } catch (ex2: Exception) {
                println("@@@@@@@@@@@@@@" + ex2.message + " " + ex2.javaClass.simpleName)
                throw Exception(ex2.message)
            }
        }
        if (!out.exists() || out.length() == 0L) {
            println("cannot extract file [no root]")
            throw Exception("cannot extract file [no root]")
        }
        return out
    }


    //Return success {and not success} extracted files...
    @Throws(Exception::class)
    private fun executeBackupWithPacking(context: Context, packageName: String, apkFiles: List<File>): List<File> {
        var apkFiles1 = apkFiles
        val newApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        val result: MutableList<File> = ArrayList()

        apkFiles1 = ArrayList(apkFiles1)
        Collections.sort(apkFiles1)

        val folderName = Config.PREV + packageName.replace(".", "_")
        var base = File(Troubleshooting.defLocation(), folderName)
        try {
            Util.buildDstPath(File(base, apkFiles1[0].name))
        } catch (ex: Exception) {
            d("Split extract dir not writable, app storage: ${ex.message}")
            base = File(appExtractDir(context), folderName)
            Util.buildDstPath(File(base, apkFiles1[0].name))
        }
        d("{@}--> base $base ")
        var out: File? = null
        try {
            for (src in apkFiles1) {
                out = File(base, src.name)
                d("{@}--> $out ")
                if (!newApi && out.exists() && out.isFile && out.length() == src.length()) {
                    d("--> Skip file..." + out.name + " " + out.exists())
                    result.add(out)
                } else {
                    d("--> Copy file..." + out.absolutePath)
                    val written = copyFileRequest(context, src, out, out.name)
                    result.add(written)
                }
            }
        } catch (ex: Exception) {
            d("@@@@@@@@@@@@@@" + ex.message + " " + ex.javaClass.simpleName)
            throw Exception(ex.message)
        }
        if (!newApi && out != null && !out.exists()) {
            d("cannot extract file [no root]")
            throw Exception("cannot extract file [no root]")
        }
        return result
    }

    /** @return actual destination file written */
    @Throws(IOException::class)
    private fun copyFileRequest(context: Context, sourceApk: File, out: File, apkName: String): File {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val resolver = context.contentResolver
                copyApkFileToDownloads(resolver, sourceApk, apkName)
                d("APK copied successfully to Downloads.")
                return out
            } catch (e: IOException) {
                e("Failed to copy APK to MediaStore: " + e.message)
                val fallback = try {
                    out.parentFile?.mkdirs()
                    if (out.parentFile?.canWrite() == true) out
                    else Util.buildDstPath(File(appExtractDir(context), apkName))
                } catch (_: Exception) {
                    Util.buildDstPath(File(appExtractDir(context), apkName))
                }
                copyFileOld(context, sourceApk, fallback, apkName)
                return fallback
            }
        } else {
            copyFileOld(context, sourceApk, out, apkName)
            return out
        }
    }

    var fileSize: String = ""


    @Throws(IOException::class)
    private fun copyFileOld(context: Context, sourceApk: File, destFile: File, apkName: String) {
        if (!destFile.exists()) {
            val tmp = destFile.createNewFile()
            d("CRATE_FILE: -> $tmp")
        }
        d("@" + sourceApk.absolutePath + "|" + destFile.absolutePath)

        val source = FileInputStream(sourceApk).channel
        val destination = FileOutputStream(destFile).channel
        fileSize = Util.getFileSizeMegaBytes(sourceApk)


//        long size = source.transferTo(0, source.size(), destination);
//        DLog.d("@ size->" + size);

//        long size = destination.transferFrom(source, 0, Long.MAX_VALUE);
//        DLog.d("@ size->" + size);
        try {
            val rbc =
                RBCWrapper(
                    source, source.size(),
                    object : RBCWrapperDelegate {
                        override fun rbcProgressCallback(
                            position: Int,
                            totalFileSize: Int,
                            rbc: RBCWrapper?,
                            progress: Double
                        ) {
                            d("position->$position")
                            //DLog.d("@@@" + index + "/" + totalFileSize + "\t" + sourceFile.getAbsolutePath() + "\t" + fileSize + "\t" + progress);
                            mMainThread.launch {
                                mView.rbcProgressCallback(
                                    index,
                                    totalFileSize,
                                    sourceApk,
                                    fileSize ?: "",
                                    progress
                                )
                            }
                        }
                    }

                )


            //CRASH __> long size = destination.transferFrom(rbc, 0, Long.MAX_VALUE);
            val size = destination.transferFrom(rbc, 0, source.size())
            d("@ size->$size")
        } catch (a: IOException) {
            d("@@@@@@@" + a.message)
        } catch (e: IllegalArgumentException) { //android 5.1
            d("xxxxxxxx" + e.message)
        } finally {
            source?.close()
            destination?.close()
        }
        d("S:" + sourceApk.length() + " " + ", D: " + destFile.length())
    }


    @Throws(IOException::class)
    private fun copyApkFileToDownloads(
        resolver: ContentResolver,
        sourceFile: File,
        apkName: String
    ) {
        // Создаем ContentValues для записи в MediaStore
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, apkName) // Имя файла
        contentValues.put(
            MediaStore.MediaColumns.MIME_TYPE,
            "application/vnd.android.package-archive"
        ) // MIME-тип для APK
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DOWNLOADS
        ) // Каталог Downloads

        // Вставляем запись в MediaStore
        val apkUri: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            apkUri = resolver.insert(
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL),
                contentValues
            )
        } else {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                throw IOException("Failed to create downloads directory.")
            }
            val destFile = File(downloadsDir, apkName)
            apkUri = Uri.fromFile(destFile)
        }

        if (apkUri == null) {
            throw IOException("Failed to create MediaStore record.")
        }
        // Открываем потоки для копирования
        try {
            FileInputStream(sourceFile).use { inputStream ->
                resolver.openOutputStream(apkUri).use { outputStream ->
                    if (outputStream == null) {
                        throw IOException("Failed to open output stream.")
                    }
                    copyStreamWithProgress(inputStream, outputStream, sourceFile)
                }
            }
        } catch (e: IOException) {
            // Удаляем запись в случае ошибки
            resolver.delete(apkUri, null, null)
            throw e
        }
    }

    @Throws(IOException::class)
    private fun copyStreamWithProgress(input: InputStream, output: OutputStream, sourceApk: File) {
        val totalBytes = sourceApk.length()
        val buffer = ByteArray(8192)
        var totalRead: Long = 0
        var bytesRead: Int

        while ((input.read(buffer).also { bytesRead = it }) != -1) {
            output.write(buffer, 0, bytesRead)
            totalRead += bytesRead.toLong()

            // Логируем прогресс копирования
            val progress = totalRead.toDouble() / totalBytes * 100
            d("Copy Progress: " + String.format("%.2f", progress) + "%")
            mMainThread.launch {
                mView.rbcProgressCallback(
                    index,
                    totalFileSize,
                    sourceApk,
                    fileSize,
                    progress
                )
            }
        }
        output.flush()
    }


//    override fun run() {}


    fun executeInternal(metas: List<PackageMeta>, context: Context) {
        this.totalFileSize = metas.size

        mThreadExecutor.launch {
            try {
                val total_extractedFiles: MutableMap<SimpleMeta, List<File>> =
                    HashMap()

                val none_extracted: MutableList<PackageMeta> = ArrayList()

                //        for (PackageInfo info : metas) {
                //            DLog.d("--> " + info.packageName);
                //        }
                for (i in 0 until totalFileSize) {
                    val meta = metas[i]
                    val name0 = meta.label
                    val apkFiles = ExtractorUtils.getAllApkFilesForCurrentPackage(context, meta.packageName)


                        //this.index = i


                    println("Extracting package: $name0")
                    println("Extracting $name0 with ${apkFiles.size} files")

                    try {
                        if (apkFiles.size == 1) {
                            val file = extractBaseApkWithoutRoot(context, meta)
                            if (file != null) {
                                mMainThread.launch {
                                    if (file.exists() && !file.isDirectory) {
                                        //File[] mm = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).listFiles();
                                        println("[***]" + file.absolutePath)
                                        MediaScannerConnection.scanFile(
                                            context,
                                            arrayOf(file.toString()),
                                            null
                                        ) { path: String?, uri: Uri? -> d("Gallery is refreshed.") }
                                    }
                                    mView.makeSnackBar(file)
                                }
                                //### successExtracted(extracted, appName);//to cloud
                                total_extractedFiles[SimpleMeta(name0, meta.packageName)] =
                                    listOf(file)
                            }
                        } else {
                            val files =
                                executeBackupWithPacking(context, meta.packageName, apkFiles)
                            d("-->" + apkFiles.size)
                            if (files.size > 0) {
                                mMainThread.launch {
                                    val file = files[0]
                                    if (file.exists() && !file.isDirectory) {
                                        //File[] mm = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).listFiles();
                                        d("[***]" + file.absolutePath)
                                        MediaScannerConnection.scanFile(
                                            context,
                                            arrayOf(file.toString()),
                                            null
                                        ) { path: String?, uri: Uri? -> d("Gallery is refreshed.") }
                                    }
                                    mView.makeSnackBar(file)
                                }
                                //### successExtracted(extracted, appName);//to cloud
                                total_extractedFiles[SimpleMeta(name0, meta.packageName)] = files
                            }
                        }
                    } catch (ex: Exception) {
                        handleException(ex)
                        none_extracted.add(meta)
                    }

                    //1234 --> mMainThread.post(() -> callback.onProgress0(finalI, totalFileSize));
                }

                //                for (Map.Entry<String, List<File>> entry : total_extractedFiles.entrySet()) {
//                    DLog.d("@@@" + entry.getKey() + ":::" + entry.getValue().toString());
//                }
                if (total_extractedFiles != null && !total_extractedFiles.isEmpty()) {
                    mMainThread.launch { mView.successExtracted(total_extractedFiles) }
                }
                mMainThread.launch {
                    mView.hideProgressBar(metas.size)
                }


                if (!none_extracted.isEmpty()) {
                    mMainThread.launch { mView.extractOnePleaseDialog(none_extracted) }
                }
            } catch (e: Exception) {
                d(e.message)
                mMainThread.launch {
                    mView.hideProgressBar(metas.size)
                }
            }
        }
    }
}