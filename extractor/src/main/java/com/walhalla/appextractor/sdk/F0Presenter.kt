package com.walhalla.appextractor.sdk


import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.StorageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.InstrumentationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.content.pm.ProviderInfo
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Process
import android.os.storage.StorageManager
import android.text.TextUtils
import android.text.format.Formatter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.walhalla.appextractor.activity.detail.DetailsF0
import com.walhalla.appextractor.utils.IntentUtil
import com.walhalla.extractor.R
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import org.qiyi.pluginlibrary.utils.ManifestParser
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri
import com.walhalla.appextractor.utils.FileUtil
import java.io.File


class F0Presenter @SuppressLint("PackageManagerGetSignatures") constructor(
    context: Context,
    meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta,
    view: DetailsF0.View
) :
    DetailsF0.Presenter {
    var icons: MutableMap<Int, Drawable?> = HashMap()

    private val view: DetailsF0.View

    private val myActivityContext: Context
    private val packageManager: PackageManager
    private val meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta

    private var targetPackageInfo: PackageInfo? = null

    init {
        val manager = context.packageManager
        this.myActivityContext = context
        try {
            this.targetPackageInfo = manager.getPackageInfo(
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
            handleException(e)
        }
        this.view = view
        this.packageManager = manager
        this.meta = meta
    }

    fun doStuff(context: Context) {
        try {
            val applicationInfo = packageManager.getApplicationInfo(meta.packageName, 0)
            val data: MutableList<BaseViewModel> =
                mmmm(context, applicationInfo, targetPackageInfo!!)
            view.swap(data)
        } catch (e: PackageManager.NameNotFoundException) {
            handleException(e)
        }
    }

    private fun mmmm(
        context: Context,
        applicationInfo: ApplicationInfo,
        targetPackageInfo: PackageInfo
    ): MutableList<BaseViewModel> {

        // Контекст для другого приложения
        var otherAppContext: Context? = null
        var otherAppPackageManager: PackageManager? = null
        try {
            otherAppContext = context.applicationContext.createPackageContext(
                meta.packageName,
                0 //Context.CONTEXT_IGNORE_SECURITY
                //Context.CONTEXT_INCLUDE_CODE//Requesting code from com.google.android.youtube (with uid 10162) to be run in process com.walhalla.appextractor (with uid 10730)
            )
        } catch (e: PackageManager.NameNotFoundException) {
            handleException(e)
        }
        if (otherAppContext != null) {
            otherAppPackageManager =
                otherAppContext.packageManager // Получаем PackageManager для другого приложения
        }


        //LinearLayout card = this.mCard();
        //ViewGroup r10 = findViewById(R.id.scrollContainer);
        //int r10 = R.id.scrollContainer;
        val data: MutableList<BaseViewModel> = ArrayList()
        base(applicationInfo, data, targetPackageInfo)
        activities(applicationInfo, data)
        instrumentations(targetPackageInfo.instrumentation, data)


        //BROWSABLE ACTIVITY
        val ia0 = Intent(Intent.ACTION_VIEW, "file:///sdcard".toUri())
        ia0.addCategory(Intent.CATEGORY_BROWSABLE)
        ia0.setPackage(targetPackageInfo.packageName)

        val aaas = packageManager.queryIntentActivities(ia0, 0)
        for (info in aaas) {
            data.add(CertLine("BROWSABLE->", "" + info.activityInfo.name))
        }

        val i0 = Intent(Intent.ACTION_VIEW)
        i0.setDataAndType(Uri.parse("/sdcard/"), "*/*")
        i0.addCategory(Intent.CATEGORY_BROWSABLE)
        i0.setPackage(targetPackageInfo.packageName)

        val infos = packageManager.queryIntentActivities(i0, 0)
        for (info in infos) {
            data.add(CertLine("BROWSABLE->", "" + info.activityInfo.name))
        }


        //END_BROWSABLE ACTIVITY
        val getServicesUseCase = GetServicesUseCase(packageManager, context)
        data.addAll(getServicesUseCase.execute(targetPackageInfo, icons))

        receivers(targetPackageInfo, packageManager, data)
        providers(targetPackageInfo.providers, packageManager, data)


        //apk metadata applicationInfo.flags
        //packageInfo.flags


//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                boolean flag_uses_cleartext_traffic = (applicationInfo.flags & ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC) == ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC;
//                data.add(new CertLine("FLAG_USES_CLEARTEXT_TRAFFIC", "" + flag_uses_cleartext_traffic));
//            }

        //boolean debuggable = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
        //data.add(new CertLine("FLAG_DEBUGGABLE", "" + debuggable));
        data.add(FlagzObject(applicationInfo.flags))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //getClass().getName() + '@' + Integer.toHexString(hashCode())

            var txt = "NOT_SET"
            if (applicationInfo.category != -1) {
                txt = ApplicationInfo.getCategoryTitle(context, applicationInfo.category)
                    .toString() + " (cat_id=>" + applicationInfo.category + ")"
            }
            data.add(DirLine("The category of this app: ", txt, R.drawable.ic_category))
        }

        //            compatibleWidthLimitDp
//            The maximum smallest screen width the application is designed for.
        data.add(CertLine("Min", "" + applicationInfo.compatibleWidthLimitDp))
        data.add(CertLine("Max", "" + applicationInfo.largestWidthLimitDp))
        data.add(
            CertLine(
                "requiresSmallestWidthDp", ""
                        + applicationInfo.requiresSmallestWidthDp
            )
        )


        val count1 =
            if (applicationInfo.sharedLibraryFiles == null) 0 else applicationInfo.sharedLibraryFiles.size
        if (count1 > 0) {
            val o = HeaderCollapsedObject(
                (context.getString(R.string.app_info_sharedlibraryfiles)
                        + DEVIDER_START + count1 + DEVIDER_END), R.drawable.ic_lib
            )
            for (i in applicationInfo.sharedLibraryFiles.indices) {
                val info = applicationInfo.sharedLibraryFiles[i]
                o.list.add(CertLine("", "" + info))
            }
            data.add(o)
        } else {
            data.add(
                HeaderObject(
                    (context.getString(R.string.app_info_sharedlibraryfiles)
                            + DEVIDER_START + count1 + DEVIDER_END), R.drawable.ic_lib
                )
            )
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            data.add(CertLine("CompileSdkVersion", "" + applicationInfo.compileSdkVersion))
            data.add(
                CertLine(
                    "CompileSdkVersionCodename",
                    "" + applicationInfo.compileSdkVersionCodename
                )
            )
        }

        data.add(CertLine("DescriptionRes", "" + applicationInfo.descriptionRes))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            data.add(CertLine("AppComponentFactory", applicationInfo.appComponentFactory ?: "None"))
        }

        applicationInfo.backupAgentName?.let {
            data.add(CertLine("BackupAgentName", it))
        }

        data.add(
            CertLine(
                "ManageSpaceActivityName",
                applicationInfo.manageSpaceActivityName ?: "Unknown"
            )
        )
        //
        data.add(CertLine("ProcessName", applicationInfo.processName))


        val appName0 =
            if ((applicationInfo.className == null)) "NOT_USED" else applicationInfo.className
        data.add(CertLine("Application Class", appName0))


        data.add(
            HeaderObject(
                context.getString(R.string.app_info_installation),
                R.drawable.ic_base_info
            )
        )
        data.add(SimpleLine(R.string.app_info_enable, applicationInfo.enabled.toString()))

        val installerPackageName = packageManager.getInstallerPackageName(
            targetPackageInfo.packageName
        ) ?: "Unknown"
        data.add(
            SimpleLine(
                R.string.app_info_installation_source,
                "" + handleInstallSource(packageManager, installerPackageName)
            )
        )


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
            val granted = hasPermissionPackageUsageStats()
            if (granted) {
                getStorageStats(data, targetPackageInfo.packageName)
            } else {
                view.snack()
            }
        }
        if (targetPackageInfo.signatures != null) {
            data.add(
                HeaderObject(
                    context.getString(R.string.app_info_certificate),
                    R.drawable.ic_cert
                )
            )
            getSignaturesInfo(data, targetPackageInfo.signatures)
        }

        //"android"
        //PackageInfo packageInfo0 = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

        //Optional name of a permission required to be able to access this application's components.
