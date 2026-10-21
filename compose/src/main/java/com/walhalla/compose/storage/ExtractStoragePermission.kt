package com.walhalla.compose.storage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.walhalla.appextractor.model.PackageMeta

/**
 * Storage gate before any [com.walhalla.appextractor.ExtractorHelper] run (phone + TV).
 * API 29+ writes Downloads via MediaStore → no prompt; older devices ask once, then run.
 * The action runs exactly once (either now or after the permission result).
 */
@Composable
fun rememberStorageGate(): (action: () -> Unit) -> Unit {
    val context = LocalContext.current
    val pending = remember { mutableListOf<() -> Unit>() }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val actions = pending.toList()
        pending.clear()
        actions.forEach { it() }
    }
    return remember(launcher) {
        { action ->
            val needsPrompt = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            if (needsPrompt) {
                pending.add(action)
                launcher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                action()
            }
        }
    }
}

@Composable
fun rememberExtractWithStoragePermission(
    extract: (PackageMeta) -> Unit,
): (PackageMeta) -> Unit {
    val gate = rememberStorageGate()
    return remember(gate, extract) { { app -> gate { extract(app) } } }
}
