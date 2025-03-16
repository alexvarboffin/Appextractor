package com.walhalla.appextractor.activity.debug

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.walhalla.appextractor.activity.manifest.ManifestPresenterXml
import com.walhalla.appextractor.resources.StringItemViewModel
import com.walhalla.appextractor.utils.NetworkUtils
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.e
import com.walhalla.ui.DLog.handleException
import okhttp3.Request

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.reflect.Field
import java.net.UnknownHostException
import java.util.Locale

class PlaylistFragment : DemoFragment() {
    private var pm: PackageManager? = null


    private var am: AssetManager? = null
    private var resources: Resources? = null
    private var text: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return makeUI(requireContext())
    }

    private fun makeUI(context: Context): View {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = layoutParams


        val aa = arrayOf(
            ".JSON CHECKER",
            "VERSION_code",
            "version name",
            "packageName",
            "packageName[0]",
            "vpn check",
            "fined strings"
        )

        for (i in aa.indices) {
            val button2 = Button(context)
            button2.text = i.toString() + ". " + aa[i]
            val finalI = i
            button2.setOnClickListener { v: View? ->
                try {
                    handlePos(context, finalI)
                } catch (e: Exception) {
                    handleException(e)
                }
            }
            linearLayout.addView(button2)
        }

        text = TextView(context)
        linearLayout.addView(text)


        //List<StringItem> m = new ArrayList<>();
        //loadApplicationResources0(this, m, "com.ang.creditonline");
        //loadManifestContent("com.bendingspoons.thirtydayfitness");

// @@      if (a == null) {
// @@          pm = getPackageManager();
// @@          fullMeta(pm);
// @@      }
// @@      for (PackageMeta meta : b) {
// @@          findNetworkSecurityConfig(this, meta.packageName);
// @@      }

        //mmm();

        //org.iqiyi.plugin.sample.MainActivity
        //InstrActivityProxy2 extends InstrActivityProxy1
//        Intent intent = new Intent(this, InstrActivityProxy1.class);
//        this.startActivity(intent);
        return linearLayout
    }

    private fun handlePos(context: Context, i: Int) {
        if (a.isEmpty()) {
            pm = context.packageManager
            fullMeta(pm!!)
        }
        if (i == 0) {
            d("" + "Нажата кнопка: " + i + ", " + a.size)
            Thread {
                for (info in a) {
                    //                    if (info.packageName.equals("com.maxbetdeluxe.jack")) {
//                        try {
//                            striker(pm, info);
//                        } catch (PackageManager.NameNotFoundException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }

                    try {
                        striker(pm!!, info)
                    } catch (e: PackageManager.NameNotFoundException) {
                        throw RuntimeException(e)
                    }
                }
            }.start()
        } else if (i == 1) {
            for (meta in b) {
                d("@@@@" + meta.versionCode)
            }
        } else if (i == 2) {
            for (meta in b) {
                d("@@@@" + meta.versionName)
            }
        } else if (i == 3) {
            for (meta in b) {
                d("\t\t" + meta.packageName)
            }
        } else if (i == 4) {
            val hSet: MutableSet<String> = HashSet()
            for (meta in b) {
                hSet.add(meta.packageName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0])
            }
            for (s in hSet) {
                d("\t\t" + s)
            }
        } else if (i == 5) {
            Thread {
                for (info in a) {
                    try {
                        val am =
                            context.createPackageContext(info.packageName, 0).assets
                        val label = info.loadLabel(pm!!).toString()
                        treeViewer(am, "", label)
                    } catch (e: PackageManager.NameNotFoundException) {
                        throw RuntimeException(e)
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
            }.start()
        } else if (i == 6) {
            Thread {
//                for (PackageMeta meta : b) {
//                    DLog.d("\t\t" + meta.packageName);
//                }
                loadManifestContent(context, b[0].packageName)
            }.start()
        }
    }


    fun loadManifestContent(context: Context, packageName: String) {
        val initAM = am
        val initRes = resources
        try {
            val otherContext = context.createPackageContext(packageName, 0)
            am = otherContext.assets

            text!!.text = ("""${otherContext.cacheDir}
${otherContext.resources.displayMetrics} ${DisplayMetrics()} """ + otherContext
                .classLoader
                .getResource(
                    otherContext.javaClass.name
                        .replace('.', '/') + ".class"
                ).path)


            //resources = new Resources(am, getResources().getDisplayMetrics(), null);
            //resources = new Resources(am, new DisplayMetrics(), resources.getConfiguration());
            resources = otherContext.resources
        } catch (name: PackageManager.NameNotFoundException) {
            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
            am = initAM
            resources = initRes
        } catch (unexpected: RuntimeException) {
            d("@@@ error configuring for package: " + packageName + " " + unexpected.message)
            am = initAM
            resources = initRes
        }
        val m: List<StringItemViewModel> = ArrayList()
        //loadApplicationResources(this, m, getPackageName());
        loadApplicationResources0(context, m, packageName)
        //mView.showSuccess(m);
    }

    var aa: Array<String> = arrayOf(
        "app"
    )

    private fun loadApplicationResources0(
        context: Context, iconPackResources: List<StringItemViewModel>,
        packageName: String
    ) { /*from w w w.  j a  va 2s. co  m*/
        val drawableItems: Array<Field>
        try {
            val appContext = context.createPackageContext(
                packageName,  //Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
                0 //java.lang.ClassNotFoundException: com.bendingspoons.thirtydayfitness.R
            )
            val clazz = Class.forName("$packageName.R\$string", true, appContext.classLoader)
            drawableItems = clazz.fields

            for (f in drawableItems) {
                val name = f.name
                d("@@@@@@@@@@@@@@$name")
            }
        } catch (e: ClassNotFoundException) {
            d("@ww@$e")
            return
        } catch (e: Exception) {
            d("@ww@$e")
            return
        }
    }

    private fun loadApplicationResources(
        context: Context, iconPackResources: MutableList<StringItemViewModel>,
        packageName: String
    ) { /*from w w w.  j a  va 2s. co  m*/
        val drawableItems: Array<Field>
        try {
            val appContext = context.createPackageContext(
                packageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )
            drawableItems =
                Class.forName("$packageName.R\$string", true, appContext.classLoader).fields
        } catch (e: Exception) {
            d("@ww@$e")
            return
        }
        for (f in drawableItems) {
            var name = f.name
            val icon = name.lowercase(Locale.getDefault())
            name = name.replace("_".toRegex(), ".")
            var stringValue = ""
            val stringId = resources!!.getIdentifier(icon, "string", packageName)
            if (stringId != 0) {
                stringValue = resources!!.getString(stringId)
            }

            //iconPackResources.add(new ResItem(name + " " + icon + " " + stringValue, null));
            //@@iconPackResources.add(new StringItem(icon, stringValue, null));
            val activityIndex = name.lastIndexOf(".")
            if (activityIndex <= 0 || activityIndex == name.length - 1) {
                continue
            }

            val iconPackage = name.substring(0, activityIndex)
            if (TextUtils.isEmpty(iconPackage)) {
                continue
            }

            //iconPackResources.add(new ResItem(iconPackage + " " + icon, null));
            //@@iconPackResources.add(new StringItem(icon, stringValue, null));
            val iconActivity = name.substring(activityIndex + 1)
            if (TextUtils.isEmpty(iconActivity)) {
                continue
            }
            //iconPackResources.add(new ResItem(iconPackage + "." + iconActivity + " " + icon, null));
            iconPackResources.add(StringItemViewModel(icon, stringValue, null))

            for (s in aa) {
                if (stringValue.contains(s) || icon.contains(s)) {
                    println("########################$s")
                    break
                }
            }
        }
    }

    inner class QHendler {
        var code: Int = -1
        var url: String = "-1"
    }

    fun print(list: List<QHendler>, label: String, pName: String) {
        for (hendler in list) {
            var res = ""
            if (423 == hendler.code) {
                res = "deactivated"
                d(hendler.url + " " + res + " " + label)
            } else if (401 == hendler.code) {
                res = "PERMISSION DENIED"
                d(hendler.url + " " + res + " " + label)
            } else if (200 == hendler.code) {
                res = "@@@@@@@@ 200 ok @@@@@@@"
                e(hendler.url + " " + res + " " + label + " " + pName)
            } else if (404 == hendler.code) {
                res = "\"404 Not Found\""
                //@@@DLog.e(hendler.url + " " + res + " " + label);
            } else {
                res = "<--->" + hendler.code + "<--->"
                d(hendler.url + " " + res + " " + label)
            }
        }
        d("======================================")
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun striker(pm: PackageManager, info: ApplicationInfo) {
        val pn = info.packageName
        val resources = pm.getResourcesForApplication(pn)
        @SuppressLint("DiscouragedApi") val var0 =
            resources.getIdentifier("firebase_database_url", "string", pn)
        val var1 = resources.getIdentifier("project_id", "string", pn)
        val stringIdGoogle_storage_bucket =
            resources.getIdentifier("google_storage_bucket", "string", pn)

        val label = info.loadLabel(pm).toString()


        val resp1 = QHendler()

        val list: MutableList<QHendler> = ArrayList()

        var val0url = ""
        var stringValue0 = ""
        if (var0 != 0) {
            stringValue0 = resources.getString(var0)
            val0url = "$stringValue0/.json"
            if (!stringValue0.contains("youtube999")) {
                try {
                    val code = getResponseCode(val0url)
                    resp1.code = code
                    resp1.url = val0url
                    list.add(resp1)
                } catch (e: UnknownHostException) {
                    //DLog.handleException(e);
                } catch (e: Exception) {
                    handleException(e)
                }
            } else {
                d("SKIPPED: $val0url {$stringIdGoogle_storage_bucket}")
            }
        } else {
            //DLog.d("@@@@"+stringId);
            //@@@
            //@@@
        }


        //@@@@@@@@@@@@@@
        //https://dropbox-com-avian-chariot-819.firebaseio.com/.json
        //https://dropbox.com:avian-chariot-819.firebaseio.com/.json
        if (var1 != 0) {
            val resourcesString = resources.getString(var1)

            //DLog.d("stringIdGoogle_storage_bucket {" + stringIdGoogle_storage_bucket + "}");
            val clr = resourcesString.replace(":", "-").replace(".", "-")

            for (s in DemoData.firebaseHosts) {
                val urlA22 = s.replace("~", clr)
                if (urlA22 != val0url) {
                    try {
                        val code = getResponseCode(urlA22)
                        val resp2 = QHendler()
                        resp2.code = code
                        resp2.url = urlA22
                        list.add(resp2)
                    } catch (e: UnknownHostException) {
                        //DLog.handleException(e);
                    } catch (e: Exception) {
                        handleException(e)
                    }
                } else {
                    //DLog.d("SKIPPED: " + url + " {" + stringIdGoogle_storage_bucket + "}");
                }
            }
        } else {
            //DLog.d("@@@@"+stringId);
            //@@@
            //@@@
        }

        print(list, label, pn)
    }


    @Throws(IOException::class)
    private fun treeViewer(am: AssetManager, rootPath: String, label: String) {
        val resourceFiles =
            am.list(rootPath) // Получение списка всех ресурсов в корневом каталоге assets
        for (resourceFile in resourceFiles!!) {
            val fullPath = if (("" == rootPath)) resourceFile else "$rootPath/$resourceFile"

            if (!fullPath.isEmpty()) {
                try {
                    if (vpnFile(resourceFile) || vpn2file(resourceFile)) {
                        if (resourceFile.endsWith(".ovpn")) {
                            d("\uD83D\uDE80 $resourceFile <$label>")
                        } else {
                            d("$resourceFile <$label>")
                        }
                    }

                    //                    if (vpn2file(resourceFile)) {
//                        DLog.d(resourceFile + " <" + label + ">");
//                    }
                } catch (e: Exception) {
                    handleException(e)
                    d("{dir}$resourceFile")
                    treeViewer(am, fullPath, label)
                }
            }
        }
    }

    private fun vpn2file(resourceFile: String): Boolean {
        return resourceFile.endsWith("txt") || resourceFile.endsWith("conf")
    }

    private fun vpnFile(resourceFile: String): Boolean {
        return resourceFile.contains("vpn") && !resourceFile.startsWith("pie_") && !resourceFile.startsWith(
            "nopie_"
        )
    }

    companion object {
        //    private void mmm() {
        //
        //        Intent[] AUTO_START_OPPO = new Intent[]{};
        //
        //        if (Build.MANUFACTURER.equals("OPPO")) {
        //
        //            AUTO_START_OPPO = new Intent[]{
        //                    new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startup.StartupAppListActivity")),
        //                    new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupapp.StartupAppListActivity")),
        //                    new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
        //                    new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startup.StartupAppListActivity"))
        //            };
        //        } else if (Build.MANUFACTURER.equals("realme")) {
        //            AUTO_START_OPPO = new Intent[]{
        //                    new Intent().setComponent(new ComponentName("com.oppo.ota",
        //                            "com.oppo.otaui.activity.EntryActivity")),
        //            };
        //        }
        //        for (Intent intent : AUTO_START_OPPO) {
        //            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
        //                try {
        //                    startActivity(intent);
        //                    break;
        //                } catch (Exception e) {
        //                    Log.d("@@@@", "OPPO - Exception: " + e.toString());
        //                }
        //            }
        //        }
        //
        //        Uri contentUri = Uri.parse("content://com.oppo.ota/app_feature");
        //
        //        // Получите ContentResolver
        //        ContentResolver resolver = getContentResolver();
        //
        //        // Выполните запрос к контент-провайдеру
        //        try {
        //            Cursor cursor = resolver.query(contentUri, null, null, null, null);
        //            if (cursor != null) {
        //                try {
        //                    // Переместите курсор на первую строку
        //                    if (cursor.moveToFirst()) {
        //                        do {
        //                            // Получите данные из столбцов
        //                            String featureName = cursor.getString(cursor.getColumnIndex("_id"));
        //                            String featureValue = cursor.getString(cursor.getColumnIndex("_id"));
        //
        //                            // Выведите данные
        //                            Log.d("@@@MainActivity", "Feature Name: " + featureName + ", Feature Value: " + featureValue);
        //                        } while (cursor.moveToNext());
        //                    }
        //                } finally {
        //                    cursor.close();
        //                }
        //            }
        //        } catch (java.lang.SecurityException ex) {
        //            String title = "" + ex.getClass();
        //            showErrorMessageDialog(this, title, "Error: " + ex.getClass() + ", " + ex.getMessage());
        //        }
        //
        //    }
        @Throws(XmlPullParserException::class, IOException::class)
        fun parseXml(xrp: XmlResourceParser): List<String> {
            val list: MutableList<String> = ArrayList()
            var eventType = xrp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // for buffer
                if (eventType == XmlPullParser.START_TAG) {
                    val tagName = xrp.name
                    if ("domain" == tagName) {
                        val domain = xrp.nextText()
                        list.add(domain)
                    }
                }
                eventType = xrp.nextToken()
            }
            return list
        }

        private fun findNetworkSecurityConfig(context: Context, packageName: String) {
            var am: AssetManager?
            var resources: Resources?
            try {
                val otherContext = context.createPackageContext(packageName, 0)
                am = otherContext.assets
                resources = otherContext.resources
                val parser = am.openXmlResourceParser(ManifestPresenterXml.NETWORK_SECURITY_CONFIG)
                try {
                    val aa0 = ManifestPresenterXml.getXMLText(parser, resources).toString()
                    d(aa0)
                } catch (ioe: IOException) {
                    handleException(ioe)
                } catch (xppe: XmlPullParserException) {
                    handleException(xppe)
                }
            } catch (name: PackageManager.NameNotFoundException) {
                //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
                am = null
                resources = null
            } catch (unexpected: RuntimeException) {
                d("@@@ error configuring for package: " + packageName + " " + unexpected.message)
                am = null
                resources = null
            } catch (e: FileNotFoundException) {
                //DLog.d("FileNotFoundException: " + packageName + " " + e.getClass().getSimpleName());
            } catch (e: IOException) {
                d("@@@ error: " + packageName + " " + e.javaClass.simpleName)
            }
        }

        @Throws(IOException::class)
        fun getResponseCode(url: String): Int {
            val client = NetworkUtils.makeOkhttp()
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            return response.code
        }
    }
}