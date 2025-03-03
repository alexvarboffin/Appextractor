package com.walhalla.appextractor.presenter;

import android.annotation.SuppressLint;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.detail.DetailsF0;
import com.walhalla.appextractor.adapter2.base.ViewModel;
import com.walhalla.appextractor.adapter2.header.HeaderObject;
import com.walhalla.appextractor.adapter2.headerCollapsed.HeaderCollapsedObject;
import com.walhalla.appextractor.adapter2.provider.ProviderLine;
import com.walhalla.appextractor.adapter2.simple.SimpleLine;
import com.walhalla.appextractor.adapter2.v2line.V2Line;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.appextractor.utils.IntentUtils;
import com.walhalla.ui.DLog;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MetaPresenter implements DetailsF0.Presenter {


    Map<Integer, Drawable> icons = new HashMap<>();

    private static final String DEVIDER_START = " (";
    private static final String DEVIDER_END = ")";

    private final DetailsF0.View view;

    private final Context context;
    private final PackageManager packageManager;
    private final PackageMeta meta;
    private PackageInfo packageInfo;

    @SuppressLint("PackageManagerGetSignatures")
    public MetaPresenter(Context context, PackageMeta meta, DetailsF0.View view) {
        final PackageManager manager = context.getPackageManager();
        this.context = context;
        try {
            this.packageInfo = manager.getPackageInfo(meta.packageName,
                    PackageManager.GET_PERMISSIONS
                            | PackageManager.GET_SIGNATURES
                            | PackageManager.GET_META_DATA
                            | PackageManager.GET_ACTIVITIES
                            | PackageManager.GET_SERVICES
                            | PackageManager.GET_PROVIDERS
                            | PackageManager.GET_RECEIVERS

            );
        } catch (PackageManager.NameNotFoundException e) {
            DLog.handleException(e);
        }
        this.view = view;
        this.packageManager = manager;
        this.meta = meta;
    }

    public void doStuff(Context context) {
        try {
            //LinearLayout card = this.mCard();
            //ViewGroup r10 = findViewById(R.id.scrollContainer);
            //int r10 = R.id.scrollContainer;
            //String m = packageManager.getInstallerPackageName(packageInfo.packageName);
            List<ViewModel> data = new ArrayList<>();
            if (packageInfo != null) {
                getAllStringResourcesFromPackage(context, packageInfo.packageName, data);
                meta7(data);
                colors7(data);
            }
            //activities(data);
            //services(data);
            view.swap(data);
        } catch (PackageManager.NameNotFoundException e) {
            DLog.handleException(e);
        }
    }

    private void colors7(List<ViewModel> data) {
        try {
            Resources resources = packageManager.getResourcesForApplication(meta.packageName);
            Field[] fields = resources.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.getType() == R.color.class) {
                    int resourceId = resources.getIdentifier(field.getName(), "color", meta.packageName);
                    int colorValue = resources.getColor(resourceId);
                    data.add(wrapV2Line("" + resourceId, "" + colorValue));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            DLog.handleException(e);
        }
    }

    private void getAllStringResourcesFromPackage(Context context, String packageName, List<ViewModel> data) {
//        try {
//            PackageManager packageManager = context.getPackageManager();
//            Resources resources = packageManager.getResourcesForApplication(packageName);
//            Field[] fields = resources.getClass().getDeclaredFields();
//
//            for (Field field : fields) {
//                if (field.getType() == R.string.class) {
//                    int resourceId = resources.getIdentifier(field.getName(), "string", packageName);
//                    String stringValue = resources.getString(resourceId);
//                    data.add(wrapV2Line("" + resourceId, "" + stringValue));
//                }
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            DLog.handleException(e);
//        }firebase_url app_name
        try {

            HashSet<String> o = new HashSet<>();
            {
                o.add("app_name");
                o.add("app_id");
                o.add("firebase_database_url");
                o.add("gcm_defaultSenderId");
                o.add("google_api_key");
                o.add("google_app_id");
                o.add("google_crash_reporting_api_key");
                o.add("google_storage_bucket");

                o.add("secret_key");
                o.add("access_token");
                o.add("client_id");
                o.add("client_secret");
                o.add("encryption_key");
                o.add("signing_key");
                o.add("keystore_password");
                o.add("oauth_client_id");
                o.add("firebase_server_key");

                o.add("default_web_client_id");
                o.add("fb_app_id");
                o.add("fb_app_name");

                o.add("project_number");
                o.add("firebase_url");
                o.add("project_id");
                o.add("storage_bucket");
                o.add("package_name");
                o.add("api_key");

            }

            data.add(new HeaderObject(context.getString(R.string.title_strings), R.drawable.ic_details_settings));
            PackageManager packageManager = context.getPackageManager();
            for (String string : o) {
                Resources resources = packageManager.getResourcesForApplication(packageName);
                int stringId = resources.getIdentifier(string, "string", packageName);
                if (stringId != 0) {
                    String stringValue = resources.getString(stringId);
                    data.add(wrapV2Line("" + string, "" + stringValue));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            DLog.handleException(e);
        }
    }

    static Map<String, Integer> map = new HashMap<>();

    static {
        map.put("applovin.", R.drawable.ic_applovin);
        map.put("com.google.firebase.", R.drawable.ic_firebase);
        map.put("com.unity3d.", R.drawable.ic_unity3d);
        map.put("com.facebook.", R.drawable.ic_facebook);

    }

    private ViewModel wrapV2Line(String key, String value) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                return new V2Line(key, "" + value, entry.getValue());
            }
        }
        return new V2Line(key, "" + value);
    }

    private void meta7(List<ViewModel> data) throws PackageManager.NameNotFoundException {

//    <activity>*
//    <activity-alias>
//    <application>*
//    <provider>*
//    <receiver>*
//    <service>  *

        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(meta.packageName, PackageManager.GET_META_DATA);


        Bundle metaData = applicationInfo.metaData;
        //String applicationId = metaData.getString("com.google.android.gms.ads.APPLICATION_ID");
        if (metaData != null) {
            data.add(new HeaderObject("Application MetaData" + DEVIDER_START + metaData.keySet().size() + DEVIDER_END,
                    R.drawable.ic_category_activity));
            for (String key : metaData.keySet()) {
                Object value = metaData.get(key);
                data.add(wrapV2Line(key, "" + value));
            }
        }


        PackageInfo packageInfo = packageManager.getPackageInfo(meta.packageName, PackageManager.GET_META_DATA
                | PackageManager.GET_ACTIVITIES
                | PackageManager.GET_SERVICES
                | PackageManager.GET_PROVIDERS
                | PackageManager.GET_RECEIVERS);

        ActivityInfo[] activities = packageInfo.activities;
        ActivityInfo[] receivers = packageInfo.receivers;

        ServiceInfo[] services = packageInfo.services;
        ProviderInfo[] providers = packageInfo.providers;

        Map<String, String> stringMap = toMap(activities);
        Map<String, String> map = toMap(receivers);

        if (!stringMap.isEmpty()) {
            data.add(new HeaderObject("Activities MetaData" + DEVIDER_START + stringMap.size() + DEVIDER_END, R.drawable.ic_category_activity));
            for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                data.add(wrapV2Line(entry.getKey(), "" + entry.getValue()));
            }
        }

        if (!map.isEmpty()) {
            data.add(new HeaderObject("Receivers MetaData" + DEVIDER_START + map.size() + DEVIDER_END, R.drawable.ic_category_receiver));
            for (Map.Entry<String, String> entry : map.entrySet()) {
                data.add(wrapV2Line(entry.getKey(), "" + entry.getValue()));
            }
        }


        if (services != null && services.length > 0) {
            Map<String, String> hashMap = new HashMap<>();
            for (ServiceInfo service : services) {

                metaData = service.metaData;

                if (metaData != null) {
                    //data.add(new HeaderObject("Services MetaData" + DEVIDER_START + metaData.keySet().size() + DEVIDER_END, R.drawable.ic_category_activity));
                    for (String key : metaData.keySet()) {
                        Object value = metaData.get(key);
                        //data.add(wrapV2Line(key, "" + value));
                        hashMap.put(key, String.valueOf(value));
                    }
                }
//                else {
//                    mView.showError("No metadata found for the component", null);
//                }
            }
            if (!hashMap.isEmpty()) {
                data.add(new HeaderObject("Services MetaData" + DEVIDER_START + hashMap.size() + DEVIDER_END, R.drawable.ic_category_services));
                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                    data.add(wrapV2Line(entry.getKey(), "" + entry.getValue()));
                }
            }
        }
        if (providers != null && providers.length > 0) {
            Map<String, String> stringMap1 = new HashMap<>();
            for (ProviderInfo provider : providers) {
                metaData = provider.metaData;
                if (metaData != null) {
                    //data.add(new HeaderObject("Provider MetaData" + DEVIDER_START + metaData.keySet().size() + DEVIDER_END, R.drawable.ic_category_activity));
                    for (String key : metaData.keySet()) {
                        Object value = metaData.get(key);
                        //data.add(wrapV2Line(key, "" + value));
                        stringMap1.put(key, String.valueOf(value));
                    }
                }
//                else {
//                    mView.showError("No metadata found for the component", null);
//                }
            }

            if (!stringMap1.isEmpty()) {
                data.add(new HeaderObject("Providers MetaData" + DEVIDER_START + stringMap1.size() + DEVIDER_END, R.drawable.ic_category_provider));
                for (Map.Entry<String, String> entry : stringMap1.entrySet()) {
                    data.add(wrapV2Line(entry.getKey(), "" + entry.getValue()));
                }
            }
        }
    }

    private Map<String, String> toMap(ActivityInfo[] activities) {
        Map<String, String> map = new HashMap<>();
        if (activities != null && activities.length > 0) {
            for (ActivityInfo activity : activities) {
                Bundle metaData = activity.metaData;
                if (metaData != null) {
//                    data.add(new HeaderObject(s + " MetaData" + DEVIDER_START + metaData.keySet().size() + DEVIDER_END,
//                            R.drawable.ic_category_activity));
                    for (String key : metaData.keySet()) {
                        Object value = metaData.get(key);
                        map.put(key, String.valueOf(value));
                    }
                }
            }
        }
        return map;
    }


