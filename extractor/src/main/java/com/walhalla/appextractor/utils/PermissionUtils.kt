package com.walhalla.appextractor.utils

import android.content.pm.PermissionInfo

object PermissionUtils {

    fun protectionLevelToString(protectionLevelKey: Int): String {
        val protectionLevel = when (protectionLevelKey) {
            PermissionInfo.PROTECTION_NORMAL -> "normal"
            PermissionInfo.PROTECTION_DANGEROUS -> "dangerous"
            PermissionInfo.PROTECTION_SIGNATURE -> "signature"
            PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM -> "signatureOrSystem"
            PermissionInfo.PROTECTION_FLAG_PRIVILEGED -> "PRIVILEGED"
            PermissionInfo.PROTECTION_FLAG_PREINSTALLED -> "PREINSTALLED"
            PermissionInfo.PROTECTION_FLAG_INSTANT -> "INSTANT"
            else -> "<unknown> $protectionLevelKey"
        }
        return protectionLevel
    }
}
