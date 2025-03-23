package com.walhalla.appextractor.activity.string

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.text.TextUtils
import com.walhalla.appextractor.resources.StringItemViewModel
import com.walhalla.appextractor.sdk.ResourcesToolForPlugin.getIconBySourceType
import com.walhalla.ui.DLog.d
import java.lang.reflect.Field
import java.util.Locale

class StringsPresenter(private val context: Context, private val mView: MvpContract.View) {
    var mPackageName: String? = null
    private var am: AssetManager? = null
    private var mResources: Resources? = null


    fun loadResourceContent(packageName: String, resourceType: String) {
        this.mPackageName = packageName

        val initAM = am
        val initRes = mResources
        try {
            am = context.createPackageContext(packageName, 0).assets
            mResources = Resources(am, context.resources.displayMetrics, null)
            //resources = new Resources(am, null, null);
        } catch (name: PackageManager.NameNotFoundException) {
            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
            am = initAM
            mResources = initRes
        } catch (unexpected: RuntimeException) {
            d("@@@ error configuring for package: " + packageName + " " + unexpected.message)
            am = initAM
            mResources = initRes
        }
        val m: MutableList<StringItemViewModel> = ArrayList()
        //loadApplicationResources(this, m, getPackageName());
        loadApplicationResources(context, m, packageName, resourceType)
        mView.showSuccess(m)
    }

    @SuppressLint("DiscouragedApi")
    private fun getResourceId(sourceName: String, sourceType: String): Int {
        if (mResources == null || TextUtils.isEmpty(mPackageName) || TextUtils.isEmpty(sourceName)) {
            return -1
        }
        return mResources!!.getIdentifier(sourceName, sourceType, mPackageName)
    }


    private fun loadApplicationResources(
        context: Context, iconPackResources: MutableList<StringItemViewModel>,
        packageName: String, resourceType: String
    ) { /*from w w w.  j a  va 2s. co  m*/

        val draw = getIconBySourceType(resourceType)
        val drawableItems: Array<Field>
        try {
            //Всегда ClassNotFoundException
            // Context appContext = context.createPackageContext(packageName,0);

            val appContext = context.createPackageContext(
                packageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )
            //drawableItems = Class.forName(packageName + ".R$drawable", true, appContext.getClassLoader()).getFields();
            drawableItems =
                Class.forName("$packageName.R$$resourceType", true, appContext.classLoader).fields
        } catch (e: ClassNotFoundException) {
            mView.showError("@ww@$e")
            return
        } catch (e: Exception) {
            d("@ww@$e")
            return
        }
        for (f in drawableItems) {
            var name = f.name
            val icon = name.lowercase(Locale.getDefault())
            name = name.replace("_".toRegex(), ".")
            var stringValue = ""
            val stringId = getResourceId(icon, resourceType)
            if (stringId > 0) {
                stringValue = try {
                    mResources!!.getString(stringId)
                } catch (e: Resources.NotFoundException) {
                    "Resources.NotFoundException $stringId"
                }
            }

            //iconPackResources.add(new ResItem(name + " " + icon + " " + stringValue, null));
            //@@iconPackResources.add(new StringItem(icon, stringValue, null));
            val activityIndex = name.lastIndexOf(".")
            if (activityIndex <= 0 || activityIndex == name.length - 1) {
                continue
            }

            val iconPackage = name.substring(0, activityIndex)
            if (TextUtils.isEmpty(iconPackage)) {
                continue
            }

            //iconPackResources.add(new ResItem(iconPackage + " " + icon, null));
            //@@iconPackResources.add(new StringItem(icon, stringValue, null));
            val iconActivity = name.substring(activityIndex + 1)
            if (TextUtils.isEmpty(iconActivity)) {
                continue
            }
            //iconPackResources.add(new ResItem(iconPackage + "." + iconActivity + " " + icon, null));
            iconPackResources.add(StringItemViewModel(icon, stringValue, draw!!))
        }
    }
}
