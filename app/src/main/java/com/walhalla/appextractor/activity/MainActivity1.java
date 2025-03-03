package com.walhalla.appextractor.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.walhalla.ui.DLog;

import java.util.Arrays;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo packageInfo : packages) {
            publishProgress(packageInfo);
        }
    }

    private void publishProgress(PackageInfo info) {

        if (info != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DLog.d("[@@]" + Arrays.toString(info.splitNames));
            }
            DLog.d("[@@]" + info.packageName);
            DLog.d("[@@]" + Arrays.toString(info.activities));
            aaa(info.applicationInfo);
            DLog.d("[@@]" + info.sharedUserId);
            DLog.d("[@@]" + info.versionName);
            DLog.d("[@@]" + info.versionCode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DLog.d("[@@]" + Arrays.toString(info.attributions));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                DLog.d("[@@]" + info.baseRevisionCode);
            }
            DLog.d("[@@]" + info.configPreferences);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DLog.d("[@@]" + info.featureGroups);
            }
            DLog.d("[@@]" + info.firstInstallTime);
            DLog.d("[@@]" + Arrays.toString(info.permissions));
            DLog.d("[@@]" + Arrays.toString(info.requestedPermissions));
            DLog.d("[@@]" + Arrays.toString(info.requestedPermissionsFlags));
            DLog.d("[@@]" + Arrays.toString(info.providers));
            DLog.d("[@@]" + Arrays.toString(info.receivers));
        }
    }

    private void aaa(ApplicationInfo info) {

        if (info != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DLog.d("[*]" + info.category);
            }
            DLog.d("[*]" + info.flags);
            DLog.d("[*]" + info);

            DLog.d("[*NAME*]" + info.name);
            String packageName = info.packageName;
            DLog.d("[*PACKAGE_NAME*]" + info.packageName);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                DLog.d("[*]" + info.appComponentFactory);
            }
            DLog.d("[*]" + info.manageSpaceActivityName);
            DLog.d("[*]" + info.permission);
            DLog.d("[*]" + info.processName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DLog.d("[*]" + info.dataDir);
            }
            DLog.d("[*]" + info.taskAffinity);
            DLog.d("[*]" + info.publicSourceDir);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DLog.d("[*]" + info.compileSdkVersionCodename);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                DLog.d("[*]" + info.banner);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DLog.d("[*]" + info.minSdkVersion);
            }
            DLog.d("[*]" + info.targetSdkVersion);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DLog.d("[*]" + info.compileSdkVersion);
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                DLog.d("[@b@]" + info.loadBanner(getPackageManager()));
            }

        }
    }
}
