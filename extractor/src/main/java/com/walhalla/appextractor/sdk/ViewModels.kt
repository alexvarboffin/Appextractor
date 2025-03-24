package com.walhalla.appextractor.sdk

import android.content.IntentFilter
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

sealed interface BaseViewModel


class DirLine(val name: String, val value: String, @param:DrawableRes val icon: Int) : BaseViewModel

class HeaderCollapsedObject(val title: String, val icon: Int) : BaseViewModel {
    val list: MutableList<BaseViewModel> = mutableListOf()

    override fun toString(): String = "HeaderCollapsedObject(title='$title', icon=$icon)"
}

class SimpleLine(val name: Int, val value: String) : BaseViewModel {
    override fun toString(): String = "SimpleLine(res0=$name, value='$value')"
}

class HeaderObject(@JvmField val title: String, @JvmField val icon: Int) : BaseViewModel {
    override fun toString(): String = "HeaderObject(title='$title', icon=$icon)"
}

class PermissionLine(val res0: String, val isGranted: Boolean, val protectionLevel: Int) : BaseViewModel

class FlagzObject(val flags: Int) : BaseViewModel {
    override fun toString(): String = "FlagzObject(flags=$flags)"
}

class InfoApkLine(val name: String, val value: String, @param:DrawableRes val icon: Int) : BaseViewModel

class ProviderLine(val icon: Drawable?, val label: String, val className: String,
    val exported: Boolean,
    val enabled: Boolean,
    val authority: String
) : BaseViewModel {
    constructor(label: String, className: String, authority: String)
            : this(null, label, className, true, true, authority)
}

class ReceiverLine //public final String authority;
    (
    val icon: Drawable?,
    val label: String, val receiverName: String,
    val exported: Boolean,
    val enabled: Boolean,
    var pkg: String
) : BaseViewModel

class V2Line (val key: String, val value: String, val drawable: Int? = null) : BaseViewModel


open class ActivityLine : BaseViewModel {
    var className: String = ""
    var label: String = ""
    var exported: Boolean = false
    var icon: Drawable? = null
    var intentFilters: List<IntentFilter> = emptyList()

    override fun toString(): String = "ActivityLine(className='$className', label='$label', exported=$exported)"
}

class CertLine(@JvmField val res0: String, @JvmField val value: String) : BaseViewModel {
    override fun toString(): String = "CertLine(res0='$res0', value='$value')"
}

class ServiceLine(val icon: Drawable?, val label: String, val className: String,
                  val exported: Boolean
) : BaseViewModel {
    override fun toString(): String =
        "ServiceLine(label='$label', class_name='$className', exported=$exported)"
} 