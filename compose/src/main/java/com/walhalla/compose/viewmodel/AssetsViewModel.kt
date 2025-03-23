package com.walhalla.compose.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.activity.assets.AssetsPresenter
import com.walhalla.appextractor.activity.assets.MvpContract
import com.walhalla.appextractor.activity.assets.ResItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssetsViewModel(application: Application) : AndroidViewModel(application) {
    private val _assets = MutableStateFlow<List<ResItem>>(emptyList())
    val assets: StateFlow<List<ResItem>> = _assets.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedAsset = MutableStateFlow<Pair<ResItem, String>?>(null)
    val selectedAsset: StateFlow<Pair<ResItem, String>?> = _selectedAsset.asStateFlow()

    private var presenter: AssetsPresenter? = null

    fun loadAssets(packageName: String) {
        presenter = AssetsPresenter(getApplication(), object : MvpContract.View {
            override fun showResourceRawText(resource: ResItem, content: String) {
                _selectedAsset.value = resource to content
            }

            override fun showError(message: String) {
                _error.value = message
            }

            override fun showSuccess(list: List<ResItem>) {
                _assets.value = list
            }

            override fun successToast(res: Int, aaa: String) {}
            override fun errorToast(res: Int, aaa: String) {}
        })

        presenter?.loadManifestContent(packageName)
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