package com.walhalla.appextractor.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.os.Handler;
import android.util.SparseArray;

import java.util.HashMap;

@TargetApi(21)
public class PackageInstallerCompatVL extends PackageInstallerCompat {
    final SparseArray<String> mActiveSessions = new SparseArray<>();
    //private final IconCache mCache;
    private final PackageInstaller.SessionCallback mCallback = new PackageInstaller.SessionCallback() {
        /* class com.android.launcher3.compat.PackageInstallerCompatVL.AnonymousClass1 */

        private void pushSessionDisplayToLauncher(int i2) {
            PackageInstaller.SessionInfo sessionInfo = PackageInstallerCompatVL.this.mInstaller.getSessionInfo(i2);
//            if (sessionInfo != null && sessionInfo.getAppPackageName() != null) {
//                PackageInstallerCompatVL.this.addSessionInfoToCahce(sessionInfo, UserHandleCompat.myUserHandle());
//                LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
//                if (instanceNoCreate != null) {
//                    instanceNoCreate.getModel().updateSessionDisplayInfo(sessionInfo.getAppPackageName());
//                }
//            }
        }

        public void onActiveChanged(int i2, boolean z) {
        }

        public void onBadgingChanged(int i2) {
            pushSessionDisplayToLauncher(i2);
        }

        public void onCreated(int i2) {
            pushSessionDisplayToLauncher(i2);
        }

        public void onFinished(int i2, boolean z) {
            String str = PackageInstallerCompatVL.this.mActiveSessions.get(i2);
            PackageInstallerCompatVL.this.mActiveSessions.remove(i2);
            if (str != null) {
                PackageInstallerCompatVL.this.sendUpdate(new PackageInstallerCompat.PackageInstallInfo(str, z ? 0 : 2, 0));
            }
        }

        public void onProgressChanged(int i2, float f2) {
            PackageInstaller.SessionInfo sessionInfo = PackageInstallerCompatVL.this.mInstaller.getSessionInfo(i2);
            if (sessionInfo != null && sessionInfo.getAppPackageName() != null) {
                PackageInstallerCompatVL.this.sendUpdate(new PackageInstallerCompat.PackageInstallInfo(sessionInfo.getAppPackageName(), 1, (int) (sessionInfo.getProgress() * 100.0f)));
            }
        }
    };
    final PackageInstaller mInstaller;
    private Handler mWorker;

    PackageInstallerCompatVL(Context context) {
        this.mInstaller = context.getPackageManager().getPackageInstaller();
//        this.mCache = LauncherAppState.getInstance().getIconCache();
//        this.mWorker = new Handler(LauncherModel.getWorkerLooper());
//        this.mInstaller.registerSessionCallback(this.mCallback, this.mWorker);
    }

    /* access modifiers changed from: package-private */
    public void addSessionInfoToCahce(PackageInstaller.SessionInfo sessionInfo, UserHandleCompat userHandleCompat) {
//        String appPackageName = sessionInfo.getAppPackageName();
//        if (appPackageName != null) {
//            this.mCache.cachePackageInstallInfo(appPackageName, userHandleCompat, sessionInfo.getAppIcon(), sessionInfo.getAppLabel());
//        }
    }

    @Override
    public void onStop() {
        this.mInstaller.unregisterSessionCallback(this.mCallback);
    }

    /* access modifiers changed from: package-private */
    public void sendUpdate(PackageInstallerCompat.PackageInstallInfo packageInstallInfo) {
//        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
//        if (instanceNoCreate != null) {
//            instanceNoCreate.getModel().setPackageState(packageInstallInfo);
//        }
    }

    @Override
    public HashMap<String, Integer> updateAndGetActiveSessionCache() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        UserHandleCompat myUserHandle = UserHandleCompat.myUserHandle();
        for (PackageInstaller.SessionInfo sessionInfo : this.mInstaller.getAllSessions()) {
            addSessionInfoToCahce(sessionInfo, myUserHandle);
            if (sessionInfo.getAppPackageName() != null) {
                hashMap.put(sessionInfo.getAppPackageName(), Integer.valueOf((int) (sessionInfo.getProgress() * 100.0f)));
                this.mActiveSessions.put(sessionInfo.getSessionId(), sessionInfo.getAppPackageName());
            }
        }
        return hashMap;
    }
}
