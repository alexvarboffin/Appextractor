package com.walhalla.appextractor.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import com.walhalla.abcsharedlib.Share
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.appextractor.utils.ShareUtils.KEY_FILE_PROVIDER
import com.walhalla.extractor.R
import com.walhalla.ui.DLog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IntentUtil {


    companion object {


        //    Длина extra.length() == 130124
        //    но уже получаю
        //    android.os.TransactionTooLargeException: data parcel size 523504 bytes
        fun shareTextLikeFile(
            context: Context,
            meta: PackageMeta,
            extra: String,
            fileName: String,
            chooserTitle: String?
        ) {
            var title = chooserTitle
            d("{share} " + extra.length)

            if (title.isNullOrEmpty()) {
                title = context.resources.getString(R.string.app_name)
            }


            val packageName = meta.packageName

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, packageName)


            //intent.putExtra(Intent.EXTRA_EMAIL, "alexvarboffin@gmail.com");//Work only with intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            //intent.setType("*/*");
//            if (extra.length < 50000) {
//                intent.putExtra(Share.comPinterestEXTRA_DESCRIPTION, text)
//            }
            intent.putExtra(Share.comPinterestEXTRA_DESCRIPTION, packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // Сохраняем текст во временный файл в temp папку
            try {
                // Создаем временный файл в папке "cache" вашего приложения
                val tempFile = File(context.cacheDir, fileName)
                val fos = FileOutputStream(tempFile)
                fos.write(extra.toByteArray())
                fos.close()

                // Получаем URI файла через FileProvider
                val fileUri = FileProvider.getUriForFile(
                    context,
                    context.packageName + KEY_FILE_PROVIDER,
                    tempFile
                )

                // Передаем файл через Intent
                intent.putExtra(Intent.EXTRA_STREAM, fileUri)
                intent.setType("text/plain")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)

                // Добавляем тему сообщения
                intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))

                // Открываем выбор приложений для отправки
                val chooser = Intent.createChooser(intent, title)
                val resInfoList = context.packageManager.queryIntentActivities(
                    chooser,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                if (!resInfoList.isEmpty()) {
                    context.startActivity(chooser)
                } else {
                    d("Not found activity...")
                }
            } catch (e: IOException) {
                handleException(e)
            }
        }




        //    Длина extra.length() == 130124
        //    но уже получаю
        //    android.os.TransactionTooLargeException: data parcel size 523504 bytes
        fun shareFile(context: Context, extra: String, fileName: String?, chooserTitle: String?) {
//            var chooserTitle = chooserTitle
//            println("{share} " + extra.length)
//
//            if (chooserTitle == null) {
//                chooserTitle = context.resources.getString(R.string.app_name)
//            }
//            val text: String = meta.packageName
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.putExtra(Intent.EXTRA_TEXT, text)
//
//
//            //intent.putExtra(Intent.EXTRA_EMAIL, "alexvarboffin@gmail.com");//Work only with intent.setType("*/*");
//            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
//            //intent.setType("*/*");
//            if (extra.length < 50000) {
//                intent.putExtra(Share.comPinterestEXTRA_DESCRIPTION, text)
//            }
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//            // Сохраняем текст во временный файл в temp папку
//            try {
//                // Создаем временный файл в папке "cache" вашего приложения
//                val tempFile = File(context.cacheDir, fileName)
//                val fos = FileOutputStream(tempFile)
//                fos.write(extra.toByteArray())
//                fos.close()
//
//                // Получаем URI файла через FileProvider
//                val fileUri = FileProvider.getUriForFile(
//                    context,
//                    context.packageName + KEY_FILE_PROVIDER,
//                    tempFile
//                )
//
//                // Передаем файл через Intent
//                intent.putExtra(Intent.EXTRA_STREAM, fileUri)
//                intent.setType(com.walhalla.ui.plugins.MimeType.TEXT_PLAIN)
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//                // Добавляем тему сообщения
//                intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
//
//                // Открываем выбор приложений для отправки
//                val chooser = Intent.createChooser(intent, chooserTitle)
//                val resInfoList = context.packageManager.queryIntentActivities(
//                    chooser,
//                    PackageManager.MATCH_DEFAULT_ONLY
//                )
//                if (!resInfoList.isEmpty()) {
//                    context.startActivity(chooser)
//                } else {
//                    println("Not found activity...")
//                }
//            } catch (e: IOException) {
//                DLog.handleException(e)
//            }
        }

        fun openSettingsForPackageName(context: Context, packageName: String) {
            try {
                val intent = Intent()
                    .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .addCategory(Intent.CATEGORY_DEFAULT)
                    .setData(Uri.parse("package:$packageName"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                context.startActivity(intent)
            } catch (e: Exception) {
                DLog.handleException(e)
            }
        }

        fun openSettingsForPackageName2(context: Context, packageName: String?) {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                context.startActivity(intent)
            } catch (e: Exception) {
                DLog.handleException(e)
            }
        }
    }
}