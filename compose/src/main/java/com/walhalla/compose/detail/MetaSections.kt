package com.walhalla.compose.detail

import android.content.Context
import com.walhalla.appextractor.activity.detail.DetailsF0
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.HeaderObject
import com.walhalla.appextractor.sdk.MetaPresenter
import com.walhalla.appextractor.sdk.V2Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class MetaSection(
    val title: String,
    val iconRes: Int,
    val items: List<V2Line>
)

fun groupMetaSections(lines: List<BaseViewModel>): List<MetaSection> {
    val sections = mutableListOf<MetaSection>()
    var title = ""
    var icon = 0
    var bucket = mutableListOf<V2Line>()

    fun flush() {
        if (title.isNotEmpty() || bucket.isNotEmpty()) {
            sections.add(MetaSection(title.ifBlank { "Metadata" }, icon, bucket.toList()))
        }
        bucket = mutableListOf()
    }

    for (line in lines) {
        when (line) {
            is HeaderObject -> {
                flush()
                title = line.title
                icon = line.icon
            }
            is V2Line -> bucket.add(line)
            else -> Unit
        }
    }
    flush()
    return sections
}

/** Shared META load — same MetaPresenter path for phone + TV. */
suspend fun loadMetaSections(context: Context, app: PackageMeta): List<MetaSection> =
    withContext(Dispatchers.Default) {
        var result: List<BaseViewModel> = emptyList()
        val presenter = MetaPresenter(
            context.applicationContext,
            app,
            object : DetailsF0.View {
                override fun swap(data: List<BaseViewModel>) {
                    result = data
                }

                override fun snack() {}
            }
        )
        presenter.doStuff(context.applicationContext)
        groupMetaSections(result)
    }
