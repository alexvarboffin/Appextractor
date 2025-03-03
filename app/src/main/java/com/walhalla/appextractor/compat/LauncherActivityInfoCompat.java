package com.walhalla.appextractor.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public abstract class LauncherActivityInfoCompat {
    LauncherActivityInfoCompat() {
    }

    public static LauncherActivityInfoCompat fromResolveInfo(ResolveInfo resolveInfo, Context context) {
        return new LauncherActivityInfoCompatV16(context, resolveInfo);
    }

    public abstract ApplicationInfo getApplicationInfo();

    public abstract ComponentName getComponentName();

    public abstract long getFirstInstallTime();

    public abstract Drawable getIcon(int i2);

    public abstract CharSequence getLabel();

    public abstract UserHandleCompat getUser();
}
