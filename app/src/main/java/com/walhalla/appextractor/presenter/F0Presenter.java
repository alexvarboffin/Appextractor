package com.walhalla.appextractor.presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.detail.DetailsF0;
import com.walhalla.appextractor.adapter2.activity.ActivityLine;
import com.walhalla.appextractor.adapter2.base.ViewModel;
import com.walhalla.appextractor.adapter2.cert.CertLine;
import com.walhalla.appextractor.adapter2.dirline.DirLine;
import com.walhalla.appextractor.adapter2.flagz.FlagzObject;
import com.walhalla.appextractor.adapter2.header.HeaderObject;
import com.walhalla.appextractor.adapter2.headerCollapsed.HeaderCollapsedObject;
import com.walhalla.appextractor.adapter2.infoapk.InfoApkLine;
import com.walhalla.appextractor.adapter2.perm.PermissionLine;
import com.walhalla.appextractor.adapter2.provider.ProviderLine;
import com.walhalla.appextractor.adapter2.receiver.ReceiverLine;
import com.walhalla.appextractor.adapter2.service.ServiceLine;
import com.walhalla.appextractor.adapter2.simple.SimpleLine;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.appextractor.utils.IntentUtils;
import com.walhalla.ui.DLog;

import org.qiyi.pluginlibrary.utils.ManifestParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class F0Presenter implements DetailsF0.Presenter {


    Map<Integer, Drawable> icons = new HashMap<>();

    private static final String DEVIDER_START = " (";
    private static final String DEVIDER_END = ")";

    private final DetailsF0.View view;

    private final Context myActivityContext;
    private final PackageManager packageManager;
    private final PackageMeta meta;
    private PackageInfo packageInfo;

    @SuppressLint("PackageManagerGetSignatures")
    public F0Presenter(Context context, PackageMeta meta, DetailsF0.View view) {
        final PackageManager manager = context.getPackageManager();
        this.myActivityContext = context;
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
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(meta.packageName, 0);

            // Контекст для другого приложения
            Context otherAppContext = null;
            PackageManager otherAppPackageManager = null;

            try {
                otherAppContext = context.getApplicationContext().createPackageContext(meta.packageName,
                        0
                        //Context.CONTEXT_IGNORE_SECURITY
                        //Context.CONTEXT_INCLUDE_CODE//Requesting code from com.google.android.youtube (with uid 10162) to be run in process com.walhalla.appextractor (with uid 10730)
                );
            } catch (PackageManager.NameNotFoundException e) {
                DLog.handleException(e);
            }
            if (otherAppContext != null) {
                otherAppPackageManager = otherAppContext.getPackageManager();// Получаем PackageManager для другого приложения
            }


            //LinearLayout card = this.mCard();
            //ViewGroup r10 = findViewById(R.id.scrollContainer);
            //int r10 = R.id.scrollContainer;

            List<ViewModel> data = new ArrayList<>();
            base(applicationInfo, data);
            activities(applicationInfo, data);
            instrumentations(packageInfo.instrumentation, data);


            //BROWSABLE ACTIVITY
            Intent ia0 = new Intent(Intent.ACTION_VIEW, Uri.parse("file:///sdcard"));
            ia0.addCategory(Intent.CATEGORY_BROWSABLE);
            ia0.setPackage(packageInfo.packageName);

            List<ResolveInfo> aaas = packageManager.queryIntentActivities(ia0, 0);
            for (ResolveInfo info : aaas) {
                data.add(new CertLine("BROWSABLE->", "" + info.activityInfo.name));
            }

            Intent i0 = new Intent(Intent.ACTION_VIEW);
            i0.setDataAndType(Uri.parse("/sdcard/"), "*/*");
            i0.addCategory(Intent.CATEGORY_BROWSABLE);
            i0.setPackage(packageInfo.packageName);

            List<ResolveInfo> infos = packageManager.queryIntentActivities(i0, 0);
            for (ResolveInfo info : infos) {
                data.add(new CertLine("BROWSABLE->", "" + info.activityInfo.name));
            }
            //END_BROWSABLE ACTIVITY


            services(data);
            receivers(packageInfo.receivers, packageManager, data);
            providers(packageInfo.providers, packageManager, data);

            //apk metadata applicationInfo.flags
            //packageInfo.flags


//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                boolean flag_uses_cleartext_traffic = (applicationInfo.flags & ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC) == ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC;
//                data.add(new CertLine("FLAG_USES_CLEARTEXT_TRAFFIC", "" + flag_uses_cleartext_traffic));
//            }

            //boolean debuggable = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
            //data.add(new CertLine("FLAG_DEBUGGABLE", "" + debuggable));
            data.add(new FlagzObject(applicationInfo.flags));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //getClass().getName() + '@' + Integer.toHexString(hashCode())

                String txt = "NOT_SET";
                if (applicationInfo.category != -1) {
                    txt = ApplicationInfo.getCategoryTitle(context, applicationInfo.category) + " (cat_id=>" + applicationInfo.category + ")";
                }
                data.add(new DirLine("The category of this app: ", txt, R.drawable.ic_category));
            }
//            compatibleWidthLimitDp
//            The maximum smallest screen width the application is designed for.

            data.add(new CertLine("Min", "" + applicationInfo.compatibleWidthLimitDp));
            data.add(new CertLine("Max", "" + applicationInfo.largestWidthLimitDp));
            data.add(new CertLine("requiresSmallestWidthDp", ""
                    + applicationInfo.requiresSmallestWidthDp));


            int count1 = applicationInfo.sharedLibraryFiles == null ? 0 : applicationInfo.sharedLibraryFiles.length;
            if (count1 > 0) {
                HeaderCollapsedObject o = new HeaderCollapsedObject(
                        context.getString(R.string.app_info_sharedlibraryfiles)
                                + DEVIDER_START + count1 + DEVIDER_END, R.drawable.ic_lib);
                for (int i = 0; i < applicationInfo.sharedLibraryFiles.length; i++) {
                    String info = applicationInfo.sharedLibraryFiles[i];
                    o.list.add(new CertLine("", "" + info));
                }
                data.add(o);
            } else {
                data.add(new HeaderObject(context.getString(R.string.app_info_sharedlibraryfiles)
                        + DEVIDER_START + count1 + DEVIDER_END, R.drawable.ic_lib));
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                data.add(new CertLine("CompileSdkVersion", "" + applicationInfo.compileSdkVersion));
                data.add(new CertLine("CompileSdkVersionCodename", "" + applicationInfo.compileSdkVersionCodename));
            }

            data.add(new CertLine("DescriptionRes", "" + applicationInfo.descriptionRes));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                data.add(new CertLine("AppComponentFactory", applicationInfo.appComponentFactory));
            }

            data.add(new CertLine("BackupAgentName", applicationInfo.backupAgentName));
            data.add(new CertLine("ManageSpaceActivityName", applicationInfo.manageSpaceActivityName));
            //
            data.add(new CertLine("ProcessName", applicationInfo.processName));


            String appName0 = (applicationInfo.className == null) ? "NOT_USED" : applicationInfo.className;
            data.add(new CertLine("Application Class", appName0));


            data.add(new HeaderObject(context.getString(R.string.app_info_installation), R.drawable.ic_base_info));
            data.add(new SimpleLine(R.string.app_info_enable, String.valueOf(applicationInfo.enabled)));

            String installerPackageName = packageManager.getInstallerPackageName(packageInfo.packageName);
            data.add(new SimpleLine(R.string.app_info_installation_source, "" + handleInstallSource(packageManager, installerPackageName)));

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                InstallSourceInfo info = packageManager.getInstallSourceInfo(applicationInfo.packageName);
//                String a12 = info.getInitiatingPackageName();
//                if (info.getInitiatingPackageSigningInfo() != null) {
//                    String a22 = info.getInitiatingPackageSigningInfo().toString();
//                    DLog.d("[a]"+a22);
//                }
//                String a33 = info.getInstallingPackageName();
//                String a55 = info.getOriginatingPackageName();
//                DLog.d("[b]"+info.ga12 + "  " + a33 + " " + a55);
//
//                this.newline(R.string.app_info_installation_source, "" + info.toString(), card);
//            }


            //data.add(new SimpleLine());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean granted = hasPermissionPackageUsageStats();
                if (granted) {
                    getStorageStats(data, packageInfo.packageName);
                } else {
                    view.snack();
                }
            }
            if (packageInfo.signatures != null) {
                data.add(new HeaderObject(context.getString(R.string.app_info_certificate), R.drawable.ic_cert));
                getSignaturesInfo(data, packageInfo.signatures);
            }

            //"android"
            //PackageInfo packageInfo0 = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

            //Optional name of a permission required to be able to access this application's components.
