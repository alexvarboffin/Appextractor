package com.walhalla.compose.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.activity.assets.AssetsPresenter
import com.walhalla.appextractor.activity.assets.MvpContract
import com.walhalla.appextractor.activity.assets.ResItem
import com.walhalla.compose.model.AssetTreeNode
import com.walhalla.compose.model.allBranchPaths
import com.walhalla.compose.model.buildAssetTree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssetsViewModel(application: Application) : AndroidViewModel(application) {
    private val _assetTree = MutableStateFlow<List<AssetTreeNode>>(emptyList())
    val assetTree: StateFlow<List<AssetTreeNode>> = _assetTree.asStateFlow()

    private val _expandedNodes = MutableStateFlow<Set<String>>(emptySet())
    val expandedNodes: StateFlow<Set<String>> = _expandedNodes.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _selectedAsset = MutableStateFlow<Pair<ResItem, String>?>(null)
    val selectedAsset: StateFlow<Pair<ResItem, String>?> = _selectedAsset.asStateFlow()

    private val _imagePreview = MutableStateFlow<Pair<ResItem, ByteArray>?>(null)
    val imagePreview: StateFlow<Pair<ResItem, ByteArray>?> = _imagePreview.asStateFlow()

    private var presenter: AssetsPresenter? = null

    fun clearToast() {
        _toastMessage.value = null
    }

    fun loadAssets(packageName: String) {
        _error.value = null
        presenter = AssetsPresenter(getApplication(), object : MvpContract.View {
            override fun showResourceRawText(resource: ResItem, content: String) {
                _selectedAsset.value = resource to content
            }

            override fun showError(message: String) {
                _error.value = message
            }

            override fun showSuccess(list: List<ResItem>) {
                val tree = buildAssetTree(list)
                _assetTree.value = tree
                // Expand top-level folders so the tree is usable immediately
                _expandedNodes.value = tree
                    .filter { it.isBranch }
                    .map { it.asset.fullPath }
                    .toSet()
            }

            override fun successToast(res: Int, aaa: String) {
                val ctx = getApplication<Application>()
                val msg = ctx.getString(res) + " " + aaa
                _toastMessage.value = msg
                com.walhalla.compose.log.AppLogBus.success(msg)
            }

            override fun errorToast(res: Int, aaa: String) {
                val ctx = getApplication<Application>()
                val msg = ctx.getString(res) + " " + aaa
                _toastMessage.value = msg
                _error.value = aaa
                com.walhalla.compose.log.AppLogBus.error(msg)
            }
        })

        presenter?.loadManifestContent(packageName)
    }

    fun toggleNodeExpansion(nodePath: String) {
        val currentExpanded = _expandedNodes.value.toMutableSet()
        if (!currentExpanded.add(nodePath)) {
            currentExpanded.remove(nodePath)
        }
        _expandedNodes.value = currentExpanded
    }

    fun expandAll() {
        _expandedNodes.value = _assetTree.value.allBranchPaths()
    }

    fun collapseAll() {
        _expandedNodes.value = emptySet()
    }

    fun readAsset(context: Context, asset: ResItem) {
        presenter?.readAssetRequest(context, asset)
    }

    /** Text stays a text preview. png/jpg/webp/gif decode as a picture, not as a string. */
    fun openAsset(context: Context, asset: ResItem) {
        if (!isImageAsset(asset.fullPath)) {
            _imagePreview.value = null
            readAsset(context, asset)
            return
        }
        viewModelScope.launch {
            val bytes = withContext(Dispatchers.IO) {
                runCatching { presenter?.readAssetBytes(asset) }
            }
            bytes.onSuccess { data ->
                _selectedAsset.value = null
                _imagePreview.value = data?.let { asset to it }
            }.onFailure { error ->
                _imagePreview.value = null
                _error.value = error.message
            }
        }
    }

    fun saveIcon(context: Context, asset: ResItem) {
        presenter?.saveAsset(context, asset)
    }

    fun exportIcon(context: Context, asset: ResItem) {
        presenter?.exportIconRequest(context, asset)
    }

    fun zipAllAssets(context: Context, asset: ResItem) {
        presenter?.zipAllAssetsRequest(context, asset)
    }

    fun clearSelectedAsset() {
        _selectedAsset.value = null
        _imagePreview.value = null
    }

    override fun onCleared() {
        super.onCleared()
        presenter = null
    }
}

private val IMAGE_EXT = setOf("png", "jpg", "jpeg", "webp", "gif")

fun isImageAsset(path: String): Boolean =
    path.substringAfterLast('.', "").lowercase() in IMAGE_EXT 