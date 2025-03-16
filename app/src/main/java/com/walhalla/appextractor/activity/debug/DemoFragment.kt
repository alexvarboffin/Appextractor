package com.walhalla.appextractor.activity.debug;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.walhalla.appextractor.ApkUtils;
import com.walhalla.appextractor.model.PackageMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DemoFragment extends Fragment {

    protected List<ApplicationInfo> a;
    protected List<PackageMeta> b;
    protected List<PackageInfo> infos;

    @SuppressLint("QueryPermissionsNeeded")
    protected void fullMeta(PackageManager pm) {
        List<ApplicationInfo> aaa = new ArrayList<>();
        List<PackageMeta> bbb = new ArrayList<>();

        //infos = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        infos = pm.getInstalledPackages(
                PackageManager.GET_PERMISSIONS
                | PackageManager.GET_SIGNATURES
                | PackageManager.GET_META_DATA
                | PackageManager.GET_ACTIVITIES
                | PackageManager.GET_SERVICES
                | PackageManager.GET_PROVIDERS
                | PackageManager.GET_RECEIVERS);

        for (PackageInfo packageInfo : infos) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            aaa.add(applicationInfo);


            boolean hasSplits = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                hasSplits = applicationInfo.splitPublicSourceDirs != null && applicationInfo.splitPublicSourceDirs.length > 0;
            }
            PackageMeta meta = new PackageMeta.Builder(applicationInfo.packageName).setLabel(applicationInfo.loadLabel(pm).toString()).setHasSplits(hasSplits).setIsSystemApp((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0).setVersionCode(ApkUtils.apiIsAtLeast(Build.VERSION_CODES.P) ? packageInfo.getLongVersionCode() : packageInfo.versionCode).setVersionName(packageInfo.versionName).setIcon(applicationInfo.icon).setInstallTime(packageInfo.firstInstallTime).setUpdateTime(packageInfo.lastUpdateTime).build();

            File file = new File(applicationInfo.publicSourceDir);
            long longsize = file.length();
            String size;
            if (longsize > 1024 && longsize <= 1024 * 1024) {
                size = (longsize / 1024 + " KB");
            } else if (longsize > 1024 * 1024 && longsize <= 1024 * 1024 * 1024) {
                size = (longsize / (1024 * 1024) + " MB");
            } else {
                size = (longsize / (1024 * 1024 * 1024) + " GB");
            }
            meta.size = size;
            meta.sourceDir = applicationInfo.sourceDir;
            meta.packageName = packageInfo.packageName;
            meta.firstInstallTime = packageInfo.firstInstallTime;
            meta.lastUpdateTime = packageInfo.lastUpdateTime;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                meta.category = applicationInfo.category;
            }
            bbb.add(meta);
        }
        a = aaa;
        b = bbb;
    }
}
