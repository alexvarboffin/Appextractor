package com.walhalla.appextractor.presenter

import android.content.Context
import android.content.res.Resources
import com.walhalla.ui.DLog


abstract class BaseManifestPresenter(
    protected val context: Context,
    protected val mView: ManifestCallback
) :
    ManifestPresenter {
    interface ManifestCallback {
        fun showError(readingXml: String, throwable: Throwable?)

        fun showManifestContent(content: String)

        fun loadDataWithPatternHTML(formatted: String)
    }


    companion object {
        /**
         * returns the value, resolving it through the provided resources if it
         * appears to be a resource ID. Otherwise just returns what was provided.
         *
         * @param in String to resolve
         * @param r  Context appropriate resource (system for system, package's for
         * package)
         * @return Resolved value, either the input, or some other string.
         */
        fun resolveValue(name: String, `in`: String?, r: Resources?): String? {
            if (`in` == null || !`in`.startsWith("@") || r == null) return `in`
            var num = 0
            try {
                num = `in`.substring(1).toInt()
                if ("theme" == name) {
                    val tmp = r.getResourceName(num)
                    return if (tmp.startsWith("android:style")) {
                        tmp
                    } else {
                        tmp.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[1]
                    }
                }
                return r.getString(num)
            } catch (e: NumberFormatException) {
                return `in`
            } catch (e: RuntimeException) {
                // formerly noted errors here, but simply not resolving works better
                DLog.d(e.localizedMessage + " " + name + " " + r.getResourceEntryName(num))
                return `in`
            }
        }
    }
}
