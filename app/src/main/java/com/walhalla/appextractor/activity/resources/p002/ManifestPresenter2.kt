package com.walhalla.appextractor.activity.resources.p002;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.resources.ResItem;
import com.walhalla.appextractor.activity.resources.ResType;
import com.walhalla.appextractor.activity.resources.p001.ManifestContract;
import com.walhalla.ui.DLog;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ManifestPresenter2 implements ManifestContract2.Presenter {
    private final Context context;
    private ManifestContract2.View view;
    private String packageName;
    private AssetManager am;
    private Resources resources;
    private int xmlResourceId;


    public ManifestPresenter2(Context context, ManifestContract2.View view) {
        this.view = view;
        this.context = context;
    }

    public boolean configForPackage(String packageName) {
        if (packageName == null || packageName.equals("")) {
            //packageName = "com.zaimi.online.na.kartu.Buckridge";
            //packageName = "com.walhalla.appextractor";

        }

        try {
            am = context.createPackageContext(packageName, 0).getAssets();
            Context context1 = context.createPackageContext(packageName, 0);
//            PackageManager packageManager = context1.getPackageManager();
//
//            //Resources resources = packageManager.getResourcesForApplication(packageName);
//            Resources resources = new Resources(am, context1.getResources().getDisplayMetrics(), null);
            PackageManager packageManager = context1.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Resources resources = packageManager.getResourcesForApplication(applicationInfo);
            Field[] fields0 = resources.getClass().getDeclaredFields();


            //ТОЛЬКО ТАК РАБОТАЕТ, null не принимает
            int resourceId = context.getResources().getIdentifier("app_name", "string", context.getPackageName());
            DLog.d("@@@@@@@@@@" + resourceId);

            //AssetManager.getResourceIdentifier(name, defType, defPackage);

//            Class<?> assetManagerClass = AssetManager.class;
//            Method getResourceIdentifierMethod = assetManagerClass.getDeclaredMethod("getResourceIdentifier", String.class, String.class, String.class);
//            getResourceIdentifierMethod.setAccessible(true);
//            resourceId = (int) getResourceIdentifierMethod.invoke(am, "app_name", "string", context.getPackageName());
            DLog.d("@@@@@@@@@@" + resourceId);

            if (77 == 87) {
                for (Field field : fields0) {
                    DLog.d("@ Raw Resource" + " " + resourceId + " " + fields0.length + " " + field);
                    if (field.getName().startsWith("raw_")) {
                        int rawResourceId = field.getInt(resources);
                        String resourceName = resources.getResourceEntryName(rawResourceId);
                        DLog.d("@ Raw Resource" + resourceName);
                    }
                }

                Field mResourcesImplField = null;
                for (Field field : fields0) {
                    if (field.getName().equals("mResourcesImpl")) {
                        mResourcesImplField = field;
                        break;
                    }
                }
                if (mResourcesImplField != null) {
                    mResourcesImplField.setAccessible(true);
                }
                Object mResourcesImplObject = null;
                DLog.d("@@@@@@@@@@" + mResourcesImplField);

                if (mResourcesImplField != null) {
                    try {
                        mResourcesImplObject = mResourcesImplField.get(resources);
                        DLog.d("@@@@@@@@@@" + mResourcesImplObject);
                        Field[] fields = resources.getClass().getDeclaredFields();
                        for (Field resField : fields) {
                            DLog.d("@@@@@@@@@@" + resField);
                            if (resField.getType() == int.class && resField.getName().startsWith("raw_")) {
                                int rawResourceId = resField.getInt(resources);
                                String resourceName = resources.getResourceEntryName(rawResourceId);
                                DLog.d("@@@@@@@@@@" + resourceName);
                            }
                        }

                    } catch (IllegalAccessException e) {
                        DLog.handleException(e);
                    }
                }
            }

            test();
        } catch (Exception e) {
            DLog.handleException(e);
        }
        return true;
    }

    private void test() {
        try {
            PackageManager packageManager = context.getPackageManager();
            Resources resources = packageManager.getResourcesForApplication(packageName);
            AssetManager assetManager = resources.getAssets();

            Class<?> assetManagerClass = AssetManager.class;
            Method getStringBlockMethod = assetManagerClass.getDeclaredMethod("getStringBlock");
            getStringBlockMethod.setAccessible(true);
            Object stringBlock = getStringBlockMethod.invoke(assetManager);

            Field[] fields = stringBlock.getClass().getDeclaredFields();
            for (Field field : fields) {
                DLog.d("@@@@@@@@@@" + field);
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    String resourceName = (String) field.get(stringBlock);
                    int resourceId = resources.getIdentifier(resourceName, "string", packageName);
                    DLog.d("String Resource" + "Name: " + resourceName + ", ID: " + resourceId);
                }
            }
        } catch (PackageManager.NameNotFoundException | NoSuchMethodException |
                 IllegalAccessException | InvocationTargetException e) {
            DLog.handleException(e);
        }
    }


    @Override
    public void loadManifestContent(String packageName) {
        configForPackage(packageName);

    }
}

