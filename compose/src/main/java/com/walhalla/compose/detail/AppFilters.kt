package com.walhalla.compose.detail

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.R

/** Quick list filters — one catalog for phone chips and TV filter dialog. */
enum class AppFilter(@param:StringRes val labelRes: Int, val icon: ImageVector) {
    ALL(R.string.filter_all, Icons.Default.Apps),
    USER(R.string.filter_user, Icons.Default.Person),
    SYSTEM(R.string.filter_system, Icons.Default.SettingsSuggest),
    TV(R.string.filter_tv, Icons.Default.Tv),
    SPLIT(R.string.filter_split, Icons.Default.CallSplit),
    SIDELOADED(R.string.filter_sideloaded, Icons.Default.InstallMobile),
}

/** Trait-based filters stay empty until [AppTraits] are loaded. */
fun PackageMeta.matches(filter: AppFilter, traits: AppTraits?): Boolean = when (filter) {
    AppFilter.ALL -> true
    AppFilter.USER -> !isSystemApp
    AppFilter.SYSTEM -> isSystemApp
    AppFilter.SPLIT -> hasSplits
    AppFilter.TV -> traits?.tvLauncher == true
    AppFilter.SIDELOADED -> traits?.installSource == InstallSource.Sideload
}

data class AppsTotals(val count: Int, val bytes: Long)

fun List<PackageMeta>.totals(): AppsTotals =
    AppsTotals(size, sumOf { it.fileSize.coerceAtLeast(0L) })
