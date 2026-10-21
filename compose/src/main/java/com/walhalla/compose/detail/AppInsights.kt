package com.walhalla.compose.detail

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.FeatureInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.os.Process
import android.os.storage.StorageManager
import androidx.annotation.StringRes
import com.walhalla.compose.R
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile

enum class InstallSource(@param:StringRes val labelRes: Int) {
    PlayStore(R.string.src_play),
    AmazonStore(R.string.src_amazon),
    Sideload(R.string.src_sideload),
    System(R.string.src_system),
    OtherStore(R.string.src_other),
}

/** Cheap per-app facts for list filters (no zip / storage I/O). */
data class AppTraits(
    val tvLauncher: Boolean,
    val installSource: InstallSource,
)

/** Detail-only facts shown in INFO overview (phone card / TV hero) and in the report. */
data class AppInsights(
    val tvLauncher: Boolean,
    val touchscreenRequired: Boolean,
    val abis: List<String>,
    val installSource: InstallSource,
    val installerPackage: String?,
    val installerLabel: String?,
    val debuggable: Boolean,
    val permsRequested: Int,
    val permsGranted: Int,
    val permsDangerous: Int,
    val splitNames: List<String>,
    val signerSha256: String?,
    val apkBytes: Long?,
    val dataBytes: Long?,
    val cacheBytes: Long?,
)

private val PLAY_INSTALLERS = setOf("com.android.vending", "com.google.android.feedback")
private val AMAZON_INSTALLERS = setOf("com.amazon.venezia", "com.amazon.appmanager")
private val PACKAGE_INSTALLERS = setOf(
    "com.android.packageinstaller",
    "com.google.android.packageinstaller",
    "com.android.shell",
    "adb",
)

fun installerPackageOf(pm: PackageManager, packageName: String): String? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        pm.getInstallSourceInfo(packageName).installingPackageName
    } else {
        @Suppress("DEPRECATION")
        pm.getInstallerPackageName(packageName)
    }
}.getOrNull()

fun classifyInstallSource(installer: String?, isSystem: Boolean): InstallSource = when {
    installer in PLAY_INSTALLERS -> InstallSource.PlayStore
    installer in AMAZON_INSTALLERS -> InstallSource.AmazonStore
    installer == null && isSystem -> InstallSource.System
    installer == null || installer in PACKAGE_INSTALLERS -> InstallSource.Sideload
    else -> InstallSource.OtherStore
}

fun loadAppTraits(pm: PackageManager, appInfo: ApplicationInfo): AppTraits {
    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    val tv = runCatching { pm.getLeanbackLaunchIntentForPackage(appInfo.packageName) != null }
        .getOrDefault(false)
    return AppTraits(tv, classifyInstallSource(installerPackageOf(pm, appInfo.packageName), isSystem))
}

/** Blocking (zip scan + binder calls) — call off the main thread. */
fun loadAppInsights(context: Context, packageName: String): AppInsights? = runCatching {
    val pm = context.packageManager
    @Suppress("DEPRECATION")
    val signingFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        PackageManager.GET_SIGNING_CERTIFICATES
    } else {
        PackageManager.GET_SIGNATURES
    }
    val flags = PackageManager.GET_PERMISSIONS or PackageManager.GET_CONFIGURATIONS or signingFlag
    val info = pm.getPackageInfo(packageName, flags)
    val appInfo = info.applicationInfo ?: return null
    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    val installer = installerPackageOf(pm, packageName)
    val installerLabel = installer?.let { pkg ->
        runCatching { pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString() }.getOrNull()
    }
    val (granted, dangerous) = permissionCounts(pm, info)
    val storage = storageBytes(context, packageName)

    AppInsights(
        tvLauncher = pm.getLeanbackLaunchIntentForPackage(packageName) != null,
        touchscreenRequired = touchscreenRequired(info),
        abis = nativeAbis(appInfo),
        installSource = classifyInstallSource(installer, isSystem),
        installerPackage = installer,
        installerLabel = installerLabel,
        debuggable = (appInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
        permsRequested = info.requestedPermissions?.size ?: 0,
        permsGranted = granted,
        permsDangerous = dangerous,
        splitNames = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appInfo.splitNames?.toList().orEmpty()
        } else {
            emptyList()
        },
        signerSha256 = signerSha256(info),
        apkBytes = storage?.get(0),
        dataBytes = storage?.get(1),
        cacheBytes = storage?.get(2),
    )
}.getOrNull()

/** Missing `<uses-feature touchscreen>` means Play treats it as required (not installable on TV). */
private fun touchscreenRequired(info: PackageInfo): Boolean {
    val feature = info.reqFeatures?.firstOrNull { it.name == "android.hardware.touchscreen" }
        ?: return true
    return (feature.flags and FeatureInfo.FLAG_REQUIRED) != 0
}

private fun nativeAbis(appInfo: ApplicationInfo): List<String> {
    val apks = buildList {
        add(appInfo.sourceDir)
        appInfo.splitSourceDirs?.let { addAll(it) }
    }
    val abis = sortedSetOf<String>()
    for (path in apks) {
        runCatching {
            ZipFile(path).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val name = entries.nextElement().name
                    if (name.startsWith("lib/") && name.endsWith(".so")) {
                        name.split('/').getOrNull(1)?.let(abis::add)
                    }
                }
            }
        }
    }
    if (abis.isEmpty()) {
        val libDir = appInfo.nativeLibraryDir?.let(::File)
        if (libDir != null && !libDir.list().isNullOrEmpty()) abis.add(libDir.name)
    }
    return abis.toList()
}

private fun permissionCounts(pm: PackageManager, info: PackageInfo): Pair<Int, Int> {
    val names = info.requestedPermissions ?: return 0 to 0
    val flags = info.requestedPermissionsFlags
    var granted = 0
    var dangerous = 0
    names.forEachIndexed { i, name ->
        if (flags != null && (flags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) granted++
        val protection = runCatching {
            val p = pm.getPermissionInfo(name, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                p.protection
            } else {
                @Suppress("DEPRECATION")
                p.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE
            }
        }.getOrNull()
        if (protection == PermissionInfo.PROTECTION_DANGEROUS) dangerous++
    }
    return granted to dangerous
}

@Suppress("DEPRECATION")
private fun signerSha256(info: PackageInfo): String? {
    val sig = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val si = info.signingInfo ?: return null
        if (si.hasMultipleSigners()) si.apkContentsSigners else si.signingCertificateHistory
    } else {
        info.signatures
    }?.firstOrNull() ?: return null
    val digest = MessageDigest.getInstance("SHA-256").digest(sig.toByteArray())
    return digest.joinToString(":") { "%02X".format(it) }
}

/** [apk, data, cache] — needs usage-access on most devices; null when denied. */
private fun storageBytes(context: Context, packageName: String): List<Long>? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null
    return runCatching {
        val ssm = context.getSystemService(StorageStatsManager::class.java)
        val stats = ssm.queryStatsForPackage(
            StorageManager.UUID_DEFAULT,
            packageName,
            Process.myUserHandle()
        )
        listOf(stats.appBytes, stats.dataBytes, stats.cacheBytes)
    }.getOrNull()
}
