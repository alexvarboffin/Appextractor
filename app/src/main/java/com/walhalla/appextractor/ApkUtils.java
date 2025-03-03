package com.walhalla.appextractor;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.walhalla.ui.DLog;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import es.dmoral.toasty.Toasty;

public class ApkUtils {

    public static final int KEY_UNINSTALL = 1443;

    public static void uninstallApp0(Activity activity, String packageName) {
        try {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + packageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);

//            Uri packageURI = Uri.parse("package:" + packageName);
//            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//            activity.startActivityForResult(uninstallIntent, KEY_UNINSTALL);

            //var1
            DLog.d("Uninstall - > " + packageName);
//            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
//            intent.setData(Uri.parse("package:" + packageName));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //var2
            activity.startActivityForResult(intent, KEY_UNINSTALL);
        } catch (ActivityNotFoundException e) {
            DLog.handleException(e);
            Toasty.error(activity, ""+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static boolean apiIsAtLeast(int sdkInt) {
        return Build.VERSION.SDK_INT >= sdkInt;
    }

    private byte[] readFile(File file) {
        // Open fileBuffer
        RandomAccessFile file1 = null;
        try {
            file1 = new RandomAccessFile(file, "r");
            // Get and check length
            long longlength = file1.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read fileBuffer and return data
            byte[] data = new byte[length];
            file1.readFully(data);
            return data;
        } catch (IOException e) {
            DLog.handleException(e);
        } finally {
            try {
                if (file1 != null)
                    file1.close();
            } catch (IOException e) {
                DLog.handleException(e);
            }
        }
        return null;
    }
}
