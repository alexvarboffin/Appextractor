package com.walhalla.compose.detail

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shop
import androidx.compose.ui.graphics.vector.ImageVector
import com.walhalla.extractor.R as ExtR

/**
 * Phone overflow + share — one catalog for phone menus and TV long-click dialog.
 * Icons are Material Icons (not Material3 widgets) — safe for phone + TV.
 */
enum class AppMenuActionId {
    Extract,
    ShareApk,
    PlayStore,
    Launch,
    Uninstall,
    CopyPackage,
    AppInfo,
    Manifest,
    SaveIcon,
    ExportIcon
}

data class AppMenuAction(
    val id: AppMenuActionId,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
)

fun appMenuActions(): List<AppMenuAction> = listOf(
    AppMenuAction(AppMenuActionId.Extract, ExtR.string.action_extract, Icons.Default.SaveAlt),
    AppMenuAction(AppMenuActionId.ShareApk, ExtR.string.action_share_app, Icons.Default.Share),
    AppMenuAction(AppMenuActionId.PlayStore, ExtR.string.open_on_google_play, Icons.Default.Shop),
    AppMenuAction(AppMenuActionId.Launch, ExtR.string.action_launch_app, Icons.Default.PlayArrow),
    AppMenuAction(AppMenuActionId.Uninstall, ExtR.string.action_uninstall_app, Icons.Default.Delete),
    AppMenuAction(AppMenuActionId.CopyPackage, ExtR.string.action_copy_package_name, Icons.Default.ContentCopy),
    AppMenuAction(AppMenuActionId.AppInfo, ExtR.string.action_app_info, Icons.Default.Info),
    AppMenuAction(AppMenuActionId.Manifest, ExtR.string.action_app_manifest, Icons.Default.Description),
    AppMenuAction(AppMenuActionId.SaveIcon, ExtR.string.action_save_icon, Icons.Default.Download),
    AppMenuAction(AppMenuActionId.ExportIcon, ExtR.string.action_export_icon, Icons.Default.Share),
)
