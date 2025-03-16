package com.walhalla.appextractor.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider

import com.walhalla.appextractor.DownloadProgressExample

import com.walhalla.appextractor.Troubleshooting.defLocation
import com.walhalla.appextractor.core.RBCWrapperDelegate

import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.appextractor.utils.FileUtil.buildDstPath

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object ShareUtils {

    const val KEY_FILE_PROVIDER: String = ".fileprovider"

    fun shareApkFile(context: Context, meta: PackageMeta, filePath: String) {
        try {
            val packageName0 = context.packageName
            val file0 = extractWithoutRoot(File(filePath))
            val path = FileProvider.getUriForFile(context, packageName0 + KEY_FILE_PROVIDER, file0)

            if (path != null) {
                val description = meta.packageName
                shareFileApk(context, packageName0, description, path)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun shareFileApk(
        context: Context,
        packageName0: String,
        description: String,
        fileUri: Uri
    ) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.setType("application/octet-stream")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, description)
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)

        // Предоставление временного доступа к файлу для других приложений
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Запуск выбора приложения для отправки (включая Gmail)
        val choozer = Intent.createChooser(emailIntent, "Send email using:")
        choozer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(choozer)
    }

    @Throws(Exception::class)
    private fun extractWithoutRoot(file: File): File {
        //DLog.d("@@@" + meta.packageName+" "+meta.sourceDir);

        var out = File(defLocation(), "base.apk")
        out = buildDstPath(out)
        if (out.exists()) {
            val res = out.delete()
        }
        try {
//            File tmp = new File(out.getAbsolutePath());
//            if (BuildConfig.DEBUG) {
//                String s = Util.getFileSizeMegaBytes(file) + "\t" + Util.getFileSizeMegaBytes(out);
//                DLog.d("ExtractWithoutRoot: --> " + s);
//                DLog.d("--> " + file.getAbsolutePath() + "-->" + file.length());
//                DLog.d("--> " + tmp.getAbsolutePath() + "-->" + tmp.length());
//                for (File f : new File(Troubleshooting.defLocation(), __CLOUD_BACKUP_LOCATION_LOCAL).listFiles()) {
//                    DLog.d("F->" + f.getName() + "\t" + f.length());
//                }
//            }

            if (out.exists() && out.isFile && out.length() == file.length()) {
                d("skip file...")
            } else {
                copyFile(file, out)
            }
        } catch (ex: Exception) {
            d("@@@@@@@@@@@@@@" + ex.message + " " + ex.javaClass.simpleName)
            throw Exception(ex.message)
        }
        if (!out.exists()) {
            d("cannot extract file [no root]")
            throw Exception("cannot extract file [no root]")
        }
        return out
    }

    @Throws(IOException::class)
    private fun copyFile(sourceFile: File, destFile: File) {
        if (!destFile.exists()) {
            val tmp = destFile.createNewFile()
            d("CRATE_FILE: -> $tmp")
        }

        d("@" + sourceFile.absolutePath + "|" + destFile.absolutePath)

        val source = FileInputStream(sourceFile).channel
        val destination = FileOutputStream(destFile).channel
        val fileSize = FileUtil.getFileSizeMegaBytes(sourceFile)


//        long size = source.transferTo(0, source.size(), destination);
//        DLog.d("@ size->" + size);

//        long size = destination.transferFrom(source, 0, Long.MAX_VALUE);
//        DLog.d("@ size->" + size);
        try {
            val rbc =
                DownloadProgressExample.RBCWrapper(
                    source, source.size(),
                     delegate = object : RBCWrapperDelegate {


                         override fun rbcProgressCallback(
                             position: Int,
                             totalFileSize: Int,
                             rbc: DownloadProgressExample.RBCWrapper?,
                             progress: Double
                         ) {

                             d("position->$position")
                             //DLog.d("@@@" + index + "/" + totalFileSize + "\t" + sourceFile.getAbsolutePath() + "\t" + fileSize + "\t" + progress);
                             //                                    mMainThread.post(new Runnable() {
                             //                                        @Override
                             //                                        public void run() {
                             //                                            mView.rbcProgressCallback(index, totalFileSize, sourceFile, fileSize, progress);
                             //                                        }
                             //                                    });
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
        d("S:" + sourceFile.length() + " " + ", D: " + destFile.length())
    }
}
