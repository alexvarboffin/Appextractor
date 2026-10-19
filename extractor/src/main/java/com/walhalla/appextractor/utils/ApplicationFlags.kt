package com.walhalla.appextractor.utils

import android.content.pm.ApplicationInfo
import android.os.Build

/** Decodes [ApplicationInfo.flags] into human-readable names (active bits only). */
object ApplicationFlags {

    fun activeNames(flags: Int): List<String> {
        val map = linkedMapOf(
            "FLAG_DEBUGGABLE" to ApplicationInfo.FLAG_DEBUGGABLE,
            "FLAG_HAS_CODE" to ApplicationInfo.FLAG_HAS_CODE,
            "FLAG_PERSISTENT" to ApplicationInfo.FLAG_PERSISTENT,
            "FLAG_FACTORY_TEST" to ApplicationInfo.FLAG_FACTORY_TEST,
            "FLAG_ALLOW_TASK_REPARENTING" to ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING,
            "FLAG_ALLOW_CLEAR_USER_DATA" to ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA,
            "FLAG_UPDATED_SYSTEM_APP" to ApplicationInfo.FLAG_UPDATED_SYSTEM_APP,
            "FLAG_TEST_ONLY" to ApplicationInfo.FLAG_TEST_ONLY,
            "FLAG_SUPPORTS_SMALL_SCREENS" to ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS,
            "FLAG_SUPPORTS_NORMAL_SCREENS" to ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS,
            "FLAG_SUPPORTS_LARGE_SCREENS" to ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS,
            "FLAG_SUPPORTS_XLARGE_SCREENS" to ApplicationInfo.FLAG_SUPPORTS_XLARGE_SCREENS,
            "FLAG_RESIZEABLE_FOR_SCREENS" to ApplicationInfo.FLAG_RESIZEABLE_FOR_SCREENS,
            "FLAG_SUPPORTS_SCREEN_DENSITIES" to ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES,
            "FLAG_VM_SAFE_MODE" to ApplicationInfo.FLAG_VM_SAFE_MODE,
            "FLAG_ALLOW_BACKUP" to ApplicationInfo.FLAG_ALLOW_BACKUP,
            "FLAG_KILL_AFTER_RESTORE" to ApplicationInfo.FLAG_KILL_AFTER_RESTORE,
            "FLAG_SYSTEM" to ApplicationInfo.FLAG_SYSTEM,
            "FLAG_RESTORE_ANY_VERSION" to ApplicationInfo.FLAG_RESTORE_ANY_VERSION,
            "FLAG_EXTERNAL_STORAGE" to ApplicationInfo.FLAG_EXTERNAL_STORAGE,
            "FLAG_LARGE_HEAP" to ApplicationInfo.FLAG_LARGE_HEAP,
            "FLAG_STOPPED" to ApplicationInfo.FLAG_STOPPED,
            "FLAG_SUPPORTS_RTL" to ApplicationInfo.FLAG_SUPPORTS_RTL,
            "FLAG_INSTALLED" to ApplicationInfo.FLAG_INSTALLED,
            "FLAG_IS_DATA_ONLY" to ApplicationInfo.FLAG_IS_DATA_ONLY,
            "FLAG_IS_GAME" to ApplicationInfo.FLAG_IS_GAME,
            "FLAG_FULL_BACKUP_ONLY" to ApplicationInfo.FLAG_FULL_BACKUP_ONLY,
            "FLAG_MULTIARCH" to ApplicationInfo.FLAG_MULTIARCH,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            map["FLAG_USES_CLEARTEXT_TRAFFIC"] = ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC
        }
        return map.filter { (_, bit) -> flags and bit == bit }.keys.toList()
    }
}
