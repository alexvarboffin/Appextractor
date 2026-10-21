package com.walhalla.compose.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.R
import com.walhalla.compose.log.AppLogBus
import com.walhalla.extractor.R as ExtR
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Human-readable overview facts — same rows on phone card, TV hero and in the report. */
fun insightFacts(context: Context, insights: AppInsights): List<Pair<String, String>> = buildList {
    fun s(id: Int) = context.getString(id)
    add(
        s(R.string.insight_tv_ready) to s(if (insights.tvLauncher) R.string.value_yes else R.string.value_no)
    )
    add(
        s(R.string.insight_touch_required) to
            s(if (insights.touchscreenRequired) R.string.value_yes else R.string.value_no)
    )
    add(
        s(R.string.insight_abi) to
            insights.abis.joinToString(", ").ifEmpty { s(R.string.insight_abi_none) }
    )
    add(
        s(R.string.insight_installer) to buildString {
            append(s(insights.installSource.labelRes))
            val who = insights.installerLabel ?: insights.installerPackage
            if (who != null && insights.installSource == InstallSource.OtherStore) append(" · ").append(who)
        }
    )
    add(
        s(R.string.insight_debuggable) to
            s(if (insights.debuggable) R.string.value_yes else R.string.value_no)
    )
    add(
        s(R.string.insight_permissions) to context.getString(
            R.string.insight_perms_value,
            insights.permsRequested,
            insights.permsGranted,
            insights.permsDangerous
        )
    )
    if (insights.apkBytes != null) {
        add(
            s(R.string.insight_storage) to listOf(
                s(R.string.storage_apk) to insights.apkBytes,
                s(R.string.storage_data) to (insights.dataBytes ?: 0L),
                s(R.string.storage_cache) to (insights.cacheBytes ?: 0L),
            ).joinToString(" · ") { (k, v) -> "$k ${prettyFileSize(v)}" }
        )
    }
    if (insights.splitNames.isNotEmpty()) {
        add(s(R.string.stat_splits) to insights.splitNames.joinToString(", "))
    }
    insights.signerSha256?.let { add(s(R.string.insight_signer) to it) }
}

private data class ReportSection(val title: String, val rows: List<Pair<String, String>>)

private fun reportSections(
    context: Context,
    app: PackageMeta,
    infoLines: List<BaseViewModel>,
    insights: AppInsights?,
): List<ReportSection> = buildList {
    if (insights != null) {
        add(ReportSection(context.getString(R.string.insight_overview), insightFacts(context, insights)))
    }
    groupIntoSections(infoLines, app).forEach { section ->
        val rows = mutableListOf<Pair<String, String>>()
        fun flatten(ui: InfoLineUi, prefix: String) {
            if (ui.children.isNotEmpty()) {
                ui.children.forEach { flatten(it, "$prefix${ui.title} / ") }
                return
            }
            val value = listOf(ui.value, ui.meta).filter { it.isNotBlank() }.joinToString(" · ")
            rows.add("$prefix${ui.title}" to value)
        }
        section.items.forEach { line ->
            mapInfoLine(context, line, app.packageName)?.let { flatten(it, "") }
        }
        if (rows.isNotEmpty()) add(ReportSection(section.title.ifBlank { "Details" }, rows))
    }
}

fun buildInfoReportText(
    context: Context,
    app: PackageMeta,
    infoLines: List<BaseViewModel>,
    insights: AppInsights?,
): String = buildString {
    appendLine("${app.label} (${app.packageName})")
    appendLine("v${app.versionName ?: "?"} (${app.versionCode})")
    reportSections(context, app, infoLines, insights).forEach { section ->
        appendLine()
        appendLine("== ${section.title} ==")
        section.rows.forEach { (k, v) -> appendLine("$k: $v") }
    }
}