//            if (packageInfo.permissions != null) {
//                // For each defined permission
//                for (PermissionInfo permission : packageInfo.permissions) {
//                    int protectionLevelKey = permission.protectionLevel;
//                    String protectionLevel = PermissionUtils.protectionLevelToString(protectionLevelKey);
//                    DLog.d(permission.name + " " + protectionLevel);
//                }
//            }
            //permissions
            //запрошенные разрешения
            //uses-permission
            if (packageInfo.requestedPermissions != null) {
                data.add(new HeaderObject(context.getString(R.string.app_info_permissions), R.drawable.ic_base_info));
                String[] permissions = packageInfo.requestedPermissions;
                for (String permission : permissions) {
                    boolean isGranted = permissionGranted(packageManager, permission);
                    String p0 = permission.replace("android.permission.", "");
                    PermissionInfo permissionInfo = null;
                    int protectionLevel = -1;
                    try {
                        if (otherAppPackageManager != null) {
                            permissionInfo = otherAppPackageManager.getPermissionInfo(permission, PackageManager.GET_META_DATA);
                        } else {
                            permissionInfo = packageManager.getPermissionInfo(permission, PackageManager.GET_META_DATA);
                        }
                        if (permissionInfo != null) {
                            protectionLevel = permissionInfo.protectionLevel;
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        //DLog.d("www "+e.getClass()+" | "+e.getLocalizedMessage());
                    }

                    data.add(new PermissionLine(p0, isGranted, protectionLevel));
                }
            }
            if (packageInfo.reqFeatures != null) {
                data.add(new HeaderObject(context.getString(R.string.app_info_features), R.drawable.ic_base_info));
                for (FeatureInfo feature : packageInfo.reqFeatures) {
                    data.add(new CertLine(feature.name, "" + feature.flags));
                }
            }
            view.swap(data);
        } catch (PackageManager.NameNotFoundException e) {
            DLog.handleException(e);
        }
    }

    private void instrumentations(InstrumentationInfo[] infos, List<ViewModel> data) {
        int count0 = (infos == null) ? 0 : infos.length;
        String categoryName = myActivityContext.getString(R.string.app_info_instrumentation);
        if (count0 > 0) {
            HeaderCollapsedObject o = new HeaderCollapsedObject(
                    categoryName
                            + DEVIDER_START + count0 + DEVIDER_END, R.drawable.ic_category_receiver);
            for (int i = 0; i < infos.length; i++) {
                InstrumentationInfo info = infos[i];
            }
        }
    }


    private void receivers(ActivityInfo[] infos, PackageManager pm, List<ViewModel> data) {
        //RECEIVERS
        int count0 = (infos == null) ? 0 : infos.length;
        String categoryName = myActivityContext.getString(R.string.app_info_receivers);
        if (count0 > 0) {
            HeaderCollapsedObject o = new HeaderCollapsedObject(
                    categoryName
                            + DEVIDER_START + count0 + DEVIDER_END, R.drawable.ic_category_receiver);
            for (int i = 0; i < infos.length; i++) {
                ActivityInfo info = infos[i];

                CharSequence label = info.loadLabel(pm);
                Drawable drawable;
                //if (ai.icon > 0) {
                drawable = icons.get(info.icon);
                if (drawable == null) {
                    drawable = info.loadIcon(pm);
                    icons.put(info.icon, drawable);
                }
                //}

                String receiverName = info.name;
                //DLog.d("{} @@@@@@@@@@@@@@@@@" + receiverName);

                // {*} Получаем Intent Filters для приемника
                Intent intent = new Intent();
                intent.setClassName(packageInfo.packageName, receiverName);
                List<ResolveInfo> resolveInfoList = pm.queryBroadcastReceivers(intent, 0);
                if (resolveInfoList != null && resolveInfoList.size() > 0) {
                    for (ResolveInfo resolveInfo : resolveInfoList) {
                        //ActivityInfo activityInfo = resolveInfo.activityInfo;
                        IntentFilter intentFilter = resolveInfo.filter;


                        if (intentFilter != null) {
                            // Выводим информацию о каждом Intent Filter
                            DLog.d("{} @@@ Intent Filter Priority: " + intentFilter.getPriority());
                            //, категориях (categories), типах данных (data) и т. д.
//                            int actions = intentFilter.countCategories();
//                            int actions = intentFilter.countDataTypes();

//                            for (int k = 0; k < intentFilter.countActions(); k++) {
//                                intentFilter.getAction(k);
//                            }
                        } else {
                            DLog.d("{} NO FILTER @@@" + packageInfo.packageName + "//" + receiverName);
                        }
                    }
                } else {
                    DLog.d("{} @@@@@@@@@@@@@@@@@");
                }

                o.list.add(new ReceiverLine(
                        drawable,
                        label.toString(),
                        receiverName,
                        info.exported,
                        info.enabled,
                        packageInfo.packageName
                ));
            }
            data.add(o);
        } else {
            data.add(new HeaderObject(categoryName
                    + DEVIDER_START + count0 + DEVIDER_END, R.drawable.ic_category_receiver));
        }
        //END_RECEIVERS
    }

    private void services(List<ViewModel> data) {
        //SERVICES
        int i1 = (packageInfo.services == null) ? 0 : packageInfo.services.length;
        String app_info_services = myActivityContext.getString(R.string.app_info_services);

        if (i1 > 0) {
            HeaderCollapsedObject collapse = new HeaderCollapsedObject(
                    app_info_services + DEVIDER_START + i1 + DEVIDER_END, R.drawable.ic_category_services);
            for (int i = 0; i < packageInfo.services.length; i++) {
                ServiceInfo service = packageInfo.services[i];

                //CertLine sl = new CertLine(, service.enabled + "}");
                CharSequence label = service.loadLabel(packageManager);
                Drawable drawable;
                //if (ai.icon > 0) {
                drawable = icons.get(service.icon);
                if (drawable == null) {
                    drawable = service.loadIcon(packageManager);
                    icons.put(service.icon, drawable);
                }
                //}
                ServiceLine sl = new ServiceLine(
                        drawable,
                        label.toString(),
                        "" + service.name, service.exported
                );
                collapse.list.add(sl);

            }
            data.add(collapse);
        } else {
            data.add(new HeaderObject(app_info_services
                    + DEVIDER_START + i1 + DEVIDER_END, R.drawable.ic_category_services));
        }
        //END_SERVICES
    }


    private void base(ApplicationInfo applicationInfo, List<ViewModel> data) {
        data.add(new HeaderObject(myActivityContext.getString(R.string.app_info_basic), R.drawable.ic_details_settings));
        data.add(new SimpleLine(R.string.app_info_version, packageInfo.versionName));
        data.add(new SimpleLine(R.string.app_info_version_code, String.valueOf(packageInfo.versionCode)));

        data.add(new SimpleLine(R.string.app_info_package_name, packageInfo.packageName));


        data.add(new SimpleLine(R.string.app_info_target_sdk, String.valueOf(applicationInfo.targetSdkVersion)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data.add(new SimpleLine(R.string.app_info_min_sdk, String.valueOf(applicationInfo.minSdkVersion)));
        }

        data.add(new SimpleLine(R.string.app_info_first_install_time, getDate(packageInfo.firstInstallTime)));
        data.add(new SimpleLine(R.string.app_info_last_update, getDate(packageInfo.lastUpdateTime)));
        data.add(new SimpleLine(R.string.app_info_uid, "" + applicationInfo.uid));

        data.add(new DirLine("NativeLibraryDir", applicationInfo.nativeLibraryDir, R.drawable.ic_folder_blue_36dp));


        String sourceDir = applicationInfo.sourceDir;
        String extractedPackageName = sourceDir.substring(sourceDir.lastIndexOf("/") + 1, sourceDir.lastIndexOf("."));


        // Строим путь к папке данных приложения
        String dataDirPath = "/data/data/" + meta.packageName;
        data.add(new InfoApkLine("dataDirPath", dataDirPath, R.drawable.ic_folder_blue_36dp));


        data.add(new InfoApkLine("sourceDir", sourceDir, R.drawable.ic_folder_blue_36dp));
        data.add(new InfoApkLine("publicSourceDir", applicationInfo.publicSourceDir, R.drawable.ic_folder_blue_36dp));
        data.add(new DirLine("dataDir", applicationInfo.dataDir, R.drawable.ic_folder_blue_36dp));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data.add(new DirLine("deviceProtectedDataDir", applicationInfo.deviceProtectedDataDir, R.drawable.ic_folder_blue_36dp));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (applicationInfo.splitPublicSourceDirs != null) {
                for (String splitPath : applicationInfo.splitPublicSourceDirs) {
                    data.add(new InfoApkLine("Split File", splitPath, R.drawable.ic_split));
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && applicationInfo.splitNames != null) {
            data.add(new DirLine("splitNames", "" + Arrays.toString(applicationInfo.splitNames), R.drawable.ic_split));
        }


        //Intent count0 = getPackageManager().getLaunchIntentForPackage_DEPRECATED(packageInfo.packageName);

    }


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

    //android.permission.PACKAGE_USAGE_STATS
    @RequiresApi(Build.VERSION_CODES.O)//26
    private void getStorageStats(List<ViewModel> data, String packageName) {
        try {
            StorageStats stats = ((StorageStatsManager) myActivityContext.getSystemService(Context.STORAGE_STATS_SERVICE))
                    .queryStatsForPackage(StorageManager.UUID_DEFAULT, packageName, android.os.Process.myUserHandle());


            String cachePath = "";//Ok
            String filesPath = "";
            String externalFilesPath = "";

            Context otherAppContext = null;

            try {
                otherAppContext = myActivityContext.getApplicationContext().createPackageContext(meta.packageName,
                        0
                        //Context.CONTEXT_IGNORE_SECURITY
                        //Context.CONTEXT_INCLUDE_CODE//Requesting code from com.google.android.youtube (with uid 10162) to be run in process com.walhalla.appextractor (with uid 10730)
                );
            } catch (PackageManager.NameNotFoundException e) {
                DLog.handleException(e);
            }
            if (otherAppContext != null) {
                File cacheDir = otherAppContext.getCacheDir();
                cachePath = cacheDir.getAbsolutePath();
                File filesDir = otherAppContext.getFilesDir();//data/data
                filesPath = filesDir.getAbsolutePath();

                //filesPath = filesDir.getAbsolutePath() + " | " + Arrays.toString(filesDir.listFiles());
                //File f0 = new File("/data/data/" + packageName);
                //filesPath = f0.getAbsolutePath() + " | " + Arrays.toString(f0.listFiles());

                File externalFilesDir = otherAppContext.getExternalFilesDir(null);
                externalFilesPath = (externalFilesDir != null) ? externalFilesDir.getAbsolutePath()
                        : "External storage is not available.";
            } else {
                File cacheDir = myActivityContext.getCacheDir();
                cachePath = cacheDir.getAbsolutePath();
                File filesDir = myActivityContext.getFilesDir();//data/data
                filesPath = filesDir.getAbsolutePath();

                // Получаем путь к папке файлов приложения на внешнем хранилище (если оно доступно)
                File externalFilesDir = myActivityContext.getExternalFilesDir(null);
                externalFilesPath = (externalFilesDir != null) ? externalFilesDir.getAbsolutePath()
                        : "External storage is not available.";
            }


//            Log.d("Cache directory", cachePath);
//            Log.d("Files directory", filesPath);
//            Log.d("External files directory", externalFilesPath);

            long appBytes = stats.getAppBytes();
            long cacheBytes = stats.getCacheBytes();
            long dataBytes = stats.getDataBytes();
            data.add(new SimpleLine(R.string.app_info_apk, ca(appBytes)));
            data.add(new SimpleLine(R.string.app_info_data, filesPath + " " + ca(dataBytes)));
            data.add(new SimpleLine(R.string.app_info_cache, cachePath + " " + ca(cacheBytes)));
            data.add(new SimpleLine(R.string.app_info_externalFilesPath, externalFilesPath));

        } catch (Exception e) {
            DLog.handleException(e);
        }
    }


    private String ca(long cacheBytes) {
        return android.text.format.Formatter.formatShortFileSize(myActivityContext, cacheBytes);
    }

    //21
    //https://www.google.com/search?client=firefox-b-d&q=content%3A%2F%2Fsms%2Finbox

    private boolean hasPermissionPackageUsageStats() {
        AppOpsManager appOpsManager = (AppOpsManager) myActivityContext.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), myActivityContext.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            return myActivityContext.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED;
        } else {
            return (mode == AppOpsManager.MODE_ALLOWED);
        }
    }

    private void providers(ProviderInfo[] providers, PackageManager pm, List<ViewModel> data) {
        int length = (providers == null) ? 0 : providers.length;
        if (length > 0) {
            HeaderCollapsedObject o = new HeaderCollapsedObject(
                    myActivityContext.getString(R.string.app_info_providers)
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
            data.add(new HeaderObject(myActivityContext.getString(R.string.app_info_providers)
                    + DEVIDER_START + length + DEVIDER_END, R.drawable.ic_category_provider));
        }
    }

    private void getSignaturesInfo(List<ViewModel> data, Signature[] signatureArr) {
        if (signatureArr == null || signatureArr.length <= 0) {
            data.add(new SimpleLine(R.string.app_info_certificate, myActivityContext.getString(R.string.unknown)));
            return;
        }
        try {
            for (Signature signature : signatureArr) {

                // Сертификат X509, X.509 - очень распространенный формат сертификата
                X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
                data.add(new SimpleLine(R.string.app_info_serial_number, x509Certificate.getSerialNumber().toString()));
//                    boolean tmpAddAlias = true;
//                    try {
//                        x509Certificate.checkValidity();
//                    } catch (CertificateExpiredException algorithms) {
//                        DLog.d("aaa console.certificateExpired tmpAlias");
//                        tmpAddAlias = false;
//                    } catch (CertificateNotYetValidException algorithms) {
//                        DLog.d("aaa console.certificateNotYetValid tmpAlias");
//                        tmpAddAlias = false;
//                    }
                String aa = x509Certificate.getIssuerX500Principal().toString();

                DLog.d("@@" + packageInfo.packageName);
                DLog.d("@@" + aa + " " + x509Certificate.getSerialNumber().toString() + "||"
                        + signature.hashCode());

                String[] mm = aa.split(", ");
                for (int i = 0; i < mm.length; i++) {
                    String[] aaa = mm[i].trim().split("=");
                    if (aaa.length == 2) {
                        data.add(new CertLine(aaa[0], aaa[1]));
                    }
                }
                String[] algorithms = new String[]{
                        "MD5",
                        "SHA1",
                        "SHA256",

                        //"AES",
                        //"HmacMD5"
                };
                for (String tpye : algorithms) {
                    MessageDigest md = MessageDigest.getInstance(tpye);
                    // Получаем открытый ключ
                    byte[] publicKey = md.digest(x509Certificate.getEncoded());
                    // Преобразование байта в шестнадцатеричный формат
                    String hexString = byte2HexFormatted(publicKey);
                    data.add(new CertLine(tpye, hexString));
                }

                //New Line
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String h = Base64.encodeToString(md.digest(), Base64.DEFAULT);
//                data.add(new CertLine("MY KEY HASH:", h));
            }
        } catch (Exception unused) {
        }
    }

    // Вот шестнадцатеричное преобразование полученного кода
    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    private String handleInstallSource(PackageManager packageManager, String str) {
        if (TextUtils.isEmpty(str)) {
            return myActivityContext.getString(R.string.unknown);
        }
        if ("com.android.vending".equals(str)) {
            return myActivityContext.getString(R.string.app_referer_google_play);
        }
        if ("com.amazon.venezia".equals(str)) {
            return myActivityContext.getString(R.string.app_referer_amazon);
        }
        try {
            return packageManager.getApplicationInfo(str, 0).loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException unused) {
            return str;
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                ComponentName c = myActivityContext.getApplicationContext().startForegroundService(intent);
            } else {
                ComponentName c = myActivityContext.startService(intent);
            }
        } catch (Exception e) {
            exception = e;
            DLog.d("[1," + packageInfo.packageName + "::" + class_name + DEVIDER_END + exception.getLocalizedMessage());

            try {
                ComponentName c = myActivityContext.startService(intent);
                exception = null;
            } catch (Exception e0) {
                exception = e0;
                DLog.d("[2," + packageInfo.packageName + "::" + class_name + DEVIDER_END + e0.getLocalizedMessage());
            }
        }
        if (exception != null) {
            Toast.makeText(myActivityContext, "" + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void activities(ApplicationInfo applicationInfo, List<ViewModel> data) {

        List<ActivityLine> list = new ArrayList<>();
        HashMap<String, ActivityLine> map = new HashMap<>();

        String sourceDir = applicationInfo.sourceDir;
        ManifestParser parser = new ManifestParser(myActivityContext, sourceDir);
        List<ManifestParser.ComponentBean> activities = parser.activities;

        for (ManifestParser.ComponentBean activity : activities) {
            ActivityLine activityLine = new ActivityLine();
            activityLine.className = activity.className;
            activityLine.intentFilters = activity.intentFilters;
            map.put(activity.className, activityLine);
        }

        int actzz = (packageInfo.activities == null) ? 0 : packageInfo.activities.length;
        String app_info_activities = myActivityContext.getString(R.string.app_info_activities);

        if (actzz > 0) {
            HeaderCollapsedObject hobject = new HeaderCollapsedObject(
                    app_info_activities
                            + DEVIDER_START + actzz + DEVIDER_END, R.drawable.ic_category_activity);
            if (packageInfo.activities != null) {

                for (ActivityInfo activityInfo : packageInfo.activities) {
//                    data.add(new CertLine("Activities" + DEVIDER_START + packageInfo.activities.length + DEVIDER_END,
//                            "" + activityInfo.name));
                    //new CertLine("Activity" + DEVIDER_START + activities.length + DEVIDER_END, "" + activityInfo.name);
                    CharSequence label = activityInfo.loadLabel(packageManager);
                    Drawable drawable;
                    //if (activityInfo.icon > 0) {
                    drawable = icons.get(activityInfo.icon);
                    if (drawable == null) {
                        drawable = activityInfo.loadIcon(packageManager);
                        icons.put(activityInfo.icon, drawable);
                    }
                    //}
                    //drawable = activityInfo.loadIcon(0);- return default icon
                    //CharSequence label = activityInfo.loadLabel(0);- return default label

                    ActivityLine activityLine = activityLine = map.get(activityInfo.name);
                    int launchMode = activityInfo.launchMode;
                    if (activityLine == null) {
                        activityLine = new ActivityLine();
                    }
                    activityLine.label = label.toString();
                    activityLine.exported = activityInfo.exported;
                    activityLine.icon = drawable;
                    activityLine.className = activityInfo.name;
                    list.add(activityLine);
                }
                hobject.list.clear();
                hobject.list.addAll(list);
            }
            data.add(hobject);
        } else {
            data.add(new HeaderObject(app_info_activities
                    + DEVIDER_START + actzz + DEVIDER_END, R.drawable.ic_category_activity));
        }


        //LAUNCH ACTIVITY {ACTION_MAIN}
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageInfo.packageName);
        List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo info : appList) {
            data.add(new DirLine("ACTION_MAIN, CATEGORY_LAUNCHER", "" + info.activityInfo.name, R.drawable.ic_item_key));
        }
        //END_LAUNCH ACTIVITY
    }

//    private void activities(ApplicationInfo applicationInfo, List<ViewModel> data) {
//        String sourceDir = applicationInfo.sourceDir;
//
//        int actzz = (packageInfo.activities == null) ? 0 : packageInfo.activities.length;
//        String app_info_activities = myActivityContext.getString(R.string.app_info_activities);
//
//        if (actzz > 0) {
//            HeaderCollapsedObject hobject = new HeaderCollapsedObject(
//                    app_info_activities
//                            + DEVIDER_START + actzz + DEVIDER_END, R.drawable.ic_category_activity);
//            if (packageInfo.activities != null) {
//                List<ActivityLine> list = new ArrayList<>();
//                for (ActivityInfo activityInfo : packageInfo.activities) {
////                    data.add(new CertLine("Activities" + DEVIDER_START + packageInfo.activities.length + DEVIDER_END,
////                            "" + activityInfo.name));
//                    //new CertLine("Activity" + DEVIDER_START + activities.length + DEVIDER_END, "" + activityInfo.name);
//                    CharSequence label = activityInfo.loadLabel(packageManager);
//                    Drawable drawable;
//                    //if (activityInfo.icon > 0) {
//                    drawable = icons.get(activityInfo.icon);
//                    if (drawable == null) {
//                        drawable = activityInfo.loadIcon(packageManager);
//                        icons.put(activityInfo.icon, drawable);
//                    }
//                    //}
//                    //drawable = activityInfo.loadIcon(0);- return default icon
//                    //CharSequence label = activityInfo.loadLabel(0);- return default label
//
//
//                    int launchMode = activityInfo.launchMode;
//
//                    ActivityLine activityLine = new ActivityLine(
//                            drawable,
//                            label.toString(),
//                            "" + activityInfo.name, activityInfo.exported
//                    );
//                    list.add(activityLine);
//
//
//                }
//                hobject.list.clear();
//                hobject.list.addAll(list);
//            }
//            data.add(hobject);
//        } else {
//            data.add(new HeaderObject(app_info_activities
//                    + DEVIDER_START + actzz + DEVIDER_END, R.drawable.ic_category_activity));
//        }
//
//
//        //LAUNCH ACTIVITY {ACTION_MAIN}
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setPackage(packageInfo.packageName);
//        List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);
//
//        for (ResolveInfo info : appList) {
//            data.add(new DirLine("ACTION_MAIN, CATEGORY_LAUNCHER", "" + info.activityInfo.name, R.drawable.ic_item_key));
//        }
//        //END_LAUNCH ACTIVITY
//    }
}
