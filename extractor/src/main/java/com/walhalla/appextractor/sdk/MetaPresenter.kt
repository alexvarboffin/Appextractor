package com.walhalla.appextractor.sdk

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.walhalla.appextractor.activity.detail.DetailsF0
import com.walhalla.ui.DLog
import com.walhalla.appextractor.utils.IntentUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MetaPresenter @SuppressLint("PackageManagerGetSignatures") constructor(
    context: Context,
    meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta,
    view: DetailsF0.View
) :
    DetailsF0.Presenter {
    var icons: MutableMap<Int, Drawable?> = HashMap()

    private val view: DetailsF0.View

    private val context: Context
    private val packageManager: PackageManager
    private val meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta
    private var packageInfo: PackageInfo? = null

    fun doStuff(context: Context) {
        try {
            //LinearLayout card = this.mCard();
            //ViewGroup r10 = findViewById(R.id.scrollContainer);
            //int r10 = R.id.scrollContainer;
            //String m = packageManager.getInstallerPackageName(packageInfo.packageName);
            val data: MutableList<BaseViewModel> = ArrayList()
            if (packageInfo != null) {
                getAllStringResourcesFromPackage(context, packageInfo!!.packageName, data)
                meta7(data)
                colors7(data)
            }
            //activities(data);
            //services(data);
            view.swap(data)
        } catch (e: PackageManager.NameNotFoundException) {
            DLog.handleException(e)
        }
    }

    private fun colors7(data: MutableList<BaseViewModel>) {
        try {
            val resources = packageManager.getResourcesForApplication(meta.packageName)
            val fields = resources.javaClass.declaredFields

            for (field in fields) {
                if (field.type == com.walhalla.extractor.R.color::class.java) {
                    val resourceId = resources.getIdentifier(field.name, "color", meta.packageName)
                    val colorValue = resources.getColor(resourceId)
                    data.add(wrapV2Line("" + resourceId, "" + colorValue))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            DLog.handleException(e)
        }
    }

    private fun getAllStringResourcesFromPackage(
        context: Context,
        packageName: String,
        data: MutableList<BaseViewModel>
    ) {
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
            val o = HashSet<String>()
            run {
                o.add("app_name")
                o.add("app_id")
                o.add("firebase_database_url")
                o.add("gcm_defaultSenderId")
                o.add("google_api_key")
                o.add("google_app_id")
                o.add("google_crash_reporting_api_key")
                o.add("google_storage_bucket")

                o.add("secret_key")
                o.add("access_token")
                o.add("client_id")
                o.add("client_secret")
                o.add("encryption_key")
                o.add("signing_key")
                o.add("keystore_password")
                o.add("oauth_client_id")
                o.add("firebase_server_key")

                o.add("default_web_client_id")
                o.add("fb_app_id")
                o.add("fb_app_name")

                o.add("project_number")
                o.add("firebase_url")
                o.add("project_id")
                o.add("storage_bucket")
                o.add("package_name")
                o.add("api_key")
            }

            data.add(
                HeaderObject(
                    context.getString(com.walhalla.extractor.R.string.title_strings),
                    com.walhalla.extractor.R.drawable.ic_details_settings
                )
            )
            val packageManager = context.packageManager
            for (string in o) {
                val resources = packageManager.getResourcesForApplication(packageName)
                val stringId = resources.getIdentifier(string, "string", packageName)
                if (stringId != 0) {
                    val stringValue = resources.getString(stringId)
                    data.add(wrapV2Line("" + string, "" + stringValue))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            DLog.handleException(e)
        }
    }

    init {
        val manager = context.packageManager
        this.context = context
        try {
            this.packageInfo = manager.getPackageInfo(
                meta.packageName,
                (PackageManager.GET_PERMISSIONS
                        or PackageManager.GET_SIGNATURES
                        or PackageManager.GET_META_DATA
                        or PackageManager.GET_ACTIVITIES
                        or PackageManager.GET_SERVICES
                        or PackageManager.GET_PROVIDERS
                        or PackageManager.GET_RECEIVERS)

            )
        } catch (e: PackageManager.NameNotFoundException) {
            DLog.handleException(e)
        }
        this.view = view
        this.packageManager = manager
        this.meta = meta
    }

    private fun wrapV2Line(key: String, value: String): BaseViewModel {
        for ((key1, value1) in map) {
            if (key.startsWith(key1)) {
                return V2Line(key, "" + value, value1)
            }
        }
        return V2Line(key, "" + value)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun meta7(data: MutableList<BaseViewModel>) {
        //    <activity>*
//    <activity-alias>
//    <application>*
//    <provider>*
//    <receiver>*
//    <service>  *

        val applicationInfo =
            packageManager.getApplicationInfo(meta.packageName, PackageManager.GET_META_DATA)


        var metaData = applicationInfo.metaData
        //String applicationId = metaData.getString("com.google.android.gms.ads.APPLICATION_ID");
        if (metaData != null) {
            data.add(
                HeaderObject(
                    "Application MetaData" + DEVIDER_START + metaData.keySet().size + DEVIDER_END,
                    com.walhalla.extractor.R.drawable.ic_category_activity
                )
            )
            for (key in metaData.keySet()) {
                val value = metaData[key]
                data.add(wrapV2Line(key, "" + value))
            }
        }


        val packageInfo = packageManager.getPackageInfo(
            meta.packageName, (PackageManager.GET_META_DATA
                    or PackageManager.GET_ACTIVITIES
                    or PackageManager.GET_SERVICES
                    or PackageManager.GET_PROVIDERS
                    or PackageManager.GET_RECEIVERS)
        )

        val activities = packageInfo.activities
        val receivers = packageInfo.receivers

        val services = packageInfo.services
        val providers = packageInfo.providers

        val stringMap = toMap(activities)
        val map = toMap(receivers)

        if (!stringMap.isEmpty()) {
            data.add(
                HeaderObject(
                    "Activities MetaData" + DEVIDER_START + stringMap.size + DEVIDER_END,
                    com.walhalla.extractor.R.drawable.ic_category_activity
                )
            )
            for ((key, value) in stringMap) {
                data.add(wrapV2Line(key, "" + value))
            }
        }

        if (!map.isEmpty()) {
            data.add(
                HeaderObject(
                    "Receivers MetaData" + DEVIDER_START + map.size + DEVIDER_END,
                    com.walhalla.extractor.R.drawable.ic_category_receiver
                )
            )
            for ((key, value) in map) {
                data.add(wrapV2Line(key, "" + value))
            }
        }


        if (services != null && services.size > 0) {
            val hashMap: MutableMap<String, String> = HashMap()
            for (service in services) {
                metaData = service.metaData

                if (metaData != null) {
                    //data.add(new HeaderObject("Services MetaData" + DEVIDER_START + metaData.keySet().size() + DEVIDER_END, R.drawable.ic_category_activity));
                    for (key in metaData.keySet()) {
                        val value = metaData[key]
                        //data.add(wrapV2Line(key, "" + value));
                        hashMap[key] = value.toString()
                    }
                }
                //                else {
//                    mView.showError("No metadata found for the component", null);
//                }
            }
            if (!hashMap.isEmpty()) {
                data.add(
                    HeaderObject(
                        "Services MetaData" + DEVIDER_START + hashMap.size + DEVIDER_END,
                        com.walhalla.extractor.R.drawable.ic_category_services
                    )
                )
                for ((key, value) in hashMap) {
                    data.add(wrapV2Line(key, "" + value))
                }
            }
        }
        if (providers != null && providers.size > 0) {
            val stringMap1: MutableMap<String, String> = HashMap()
            for (provider in providers) {
                metaData = provider.metaData
                if (metaData != null) {
                    //data.add(new HeaderObject("Provider MetaData" + DEVIDER_START + metaData.keySet().size() + DEVIDER_END, R.drawable.ic_category_activity));
                    for (key in metaData.keySet()) {
                        val value = metaData[key]
                        //data.add(wrapV2Line(key, "" + value));
                        stringMap1[key] = value.toString()
                    }
                }
                //                else {
//                    mView.showError("No metadata found for the component", null);
//                }
            }

            if (!stringMap1.isEmpty()) {
                data.add(
                    HeaderObject(
                        "Providers MetaData" + DEVIDER_START + stringMap1.size + DEVIDER_END,
                        com.walhalla.extractor.R.drawable.ic_category_provider
                    )
                )
                for ((key, value) in stringMap1) {
                    data.add(wrapV2Line(key, "" + value))
                }
            }
        }
    }

    private fun toMap(activities: Array<ActivityInfo>?): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        if (activities != null && activities.size > 0) {
            for (activity in activities) {
                val metaData = activity.metaData
                if (metaData != null) {
//                    data.add(new HeaderObject(s + " MetaData" + DEVIDER_START + metaData.keySet().size() + DEVIDER_END,
//                            R.drawable.ic_category_activity));
                    for (key in metaData.keySet()) {
                        val value = metaData[key]
                        map[key] = value.toString()
                    }
                }
            }
        }
        return map
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
    private fun getDate(time: Long): String {
        try {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(time))
        } catch (ex: Exception) {
            return ""
        }
    }

    private fun permissionGranted(manager: PackageManager, permission: String): Boolean {
        return manager.checkPermission(
            permission,
            packageInfo!!.packageName
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun providers(
        providers: Array<ProviderInfo>?,
        pm: PackageManager,
        data: MutableList<BaseViewModel>
    ) {
        val length = if ((providers == null)) 0 else providers.size
        if (length > 0) {
            val o = HeaderCollapsedObject(
                (context.getString(com.walhalla.extractor.R.string.app_info_providers)
                        + DEVIDER_START
                        + length
                        + DEVIDER_END), com.walhalla.extractor.R.drawable.ic_category_provider
            )
            for (i in 0 until length) {
                val info = providers!![i]
                val granted = info.grantUriPermissions
                val label = info.loadLabel(pm)
                var drawable: Drawable?
                //if (ai.icon > 0) {
                drawable = icons[info.icon]
                if (drawable == null) {
                    drawable = info.loadIcon(pm)
                    icons[info.icon] = drawable
                }


                //}
                //FirebaseInitProvider
                //@int iconResource = info.getIconResource();
                //@int logoResource = info.getLogoResource(); == 0


                //info.pathPermissions;
                val uri = Uri.parse("content://" + info.authority + "/")
                DLog.d("@p@$uri")

                val line = ProviderLine(
                    drawable,
                    label.toString(),
                    info.name,
                    info.exported,
                    info.enabled,
                    uri.toString()
                )
                o.list.add(line)
            }
            data.add(o)
        } else {
            data.add(
                HeaderObject(
                    (context.getString(com.walhalla.extractor.R.string.app_info_providers)
                            + DEVIDER_START + length + DEVIDER_END), com.walhalla.extractor.R.drawable.ic_category_provider
                )
            )
        }
    }

    fun openSettingsRequest(context: Context) {
        if (!TextUtils.isEmpty(packageInfo!!.packageName)) {
            //app info
            IntentUtil.openSettingsForPackageName(context, packageInfo!!.packageName)
        }
    }


    fun onLaunchExportedService(class_name: String) {
        var exception: Exception? = null

        //background.systemjob.SystemJobService
        val intent = Intent()
        intent.setComponent(ComponentName(packageInfo!!.packageName, class_name))

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val c = context.applicationContext.startForegroundService(intent)
            } else {
                val c = context.startService(intent)
            }
        } catch (e: Exception) {
            exception = e
            DLog.d("[1," + packageInfo!!.packageName + "::" + class_name + DEVIDER_END + exception.localizedMessage)

            try {
                val c = context.startService(intent)
                exception = null
            } catch (e0: Exception) {
                exception = e0
                DLog.d("[2," + packageInfo!!.packageName + "::" + class_name + DEVIDER_END + e0.localizedMessage)
            }
        }
        if (exception != null) {
            Toast.makeText(context, "" + exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }


    private fun showErrorMessageDialog(context: Context, errorMessage: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Warning...")
            .setMessage(errorMessage)
            .setPositiveButton(
                "OK"
            ) { dialog: DialogInterface, id: Int ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }


    companion object {
        private const val DEVIDER_START = " ("
        private const val DEVIDER_END = ")"

        var map: MutableMap<String, Int> = HashMap()

        init {
            map["applovin."] = com.walhalla.extractor.R.drawable.ic_applovin
            map["com.google.firebase."] = com.walhalla.extractor.R.drawable.ic_firebase
            map["com.unity3d."] = com.walhalla.extractor.R.drawable.ic_unity3d
            map["com.facebook."] = com.walhalla.extractor.R.drawable.ic_facebook
        }
    }
}