fun buildInfoReportJson(
    context: Context,
    app: PackageMeta,
    infoLines: List<BaseViewModel>,
    insights: AppInsights?,
): String {
    val root = JSONObject()
        .put("label", app.label)
        .put("packageName", app.packageName)
        .put("versionName", app.versionName ?: JSONObject.NULL)
        .put("versionCode", app.versionCode)
        .put("fileSize", app.fileSize)
        .put("system", app.isSystemApp)
    if (insights != null) {
        root.put(
            "insights",
            JSONObject()
                .put("tvLauncher", insights.tvLauncher)
                .put("touchscreenRequired", insights.touchscreenRequired)
                .put("abis", JSONArray(insights.abis))
                .put("installSource", insights.installSource.name)
                .put("installer", insights.installerPackage ?: JSONObject.NULL)
                .put("debuggable", insights.debuggable)
                .put("permsRequested", insights.permsRequested)
                .put("permsGranted", insights.permsGranted)
                .put("permsDangerous", insights.permsDangerous)
                .put("splits", JSONArray(insights.splitNames))
                .put("signerSha256", insights.signerSha256 ?: JSONObject.NULL)
                .put("apkBytes", insights.apkBytes ?: JSONObject.NULL)
                .put("dataBytes", insights.dataBytes ?: JSONObject.NULL)
                .put("cacheBytes", insights.cacheBytes ?: JSONObject.NULL)
        )
    }
    val sections = JSONArray()
    reportSections(context, app, infoLines, insights).forEach { section ->
        val rows = JSONArray()
        section.rows.forEach { (k, v) -> rows.put(JSONObject().put("name", k).put("value", v)) }
        sections.put(JSONObject().put("title", section.title).put("rows", rows))
    }
    root.put("sections", sections)
    return root.toString(2)
}

enum class ReportAction(@param:StringRes val labelRes: Int, val icon: ImageVector) {
    COPY(R.string.report_copy, Icons.Default.ContentCopy),
    SHARE(R.string.report_share, Icons.Default.Share),
    SAVE_JSON(R.string.report_save_json, Icons.Default.Save),
}

suspend fun runReportAction(
    context: Context,
    action: ReportAction,
    app: PackageMeta,
    infoLines: List<BaseViewModel>,
    insights: AppInsights?,
) {
    when (action) {
        ReportAction.COPY, ReportAction.SHARE -> {
            val text = withContext(Dispatchers.Default) {
                buildInfoReportText(context, app, infoLines, insights)
            }
            if (action == ReportAction.COPY) {
                InfoReportActions.copy(context, text)
            } else {
                InfoReportActions.share(context, app.label, text)
            }
        }
        ReportAction.SAVE_JSON -> withContext(Dispatchers.IO) {
            val json = buildInfoReportJson(context, app, infoLines, insights)
            InfoReportActions.saveToDownloads(
                context,
                "${app.packageName}_${app.versionCode}_info.json",
                "application/json",
                json
            )
        }
    }
}

object InfoReportActions {

    fun copy(context: Context, text: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("report", text))
        AppLogBus.info(context.getString(ExtR.string.copied_to_clipboard))
    }

    fun share(context: Context, subject: String, text: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        runCatching {
            context.startActivity(
                Intent.createChooser(send, subject).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.onFailure {
            AppLogBus.error(context.getString(ExtR.string.no_app_available_to_share))
        }
    }

    /** Blocking — writes `Downloads/<fileName>`; returns the visible path or null. */
    fun saveToDownloads(context: Context, fileName: String, mime: String, content: String): String? {
        val path = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mime)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(
                    MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL),
                    values
                ) ?: return@runCatching null
                resolver.openOutputStream(uri)?.use { it.write(content.toByteArray()) }
                "${Environment.DIRECTORY_DOWNLOADS}/$fileName"
            } else {
                @Suppress("DEPRECATION")
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                dir.mkdirs()
                val file = File(dir, fileName)
                file.writeText(content)
                file.path
            }
        }.getOrNull()
        if (path != null) {
            AppLogBus.success(context.getString(R.string.report_saved, path))
        } else {
            AppLogBus.error(context.getString(R.string.report_failed))
        }
        return path
    }
}
