package com.walhalla.appextractor.abba;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Proxy;
import android.net.Uri;
import android.os.Build;
import android.view.Menu;
import android.view.SubMenu;
import android.widget.Toast;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.Troubleshooting;
import com.walhalla.appextractor.Util;
import com.walhalla.ui.DLog;
import com.walhalla.ui.plugins.MimeType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class IntentReaper {

    private final Map<String, String> cache_appName = Collections.synchronizedMap(new LinkedHashMap<>(10, 1.5f, true));
    public static Set<String> mime_all = new TreeSet<>(); //mime_all

    static {
        mime_all.add("application/x-www-form-urlencoded");
        mime_all.add("application/vnd.android.package-archive");//.apk, if install
        mime_all.add("application/octet-stream");
        mime_all.add(MimeType.TEXT_PLAIN);
        mime_all.add("image/jpeg");
        mime_all.add("*/*");
        mime_all.add("image/*");
        mime_all.add("vnd.android.cursor.dir/*");
        mime_all.add("resource/folder");
        mime_all.add("text/csv");
        mime_all.add("vnd.android.document/directory");
        mime_all.add("vnd.android.cursor.dir/lysesoft.andexplorer.director");
    }


    String[] apk_actions = new String[]{
            Intent.ACTION_VIEW,

            //@@@@@ Intent.ACTION_INSTALL_PACKAGE,
            //Intent.ACTION_UNINSTALL_PACKAGE,

            //Intent.ACTION_GET_CONTENT, //We not use
            Intent.ACTION_DELETE,
            Intent.ACTION_OPEN_DOCUMENT,
            Intent.ACTION_SEND,
            //Intent.ACTION_SEND_MULTIPLE,
            Intent.ACTION_SENDTO,
    };

    private final List<QWrap> list = new ArrayList<>();
    private final PackageManager pm;
    private final Context context;

    public IntentReaper(Context context) {
        this.context = context;
        this.pm = context.getPackageManager();
    }

    //private String dir_mime = "vnd.android.document/directory";
    private final String dir_mime = "vnd.android.cursor.dir/*";

    //private String dir_mime = "*/*";

    public void makeMimeDir() {
        File fake = Troubleshooting.defLocation();
        makeMime(dir_mime, fake, null);
    }

    /**
     * DONT Set com.google.android.packageinstaller
     */
    public void makeMime(String mime, File file, String[] actions) {
        try {

            if (actions == null) {
                actions = Mimiq.actions;
            }
            Uri apkUri = null;
            if (file != null) {
                apkUri = Util.makeURI(context, file);
            }
            Map<String, List<ResolveInfo>> map = new HashMap<>();
            for (String action : actions) {
                Intent intent0 = intentMaker(action, mime, apkUri);

                List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(intent0, 0);
                //List<ResolveInfo> resolvedActivityList = pm.queryIntentServices(intent0, PackageManager.GET_META_DATA);

                if (resolvedActivityList.size() > 0) {
                    if (Intent.ACTION_VIEW.equals(action)) {
                        List<ResolveInfo> newValue = new ArrayList<>();
                        for (ResolveInfo info : resolvedActivityList) {
                            String packageName = Util.packageName(info);
                            if (!packageName.startsWith("com.google.android.packageinstaller")) {
                                newValue.add(info);
                            }
                        }
                        map.put(action, newValue);
                    } else {
                        map.put(action, resolvedActivityList);
                    }
                }
            }
            list.add(new QWrap(mime, map));
        } catch (java.lang.IllegalArgumentException r) {
            DLog.handleException(r);
        }
    }


    public void makeMimeApk(File file) {
        makeMime("application/vnd.android.package-archive", file, apk_actions);
    }

    public void makeMimeApk() {
        File fake = new File(Troubleshooting.defLocation(), "fake.apk");
        makeMime("application/vnd.android.package-archive", fake, apk_actions);
    }

    private Intent intentMaker(String action, String mime, Uri apkUri) {
        Intent intent0 = new Intent(action);
        if (mime != null) {
            //String url = "/storage/emulated/0/Download/com.Mobilicks.PillIdentifier_v4.apk";
            //apk=new File(url);
            //intent0 = new Intent(action, apkUri);
            if (apkUri != null) {
                intent0.setDataAndType(apkUri, mime);
            } else {
                intent0.setType(mime);
            }
        }

//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent0;
    }


    /**
     * Exception if false Uri, if apk is system file!
     */
    public void wrapper(Menu menu, File apk) {

//        if (!apk.canRead()) {
//            return;
//        }
        try {
            Uri apkUri = Util.makeURI(context, apk);
            for (QWrap wrap : list) {
                String mime = wrap.mime;
                for (Map.Entry<String, List<ResolveInfo>> entry : wrap.map.entrySet()) {
                    String action = entry.getKey();
                    List<ResolveInfo> values = entry.getValue();
                    if (!values.isEmpty()) {
                        String menu_name = action.replace("android.intent.action.", "");
                        if (list.size() > 1) {
                            menu_name = mime + "::" + menu_name;
                        }
                        //menu_name=menu_name + " [" + values.size() + "]";
                        SubMenu actionMenu = menu.addSubMenu(menu_name)
                                .setIcon(R.drawable.ic_action_android);

                        //DLog.d(action + " " + values.size());

                        for (ResolveInfo info : values) {

                            Intent serviceIntent = intentMaker(action, mime, apkUri);
                            serviceIntent.setPackage(Util.packageName(info));

                            //WARNING
                            //pm.resolveService && pm.resolveActivity not work with
                            //                    Intent serviceIntent = new Intent(action);
                            //                    serviceIntent.setPackage(Util.packageName(info));
                            final Drawable icon = info.loadIcon(pm);
                            if (pm.resolveService(serviceIntent, 0) != null) {
                                //packagesSupportingCustomTabs.add(info);
                                DLog.d("\t\t-----0------" + info);
                                actionMenu
                                        .add(0, Menu.FIRST, Menu.NONE, "@S@" + info.serviceInfo.name)
                                        .setOnMenuItemClickListener(item -> {
                                            handle(info, action, mime, apk);
                                            return false;
                                        })
                                        //.addSubMenu("@S@" + info.serviceInfo.name)
                                        .setIcon(icon);
                            } else if (pm.resolveActivity(serviceIntent, 0) != null) {

                                actionMenu
                                        .add(0, Menu.FIRST, Menu.NONE, info.loadLabel(pm))
                                        .setOnMenuItemClickListener(item -> {
                                            handle(info, action, mime, apk);
                                            return false;
                                        })
                                        //.addSubMenu("@A@" + info.activityInfo.name)
                                        .setIcon(icon);
                            }
                        }
                    }
                }
            }
        } catch (java.lang.IllegalArgumentException e) {
            DLog.handleException(e);
        }

    }

    private void handle(ResolveInfo info, String action, String mime, File file) {

//        String url = "/storage/emulated/0/Download/com.Mobilicks.PillIdentifier_v4.apk";
//        showFileList();

        String packageName = Util.packageName(info);
        DLog.d("@@@" + info.icon);
        DLog.d("@@@" + info.resolvePackageName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            DLog.d("@@@" + info.isCrossProfileIntentForwarderActivity());
        }

        if (info.activityInfo != null) {
            DLog.d("[@@]" + info.activityInfo);
            DLog.d("[@@]" + info.activityInfo.name);

            DLog.d("[@@]" + info.activityInfo.targetActivity);
            DLog.d("[@@]" + info.activityInfo.parentActivityName);
            DLog.d("[@@]" + info.activityInfo.permission);
            DLog.d("[@@]" + info.activityInfo.processName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DLog.d("[@@]" + info.activityInfo.splitName);
            }
            DLog.d("[@@]" + info.activityInfo.taskAffinity);
            DLog.d("[@@]" + info.activityInfo.applicationInfo);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DLog.d("[@@]" + Arrays.toString(info.activityInfo.attributionTags));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                DLog.d("[@@]" + info.activityInfo.banner);
            }

            DLog.d("[@@]" + info.activityInfo.getThemeResource());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                DLog.d("[@b@]" + info.activityInfo.loadBanner(pm));
            }
        }