//    private void wrap(ActivityInfo[] activities, String s, List<ViewModel> data) {
//        if (activities != null && activities.length > 0) {
//            for (ActivityInfo activity : activities) {
//                Bundle metaData = activity.metaData;
//                if (metaData != null) {
//                    data.add(new HeaderObject(s + " MetaData" + DEVIDER_START + metaData.keySet().size() + DEVIDER_END,
//                            R.drawable.ic_category_activity));
//                    for (String key : metaData.keySet()) {
//                        Object value = metaData.get(key);
//                        data.add(wrapV2Line(key, "" + value));
//                    }
//                }
//            }
//        }
//    }

    private String getDate(long time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date(time));
        } catch (Exception ex) {
            return "";
        }
    }

    private boolean permissionGranted(PackageManager manager, String permission) {
        return manager.checkPermission(permission, packageInfo.packageName) == PackageManager.PERMISSION_GRANTED;
    }

    private void providers(ProviderInfo[] providers, PackageManager pm, List<ViewModel> data) {
        int length = (providers == null) ? 0 : providers.length;
        if (length > 0) {
            HeaderCollapsedObject o = new HeaderCollapsedObject(
                    context.getString(R.string.app_info_providers)
                            + DEVIDER_START
                            + length
                            + DEVIDER_END, R.drawable.ic_category_provider);
            for (int i = 0; i < length; i++) {
                ProviderInfo info = providers[i];
                boolean granted = info.grantUriPermissions;
                CharSequence label = info.loadLabel(pm);
                Drawable drawable;
                //if (ai.icon > 0) {
                drawable = icons.get(info.icon);
                if (drawable == null) {
                    drawable = info.loadIcon(pm);
                    icons.put(info.icon, drawable);
                }
                //}
                //FirebaseInitProvider
                //@int iconResource = info.getIconResource();
                //@int logoResource = info.getLogoResource(); == 0


                //info.pathPermissions;

                Uri uri = Uri.parse("content://" + info.authority + "/");
                DLog.d("@p@" + uri);

                ProviderLine line = new ProviderLine(
                        drawable,
                        label.toString(),
                        info.name,
                        info.exported,
                        info.enabled,
                        uri.toString()
                );
                o.list.add(line);
            }
            data.add(o);
        } else {
            data.add(new HeaderObject(context.getString(R.string.app_info_providers)
                    + DEVIDER_START + length + DEVIDER_END, R.drawable.ic_category_provider));
        }
    }

    public void openSettingsRequest(Context context) {
        if (!TextUtils.isEmpty(packageInfo.packageName)) {
            //app info
            IntentUtils.openSettingsForPackageName(context, packageInfo.packageName);
        }
    }



    public void onLaunchExportedService(String class_name) {
        Exception exception = null;

        //background.systemjob.SystemJobService
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageInfo.packageName, class_name));

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ComponentName c = context.getApplicationContext().startForegroundService(intent);
            } else {
                ComponentName c = context.startService(intent);
            }
        } catch (Exception e) {
            exception = e;
            DLog.d("[1," + packageInfo.packageName + "::" + class_name + DEVIDER_END + exception.getLocalizedMessage());

            try {
                ComponentName c = context.startService(intent);
                exception = null;
            } catch (Exception e0) {
                exception = e0;
                DLog.d("[2," + packageInfo.packageName + "::" + class_name + DEVIDER_END + e0.getLocalizedMessage());
            }
        }
        if (exception != null) {
            Toast.makeText(context, "" + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void showErrorMessageDialog(Context context, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Warning...")
                .setMessage(errorMessage)
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
