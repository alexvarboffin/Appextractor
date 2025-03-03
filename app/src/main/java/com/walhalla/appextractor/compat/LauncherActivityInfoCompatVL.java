package com.walhalla.appextractor.compat;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.graphics.drawable.Drawable;

@TargetApi(21)
public class LauncherActivityInfoCompatVL extends LauncherActivityInfoCompat {
    private final LauncherActivityInfo mLauncherActivityInfo;

    LauncherActivityInfoCompatVL(LauncherActivityInfo launcherActivityInfo) {
        this.mLauncherActivityInfo = launcherActivityInfo;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return this.mLauncherActivityInfo.getApplicationInfo();
    }

    @Override
    public ComponentName getComponentName() {
        return this.mLauncherActivityInfo.getComponentName();
    }

    @Override
    public long getFirstInstallTime() {
        return this.mLauncherActivityInfo.getFirstInstallTime();
    }

    @Override
    public Drawable getIcon(int i2) {
        return this.mLauncherActivityInfo.getIcon(i2);
    }

    @Override
    public CharSequence getLabel() {
        return this.mLauncherActivityInfo.getLabel();
    }

    @Override
    public UserHandleCompat getUser() {
        return UserHandleCompat.fromUser(this.mLauncherActivityInfo.getUser());
    }
}