//            if (targetPackageInfo.permissions != null) {
//                // For each defined permission
//                for (permission in packageInfo.permissions) {
//                    val protectionLevelKey = permission.protectionLevel
//                    val protectionLevel = protectionLevelToString(protectionLevelKey)
//                    d(permission.name + " " + protectionLevel)
//                }
//            }
        //permissions
        //запрошенные разрешения
        //uses-permission
        if (targetPackageInfo.requestedPermissions != null) {
            data.add(
                HeaderObject(
                    context.getString(R.string.app_info_permissions),
                    R.drawable.ic_base_info
                )
            )
            val permissions = targetPackageInfo.requestedPermissions
            for (permission in permissions!!) {
                val isGranted = permissionGranted(packageManager, permission)
                val p0 = permission.replace("android.permission.", "")
                var permissionInfo: PermissionInfo? = null
                var protectionLevel = -1
                try {
                    permissionInfo = if (otherAppPackageManager != null) {
                        otherAppPackageManager.getPermissionInfo(
                            permission,
                            PackageManager.GET_META_DATA
                        )
                    } else {
                        packageManager.getPermissionInfo(
                            permission,
                            PackageManager.GET_META_DATA
                        )
                    }
                    if (permissionInfo != null) {
                        protectionLevel = permissionInfo.protectionLevel
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    //DLog.d("www "+e.getClass()+" | "+e.getLocalizedMessage());
                }

                data.add(PermissionLine(p0, isGranted, protectionLevel))
            }
        }
        if (targetPackageInfo.reqFeatures != null) {
            data.add(
                HeaderObject(
                    context.getString(R.string.app_info_features),
                    R.drawable.ic_base_info
                )
            )
            for (feature in targetPackageInfo.reqFeatures!!) {
                data.add(CertLine(feature.name, "" + feature.flags))
            }
        }

        return data
    }

    private fun instrumentations(infos: Array<InstrumentationInfo>?, data: List<BaseViewModel>) {
        val count0 = if ((infos == null)) 0 else infos.size
        val categoryName = myActivityContext.getString(R.string.app_info_instrumentation)
        if (count0 > 0) {
            val o = HeaderCollapsedObject(
                (categoryName
                        + DEVIDER_START + count0 + DEVIDER_END), R.drawable.ic_category_receiver
            )
            for (i in infos!!.indices) {
                val info = infos[i]
            }
        }
    }


    private fun receivers(
        info: PackageInfo,
        pm: PackageManager,
        data: MutableList<BaseViewModel>
    ) {

        val infos: Array<ActivityInfo>? = info.receivers

        //RECEIVERS
        val count0 = if ((infos == null)) 0 else infos.size
        val categoryName = myActivityContext.getString(R.string.app_info_receivers)
        if (count0 > 0) {
            val o = HeaderCollapsedObject(
                (categoryName
                        + DEVIDER_START + count0 + DEVIDER_END), R.drawable.ic_category_receiver
            )
            for (i in infos!!.indices) {
                val info = infos[i]

                val label = info.loadLabel(pm)
                var drawable: Drawable?
                //if (ai.icon > 0) {
                drawable = icons[info.icon]
                if (drawable == null) {
                    drawable = info.loadIcon(pm)
                    icons[info.icon] = drawable
                }

                //}
                val receiverName = info.name

                //DLog.d("{} @@@@@@@@@@@@@@@@@" + receiverName);

                // {*} Получаем Intent Filters для приемника
                val intent = Intent()
                intent.setClassName(targetPackageInfo!!.packageName, receiverName)
                val resolveInfoList = pm.queryBroadcastReceivers(intent, 0)
                if (resolveInfoList != null && resolveInfoList.size > 0) {
                    for (resolveInfo in resolveInfoList) {
                        //ActivityInfo activityInfo = resolveInfo.activityInfo;
                        val intentFilter = resolveInfo.filter


                        if (intentFilter != null) {
                            // Выводим информацию о каждом Intent Filter
                            d("{} @@@ Intent Filter Priority: " + intentFilter.priority)

                            //, категориях (categories), типах данных (data) и т. д.
//                            int actions = intentFilter.countCategories();
//                            int actions = intentFilter.countDataTypes();

//                            for (int k = 0; k < intentFilter.countActions(); k++) {
//                                intentFilter.getAction(k);
//                            }
                        } else {
                            d("{} NO FILTER @@@" + targetPackageInfo!!.packageName + "//" + receiverName)
                        }
                    }
                } else {
                    d("{} @@@@@@@@@@@@@@@@@")
                }

                o.list.add(
                    ReceiverLine(
                        drawable,
                        label.toString(),
                        receiverName,
                        info.exported,
                        info.enabled,
                        targetPackageInfo!!.packageName
                    )
                )
            }
            data.add(o)
        } else {
            data.add(
                HeaderObject(
                    (categoryName
                            + DEVIDER_START + count0 + DEVIDER_END), R.drawable.ic_category_receiver
                )
            )
        }
        //END_RECEIVERS
    }


    private fun base(
        applicationInfo: ApplicationInfo,
        data: MutableList<BaseViewModel>,
        targetPackageInfo: PackageInfo
    ) {
        data.add(
            HeaderObject(
                myActivityContext.getString(R.string.app_info_basic),
                R.drawable.ic_details_settings
            )
        )
        data.add(SimpleLine(R.string.app_info_version, targetPackageInfo.versionName ?: ""))
        data.add(
            SimpleLine(
                R.string.app_info_version_code,
                targetPackageInfo.versionCode.toString()
            )
        )

        data.add(SimpleLine(R.string.app_info_package_name, targetPackageInfo.packageName))


        data.add(
            SimpleLine(
                R.string.app_info_target_sdk,
                applicationInfo.targetSdkVersion.toString()
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data.add(
                SimpleLine(
                    R.string.app_info_min_sdk,
                    applicationInfo.minSdkVersion.toString()
                )
            )
        }

        data.add(
            SimpleLine(
                R.string.app_info_first_install_time, getDate(
                    targetPackageInfo.firstInstallTime
                )
            )
        )
        data.add(
            SimpleLine(
                R.string.app_info_last_update, getDate(
                    targetPackageInfo.lastUpdateTime
                )
            )
        )
        data.add(SimpleLine(R.string.app_info_uid, "" + applicationInfo.uid))


        //=================================================
        prettyDir(applicationInfo.nativeLibraryDir, "NativeLibraryDir", data)
        //=================================================

        val sourceDir = applicationInfo.sourceDir
        val extractedPackageName =
            sourceDir.substring(sourceDir.lastIndexOf("/") + 1, sourceDir.lastIndexOf("."))


        // Строим путь к папке данных приложения
        val dataDirPath = "/data/data/" + meta.packageName
        //=================================================
        prettyDir(dataDirPath, "dataDirPath", data)
        //=================================================

        data.add(InfoApkLine("sourceDir", sourceDir, R.drawable.ic_folder_blue_36dp))
        data.add(
            InfoApkLine(
                "publicSourceDir",
                applicationInfo.publicSourceDir,
                R.drawable.ic_folder_blue_36dp
            )
        )

        //=================================================
        prettyDir(applicationInfo.dataDir, "dataDir", data)
        //=================================================


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data.add(
                DirLine(
                    "deviceProtectedDataDir",
                    applicationInfo.deviceProtectedDataDir,
                    R.drawable.ic_folder_blue_36dp
                )
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (applicationInfo.splitPublicSourceDirs != null) {
                for (splitPath in applicationInfo.splitPublicSourceDirs!!) {
                    data.add(InfoApkLine("Split File", splitPath, R.drawable.ic_split))
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && applicationInfo.splitNames != null) {
            data.add(
                DirLine(
                    "splitNames",
                    "" + applicationInfo.splitNames.contentToString(),
                    R.drawable.ic_split
                )
            )
        }


        //Intent count0 = getPackageManager().getLaunchIntentForPackage_DEPRECATED(packageInfo.packageName);
    }

    private fun prettyDir(nativeLibraryDir: String, title: String, data: MutableList<BaseViewModel>) {
        val m = File(nativeLibraryDir)
        val files = m.listFiles()
        if (files != null && files.size > 0) {
            val hobject = HeaderCollapsedObject(
                (
                        "$title ${nativeLibraryDir}" + DEVIDER_START + files.size + DEVIDER_END),
                R.drawable.ic_category_activity
            )
            files.forEach {
                val x=File(m, it.name)
                hobject.list.add(
                    InfoApkLine(x.absolutePath, FileUtil.getFileSizeMegaBytes(x), R.drawable.ic_baseline_text_snippet_24)
                )
            }
            data.add(hobject)
        } else {
            if(files==null){
                data.add(
                    DirLine(
                        "$title ${nativeLibraryDir}", nativeLibraryDir,
                        R.drawable.ic_folder_blue_36dp
                    )
                )
            }else{
                data.add(
                    DirLine(
                        "$title ${nativeLibraryDir}", files.contentToString(),
                        R.drawable.ic_folder_blue_36dp
                    )
                )
            }
        }
    }


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
            targetPackageInfo!!.packageName
        ) == PackageManager.PERMISSION_GRANTED
    }

    //android.permission.PACKAGE_USAGE_STATS
    @RequiresApi(Build.VERSION_CODES.O) //26
    private fun getStorageStats(data: MutableList<BaseViewModel>, packageName: String) {
        try {
            val stats =
                (myActivityContext.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager)
                    .queryStatsForPackage(
                        StorageManager.UUID_DEFAULT,
                        packageName,
                        Process.myUserHandle()
                    )


            var cachePath = "" //Ok
            var filesPath = ""
            var externalFilesPath = ""

            var otherAppContext: Context? = null

            try {
                otherAppContext = myActivityContext.applicationContext.createPackageContext(
                    meta.packageName,
                    0 //Context.CONTEXT_IGNORE_SECURITY
                    //Context.CONTEXT_INCLUDE_CODE//Requesting code from com.google.android.youtube (with uid 10162) to be run in process com.walhalla.appextractor (with uid 10730)
                )
            } catch (e: PackageManager.NameNotFoundException) {
                handleException(e)
            }
            if (otherAppContext != null) {
                val cacheDir = otherAppContext.cacheDir
                cachePath = cacheDir.absolutePath
                val filesDir = otherAppContext.filesDir //data/data
                filesPath = filesDir.absolutePath

                //filesPath = filesDir.getAbsolutePath() + " | " + Arrays.toString(filesDir.listFiles());
                //File f0 = new File("/data/data/" + packageName);
                //filesPath = f0.getAbsolutePath() + " | " + Arrays.toString(f0.listFiles());
                val externalFilesDir = otherAppContext.getExternalFilesDir(null)
                externalFilesPath = if ((externalFilesDir != null))
                    externalFilesDir.absolutePath
                else
                    "External storage is not available."
            } else {
                val cacheDir = myActivityContext.cacheDir
                cachePath = cacheDir.absolutePath
                val filesDir = myActivityContext.filesDir //data/data
                filesPath = filesDir.absolutePath

                // Получаем путь к папке файлов приложения на внешнем хранилище (если оно доступно)
                val externalFilesDir = myActivityContext.getExternalFilesDir(null)
                externalFilesPath = if ((externalFilesDir != null))
                    externalFilesDir.absolutePath
                else
                    "External storage is not available."
            }


//            Log.d("Cache directory", cachePath);
//            Log.d("Files directory", filesPath);
//            Log.d("External files directory", externalFilesPath);
            val appBytes = stats.appBytes
            val cacheBytes = stats.cacheBytes
            val dataBytes = stats.dataBytes
            data.add(SimpleLine(R.string.app_info_apk, ca(appBytes)))
            data.add(SimpleLine(R.string.app_info_data, filesPath + " " + ca(dataBytes)))
            data.add(SimpleLine(R.string.app_info_cache, cachePath + " " + ca(cacheBytes)))
            data.add(SimpleLine(R.string.app_info_externalFilesPath, externalFilesPath))
        } catch (e: Exception) {
            handleException(e)
        }
    }


    private fun ca(cacheBytes: Long): String {
        return Formatter.formatShortFileSize(myActivityContext, cacheBytes)
    }

    //21
    //https://www.google.com/search?client=firefox-b-d&q=content%3A%2F%2Fsms%2Finbox
    private fun hasPermissionPackageUsageStats(): Boolean {
        val appOpsManager =
            myActivityContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            myActivityContext.packageName
        )

        return if (mode == AppOpsManager.MODE_DEFAULT) {
            myActivityContext.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            (mode == AppOpsManager.MODE_ALLOWED)
        }
    }

    private fun providers(
        providers: Array<ProviderInfo>?,
        pm: PackageManager,
        data: MutableList<BaseViewModel>
    ) {
        val length = if ((providers == null)) 0 else providers.size
        if (length > 0) {
            val o = HeaderCollapsedObject(
                (myActivityContext.getString(R.string.app_info_providers)
                        + DEVIDER_START
                        + length
                        + DEVIDER_END), R.drawable.ic_category_provider
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
                d("@p@$uri")

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
                    (myActivityContext.getString(R.string.app_info_providers)
                            + DEVIDER_START + length + DEVIDER_END), R.drawable.ic_category_provider
                )
            )
        }
    }

    private fun getSignaturesInfo(
        data: MutableList<BaseViewModel>,
        signatureArr: Array<Signature>?
    ) {
        if (signatureArr == null || signatureArr.size <= 0) {
            data.add(
                SimpleLine(
                    R.string.app_info_certificate,
                    myActivityContext.getString(R.string.unknown)
                )
            )
            return
        }
        try {
            for (signature in signatureArr) {
                // Сертификат X509, X.509 - очень распространенный формат сертификата

                val x509Certificate = CertificateFactory.getInstance("X.509").generateCertificate(
                    ByteArrayInputStream(signature.toByteArray())
                ) as X509Certificate
                data.add(
                    SimpleLine(
                        R.string.app_info_serial_number,
                        x509Certificate.serialNumber.toString()
                    )
                )
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
                val aa = x509Certificate.issuerX500Principal.toString()

                d("@@" + targetPackageInfo!!.packageName)
                d(
                    ("@@" + aa + " " + x509Certificate.serialNumber.toString() + "||"
                            + signature.hashCode())
                )

                val mm = aa.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in mm.indices) {
                    val aaa =
                        mm[i].trim { it <= ' ' }.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    if (aaa.size == 2) {
                        data.add(CertLine(aaa[0], aaa[1]))
                    }
                }
                val algorithms = arrayOf(
                    "MD5",
                    "SHA1",
                    "SHA256",  //"AES",
                    //"HmacMD5"

                )
                for (tpye in algorithms) {
                    val md = MessageDigest.getInstance(tpye)
                    // Получаем открытый ключ
                    val publicKey = md.digest(x509Certificate.encoded)
                    // Преобразование байта в шестнадцатеричный формат
                    val hexString = byte2HexFormatted(publicKey)
                    data.add(CertLine(tpye, hexString))
                }

                //New Line
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String h = Base64.encodeToString(md.digest(), Base64.DEFAULT);
//                data.add(new CertLine("MY KEY HASH:", h));
            }
        } catch (unused: Exception) {
        }
    }

    private fun handleInstallSource(packageManager: PackageManager, str: String): String {
        if (TextUtils.isEmpty(str)) {
            return myActivityContext.getString(R.string.unknown)
        }
        if ("com.android.vending" == str) {
            return myActivityContext.getString(R.string.app_referer_google_play)
        }
        if ("com.amazon.venezia" == str) {
            return myActivityContext.getString(R.string.app_referer_amazon)
        }
        return try {
            packageManager.getApplicationInfo(str, 0).loadLabel(packageManager).toString()
        } catch (unused: PackageManager.NameNotFoundException) {
            str
        }
    }


    fun openSettingsRequest(context: Context?) {
        if (!TextUtils.isEmpty(targetPackageInfo!!.packageName)) {
            //app info
            IntentUtil.openSettingsForPackageName(context!!, targetPackageInfo!!.packageName)
        }
    }


    fun onLaunchExportedService(class_name: String) {
        var exception: Exception? = null

        //background.systemjob.SystemJobService
        val intent = Intent()
        intent.setComponent(ComponentName(targetPackageInfo!!.packageName, class_name))

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val c = myActivityContext.applicationContext.startForegroundService(intent)
            } else {
                val c = myActivityContext.startService(intent)
            }
        } catch (e: Exception) {
            exception = e
            d("[1," + targetPackageInfo!!.packageName + "::" + class_name + DEVIDER_END + exception.localizedMessage)

            try {
                val c = myActivityContext.startService(intent)
                exception = null
            } catch (e0: Exception) {
                exception = e0
                d("[2," + targetPackageInfo!!.packageName + "::" + class_name + DEVIDER_END + e0.localizedMessage)
            }
        }
        if (exception != null) {
            Toast.makeText(myActivityContext, "" + exception.localizedMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun activities(applicationInfo: ApplicationInfo, data: MutableList<BaseViewModel>) {
        val list: MutableList<ActivityLine> = ArrayList()
        val map = HashMap<String, ActivityLine>()

        val sourceDir = applicationInfo.sourceDir
        val parser = ManifestParser(myActivityContext, sourceDir)
        val activities = parser.activities

        for (activity in activities) {
            val activityLine = ActivityLine()
            activityLine.className = activity.className
            activityLine.intentFilters = activity.intentFilters
            map[activity.className] = activityLine
        }
        var arrayOfActivityInfos = targetPackageInfo?.activities ?: emptyArray()
        val actzz =
            if ((targetPackageInfo!!.activities == null)) 0 else arrayOfActivityInfos.size
        val app_info_activities = myActivityContext.getString(R.string.app_info_activities)

        if (actzz > 0) {
            val hobject = HeaderCollapsedObject(
                (app_info_activities
                        + DEVIDER_START + actzz + DEVIDER_END), R.drawable.ic_category_activity
            )
            if (arrayOfActivityInfos != null) {
                for (activityInfo in arrayOfActivityInfos) {
//                    data.add(new CertLine("Activities" + DEVIDER_START + packageInfo.activities.length + DEVIDER_END,
//                            "" + activityInfo.name));
                    //new CertLine("Activity" + DEVIDER_START + activities.length + DEVIDER_END, "" + activityInfo.name);
                    val label = activityInfo.loadLabel(packageManager)
                    var drawable: Drawable?
                    //if (activityInfo.icon > 0) {
                    drawable = icons[activityInfo.icon]
                    if (drawable == null) {
                        drawable = activityInfo.loadIcon(packageManager)
                        icons[activityInfo.icon] = drawable
                    }

                    //}
                    //drawable = activityInfo.loadIcon(0);- return default icon
                    //CharSequence label = activityInfo.loadLabel(0);- return default label

                    var activityLine: ActivityLine? = map[activityInfo.name]
                    val launchMode = activityInfo.launchMode
                    if (activityLine == null) {
                        activityLine = ActivityLine()
                    }
                    activityLine.label = label.toString()
                    activityLine.exported = activityInfo.exported
                    activityLine.icon = drawable
                    activityLine.className = activityInfo.name
                    list.add(activityLine)
                }
                hobject.list.clear()
                hobject.list.addAll(list)
            }
            data.add(hobject)
        } else {
            data.add(
                HeaderObject(
                    (app_info_activities
                            + DEVIDER_START + actzz + DEVIDER_END), R.drawable.ic_category_activity
                )
            )
        }


        //LAUNCH ACTIVITY {ACTION_MAIN}
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(targetPackageInfo!!.packageName)
        val appList = packageManager.queryIntentActivities(intent, 0)

        for (info in appList) {
            data.add(
                DirLine(
                    "ACTION_MAIN, CATEGORY_LAUNCHER",
                    "" + info.activityInfo.name,
                    R.drawable.ic_item_key
                )
            )
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

    companion object {
        const val DEVIDER_START = " ("
        const val DEVIDER_END = ")"

        // Вот шестнадцатеричное преобразование полученного кода
        private fun byte2HexFormatted(arr: ByteArray): String {
            val str = StringBuilder(arr.size * 2)
            for (i in arr.indices) {
                var h = Integer.toHexString(arr[i].toInt())
                val l = h.length
                if (l == 1) h = "0$h"
                if (l > 2) h = h.substring(l - 2, l)
                str.append(h.uppercase(Locale.getDefault()))
                if (i < (arr.size - 1)) str.append(':')
            }
            return str.toString()
        }
    }
}