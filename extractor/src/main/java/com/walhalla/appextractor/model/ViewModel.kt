package com.walhalla.appextractor.model

interface ViewModel {

    val id: Int

    fun longId(): Long {
        return id.toLong()
    }
}
