package com.walhalla.appextractor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.walhalla.ui.DLog;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EasyPermissions {

    //public static final int REQUEST_CODE = 100500;
    private static final String[] DEFAULT_PERMISSIONS = new String[]{
//			"android.permission.ACCESS_LOCATION_EXTRA_COMMANDS",
//			"android.permission.ACCESS_NETWORK_STATE",
//			"android.permission.ACCESS_NOTIFICATION_POLICY",
//			"android.permission.ACCESS_WIFI_STATE",
//			"android.permission.ACCESS_WIMAX_STATE",
//			"android.permission.BLUETOOTH",
//			"android.permission.BLUETOOTH_ADMIN",
//			"android.permission.BROADCAST_STICKY",
//			"android.permission.CHANGE_NETWORK_STATE",
//			"android.permission.CHANGE_WIFI_MULTICAST_STATE",
//			"android.permission.CHANGE_WIFI_STATE",
//			"android.permission.CHANGE_WIMAX_STATE",
//			"android.permission.DISABLE_KEYGUARD",
//			"android.permission.EXPAND_STATUS_BAR",
//			"android.permission.FLASHLIGHT",
//			"android.permission.GET_ACCOUNTS",
//			"android.permission.GET_PACKAGE_SIZE",
//			"android.permission.INTERNET",
//			"android.permission.KILL_BACKGROUND_PROCESSES",
//			"android.permission.MODIFY_AUDIO_SETTINGS",
//			"android.permission.NFC",
//			"android.permission.READ_SYNC_SETTINGS",
//			"android.permission.READ_SYNC_STATS",
//			"android.permission.RECEIVE_BOOT_COMPLETED",
//			"android.permission.REORDER_TASKS",
//			"android.permission.REQUEST_INSTALL_PACKAGES",
//			"android.permission.SET_TIME_ZONE",
//			"android.permission.SET_WALLPAPER",
//			"android.permission.SET_WALLPAPER_HINTS",
//			"android.permission.SUBSCRIBED_FEEDS_READ",
//			"android.permission.TRANSMIT_IR",
//			"android.permission.USE_FINGERPRINT",
//			"android.permission.VIBRATE",
//			"android.permission.WAKE_LOCK",
//			"android.permission.WRITE_SYNC_SETTINGS",
//			"com.android.alarm.permission.SET_ALARM",
//			"com.android.launcher.permission.INSTALL_SHORTCUT",
//			"com.android.launcher.permission.UNINSTALL_SHORTCUT",
//			"android.permission.ACCESS_SUPERUSER",
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE//no
    };

    private final Activity activity;

    public EasyPermissions(Activity a) {
        this.activity = a;
    }

//    public boolean resolve() {
//        if (Build.VERSION.SDK_INT < 23) return true;
//
//        String[] unmet_permissions = getUnmetPermissions();
//        if (unmet_permissions.length < 1) return true;
//
//        activity.requestPermissions(unmet_permissions, REQUEST_CODE);
//        //    ActivityCompat.requestPermissions(activity, unmet_permissions, REQUEST_CODE);
//        return false;
//    }

//    public void requestStoragePermission0(Activity activity, ActivityResultLauncher<String[]> requestPermissionLauncher) {
//        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
//    }

    public String[] notResolved_() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            return new String[0];
        } else if (Build.VERSION.SDK_INT < 23) return new String[0];
        return getUnmetPermissions();
    }

    @SuppressLint("NewApi")
    private String[] getUnmetPermissions() {
        List<String> unmet_permissions = new LinkedList<String>();
        try {
            List<String> def = Arrays.asList(DEFAULT_PERMISSIONS);
//            PackageInfo info = activity.getPackageManager()
//                    .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = DEFAULT_PERMISSIONS; //info.requestedPermissions;

            for (String perm : permissions) {
                //DLog.d("[+] Permissions: " + perm);
                //if (def.contains(perm)) continue;
                if (
                    //activity.checkSelfPermission(perm)
                        ContextCompat.checkSelfPermission(activity, perm) == PackageManager.PERMISSION_GRANTED) {
                    continue;
                }
                unmet_permissions.add(perm);
            }
        } catch (Exception e /*PackageManager.NameNotFoundException e*/) {
            DLog.handleException(e);
            return new String[0];
        }

        if (unmet_permissions.size() < 1) return new String[0];

        String[] arr = new String[unmet_permissions.size()];
        unmet_permissions.toArray(arr);
        return arr;
    }


//    public static void onRequestPermissionsResult(
//            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity) {
//        if (requestCode != REQUEST_CODE) return;
//        boolean granted = true;
//        for (int i = 0; i < grantResults.length; i++) {
//            DLog.d(permissions[i] + ": " + grantResults[i]);
//            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                granted = false;
//                break;
//            }
//        }
//        if (granted) return;
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            //Show permission explanation dialog...
//            showPermissionDialog(activity);
//        } else {
//            //Never ask again selected, or device policy prohibits the app from having that permission.
//            //So, disable that feature, or fall back to another situation...
//        }
//    }

//    private boolean shouldDisplayRationaleForAction(FileAction action) {
//        for (String permission : action.p) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private static void showPermissionDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.alert_perm_body)
                .setTitle(R.string.alert_perm_title)
                .setPositiveButton(R.string.alert_perm_btn, (dialogInterface, i) -> activity.startActivity(
                        new Intent()
                                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .addCategory(Intent.CATEGORY_DEFAULT)
                                .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                ))
                .create()
                .show();
    }
}