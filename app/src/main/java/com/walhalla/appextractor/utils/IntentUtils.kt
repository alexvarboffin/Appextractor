package com.walhalla.appextractor.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.walhalla.ui.DLog;

public class IntentUtils {
    public static void openSettingsForPackageName(Context context, String packageName) {
        try {
            Intent intent = new Intent()
                    .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .addCategory(Intent.CATEGORY_DEFAULT)
                    .setData(Uri.parse("package:" + packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }

    public static void openSettingsForPackageName2(Context context, String packageName) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }
}
