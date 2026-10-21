package com.walhalla.compose.log

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.walhalla.extractor.R as ExtR
import java.io.File

/** Extracted-APK log row: share via FileProvider (phone LogItem + TV log actions). */
fun shareApkFile(context: Context, file: File) {
    if (!file.exists() || !file.canRead()) return
    runCatching {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(intent, context.getString(ExtR.string.action_share_file))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }.onFailure {
        AppLogBus.error(context.getString(ExtR.string.no_app_available_to_share))
    }
}
