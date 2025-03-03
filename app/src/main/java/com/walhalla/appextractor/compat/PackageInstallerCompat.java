package com.walhalla.appextractor.compat;

import android.content.Context;

import com.walhalla.appextractor.activity.Utilities;

import java.util.HashMap;

public abstract class PackageInstallerCompat {
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_INSTALLED = 0;
    public static final int STATUS_INSTALLING = 1;
    private static PackageInstallerCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public static PackageInstallerCompat getInstance(Context context) {
        PackageInstallerCompat packageInstallerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Utilities.ATLEAST_LOLLIPOP) {
                    sInstance = new PackageInstallerCompatVL(context);
                } else {
                    sInstance = new PackageInstallerCompatV16();
                }
            }
            packageInstallerCompat = sInstance;
        }
        return packageInstallerCompat;
    }

    public abstract void onStop();

    public abstract HashMap<String, Integer> updateAndGetActiveSessionCache();

    public static final class PackageInstallInfo {
        public final String packageName;
        public int progress;
        public int state;

        public PackageInstallInfo(String str) {
            this.packageName = str;
        }

        public PackageInstallInfo(String str, int i2, int i3) {
            this.packageName = str;
            this.state = i2;
            this.progress = i3;
        }
    }
}
