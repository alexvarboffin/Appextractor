package com.walhalla.appextractor.compat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import com.walhalla.appextractor.activity.Utilities;

import java.util.ArrayList;
import java.util.List;

public class LauncherAppsCompatV16 extends LauncherAppsCompat {
    private List<OnAppsChangedCallbackCompat> mCallbacks = new ArrayList();
    private Context mContext;
    private PackageMonitor mPackageMonitor;
    private PackageManager mPm;

    /* access modifiers changed from: package-private */
    public class PackageMonitor extends BroadcastReceiver {
        PackageMonitor() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UserHandleCompat myUserHandle = UserHandleCompat.myUserHandle();
            if ("android.intent.action.PACKAGE_CHANGED".equals(action) || "android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_ADDED".equals(action)) {
                String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
                if (schemeSpecificPart != null && schemeSpecificPart.length() != 0) {
                    if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                        for (LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat : LauncherAppsCompatV16.this.getCallbacks()) {
                            onAppsChangedCallbackCompat.onPackageChanged(schemeSpecificPart, myUserHandle);
                        }
                    } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                        if (!booleanExtra) {
                            for (LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat2 : LauncherAppsCompatV16.this.getCallbacks()) {
                                onAppsChangedCallbackCompat2.onPackageRemoved(schemeSpecificPart, myUserHandle);
                            }
                        }
                    } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                        if (!booleanExtra) {
                            for (LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat3 : LauncherAppsCompatV16.this.getCallbacks()) {
                                onAppsChangedCallbackCompat3.onPackageAdded(schemeSpecificPart, myUserHandle);
                            }
                            return;
                        }
                        for (LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat4 : LauncherAppsCompatV16.this.getCallbacks()) {
                            onAppsChangedCallbackCompat4.onPackageChanged(schemeSpecificPart, myUserHandle);
                        }
                    }
                }
            } else if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action)) {
                boolean booleanExtra2 = intent.getBooleanExtra("android.intent.extra.REPLACING", !Utilities.ATLEAST_KITKAT);
                String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                for (LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat5 : LauncherAppsCompatV16.this.getCallbacks()) {
                    onAppsChangedCallbackCompat5.onPackagesAvailable(stringArrayExtra, myUserHandle, booleanExtra2);
                }
            } else if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                boolean booleanExtra3 = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
                String[] stringArrayExtra2 = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                for (LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat6 : LauncherAppsCompatV16.this.getCallbacks()) {
                    onAppsChangedCallbackCompat6.onPackagesUnavailable(stringArrayExtra2, myUserHandle, booleanExtra3);
                }
            }
        }
    }

    LauncherAppsCompatV16(Context context) {
        this.mPm = context.getPackageManager();
        this.mContext = context;
        this.mPackageMonitor = new PackageMonitor();
    }

    private void registerForPackageIntents() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver(this.mPackageMonitor, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiver(this.mPackageMonitor, intentFilter2);
    }

    private void unregisterForPackageIntents() {
        this.mContext.unregisterReceiver(this.mPackageMonitor);
    }

    @Override
    public synchronized void addOnAppsChangedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
        if (onAppsChangedCallbackCompat != null) {
            if (!this.mCallbacks.contains(onAppsChangedCallbackCompat)) {
                this.mCallbacks.add(onAppsChangedCallbackCompat);
                if (this.mCallbacks.size() == 1) {
                    registerForPackageIntents();
                }
            }
        }
    }

    @Override
    public List<LauncherActivityInfoCompat> getActivityList(String str, UserHandleCompat userHandleCompat) {
//        List<ResolveInfo> queryIntentActivities;
//        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
//        intent.addCategory("android.intent.category.LAUNCHER");
//        intent.setPackage(str);
//        List<ResolveInfo> queryIntentActivities2 = this.mPm.queryIntentActivities(intent, 0);
//        Intent intent2 = new Intent("android.intent.action.APPLICATION_PREFERENCES");
//        intent2.setPackage(this.mContext.getPackageName());
//        List<ResolveInfo> queryIntentActivities3 = this.mPm.queryIntentActivities(intent2, 0);
//        if (queryIntentActivities3 != null) {
//            queryIntentActivities2.addAll(queryIntentActivities3);
//        }
//        List<ResolveInfo> queryIntentActivities4 = this.mPm.queryIntentActivities(new Intent(this.mContext, ThemeBridgeActivity.class), 0);
//        if (queryIntentActivities4 != null) {
//            queryIntentActivities2.addAll(queryIntentActivities4);
//        }
//        if (ApexWeatherSettingActivity.a(this.mContext) && (queryIntentActivities = this.mPm.queryIntentActivities(new Intent(this.mContext, ApexWeatherActivity.class), 0)) != null) {
//            queryIntentActivities2.addAll(queryIntentActivities);
//        }
        ArrayList arrayList = new ArrayList();//queryIntentActivities2.size()
//        for (ResolveInfo resolveInfo : queryIntentActivities2) {
//            arrayList.add(new LauncherActivityInfoCompatV16(this.mContext, resolveInfo));
//        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public synchronized List<OnAppsChangedCallbackCompat> getCallbacks() {
        return new ArrayList(this.mCallbacks);
    }

    @Override
    public boolean isActivityEnabledForProfile(ComponentName componentName, UserHandleCompat userHandleCompat) {
        try {
            ActivityInfo activityInfo = this.mPm.getActivityInfo(componentName, 0);
            if (activityInfo == null || !activityInfo.isEnabled()) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    @Override
    public boolean isPackageEnabledForProfile(String str, UserHandleCompat userHandleCompat) {
        return false;//PackageManagerHelper.isAppEnabled(this.mPm, str);
    }

    @Override
    public boolean isPackageSuspendedForProfile(String str, UserHandleCompat userHandleCompat) {
        return false;
    }

    @Override
    public synchronized void removeOnAppsChangedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
        this.mCallbacks.remove(onAppsChangedCallbackCompat);
        if (this.mCallbacks.size() == 0) {
            unregisterForPackageIntents();
        }
    }

    @Override
    public LauncherActivityInfoCompat resolveActivity(Intent intent, UserHandleCompat userHandleCompat) {
        ResolveInfo resolveActivity = this.mPm.resolveActivity(intent, 0);
        if (resolveActivity != null) {
            return new LauncherActivityInfoCompatV16(this.mContext, resolveActivity);
        }
        return null;
    }

    @Override
    public void showAppDetailsForProfile(ComponentName componentName, UserHandleCompat userHandleCompat) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", componentName.getPackageName(), null));
        intent.setFlags(268468224);
        this.mContext.startActivity(intent, null);
    }

    @Override
    public void startActivityForProfile(ComponentName componentName, UserHandleCompat userHandleCompat, Rect rect, Bundle bundle) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentName);
        intent.setSourceBounds(rect);
        //intent.addFlags(C.ENCODING_PCM_MU_LAW);
        this.mContext.startActivity(intent, bundle);
    }
}
