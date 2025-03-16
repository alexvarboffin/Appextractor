package com.walhalla.appextractor.utils

import android.annotation.SuppressLint
import android.os.Build
import com.walhalla.appextractor.Config
import com.walhalla.ui.DLog.handleException
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale

object FileUtil {


    @SuppressLint("ObsoleteSdkInt")
    fun get_out_filename(meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta): String {
        //        long versionCode;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            versionCode = meta.getLongVersionCode();
//        } else {
//            versionCode = meta.versionCode;
//        }

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            ("/Download/"
                    + Config.__CLOUD_BACKUP_LOCATION_LOCAL + meta.packageName + "_v" + meta.versionCode + ".apk")
        } else {
            ("/"
                    + Config.__CLOUD_BACKUP_LOCATION_LOCAL + meta.packageName + "_v" + meta.versionCode + ".apk")
        }
    }




    @Throws(Exception::class)
    fun extractWithRoot(meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta): String {
        val src = File(meta.sourceDir)
        val path = System.getenv("EXTERNAL_STORAGE") + get_out_filename(meta)

        val dst = buildDstPath(File(path))
        if (dst.exists()) {
            val res = dst.delete()
        }

        var p: Process? = null
        val err = StringBuilder()
        try {
            p = Runtime.getRuntime()
                .exec("su -c cat " + src.absolutePath + " > " + dst.absolutePath)
            p.waitFor()

            if (p.exitValue() != 0) {
                val reader = BufferedReader(InputStreamReader(p.errorStream))
                var line: String? = ""
                while ((reader.readLine().also { line = it }) != null) {
                    err.append(line)
                    err.append("\n")
                }

                throw Exception(err.toString())
            }
        } catch (e: IOException) {
            throw Exception(e.message)
        } catch (e: InterruptedException) {
            throw Exception(e.message)
        } finally {
            if (p != null) {
                try {
                    p.destroy()
                } catch (e: Exception) {
                    handleException(e)
                }
            }
        }
        if (!dst.exists()) {
            throw Exception("cannot exctract file [root]")
        }
        return dst.absolutePath
    }

    fun buildDstPath(path: File): File {
        if ((!path.parentFile.exists() && !path.parentFile.mkdirs()) || !path.parentFile.isDirectory) {
            throw IOException("Cannot create directory: " + path.parentFile.absolutePath)
        }
        if (!path.exists()) return path

        var dst = path
        val fname = path.name
        val index = fname.lastIndexOf(".")
        val ext = fname.substring(index)
        val name = fname.substring(0, index)

        var i = 0
        while (dst.exists()) {
            dst = File(path.parentFile, "$name-$i$ext")
            i++
        }

        return dst
    }

    fun getFileSizeMegaBytes(file: File): String {
        return String.format(
            Locale.CANADA,
            "%.2f MB",
            getFolderSize(file).toDouble() / (1024 * 1024)
        )
    }

    fun getFolderSize(folderOrFile: File): Long {
        var length: Long = 0
        if (folderOrFile.isFile) {
            length = folderOrFile.length()
        } else {
            // listFiles() is used to list the
            // contents of the given folder
            val files = folderOrFile.listFiles()

            if (files != null) {
                val count = files.size
                // loop for traversing the directory
                for (i in 0 until count) {
                    length += if (files[i].isFile) {
                        files[i].length()
                    } else {
                        getFolderSize(files[i])
                    }
                }
            }
        }
        return length
    }
}
