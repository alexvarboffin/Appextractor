package com.walhalla.compose.detail

import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.HeaderObject
import com.walhalla.appextractor.sdk.V2Line
import com.walhalla.appextractor.utils.prettyFileSize

/** Section bucket for INFO tab — shared by phone + TV. */
data class InfoSection(
    val title: String,
    val iconRes: Int,
    val items: List<BaseViewModel>
)

fun groupIntoSections(
    lines: List<BaseViewModel>,
    app: PackageMeta
): List<InfoSection> {
    val sections = mutableListOf<InfoSection>()
    var title = ""
    var icon = 0
    var bucket = mutableListOf<BaseViewModel>()

    fun flush() {
        if (title.isNotEmpty() || bucket.isNotEmpty()) {
            sections.add(InfoSection(title, icon, bucket.toList()))
        }
        bucket = mutableListOf()
    }

    // PackageMeta-only fields that F0Presenter does not always expose
    val extras = mutableListOf<BaseViewModel>()
    if (app.fileSize > 0) {
        extras.add(V2Line("Size", prettyFileSize(app.fileSize)))
    }
    extras.add(V2Line("Has splits", if (app.hasSplits) "Yes" else "No"))
    extras.add(V2Line("Certificate pinning", if (app.hasPinning) "Yes" else "No"))

    for (line in lines) {
        when (line) {
            is HeaderObject -> {
                flush()
                title = line.title
                icon = line.icon
                if (title.contains("Basic", ignoreCase = true)) {
                    bucket.addAll(extras)
                }
            }
            else -> bucket.add(line)
        }
    }
    flush()

    if (sections.isEmpty() && extras.isNotEmpty()) {
        sections.add(InfoSection("Basic Information", 0, extras))
    }
    return sections
}
