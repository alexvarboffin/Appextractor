package com.walhalla.compose.storage

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.walhalla.appextractor.model.PackageMeta

/**
 * Same storage gate phone Extractor uses before [ExtractorViewModel.extractApkRequest].
 * TV must use this too — extract is shared [ExtractorHelper], not a TV fork.
 */
@Composable
fun rememberExtractWithStoragePermission(
    extract: (PackageMeta) -> Unit,
): (PackageMeta) -> Unit {
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
    }
    val pending = remember { mutableListOf<PackageMeta>() }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val apps = pending.toList()
        pending.clear()
        apps.forEach(extract)
    }
    return remember(launcher, extract) {
        { app ->
            pending.clear()
            pending.add(app)
            launcher.launch(permissions)
            // Match phone: also kick extract immediately (already-granted devices).
            extract(app)
        }
    }
}
