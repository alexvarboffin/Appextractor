package com.walhalla.appextractor.abba

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Proxy
import android.net.Uri
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.walhalla.appextractor.Troubleshooting.defLocation
import com.walhalla.appextractor.utils.Util
import com.walhalla.appextractor.utils.Util.makeURI
import com.walhalla.appextractor.utils.Util.packageName
import com.walhalla.extractor.R
import com.walhalla.ui.DLog
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.ui.plugins.MimeType

import java.io.File
import java.util.Arrays
import java.util.Collections
import java.util.TreeSet

class IntentReaper(private val context: Context) {
    private val cache_appName: Map<String, String> =
        Collections.synchronizedMap(LinkedHashMap(10, 1.5f, true))
    var apk_actions: Array<String> = arrayOf(
        Intent.ACTION_VIEW,  //@@@@@ Intent.ACTION_INSTALL_PACKAGE,
        //Intent.ACTION_UNINSTALL_PACKAGE,
        //Intent.ACTION_GET_CONTENT, //We not use

        Intent.ACTION_DELETE,
        Intent.ACTION_OPEN_DOCUMENT,
        Intent.ACTION_SEND,  //Intent.ACTION_SEND_MULTIPLE,
        Intent.ACTION_SENDTO,
    )

    private val list: MutableList<QWrap> = ArrayList()
    private val pm: PackageManager = context.packageManager

    //private String dir_mime = "vnd.android.document/directory";
    private val dir_mime = "vnd.android.cursor.dir/*"

    //private String dir_mime = "*/*";
    fun makeMimeDir() {
        val fake = defLocation()
        makeMime(dir_mime, fake, null)
    }

    /**
     * DONT Set com.google.android.packageinstaller
     */
    fun makeMime(mime: String, file: File?, actions: Array<String>?) {
        var actions = actions
        try {
            if (actions == null) {
                actions = Mimiq.actions
            }
            var apkUri: Uri? = null
            if (file != null) {
                apkUri = Util.makeURI(context, file)
            }
            val map: MutableMap<String, List<ResolveInfo>> = HashMap()
            for (action in actions) {
                val intent0 = intentMaker(action, mime, apkUri)

                val resolvedActivityList = pm.queryIntentActivities(intent0, 0)

                //List<ResolveInfo> resolvedActivityList = pm.queryIntentServices(intent0, PackageManager.GET_META_DATA);
                if (resolvedActivityList.size > 0) {
                    if (Intent.ACTION_VIEW == action) {
                        val newValue: MutableList<ResolveInfo> = ArrayList()
                        for (info in resolvedActivityList) {
                            val packageName = Util.packageName(info)
                            if (!packageName!!.startsWith("com.google.android.packageinstaller")) {
                                newValue.add(info)
                            }
                        }
                        map[action] = newValue
                    } else {
                        map[action] = resolvedActivityList
                    }
                }
            }
            list.add(QWrap(mime, map))
        } catch (r: IllegalArgumentException) {
            DLog.handleException(r)
        }
    }


    fun makeMimeApk(file: File?) {
        makeMime("application/vnd.android.package-archive", file, apk_actions)
    }

    fun makeMimeApk() {
        val fake = File(defLocation(), "fake.apk")
        makeMime("application/vnd.android.package-archive", fake, apk_actions)
    }

    private fun intentMaker(action: String, mime: String?, apkUri: Uri?): Intent {
        val intent0 = Intent(action)
        if (mime != null) {
            //String url = "/storage/emulated/0/Download/com.Mobilicks.PillIdentifier_v4.apk";
            //apk=new File(url);
            //intent0 = new Intent(action, apkUri);
            if (apkUri != null) {
                intent0.setDataAndType(apkUri, mime)
            } else {
                intent0.setType(mime)
            }
        }

//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return intent0
    }


