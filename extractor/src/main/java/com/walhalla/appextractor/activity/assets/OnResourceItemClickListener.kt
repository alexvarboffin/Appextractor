package com.walhalla.appextractor.activity.assets

open interface OnResourceItemClickListener {
    fun readAssetRequest(resource: ResItem)

    fun saveIconRequest(resource: ResItem)

    fun exportIconRequest(resource: ResItem)

    fun zipAllAssetsRequest(resource: ResItem)
}