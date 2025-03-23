//package com.walhalla.appextractor.activity.resources.p002
//
//import android.content.Context
//import android.content.pm.PackageManager
//import android.content.res.AssetManager
//import android.content.res.Resources
//import com.walhalla.appextractor.activity.assets.ResItem
//import com.walhalla.ui.DLog.d
//import com.walhalla.ui.DLog.handleException
//import java.lang.reflect.Field
//import java.lang.reflect.InvocationTargetException
//
//class ManifestPresenter2(private val context: Context, private val view: ManifestPresenter2View) {
//
//    private val packageName: String? = null
//    private var am: AssetManager? = null
//    private val resources: Resources? = null
//    private val xmlResourceId = 0
//
//
//
//    interface ManifestPresenter2View {
//        fun showManifestContent(content: String)
//        fun showError(message: String)
//
//        fun showSuccess(list: List<ResItem>)
//    }
//
//    fun configForPackage(packageName: String?): Boolean {
//        if (packageName == null || packageName == "") {
//            //packageName = "com.zaimi.online.na.kartu.Buckridge";
//            //packageName = "com.walhalla.appextractor";
//        }
//
//        try {
//            am = context.createPackageContext(packageName, 0).assets
//            val context1 = context.createPackageContext(packageName, 0)
//            //            PackageManager packageManager = context1.getPackageManager();
////
////            //Resources resources = packageManager.getResourcesForApplication(packageName);
////            Resources resources = new Resources(am, context1.getResources().getDisplayMetrics(), null);
//            val packageManager = context1.packageManager
//            val applicationInfo = packageManager.getApplicationInfo(
//                packageName!!, PackageManager.GET_META_DATA
//            )
//            val resources = packageManager.getResourcesForApplication(applicationInfo)
//            val fields0 = resources.javaClass.declaredFields
//
//
//            //ТОЛЬКО ТАК РАБОТАЕТ, null не принимает
//            val resourceId =
//                context.resources.getIdentifier("app_name", "string", context.packageName)
//            d("@@@@@@@@@@$resourceId")
//
//            //AssetManager.getResourceIdentifier(name, defType, defPackage);
//
////            Class<?> assetManagerClass = AssetManager.class;
////            Method getResourceIdentifierMethod = assetManagerClass.getDeclaredMethod("getResourceIdentifier", String.class, String.class, String.class);
////            getResourceIdentifierMethod.setAccessible(true);
////            resourceId = (int) getResourceIdentifierMethod.invoke(am, "app_name", "string", context.getPackageName());
//            d("@@@@@@@@@@$resourceId")
//
//            if (77 == 87) {
//                for (field in fields0) {
//                    d("@ Raw Resource" + " " + resourceId + " " + fields0.size + " " + field)
//                    if (field.name.startsWith("raw_")) {
//                        val rawResourceId = field.getInt(resources)
//                        val resourceName = resources.getResourceEntryName(rawResourceId)
//                        d("@ Raw Resource$resourceName")
//                    }
//                }
//
//                var mResourcesImplField: Field? = null
//                for (field in fields0) {
//                    if (field.name == "mResourcesImpl") {
//                        mResourcesImplField = field
//                        break
//                    }
//                }
//                if (mResourcesImplField != null) {
//                    mResourcesImplField.isAccessible = true
//                }
//                var mResourcesImplObject: Any? = null
//                d("@@@@@@@@@@$mResourcesImplField")
//
//                if (mResourcesImplField != null) {
//                    try {
//                        mResourcesImplObject = mResourcesImplField[resources]
//                        d("@@@@@@@@@@$mResourcesImplObject")
//                        val fields = resources.javaClass.declaredFields
//                        for (resField in fields) {
//                            d("@@@@@@@@@@$resField")
//                            if (resField.type == Int::class.javaPrimitiveType && resField.name.startsWith(
//                                    "raw_"
//                                )
//                            ) {
//                                val rawResourceId = resField.getInt(resources)
//                                val resourceName = resources.getResourceEntryName(rawResourceId)
//                                d("@@@@@@@@@@$resourceName")
//                            }
//                        }
//                    } catch (e: IllegalAccessException) {
//                        handleException(e)
//                    }
//                }
//            }
//
//            test()
//        } catch (e: Exception) {
//            handleException(e)
//        }
//        return true
//    }
//
//    private fun test() {
//        try {
//            val packageManager = context.packageManager
//            val resources = packageManager.getResourcesForApplication(
//                packageName!!
//            )
//            val assetManager = resources.assets
//
//            val assetManagerClass: Class<*> = AssetManager::class.java
//            val getStringBlockMethod = assetManagerClass.getDeclaredMethod("getStringBlock")
//            getStringBlockMethod.isAccessible = true
//            val stringBlock = getStringBlockMethod.invoke(assetManager)
//
//            val fields = stringBlock!!.javaClass.declaredFields
//            for (field in fields) {
//                d("@@@@@@@@@@$field")
//                if (field.type == String::class.java) {
//                    field.isAccessible = true
//                    val resourceName = field[stringBlock] as String
//                    val resourceId = resources.getIdentifier(resourceName, "string", packageName)
//                    d("String ResourceName: $resourceName, ID: $resourceId")
//                }
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//            handleException(e)
//        } catch (e: NoSuchMethodException) {
//            handleException(e)
//        } catch (e: IllegalAccessException) {
//            handleException(e)
//        } catch (e: InvocationTargetException) {
//            handleException(e)
//        }
//    }
//
//
//    fun loadManifestContent(packageName: String) {
//        configForPackage(packageName)
//    }
//}
//