    /**
     * Exception if false Uri, if apk is system file!
     */
    fun wrapper(menu: Menu, apk: File) {
//        if (!apk.canRead()) {
//            return;
//        }

        try {
            val apkUri = makeURI(context, apk)
            for (wrap in list) {
                val mime = wrap.mime
                for ((action, values) in wrap.map) {
                    if (!values.isEmpty()) {
                        var menu_name = action.replace("android.intent.action.", "")
                        if (list.size > 1) {
                            menu_name = "$mime::$menu_name"
                        }
                        //menu_name=menu_name + " [" + values.size() + "]";
                        val actionMenu = menu.addSubMenu(menu_name)
                            .setIcon(R.drawable.ic_action_android)

                        //DLog.d(action + " " + values.size());
                        for (info in values) {
                            val serviceIntent = intentMaker(action, mime, apkUri)
                            serviceIntent.setPackage(packageName(info))

                            //WARNING
                            //pm.resolveService && pm.resolveActivity not work with
                            //                    Intent serviceIntent = new Intent(action);
                            //                    serviceIntent.setPackage(Util.packageName(info));
                            val icon = info.loadIcon(pm)
                            if (pm.resolveService(serviceIntent, 0) != null) {
                                //packagesSupportingCustomTabs.add(info);
                                d("\t\t-----0------$info")
                                actionMenu
                                    .add(0, Menu.FIRST, Menu.NONE, "@S@" + info.serviceInfo.name)
                                    .setOnMenuItemClickListener { item: MenuItem? ->
                                        handle(info, action, mime, apk)
                                        false
                                    } //.addSubMenu("@S@" + info.serviceInfo.name)
                                    .setIcon(icon)
                            } else if (pm.resolveActivity(serviceIntent, 0) != null) {
                                actionMenu
                                    .add(0, Menu.FIRST, Menu.NONE, info.loadLabel(pm))
                                    .setOnMenuItemClickListener { item: MenuItem? ->
                                        handle(info, action, mime, apk)
                                        false
                                    } //.addSubMenu("@A@" + info.activityInfo.name)
                                    .setIcon(icon)
                            }
                        }
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            handleException(e)
        }
    }

    private fun handle(info: ResolveInfo, action: String, mime: String, file: File) {
//        String url = "/storage/emulated/0/Download/com.Mobilicks.PillIdentifier_v4.apk";
//        showFileList();

        val packageName = packageName(info)
        d("@@@" + info.icon)
        d("@@@" + info.resolvePackageName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            d("@@@" + info.isCrossProfileIntentForwarderActivity)
        }

        if (info.activityInfo != null) {
            d("[@@]" + info.activityInfo)
            d("[@@]" + info.activityInfo.name)

            d("[@@]" + info.activityInfo.targetActivity)
            d("[@@]" + info.activityInfo.parentActivityName)
            d("[@@]" + info.activityInfo.permission)
            d("[@@]" + info.activityInfo.processName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                d("[@@]" + info.activityInfo.splitName)
            }
            d("[@@]" + info.activityInfo.taskAffinity)
            d("[@@]" + info.activityInfo.applicationInfo)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                d("[@@]" + info.activityInfo.attributionTags.contentToString())
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                d("[@@]" + info.activityInfo.banner)
            }

            d("[@@]" + info.activityInfo.themeResource)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                d("[@b@]" + info.activityInfo.loadBanner(pm))
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
        val intent = Intent(action)
        intent.setPackage(packageName)
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (rr: ActivityNotFoundException) {
            if (file.exists() && !file.isDirectory) {
            }

            if (!file.exists() || !file.canRead()) {
                Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                val uri = makeURI(context, file)
                if (uri != null) {
                    val type = context.contentResolver.getType(uri)
                    d("___E 1___ $type $mime $packageName")

                    //String url = "/data/app/SmokeTestApp/SmokeTestApp.apk";

//            if (!url.startsWith("http://") && !url.startsWith("https://")) {
//                url = "http://" + url;
//            }
//            uri = Uri.parse(url);
                    val intent1 = Intent(action, uri)
                    intent1.setPackage(packageName)
                    intent1.putExtra(Intent.EXTRA_TEXT, "11111111")
                    if (1 == 1) {
                        intent1.putExtra(Intent.EXTRA_EMAIL, arrayOf("alexvarboffin@gmai.com"))
                    }

                    //Gmail title
                    //DropBox - document name
                    intent1.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))

                    //GMail Send File
                    if ("com.google.android.gm" == packageName) {
                        intent1.setType(mime) //Not work if false mimitype
                        intent1.putExtra(Intent.EXTRA_STREAM, uri)
                    } else if ("com.google.android.apps.docs" == packageName) { //Google docs disc


                        val to = "YourEmail@somewhere.com"
                        val subject = "Backup"
                        val message = "Your backup is attached"
                        //Intent inten1 = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        intent1.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
                        intent1.putExtra(Intent.EXTRA_SUBJECT, subject)
                        intent1.putExtra(Intent.EXTRA_TEXT, message)
                        intent1.putExtra("accountName", "zzz@gmail.com")
                        intent1.putExtra("deleteOriginalFile", true)
                        intent1.putExtra("documentTitle", "11111")


                        //intent1.setType(mime);//Not work if false mimitype

                        //Uri uRi = Uri.parse("file://" + file);
                        d("@@file://" + file + "  " + Uri.fromFile(file) + " " + uri.scheme)

                        //Exception intent1.setDataAndType(Uri.fromFile(file), "*/*");
                        intent1.putParcelableArrayListExtra(
                            Intent.EXTRA_STREAM,
                            ArrayList(Arrays.asList(uri, uri, uri))
                        )
                        intent1.setType("text/*")


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
                    } else if ("com.google.android.packageinstaller" == packageName) {
                        d("-------[apk INSTALLER]")
                        intent1.setDataAndType(uri, mime)
                    } else if (packageName!!.startsWith("com.dropbox.android")) {
                        d("@DROP_BOX@")
                        intent1.setDataAndType(uri, mime)
                    } else {
                        d("------------------------------------")
                        intent1.setDataAndType(uri, mime)
                    }
                    intent1.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    try {
                        val aa = intent1.resolveActivity(pm)
                        if (aa != null) {
                            d(aa.toString())
                            context.startActivity(intent1) //FileUriExposedException
                        } else {
                            d("@@@@@@")
                        }
                    } catch (rr9: ActivityNotFoundException) {
                        d("___E 2___ $mime $mime $packageName")

                        //                        if (intent2.resolveActivity(getPackageManager()) != null) {
//                            startActivity(intent2);
//                        } else {
//                            DLog.d("@@empty@@@");
//                        }
                    }
                } else {
                    Toast.makeText(context, "@ Try Latter", Toast.LENGTH_SHORT).show()
                }
            } catch (e: StringIndexOutOfBoundsException) {
                handleException(e)
            }
        }
    }

    fun makemimeAll() {
        val fake = defLocation()
        for (mime in mime_all) {
            makeMime(mime, null, null)
        }
    }

    fun makemimeProxy() {
        val aa = arrayOf(
            Proxy.PROXY_CHANGE_ACTION
        )
        val fake = defLocation()
        makeMime("*/*", null, aa)
    }

    companion object {
        var mime_all: MutableSet<String> = TreeSet() //mime_all

        init {
            mime_all.add("application/x-www-form-urlencoded")
            mime_all.add("application/vnd.android.package-archive") //.apk, if install
            mime_all.add("application/octet-stream")
            mime_all.add(MimeType.TEXT_PLAIN)
            mime_all.add("image/jpeg")
            mime_all.add("*/*")
            mime_all.add("image/*")
            mime_all.add("vnd.android.cursor.dir/*")
            mime_all.add("resource/folder")
            mime_all.add("text/csv")
            mime_all.add("vnd.android.document/directory")
            mime_all.add("vnd.android.cursor.dir/lysesoft.andexplorer.director")
        }
    }
}
