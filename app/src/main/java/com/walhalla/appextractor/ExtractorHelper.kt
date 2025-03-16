package com.walhalla.appextractor

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import com.walhalla.appextractor.DownloadProgressExample.RBCWrapper
import com.walhalla.appextractor.core.RBCWrapperDelegate
import com.walhalla.appextractor.domain.interactors.SimpleMeta
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.model.ViewModel
import com.walhalla.appextractor.utils.DLog

import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.e
import com.walhalla.ui.DLog.handleException
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
    }

    //stat
    private var totalFileSize = 0
    private var index = 0

    @Throws(Exception::class)
    protected fun getAllApkFilesForPackage(context: Context, pkg: String): List<File> {
        val applicationInfo = context.packageManager.getApplicationInfo(pkg, 0)

        val apkFiles: MutableList<File> = ArrayList()
        apkFiles.add(File(applicationInfo.publicSourceDir))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (applicationInfo.splitPublicSourceDirs != null) {
                for (splitPath in applicationInfo.splitPublicSourceDirs!!) apkFiles.add(
                    File(
                        splitPath
                    )
                )
            }
        }
        return apkFiles
    }

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
    @Throws(Exception::class)
    private fun extractWithoutRoot(context: Context, meta: PackageMeta): File {
        val newApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        //DLog.d("@@@" + meta.packageName+" "+meta.sourceDir);
        val src = File(meta.sourceDir)
        val fileName = Util.get_out_filename(meta)
        var out = File(Troubleshooting.defLocation(), fileName)
        out = Util.buildDstPath(out)
        if (out.exists()) {
            val res = out.delete()
        }
        try {
//            File tmp = new File(out.getAbsolutePath());
//            if (BuildConfig.DEBUG) {
//                String s = Util.getFileSizeMegaBytes(src) + "\t" + Util.getFileSizeMegaBytes(out);
//                DLog.d("ExtractWithoutRoot: --> " + s);
//                DLog.d("--> " + src.getAbsolutePath() + "-->" + src.length());
//                DLog.d("--> " + tmp.getAbsolutePath() + "-->" + tmp.length());
//                for (File f : new File(Troubleshooting.defLocation(), __CLOUD_BACKUP_LOCATION_LOCAL).listFiles()) {
//                    DLog.d("F->" + f.getName() + "\t" + f.length());
//                }
//            }


            if (!newApi && out.exists() && out.isFile && out.length() == src.length()) {
                d("skip file...")
            } else {
                copyFileRequest(context, src, out, out.name)
            }
        } catch (ex: Exception) {
            d("@@@@@@@@@@@@@@" + ex.message + " " + ex.javaClass.simpleName)
            throw Exception(ex.message)
        }
        if (!newApi && !out.exists()) {
            d("cannot extract file [no root]")
            throw Exception("cannot extract file [no root]")
        }
        return out
    }


    //Return success {and not success} extracted files...
    @Throws(Exception::class)
    private fun executeBackupWithPacking(
        context: Context,
        packageName: String,
        apkFiles: List<File>
    ): List<File> {
        var apkFiles1 = apkFiles
        val newApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        val result: MutableList<File> = ArrayList()

        apkFiles1 = ArrayList(apkFiles1)
        Collections.sort(apkFiles1)

        val base = File(Troubleshooting.defLocation(), Config.PREV + packageName.replace(".", "_"))
        val out00 = Util.buildDstPath(File(base, apkFiles1[0].name)) //Создаем папку куда дропать
        d("{@}--> $out00 ")
        //        if (out0.exists()) {
//            boolean res = out0.delete();
//        }
        var out: File? = null
        try {
//            File tmp = new File(out.getAbsolutePath());
//            if (BuildConfig.DEBUG) {
//                String s = Util.getFileSizeMegaBytes(src) + "\t" + Util.getFileSizeMegaBytes(out);
//                DLog.d("ExtractWithoutRoot: --> " + s);
//                DLog.d("--> " + src.getAbsolutePath() + "-->" + src.length());
//                DLog.d("--> " + tmp.getAbsolutePath() + "-->" + tmp.length());
//                for (File f : new File(Troubleshooting.defLocation(), __CLOUD_BACKUP_LOCATION_LOCAL).listFiles()) {
//                    DLog.d("F->" + f.getName() + "\t" + f.length());
//                }
//            }

            for (src in apkFiles1) {
                out = File(base, src.name)
                d("{@}--> $out ")
                if (!newApi && out.exists() && out.isFile && out.length() == src.length()) {
                    d("--> Skip file..." + out.name + " " + out.exists())
                    result.add(out)
                } else {
                    d("--> Copy file..." + out.absolutePath)
                    copyFileRequest(context, src, out, out.name)
                    result.add(out)
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

    @Throws(IOException::class)
    private fun copyFileRequest(context: Context, sourceApk: File, out: File, apkName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val resolver = context.contentResolver
                //String apkName = "example_app_" + System.currentTimeMillis() + ".apk";
                copyApkFileToDownloads(resolver, sourceApk, apkName)
                d("APK copied successfully to Downloads.")
            } catch (e: IOException) {
                e("Failed to copy APK: " + e.message)
                copyFileOld(context, sourceApk, out, apkName)
            }
        } else {
            copyFileOld(context, sourceApk, out, apkName)
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

                val none_extracted: MutableList<PackageMeta> =
                    ArrayList()

                //        for (PackageInfo info : metas) {
                //            DLog.d("--> " + info.packageName);
                //        }
                for (i in 0 until totalFileSize) {
                    val meta = metas[i]
                    val name0 = meta.label
                    val apkFiles = getAllApkFilesForPackage(context, meta.packageName)


                    //@@@ this.index = i

                    try {
                        if (apkFiles.size == 1) {
                            val file = extractWithoutRoot(context, meta)
                            if (file != null) {
                                mMainThread.launch {
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
                    EXTRACT_ONE_PLEASE(none_extracted, context)
                }
            } catch (e: Exception) {
                d(e.message)
                mMainThread.launch {
                    mView.hideProgressBar(metas.size)
                }
            }
        }
    }

    fun EXTRACT_ONE_PLEASE(data: List<PackageMeta>, context: Context?) {
        d("**** NONE EXTRACTED: " + data.size)


        if (context != null) {
            mMainThread.launch {
                AlertDialog.Builder(context)
                    .setTitle(R.string.alert_root_title)
                    .setMessage(
                        context.getString(
                            R.string.alert_root_body,
                            context.getString(R.string.app_name_full)
                        )
                    )
                    .setPositiveButton(
                        R.string.alert_root_yes
                    ) { dialog: DialogInterface?, which: Int ->
                        try {
                            for (info in data) {
                                val file =
                                    File(Util.extractWithRoot(info))
                                mView.makeSnackBar(file)
                                //callback.printOutput(new LFileViewModel(file[0], "Success",R.drawable.ic_baseline_sd_card_24));
                                //###
                                // successExtracted(mFile, appName);//to cloud
                            }
                        } catch (e: Exception) {
                            mView.failureExtracted(R.string.toast_failed)
                        }
                    }
                    .setNegativeButton(
                        R.string.alert_root_no
                    ) { dialog: DialogInterface?, which: Int -> d("cancel") }
                    .show()
            }
        }
    }
}