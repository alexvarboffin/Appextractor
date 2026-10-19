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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
                _toastMessage.value = ctx.getString(res) + " " + aaa
            }

            override fun errorToast(res: Int, aaa: String) {
                val ctx = getApplication<Application>()
                _toastMessage.value = ctx.getString(res) + " " + aaa
                _error.value = aaa
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
    }

    override fun onCleared() {
        super.onCleared()
        presenter = null
    }
} 