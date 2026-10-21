package com.walhalla.compose.transfer

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.R
import java.io.File

/** Base APK + split APKs of an installed app, named for the phone's Downloads. */
fun appSharedFiles(context: Context, app: PackageMeta): List<SharedFile> {
    val info = runCatching { context.packageManager.getApplicationInfo(app.packageName, 0) }
        .getOrNull()
    val prefix = "${app.label}_${app.versionName ?: app.versionCode}"
    val base = File(info?.sourceDir ?: app.sourceDir)
    val splits = info?.splitSourceDirs.orEmpty().map(::File)
    return buildList {
        add(SharedFile(base, if (splits.isEmpty()) "$prefix.apk" else "${prefix}_base.apk"))
        splits.forEach { add(SharedFile(it, "${prefix}_${it.name}")) }
    }
}

data class QrTransferSession(
    val title: String,
    val files: List<SharedFile>,
    val url: String?,
    val qr: ImageBitmap?,
    @param:StringRes val errorRes: Int?,
)

/** Owns the running [LanFileServer]; stopped on [stop] or when the host leaves composition. */
class QrTransferController {
    var session by mutableStateOf<QrTransferSession?>(null)
        private set
    private var server: LanFileServer? = null

    fun start(title: String, files: List<SharedFile>) {
        stop()
        val readable = files.filter { it.file.canRead() }
        val host = LanFileServer.lanAddress()
        session = when {
            readable.isEmpty() -> QrTransferSession(title, files, null, null, R.string.qr_file_missing)
            host == null -> QrTransferSession(title, readable, null, null, R.string.qr_no_network)
            else -> {
                val srv = LanFileServer(title, readable).start()
                server = srv
                val url = srv.url(host)
                QrTransferSession(title, readable, url, qrBitmap(url), null)
            }
        }
    }

    fun stop() {
        server?.close()
        server = null
        session = null
    }
}

@Composable
fun rememberQrTransfer(): QrTransferController {
    val controller = remember { QrTransferController() }
    DisposableEffect(controller) { onDispose { controller.stop() } }
    return controller
}

/** Black-on-white bitmap regardless of theme — scanners need the contrast. */
fun qrBitmap(text: String, size: Int = 720): ImageBitmap {
    val matrix = QRCodeWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        size,
        size,
        mapOf(EncodeHintType.MARGIN to 1)
    )
    val pixels = IntArray(size * size) { i ->
        if (matrix[i % size, i / size]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
    }
    return Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888).asImageBitmap()
}
