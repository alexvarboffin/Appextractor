package com.walhalla.appextractor.compat;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.UserHandle;

import com.walhalla.ui.DLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(21)
public class LauncherAppsCompatVL extends LauncherAppsCompatV16 {
    private Map<OnAppsChangedCallbackCompat, WrappedCallback> mCallbacks = new HashMap();
    private Context mContext;
    protected LauncherApps mLauncherApps;

    private static class WrappedCallback extends LauncherApps.Callback {
        private LauncherAppsCompat.OnAppsChangedCallbackCompat mCallback;

        public WrappedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
            this.mCallback = onAppsChangedCallbackCompat;
        }

        public void onPackageAdded(String str, UserHandle userHandle) {
            this.mCallback.onPackageAdded(str, UserHandleCompat.fromUser(userHandle));
        }

        public void onPackageChanged(String str, UserHandle userHandle) {
            this.mCallback.onPackageChanged(str, UserHandleCompat.fromUser(userHandle));
        }

        public void onPackageRemoved(String str, UserHandle userHandle) {
            this.mCallback.onPackageRemoved(str, UserHandleCompat.fromUser(userHandle));
        }

        public void onPackagesAvailable(String[] strArr, UserHandle userHandle, boolean z) {
            this.mCallback.onPackagesAvailable(strArr, UserHandleCompat.fromUser(userHandle), z);
        }

        public void onPackagesSuspended(String[] strArr, UserHandle userHandle) {
            this.mCallback.onPackagesSuspended(strArr, UserHandleCompat.fromUser(userHandle));
        }

        public void onPackagesUnavailable(String[] strArr, UserHandle userHandle, boolean z) {
            this.mCallback.onPackagesUnavailable(strArr, UserHandleCompat.fromUser(userHandle), z);
        }

        public void onPackagesUnsuspended(String[] strArr, UserHandle userHandle) {
            this.mCallback.onPackagesUnsuspended(strArr, UserHandleCompat.fromUser(userHandle));
        }

        @Override // android.content.pm.LauncherApps.Callback
        public void onShortcutsChanged(String str, List<ShortcutInfo> list, UserHandle userHandle) {
            ArrayList arrayList = new ArrayList(list.size());
//            for (ShortcutInfo shortcutInfo : list) {
//                arrayList.add(new ShortcutInfoCompat(shortcutInfo));
//            }
            this.mCallback.onShortcutsChanged(str, arrayList, UserHandleCompat.fromUser(userHandle));
        }
    }

    LauncherAppsCompatVL(Context context) {
        super(context);
        this.mContext = context;
        this.mLauncherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
    }

    @Override
   
    public void addOnAppsChangedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
        WrappedCallback wrappedCallback = new WrappedCallback(onAppsChangedCallbackCompat);
        synchronized (this.mCallbacks) {
            this.mCallbacks.put(onAppsChangedCallbackCompat, wrappedCallback);
        }
        this.mLauncherApps.registerCallback(wrappedCallback);
    }

    @Override
   
    public List<LauncherActivityInfoCompat> getActivityList(String str, UserHandleCompat userHandleCompat) {
        LauncherActivityInfo resolveActivity;
//        try {
//            ArrayList<LauncherActivityInfo> arrayList = new ArrayList(this.mLauncherApps.getActivityList(str, userHandleCompat.getUser()));
//            LauncherActivityInfo resolveActivity2 = this.mLauncherApps.resolveActivity(new Intent(this.mContext, SettingsActivity.class), userHandleCompat.getUser());
//            if (resolveActivity2 != null) {
//                arrayList.add(resolveActivity2);
//            }
//            LauncherActivityInfo resolveActivity3 = this.mLauncherApps.resolveActivity(new Intent(this.mContext, ThemeBridgeActivity.class), userHandleCompat.getUser());
//            if (resolveActivity3 != null) {
//                arrayList.add(resolveActivity3);
//            }
//            if (ApexWeatherSettingActivity.a(this.mContext) && (resolveActivity = this.mLauncherApps.resolveActivity(new Intent(this.mContext, ApexWeatherActivity.class), userHandleCompat.getUser())) != null) {
//                arrayList.add(resolveActivity);
//            }
//            if (arrayList.size() == 0) {
//                return new ArrayList();
//            }
//            ArrayList arrayList2 = new ArrayList(arrayList.size());
//            for (LauncherActivityInfo launcherActivityInfo : arrayList) {
//                arrayList2.add(new LauncherActivityInfoCompatVL(launcherActivityInfo));
//            }
//            return arrayList2;
//        } catch (Exception unused) {
//            return new ArrayList();
//        }

        return new ArrayList<>();
    }

    @Override
   
    public boolean isActivityEnabledForProfile(ComponentName componentName, UserHandleCompat userHandleCompat) {
        return this.mLauncherApps.isActivityEnabled(componentName, userHandleCompat.getUser());
    }

    @Override
   
    public boolean isPackageEnabledForProfile(String str, UserHandleCompat userHandleCompat) {
        try {
            return this.mLauncherApps.isPackageEnabled(str, userHandleCompat.getUser());
        } catch (Throwable unused) {
            return false;
        }
    }

    @Override
   
    public boolean isPackageSuspendedForProfile(String str, UserHandleCompat userHandleCompat) {
        return false;
    }

    @Override
   
    public void removeOnAppsChangedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat onAppsChangedCallbackCompat) {
        WrappedCallback remove;
        synchronized (this.mCallbacks) {
            remove = this.mCallbacks.remove(onAppsChangedCallbackCompat);
        }
        if (remove != null) {
            this.mLauncherApps.unregisterCallback(remove);
        }
    }

    @Override
   
    public LauncherActivityInfoCompat resolveActivity(Intent intent, UserHandleCompat userHandleCompat) {
        LauncherActivityInfo resolveActivity = this.mLauncherApps.resolveActivity(intent, userHandleCompat.getUser());
        if (resolveActivity != null) {
            return new LauncherActivityInfoCompatVL(resolveActivity);
        }
        return null;
    }

    @Override
    public void showAppDetailsForProfile(ComponentName componentName, UserHandleCompat userHandleCompat) {
        try {
            this.mLauncherApps.startAppDetailsActivity(componentName, userHandleCompat.getUser(), null, null);
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }

    @Override
   
    public void startActivityForProfile(ComponentName componentName, UserHandleCompat userHandleCompat, Rect rect, Bundle bundle) {
        try {
            this.mLauncherApps.startMainActivity(componentName, userHandleCompat.getUser(), rect, bundle);
        } catch (Throwable unused) {
            // Toast.makeText(LauncherApplication.getAppContext(), (int) R.string.error_title, 0).show();
        }
    }
}
