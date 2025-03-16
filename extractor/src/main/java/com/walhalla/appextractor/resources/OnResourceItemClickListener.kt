package com.walhalla.appextractor.resources

open interface OnResourceItemClickListener {
    fun readAssetRequest(resource: ResItem)

    fun saveIconRequest(resource: ResItem)

    fun exportIconRequest(resource: ResItem)

    fun zipAllAssetsRequest(resource: ResItem)
}