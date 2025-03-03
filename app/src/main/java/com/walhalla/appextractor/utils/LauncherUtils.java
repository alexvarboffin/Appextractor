package com.walhalla.appextractor.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.walhalla.appextractor.adapter2.provider.ProviderLine;
import com.walhalla.ui.DLog;

import java.util.Arrays;

public class LauncherUtils {

    public static final Uri CONTENT_URI = Uri.parse("content://com.oppo.ota/");

    public static void onLaunchExportedActivity(Context context, String packageName, String className) {
        String title = "Launch Activity";

        //android:exported="true"
        //        if ("com.facebook.CustomTabActivity".equalsIgnoreCase(className)) {
////<activity android:name="com.facebook.CustomTabActivity" android:exported="true">
////            <intent-filter>
////                <action android:name="android.intent.action.VIEW"/>
////                <category android:name="android.intent.category.DEFAULT"/>
////                <category android:name="android.intent.category.BROWSABLE"/>
////                <data android:scheme="fbconnect" android:host="cct.app.dogo.com.dogo_androi"/>
////            </intent-filter>
////        </activity>
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
            Intent intent = new Intent();

//            if("com.facebook.CustomTabActivity".equals(className)){
//                intent = new Intent(Intent.ACTION_VIEW);
//                //intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                intent.setDataAndType(Uri.parse("https://2ip.ru"), "*/*");
//                Toast.makeText(this, "INJECTION", Toast.LENGTH_SHORT).show();
//            }else {
//                intent = new Intent();
//            }

            intent.setComponent(new ComponentName(packageName, className));
            //no intent.setPackage(packageInfo.packageName);
            //no intent.setClassName(packageInfo.packageName, className);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //@launcher.launch(intent);
            //startActivityForResult(intent, 444);

            context.getApplicationContext().startActivity(intent);
        } catch (RuntimeException e) {
            DLog.handleException(e);
            showErrorMessageDialog(context, title, e.getLocalizedMessage());
            //DLog.d("@@@@"+e.getLocalizedMessage());
        } catch (Exception e) {
            DLog.handleException(e);
            showErrorMessageDialog(context, title, e.getLocalizedMessage());
            //DLog.d("@@@@"+e.getLocalizedMessage());
        }
    }

    public static void onLaunchProvider(Context myActivityContext, ProviderLine provider, LauCallback callback) {
        onLaunchProviderByAuthority(myActivityContext, provider.authority, callback);
        //onLaunchProviderByClassName(myActivityContext, provider.class_name);
    }

    private static void onLaunchProviderByClassName(Context myActivityContext, String className) {
        //@@
    }


    public static interface LauCallback {

        void showError(String s);
    }


    //content://org.telegram.messenger.web.call_sound_provider/
    //Почему Attempt to get length of null array

    private static void onLaunchProviderByAuthority(Context myActivityContext, String authority, LauCallback callback) {
        String title = "Provider";
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
        Uri uri = Uri.parse(authority);
        if (uri.equals(CONTENT_URI)) {
            uri = Uri.parse(authority + "" + "patch");
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
            DLog.d("//111");
            //CONTENT_URI
            Cursor cursor = myActivityContext.getContentResolver().query(uri, null, null, null, null);
            //exported=true
            DLog.d("//2222 " + cursor);
            if (cursor != null) {
                final int count = cursor.getCount();
                StringBuilder sb = new StringBuilder();
                DLog.d("//,,,,");
                String[] columnNames = cursor.getColumnNames();
                sb.append("Column Names: ").append(Arrays.toString(columnNames));
                DLog.d("// Переместите курсор на первую строку");
                if (cursor.moveToFirst()) {
                    do {
                        if (columnNames != null) {
                            // Проход по всем строкам и вывод данных для каждой строки
                            for (String columnName : columnNames) {
                                DLog.d("Получите данные из каждого столбца: " + columnName);
                                int columnIndex = cursor.getColumnIndex(columnName);
                                try {
                                    String columnValue = cursor.getString(columnIndex);
                                    sb.append(columnName + ": " + columnValue);
                                } catch (Exception e) {
                                    sb.append(columnName + ": " + e.getLocalizedMessage());
                                }
                            }
                        } else {
                            sb.append("String[] columnNames: NULL");
                        }
                    } while (cursor.moveToNext());
                }
                showSuccessMessageDialog(myActivityContext, title, sb.toString());
            } else {
                callback.showError("cursor null " + uri);
            }

        } catch (java.lang.SecurityException ex) {

            //exported=false
            DLog.d("Error: " + ex.getClass() + ", " + ex.getMessage());
            showErrorMessageDialog(myActivityContext, title, "Error: " + ex.getClass() + ", " + ex.getMessage());
        } catch (Exception ex) {
            DLog.d("Error: " + ex.getClass() + ", " + ex.getMessage());
            showErrorMessageDialog(myActivityContext, title, "Error: " + ex.getClass() + ", " + ex.getMessage());
        }
    }

    public static void showErrorMessageDialog(Context context, String title, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(errorMessage)
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void showSuccessMessageDialog(Context context, String title, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(errorMessage)
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
