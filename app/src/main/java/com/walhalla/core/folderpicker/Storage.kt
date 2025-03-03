package com.walhalla.core.folderpicker


class Storage(val path: String, val type: StorageType) {

    override fun toString(): String {
        return "Storage{" +
                "path='" + path + '\'' +
                ", type=" + type +
                '}'
    }

    enum class StorageType {
        INTERNAL, EXTERNAL, CURRENT
    }
}