//        if (info.serviceInfo != null) {
//            DLog.d("[@@]" + info.serviceInfo);
//            DLog.d("[@@]" + info.serviceInfo.name);
//
//            DLog.d("[@@]" + info.serviceInfo.permission);
//            DLog.d("[@@]" + info.serviceInfo.processName);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                DLog.d("[@@]" + info.serviceInfo.splitName);
//            }
//
//            DLog.d("[@@]" + info.serviceInfo.applicationInfo);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                DLog.d("[@@]" + Arrays.toString(info.serviceInfo.attributionTags));
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//                DLog.d("[@@]" + info.serviceInfo.banner);
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//                DLog.d("[@b@]" + info.serviceInfo.loadBanner(pm));
//            }
//
//        }
//        DLog.d("@@@" + info.icon);
//        DLog.d("@@@" + info.resolvePackageName);
//        
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            DLog.d("@@@" + info.isCrossProfileIntentForwarderActivity());
//        }


        Intent intent = new Intent(action);
        intent.setPackage(packageName);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (ActivityNotFoundException rr) {


            if (file.exists() && !file.isDirectory()) {
            }

            if (!file.exists() || !file.canRead()) {
                Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Uri uri = Util.makeURI(context, file);
                if (uri != null) {
                    String type = context.getContentResolver().getType(uri);
                    DLog.d("___E 1___ " + type + " " + mime + " " + packageName);

//String url = "/data/app/SmokeTestApp/SmokeTestApp.apk";

//            if (!url.startsWith("http://") && !url.startsWith("https://")) {
//                url = "http://" + url;
//            }
//            uri = Uri.parse(url);
                    Intent intent1 = new Intent(action, uri);
                    intent1.setPackage(packageName);
                    intent1.putExtra(Intent.EXTRA_TEXT, "11111111");
                    if (1 == 1) {
                        intent1.putExtra(Intent.EXTRA_EMAIL, new String[]{"alexvarboffin@gmai.com"});
                    }

                    //Gmail title
                    //DropBox - document name
                    intent1.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));

                    //GMail Send File
                    if ("com.google.android.gm".equals(packageName)) {
                        intent1.setType(mime);//Not work if false mimitype
                        intent1.putExtra(Intent.EXTRA_STREAM, uri);
                    }


                    //com.google.android.apps.docs.shareitem.UploadMenuActivity
                    else if ("com.google.android.apps.docs".equals(packageName)) { //Google docs disc


                        String to = "YourEmail@somewhere.com";
                        String subject = "Backup";
                        String message = "Your backup is attached";
                        //Intent inten1 = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        intent1.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                        intent1.putExtra(Intent.EXTRA_SUBJECT, subject);
                        intent1.putExtra(Intent.EXTRA_TEXT, message);
                        intent1.putExtra("accountName", "zzz@gmail.com");
                        intent1.putExtra("deleteOriginalFile", true);
                        intent1.putExtra("documentTitle", "11111");


                        //intent1.setType(mime);//Not work if false mimitype

                        //Uri uRi = Uri.parse("file://" + file);
                        DLog.d("@@file://" + file + "  " + Uri.fromFile(file) + " " + uri.getScheme());
                        //Exception intent1.setDataAndType(Uri.fromFile(file), "*/*");

                        intent1.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                                new ArrayList<>(Arrays.asList(uri, uri, uri)));
                        intent1.setType("text/*");
                        //intent1.setDataAndType(uri, "*/*");

                        //send, send_multiple */*
                        // or
                        //send <data android:scheme="file"/>

//                        // Old Approach
//                        install.setDataAndType(Uri.fromFile(file), mimeType);
//// End Old approach
//// New Approach
//                        Uri apkURI = FileProvider.getUriForFile(
//                                context,
//                                context.getApplicationContext()
//                                        .getPackageName() + ".provider", file);
//                        install.setDataAndType(apkURI, mimeType);


                    } else if ("com.google.android.packageinstaller".equals(packageName)) {
                        DLog.d("-------[apk INSTALLER]");
                        intent1.setDataAndType(uri, mime);
                    } else if (packageName.startsWith("com.dropbox.android")) {
                        DLog.d("@DROP_BOX@");
                        intent1.setDataAndType(uri, mime);
                    } else {
                        DLog.d("------------------------------------");
                        intent1.setDataAndType(uri, mime);
                    }
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        ComponentName aa = intent1.resolveActivity(pm);
                        if (aa != null) {
                            DLog.d(aa.toString());
                            context.startActivity(intent1);//FileUriExposedException
                        } else {
                            DLog.d("@@@@@@");
                        }
                    } catch (ActivityNotFoundException rr9) {

                        DLog.d("___E 2___ " + mime + " " + mime + " " + packageName);

//                        if (intent2.resolveActivity(getPackageManager()) != null) {
//                            startActivity(intent2);
//                        } else {
//                            DLog.d("@@empty@@@");
//                        }
                    }
                } else {
                    Toast.makeText(context, "@ Try Latter", Toast.LENGTH_SHORT).show();
                }

            } catch (StringIndexOutOfBoundsException e) {
                DLog.handleException(e);
            }
        }
    }

    public void makemimeAll() {
        File fake = Troubleshooting.defLocation();
        for (String mime : mime_all) {
            makeMime(mime, null, null);
        }
    }

    public void makemimeProxy() {
        String[] aa = {
                Proxy.PROXY_CHANGE_ACTION
        };
        File fake = Troubleshooting.defLocation();
        makeMime("*/*", null, aa);
    }
}
