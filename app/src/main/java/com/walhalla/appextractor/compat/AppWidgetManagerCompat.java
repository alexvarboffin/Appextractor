package com.walhalla.appextractor.compat;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import java.util.List;

public abstract class AppWidgetManagerCompat {
    private static AppWidgetManagerCompat sInstance;
    private static final Object sInstanceLock = new Object();
    final AppWidgetManager mAppWidgetManager;
    final Context mContext;

    AppWidgetManagerCompat(Context context) {
        this.mContext = context;
        this.mAppWidgetManager = AppWidgetManager.getInstance(context);
    }

//    public static AppWidgetManagerCompat getInstance(Context context) {
//        AppWidgetManagerCompat appWidgetManagerCompat;
//        synchronized (sInstanceLock) {
//            if (sInstance == null) {
//                if (Utilities.ATLEAST_LOLLIPOP) {
//                    sInstance = new AppWidgetManagerCompatVL(context.getApplicationContext());
//                } else {
//                    sInstance = new AppWidgetManagerCompatV16(context.getApplicationContext());
//                }
//            }
//            appWidgetManagerCompat = sInstance;
//        }
//        return appWidgetManagerCompat;
//    }
//
//    public abstract boolean bindAppWidgetIdIfAllowed(int i2, AppWidgetProviderInfo appWidgetProviderInfo, Bundle bundle);
//
//    public LauncherAppWidgetProviderInfo findCustomAppWidgetProvider(ComponentName componentName) {
//        CustomAppWidget customAppWidget;
//        if (componentName == null) {
//            return null;
//        }
//        if (SearchWidget.class.getName().equals(componentName.getClassName())) {
//            customAppWidget = new SearchWidget(this.mContext.getResources());
//        } else {
//            customAppWidget = WeatherClockWidget.class.getName().equals(componentName.getClassName()) ? new WeatherClockWidget(this.mContext.getResources()) : null;
//        }
//        if (customAppWidget != null) {
//            return new LauncherAppWidgetProviderInfo(this.mContext, customAppWidget);
//        }
//        return null;
//    }
//
//    public abstract LauncherAppWidgetProviderInfo findProvider(ComponentName componentName, UserHandleCompat userHandleCompat);
//
//    public abstract List<AppWidgetProviderInfo> getAllProviders();
//
//    public abstract HashMap<ComponentKey, AppWidgetProviderInfo> getAllProvidersMap();
//
//    public AppWidgetProviderInfo getAppWidgetInfo(int i2) {
//        return this.mAppWidgetManager.getAppWidgetInfo(i2);
//    }
//
//    public abstract Bitmap getBadgeBitmap(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, Bitmap bitmap, int i2, int i3);
//
//    public LauncherAppWidgetProviderInfo getLauncherAppWidgetInfo(int i2) {
//        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo(i2);
//        if (appWidgetInfo == null) {
//            return null;
//        }
//        return LauncherAppWidgetProviderInfo.fromProviderInfo(appWidgetInfo);
//    }
//
//    public abstract UserHandleCompat getUser(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo);
//
//    public abstract Drawable loadIcon(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, IconCache iconCache);
//
//    public abstract String loadLabel(LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo);
//
//    public abstract Drawable loadPreview(AppWidgetProviderInfo appWidgetProviderInfo);
//
//    public abstract void startConfigActivity(AppWidgetProviderInfo appWidgetProviderInfo, int i2, Activity activity, AppWidgetHost appWidgetHost, int i3);
}
