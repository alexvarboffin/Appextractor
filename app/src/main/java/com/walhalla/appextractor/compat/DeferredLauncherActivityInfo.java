package com.walhalla.appextractor.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class DeferredLauncherActivityInfo extends LauncherActivityInfoCompat {
    private LauncherActivityInfoCompat mActualInfo;
    private final ComponentName mComponent;
    private final Context mContext;
    private final UserHandleCompat mUser;

    public DeferredLauncherActivityInfo(ComponentName componentName, UserHandleCompat userHandleCompat, Context context) {
        this.mComponent = componentName;
        this.mUser = userHandleCompat;
        this.mContext = context;
    }

    private synchronized LauncherActivityInfoCompat getActualInfo() {
        if (this.mActualInfo == null) {
            this.mActualInfo = LauncherAppsCompat.getInstance(this.mContext).resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(this.mComponent), this.mUser);
        }
        return this.mActualInfo;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return getActualInfo().getApplicationInfo();
    }

    @Override
    public ComponentName getComponentName() {
        return this.mComponent;
    }

    @Override
    public long getFirstInstallTime() {
        return getActualInfo().getFirstInstallTime();
    }

    @Override
    public Drawable getIcon(int i2) {
        return getActualInfo().getIcon(i2);
    }

    @Override
    public CharSequence getLabel() {
        return getActualInfo().getLabel();
    }

    @Override
    public UserHandleCompat getUser() {
        return this.mUser;
    }
}
