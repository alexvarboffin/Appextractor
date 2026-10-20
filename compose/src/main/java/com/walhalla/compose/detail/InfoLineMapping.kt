package com.walhalla.compose.detail

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import com.walhalla.appextractor.sdk.ActivityLine
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.CertLine
import com.walhalla.appextractor.sdk.DirLine
import com.walhalla.appextractor.sdk.FlagzObject
import com.walhalla.appextractor.sdk.HeaderCollapsedObject
import com.walhalla.appextractor.sdk.HeaderObject
import com.walhalla.appextractor.sdk.InfoApkLine
import com.walhalla.appextractor.sdk.PermissionLine
import com.walhalla.appextractor.sdk.ProviderLine
import com.walhalla.appextractor.sdk.ReceiverLine
import com.walhalla.appextractor.sdk.ServiceLine
import com.walhalla.appextractor.sdk.SimpleLine
import com.walhalla.appextractor.sdk.V2Line
import com.walhalla.appextractor.utils.ApplicationFlags
import com.walhalla.compose.model.getPermissionDescription

enum class InfoLineAction {
    None,
    Copy,
    LaunchActivity,
    LaunchService,
    LaunchProvider,
    OpenSettings,
    ViewXml
}

enum class InfoLineStatus {
    Granted,
    Denied,
    Exported,
    NotExported
}

/**
 * Platform-agnostic INFO row model — phone/TV only differ in chrome (Material3 vs tv.material3).
 */
data class InfoLineUi(
    val title: String,
    val value: String,
    val meta: String = "",
    val status: InfoLineStatus? = null,
    val action: InfoLineAction = InfoLineAction.None,
    /** className / authority / xml path / value to copy */
    val actionPayload: String = "",
    val providerLine: ProviderLine? = null,
    val children: List<InfoLineUi> = emptyList()
)

fun resolvePermissionDescription(context: Context, shortOrFull: String): String {
    val pm = context.packageManager
    val candidates = buildList {
        add(shortOrFull)
        if (!shortOrFull.startsWith("android.permission.")) {
            add("android.permission.$shortOrFull")
        }
    }
    for (name in candidates) {
        try {
            val info = pm.getPermissionInfo(name, 0)
            val desc = info.loadDescription(pm)?.toString()
            if (!desc.isNullOrBlank()) return desc
        } catch (_: PackageManager.NameNotFoundException) {
            // try next / fallback
        }
    }
    return getPermissionDescription(shortOrFull)
}

fun mapInfoLine(
    context: Context,
    line: BaseViewModel,
    packageName: String
): InfoLineUi? = when (line) {
    is HeaderObject -> null // section headers handled by groupIntoSections
    is SimpleLine -> InfoLineUi(
        title = runCatching { context.getString(line.name) }.getOrElse { line.name.toString() },
        value = line.value,
        action = InfoLineAction.Copy,
        actionPayload = line.value
    )
    is CertLine -> InfoLineUi(
        title = line.res0.ifBlank { "—" },
        value = line.value,
        action = InfoLineAction.Copy,
        actionPayload = line.value
    )
    is V2Line -> InfoLineUi(
        title = line.key,
        value = line.value,
        action = InfoLineAction.Copy,
        actionPayload = line.value
    )
    is DirLine -> InfoLineUi(
        title = line.name,
        value = line.value,
        action = InfoLineAction.Copy,
        actionPayload = line.value
    )
    is InfoApkLine -> InfoLineUi(
        title = line.name,
        value = line.value,
        action = InfoLineAction.ViewXml,
        actionPayload = line.value
    )
    is PermissionLine -> {
        val level = when (line.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) {
            PermissionInfo.PROTECTION_DANGEROUS -> "dangerous"
            PermissionInfo.PROTECTION_NORMAL -> "normal"
            PermissionInfo.PROTECTION_SIGNATURE -> "signature"
            else -> "level=${line.protectionLevel}"
        }
        InfoLineUi(
            title = line.res0,
            value = resolvePermissionDescription(context, line.res0),
            meta = level,
            status = if (line.isGranted) InfoLineStatus.Granted else InfoLineStatus.Denied,
            action = InfoLineAction.OpenSettings
        )
    }
    is FlagzObject -> {
        val names = ApplicationFlags.activeNames(line.flags)
            .map { it.removePrefix("FLAG_") }
        InfoLineUi(
            title = "Flags (0x${Integer.toHexString(line.flags)})",
            value = if (names.isEmpty()) "—" else names.joinToString(", ")
        )
    }
    is ActivityLine -> {
        val label = line.label.ifBlank { line.className.substringAfterLast('.') }
        InfoLineUi(
            title = label,
            value = line.className,
            status = if (line.exported) InfoLineStatus.Exported else InfoLineStatus.NotExported,
            action = if (line.exported) InfoLineAction.LaunchActivity else InfoLineAction.None,
            actionPayload = line.className
        )
    }
    is ServiceLine -> {
        val label = line.label.ifBlank { line.className.substringAfterLast('.') }
        InfoLineUi(
            title = label,
            value = line.className,
            status = if (line.exported) InfoLineStatus.Exported else InfoLineStatus.NotExported,
            action = if (line.exported) InfoLineAction.LaunchService else InfoLineAction.None,
            actionPayload = line.className
        )
    }
    is ReceiverLine -> {
        val label = line.label.ifBlank { line.receiverName.substringAfterLast('.') }
        InfoLineUi(
            title = label,
            value = line.receiverName,
            status = if (line.exported) InfoLineStatus.Exported else InfoLineStatus.NotExported
        )
    }
    is ProviderLine -> InfoLineUi(
        title = line.label.ifBlank { line.className.substringAfterLast('.') },
        value = line.className,
        meta = line.authority,
        status = if (line.exported) InfoLineStatus.Exported else InfoLineStatus.NotExported,
        action = if (line.exported && line.authority.isNotBlank()) {
            InfoLineAction.LaunchProvider
        } else {
            InfoLineAction.None
        },
        actionPayload = line.authority,
        providerLine = line
    )
    is HeaderCollapsedObject -> InfoLineUi(
        title = line.title,
        value = "${line.list.size}",
        children = line.list.mapNotNull { mapInfoLine(context, it, packageName) }
    )
}
