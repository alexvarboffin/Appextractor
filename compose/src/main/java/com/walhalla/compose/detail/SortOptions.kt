package com.walhalla.compose.detail

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Update
import androidx.compose.ui.graphics.vector.ImageVector
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.R

/** Shared phone + TV extractor sort modes. */
enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    PACKAGE_ASC,
    PACKAGE_DESC,
    SIZE_ASC,
    SIZE_DESC,
    UPDATE_DATE_ASC,
    UPDATE_DATE_DESC,
    SYSTEM_FIRST,
    USER_FIRST
}

data class SortMenuItem(
    val option: SortOption,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
)

fun sortMenuItems(): List<SortMenuItem> = listOf(
    SortMenuItem(SortOption.NAME_ASC, R.string.sort_name_asc, Icons.Default.SortByAlpha),
    SortMenuItem(SortOption.NAME_DESC, R.string.sort_name_desc, Icons.Default.SortByAlpha),
    SortMenuItem(SortOption.PACKAGE_ASC, R.string.sort_package_asc, Icons.Default.Code),
    SortMenuItem(SortOption.PACKAGE_DESC, R.string.sort_package_desc, Icons.Default.Code),
    SortMenuItem(SortOption.SIZE_ASC, R.string.sort_size_asc, Icons.Default.Storage),
    SortMenuItem(SortOption.SIZE_DESC, R.string.sort_size_desc, Icons.Default.Storage),
    SortMenuItem(SortOption.UPDATE_DATE_ASC, R.string.sort_update_asc, Icons.Default.Update),
    SortMenuItem(SortOption.UPDATE_DATE_DESC, R.string.sort_update_desc, Icons.Default.Update),
    SortMenuItem(SortOption.SYSTEM_FIRST, R.string.sort_system_first, Icons.Default.Android),
    SortMenuItem(SortOption.USER_FIRST, R.string.sort_user_first, Icons.Default.Person),
)

fun List<PackageMeta>.sortedByOption(option: SortOption): List<PackageMeta> = when (option) {
    SortOption.NAME_ASC -> sortedBy { it.label }
    SortOption.NAME_DESC -> sortedByDescending { it.label }
    SortOption.PACKAGE_ASC -> sortedBy { it.packageName }
    SortOption.PACKAGE_DESC -> sortedByDescending { it.packageName }
    SortOption.SIZE_ASC -> sortedBy { it.fileSize }
    SortOption.SIZE_DESC -> sortedByDescending { it.fileSize }
    SortOption.UPDATE_DATE_ASC -> sortedBy { it.updateTime }
    SortOption.UPDATE_DATE_DESC -> sortedByDescending { it.updateTime }
    SortOption.SYSTEM_FIRST ->
        sortedWith(compareByDescending<PackageMeta> { it.isSystemApp }.thenBy { it.label })
    SortOption.USER_FIRST ->
        sortedWith(compareBy<PackageMeta> { it.isSystemApp }.thenBy { it.label })
}
