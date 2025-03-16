package com.walhalla.appextractor.utils

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.walhalla.appextractor.sdk.ProviderLine
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException


object LauncherUtils {
    val CONTENT_URI: Uri = Uri.parse("content://com.oppo.ota/")

    fun onLaunchExportedActivity(context: Context, packageName: String, className: String) {
        val title = "Launch Activity"

        //android:exported="true"
        //        if ("com.facebook.CustomTabActivity".equalsIgnoreCase(className)) {
        /*<activity android : name ="com.facebook.CustomTabActivity" android:exported = "true">
        * /            <intent-filter>
        * /                <action android:name = "android.intent.action.VIEW"></action>
        * /                <category android:name = "android.intent.category.DEFAULT"></category>
        * /                <category android:name = "android.intent.category.BROWSABLE"></category>
        * /                <data android:scheme = "fbconnect" android:host = "cct.app.dogo.com.dogo_androi"></data>
        * /            </intent-filter>
        * /        </activity> */
//
//            try {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("fbconnect://cct.app.dogo.com.dogo_androi"));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.getApplicationContext().startActivity(intent);
//            } catch (RuntimeException e) {
//                DLog.handleException(e);
//                Toast.makeText(context, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                //DLog.d("@@@@"+e.getLocalizedMessage());
//            }
//            return;
//        }

        //android:exported="true"
        try {
            // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
            // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
            val intent = Intent()

            //            if("com.facebook.CustomTabActivity".equals(className)){
//                intent = new Intent(Intent.ACTION_VIEW);
//                //intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                intent.setDataAndType(Uri.parse("https://2ip.ru"), "*/*");
//                Toast.makeText(this, "INJECTION", Toast.LENGTH_SHORT).show();
//            }else {
//                intent = new Intent();
//            }
            intent.setComponent(ComponentName(packageName, className))

            //no intent.setPackage(packageInfo.packageName);
            //no intent.setClassName(packageInfo.packageName, className);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            //@launcher.launch(intent);
            //startActivityForResult(intent, 444);
            context.applicationContext.startActivity(intent)
        } catch (e: RuntimeException) {
            handleException(e)
            showErrorMessageDialog(context, title, e.localizedMessage)
            //DLog.d("@@@@"+e.getLocalizedMessage());
        } catch (e: Exception) {
            handleException(e)
            showErrorMessageDialog(context, title, e.localizedMessage)
            //DLog.d("@@@@"+e.getLocalizedMessage());
        }
    }

    fun onLaunchProvider(
        myActivityContext: Context,
        provider: ProviderLine,
        callback: LauCallback
    ) {
        onLaunchProviderByAuthority(myActivityContext, provider.authority, callback)
        //onLaunchProviderByClassName(myActivityContext, provider.class_name);
    }

    private fun onLaunchProviderByClassName(myActivityContext: Context, className: String) {
        //@@
    }


    //content://org.telegram.messenger.web.call_sound_provider/
    //Почему Attempt to get length of null array
    private fun onLaunchProviderByAuthority(
        myActivityContext: Context,
        authority: String,
        callback: LauCallback
    ) {
        val title = "Provider"

        //        try {
//            DLog.d("---"+uriString);
//            Intent intent = new Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse(uriString)
//            );
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }catch (Exception re){
//            DLog.handleException(re);
//        }

        //Uri uri = Uri.parse("content://....");
        var uri = Uri.parse(authority)
        if (uri == CONTENT_URI) {
            uri = Uri.parse(authority + "" + "patch")
        }

        //        uri = Uri.parse("content://com.oplus.customize.coreapp.configmanager.configprovider.AppFeatureProvider")
//                .buildUpon().appendPath("app_feature").build();
//        String type = myActivityContext.getContentResolver().getType(uri);
//        if (type == null) {
//            // Тип данных не определен, возможно, URI неверен или контент-провайдер недоступен
//            Log.e("ContentResolver", "No data type found for URI: " + uri.toString());
//        } else {
//            Log.d("ContentResolver", "Data type: " + type);
//        }
        try {
            d("//111")
            //CONTENT_URI
            val cursor = myActivityContext.contentResolver.query(uri, null, null, null, null)
            //exported=true
            d("//2222 $cursor")
            if (cursor != null) {
                val count = cursor.count
                val sb = StringBuilder()
                d("//,,,,")
                val columnNames = cursor.columnNames
                sb.append("Column Names: ").append(columnNames.contentToString())
                d("// Переместите курсор на первую строку")
                if (cursor.moveToFirst()) {
                    do {
                        if (columnNames != null) {
                            // Проход по всем строкам и вывод данных для каждой строки
                            for (columnName in columnNames) {
                                d("Получите данные из каждого столбца: $columnName")
                                val columnIndex = cursor.getColumnIndex(columnName)
                                try {
                                    val columnValue = cursor.getString(columnIndex)
                                    sb.append("$columnName: $columnValue")
                                } catch (e: Exception) {
                                    sb.append(columnName + ": " + e.localizedMessage)
                                }
                            }
                        } else {
                            sb.append("String[] columnNames: NULL")
                        }
                    } while (cursor.moveToNext())
                }
                showSuccessMessageDialog(myActivityContext, title, sb.toString())
            } else {
                callback.showError("cursor null $uri")
            }
        } catch (ex: SecurityException) {
            //exported=false

            d("Error: " + ex.javaClass + ", " + ex.message)
            showErrorMessageDialog(
                myActivityContext,
                title,
                "Error: " + ex.javaClass + ", " + ex.message
            )
        } catch (ex: Exception) {
            d("Error: " + ex.javaClass + ", " + ex.message)
            showErrorMessageDialog(
                myActivityContext,
                title,
                "Error: " + ex.javaClass + ", " + ex.message
            )
        }
    }

    fun showErrorMessageDialog(context: Context, title: String?, errorMessage: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(errorMessage)
            .setPositiveButton(
                "OK"
            ) { dialog: DialogInterface, id: Int ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showSuccessMessageDialog(context: Context, title: String, errorMessage: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(errorMessage)
            .setPositiveButton(
                "OK"
            ) { dialog: DialogInterface, id: Int ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    interface LauCallback {
        fun showError(s: String)
    }
}
