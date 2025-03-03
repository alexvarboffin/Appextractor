package com.walhalla.appextractor.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class LauncherActivityInfoCompatV16 extends LauncherActivityInfoCompat {
    private final ActivityInfo mActivityInfo;
    private final ComponentName mComponentName;
    private final PackageManager mPm;
    private final ResolveInfo mResolveInfo;

    LauncherActivityInfoCompatV16(Context context, ResolveInfo resolveInfo) {
        this.mResolveInfo = resolveInfo;
        this.mActivityInfo = resolveInfo.activityInfo;
        this.mPm = context.getPackageManager();
        mComponentName = new ComponentName(this.mActivityInfo.packageName, this.mActivityInfo.name);
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return this.mActivityInfo.applicationInfo;
    }

    @Override
    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    @Override
    public long getFirstInstallTime() {
        try {
            PackageInfo packageInfo = this.mPm.getPackageInfo(this.mActivityInfo.packageName, 0);
            if (packageInfo != null) {
                return packageInfo.firstInstallTime;
            }
            return 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return 0;
        }
    }

    @Override
    public Drawable getIcon(int i2) {
        return null;
    }


    @Override
    public CharSequence getLabel() {
        return this.mResolveInfo.loadLabel(this.mPm);
    }

    public String getName() {
        return this.mActivityInfo.name;
    }

    @Override
    public UserHandleCompat getUser() {
        return UserHandleCompat.myUserHandle();
    }
}
