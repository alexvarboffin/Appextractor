package com.walhalla.compose.ui.tv.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.walhalla.appextractor.activity.assets.StringItemViewModel
import com.walhalla.appextractor.activity.string.MvpContract
import com.walhalla.appextractor.activity.string.StringsPresenter
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.ResourcesToolForPlugin
import com.walhalla.compose.ui.tv.components.TvActionHintIcon
import com.walhalla.extractor.R as ExtR

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvStringTab(
    app: PackageMeta,
    resourceType: String,
    onViewXml: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var resources by remember(app.packageName, resourceType) {
        mutableStateOf<List<StringItemViewModel>>(emptyList())
    }
    var error by remember(app.packageName, resourceType) { mutableStateOf<String?>(null) }
    var loading by remember(app.packageName, resourceType) { mutableStateOf(true) }
    val isXml = resourceType == ResourcesToolForPlugin.XML

    DisposableEffect(app.packageName, resourceType) {
        loading = true
        error = null
        val presenter = StringsPresenter(context, object : MvpContract.View {
            override fun showResourceRawText(resource: StringItemViewModel, content: String) {}
            override fun showError(message: String) {
                error = message
                loading = false
            }
            override fun showSuccess(list: List<StringItemViewModel>) {
                resources = list
                loading = false
            }
            override fun successToast(res: Int, aaa: String) {}
            override fun errorToast(res: Int, aaa: String) {}
        })
        presenter.loadResourceContent(app.packageName, resourceType)
        onDispose {}
    }

    when {
        loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(ExtR.string.dlg_loading_title))
        }
        error != null -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
        }
        resources.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("—", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(resources, key = { it.name + it.text }) { item ->
                var focused by remember { mutableStateOf(false) }
                Surface(
                    onClick = {
                        if (isXml) {
                            onViewXml(item.name)
                        } else {
                            clipboard.setText(AnnotatedString("${item.name}\n${item.text}"))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focused = it.isFocused },
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                    )
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                            Text(
                                item.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        TvActionHintIcon(
                            icon = if (isXml) Icons.Default.Description else Icons.Default.ContentCopy,
                            focused = focused,
                            contentDescription = stringResource(
                                if (isXml) ExtR.string.action_xml_viewer
                                else ExtR.string.menu_popup_copy
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
