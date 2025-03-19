package com.walhalla.manifest

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.text.TextUtils
import com.walhalla.appextractor.presenter.BaseManifestPresenter
import com.walhalla.appextractor.presenter.ManifestPresenter.Companion.TAG_MANIFEST
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class ManifestPresenterXml(m: Context, mView: ManifestCallback) : BaseManifestPresenter(m, mView) {
    //String[] mPkgs = null;
    var packageName: String? = null
    private var assets: AssetManager? = null
    private var resources: Resources? = null
    private var apkPath: String? = null


    fun mOutgetText(xrp: XmlResourceParser, currentResources: Resources?): String {
        var aa = ""
        try {
            aa = getXMLText(xrp, currentResources).toString()
        } catch (ioe: IOException) {
            mView.showError("Reading XML", ioe)
        } catch (xppe: XmlPullParserException) {
            mView.showError("Parsing XML", xppe)
        }
        return aa
    }

    override fun configForPackage(packageName: String, apkPath0: String): Boolean {

        if (packageName.isNullOrEmpty()) {
            this.packageName = "android"
        }else{
            this.packageName = packageName
        }

        this.apkPath = apkPath0

        val initAM = assets
        val initRes = resources

        try {
//            PackageInfo pi = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
//            pi.applicationInfo.sourceDir = apkPath;
//            pi.applicationInfo.publicSourceDir = apkPath;

            //ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            //apkPath = applicationInfo.sourceDir;//sourcedir == public

            //Base APK
            //assetManager = context.createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE).getAssets();
            //assetManager = context.createPackageContext(packageName, 0).getAssets();
            //context.getPackageResourcePath()

            val otherAppContext = context.applicationContext.createPackageContext(packageName,
                0 //Context.CONTEXT_IGNORE_SECURITY
                //Context.CONTEXT_INCLUDE_CODE//Requesting code from com.google.android.youtube (with uid 10162) to be run in process com.walhalla.appextractor (with uid 10730)
            )
            assets = otherAppContext.assets

            //            try {
//                Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
//                addAssetPath.setAccessible(true);
//                Integer cookie = (Integer) addAssetPath.invoke(assets, apkPath);
//                if (cookie == null || cookie == 0) {
//                    mView.showError("cookie == 0 " + applicationInfo.sourceDir, null);
//                } else {
//                    //mView.showError("cookie  " + cookie, null);
//                }
//                //XmlResourceParser parser = am.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME);
//                //parseManifest(parser);
//            } catch (Exception e) {
//                DLog.handleException(e);
//                mView.showError("XML", e);
//            }

//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
//            XmlResourceParser xmlResourceParser0 = packageInfo.applicationInfo.loadXmlMetaData(context.getPackageManager(), TrustAgentService.TRUST_AGENT_META_DATA);
            val otherAppResources = otherAppContext.resources


//            int resourceId = otherAppResources.getIdentifier("app_name", "string", packageName);
//            String text = otherAppResources.getString(resourceId);
//            DLog.d("@@@" + text);


            //am = context.createPackageContext(packageName, Context.CONTEXT_RESTRICTED).getAssets();

            //default
            //resources = new Resources(am, context.getResources().getDisplayMetrics(), null);

            //resources = new Resources(am, null, null);
            //resources = new Resources(am, null, null);
            resources = otherAppResources


            //            DLog.d("@@@@@@@@@@@" + applicationInfo.sourceDir);
//            DLog.d("@@@@@@@@@@@" + applicationInfo.publicSourceDir);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                DLog.d("@@@@@@@@@@@" + Arrays.toString(applicationInfo.splitPublicSourceDirs));
//            }
        } catch (name: PackageManager.NameNotFoundException) {
            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
            assets = initAM
            resources = initRes
            return false
        } catch (unexpected: RuntimeException) {
            println("----------------------->" + packageName + " " + unexpected.message)
            assets = initAM
            resources = initRes
            return false
        }
        return true
    }

    //    public boolean configForPackage000(String packageName) {
    //        if (packageName == null || packageName.equals("")) {
    //            packageName = "android";
    //        }
    //
    //        AssetManager initAM = assetManager;
    //        Resources initRes = resources;
    //        try {
    //            assetManager = mView.createPackageContext(packageName, 0).getAssets();
    //            resources = new Resources(assetManager, getResources().getDisplayMetrics(), null);
    //            xmlResourceId = resources.getIdentifier("strings", "xml", packageName); // Получение идентификатора ресурса
    //
    //            // Перебор всех возможных идентификаторов ресурсов
    ////            for (int id = 1; id < Integer.MAX_VALUE; id++) {
    ////                try {
    ////                    String resourceName = resources.getResourceName(id); // Получение имени ресурса по идентификатору
    ////                    if (resourceName.startsWith(packageName)) {
    ////                        DLog.d("Resource found: " + resourceName);
    ////                    }
    ////                } catch (Resources.NotFoundException e) {
    ////                    // Ресурс не найден
    ////                }
    ////            }
    //
    //            String[] resourceTypes = {
    //                    "string", "drawable", "layout", "color", "dimen", "style", "array"
    //                    , "string", "drawable", "layout", "color", "dimen", "style", "array", "id"
    //            };
    //
    //            for (String resourceType : resourceTypes) {
    //                //int resourceId = resources.getIdentifier(resourceType, null, packageName);
    //                int resourceId = resources.getIdentifier("resource_name", resourceType, packageName);
    //
    //                if (resourceId != 0) {
    //                    DLog.d("Resource Type: " + resourceType + ", Resource ID: " + resourceId);
    //                } else {
    //                    DLog.d("Resource Type: " + resourceType + ", Resource ID: " + resourceId);
    //                }
    //            }
    ////            try {
    ////                // Получение идентификатора массива ресурсов строк
    ////                int resourceId = resources.getIdentifier("string", "array", getPackageName());
    ////
    ////                // Получение TypedArray для ресурса
    ////                TypedArray typedArray = resources.obtainTypedArray(resourceId);
    ////
    ////                // Перебор строковых ресурсов
    ////                for (int i = 0; i < typedArray.length(); i++) {
    ////                    int stringResourceId = typedArray.getResourceId(i, 0);
    ////                    String stringResourceValue = resources.getString(stringResourceId);
    ////                    DLog.d("String Resource Value: " + stringResourceValue);
    ////                }
    ////
    ////                // Освобождение ресурсов TypedArray
    ////                typedArray.recycle();
    ////            } catch (Exception e) {
    ////                DLog.handleException(e);
    ////            }
    //
    //        } catch (PackageManager.NameNotFoundException name) {
    //            Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
    //            assetManager = initAM;
    //            resources = initRes;
    //            return false;
    //        } catch (RuntimeException unexpected) {
    //            Log.e("@@@", "error configuring for package: " + packageName + " " + unexpected.getMessage());
    //            assetManager = initAM;
    //            resources = initRes;
    //            return false;
    //        }
    //        return true;
    //    }
    //    protected String[] getPackages() {
    //        ArrayList<String> res = new ArrayList<String>();
    //        PackageManager pm = getPackageManager();
    //        //List<PackageInfo> l = getPackageManager().getInstalledPackages(0xFFFFFFFF);
    //        List<PackageInfo> l = pm.getInstalledPackages(PackageManager.GET_META_DATA);
    //        for (PackageInfo pi : l)
    //            res.add(pi.packageName);
    //        return res.toArray(new String[res.size()]);
    //    }

    @SuppressLint("PrivateApi")
    fun mOutgetText(binaryXmlFileName: String): String {
        val filePath = "/"
        var cookie = 0
        val xml: XmlResourceParser

        try {
            if (!TextUtils.isEmpty(apkPath)) {
                try {
                    val addAssetPath = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
                    addAssetPath.isAccessible = true
                    println((assets == null).toString() + " " + apkPath)

                    cookie = addAssetPath.invoke(assets, apkPath) as Int
                    if (cookie == null || cookie == 0) {
                        mView.showError("cookie == 0 ", null)
                    } else {
                        //mView.showError("cookie  " + cookie, null);
                    }

                    //parseManifest(parser);
                } catch (e: Exception) {
                    handleException(e)
                    mView.showError("XML", e)
                }
            }
            xml = if (cookie == null || cookie == 0) {
                //mView.showError("{0}", null);
                assets!!.openXmlResourceParser(binaryXmlFileName)
            } else {
                assets!!.openXmlResourceParser(cookie, binaryXmlFileName)
            }

            //            am.openXmlResourceParser(cookie, binaryXmlFileName);
            val xmlText = getXMLText(xml, resources)
            //this.mOut.append(xmlText);
//            this.mOut.loadData(
//                    "<manifest\n" +
//                            "    versionCode=\"221208\"/>"
//
//                    , "text/xml; charset=UTF-8", null);
            return xmlText.toString()
        } catch (ioe: Exception) {
            mView.showError("Reading XML", ioe)
        }
        return ""
    }

    @SuppressLint("PrivateApi")
    fun renderSimpleText(xmlFile: String) {
        try {
            //int xmlResourceId = resources.getIdentifier("AndroidManifest", "xml", packageName);
            //XmlResourceParser xml = resources.getXml(xmlResourceId);
            var cookie = 0
            var xml: XmlResourceParser

            if (!TextUtils.isEmpty(apkPath)) {
                try {
                    //java.lang.NullPointerException: null receiver

                    val addAssetPath = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
                    addAssetPath.isAccessible = true
                    cookie = addAssetPath.invoke(assets, apkPath) as Int


                    if (cookie == null || cookie == 0) {
                        mView.showError("cookie == 0 ", null)
                    } else {
                        //mView.showError("cookie  " + cookie, null);
                    }

                    //parseManifest(parser);
                } catch (e: Exception) {
                    handleException(e)
                    mView.showError("@@@@@@@@@@@@@@@@@@@@@@@@@@@", e)
                }
            }
            if (cookie == 0) {
                //mView.showError("{0}", null);
                xml = assets!!.openXmlResourceParser(xmlFile)
            } else {
                xml = assets!!.openXmlResourceParser(cookie, xmlFile)
                if (xml == null) {
                    xml = assets!!.openXmlResourceParser(xmlFile)
                }
            }

            //            ApplicationInfo appInfo = getPackageManager().getApplicationInfo("ваше_пакетное_имя_приложения", PackageManager.GET_META_DATA);
//            String apkPath = appInfo.sourceDir; // Путь к базовому APK
//            am.addAssetPath(apkPath);
//            XmlResourceParser xmlResourceParser = assetManager.openXmlResourceParser(lllll);

            //String txt = "/sdcard/" + getPackageName() + ".txt";
            val xmlText = getXMLText(xml, resources)

            //String txt0 = convertStreamToReadableString("<manifest\n" + "    versionCode=\"221208\"/>");

            //this.mOut.append(xmlText);
//            this.mOut.loadData(
//                    "<manifest\n" +
//                            "    versionCode=\"221208\"/>"
//
//                    , "text/xml; charset=UTF-8", null);
            mView.showManifestContent(xmlText.toString()) //loadDataWithPatternXML
        } catch (ioe: Exception) {
            mView.showError("Reading XML", ioe)
        }
    }


    //==========================================================================================
    fun renderXML(xmlFile: String) {
        try {
            var cookie = 0

            if (!TextUtils.isEmpty(apkPath)) {
                try {
                    val addAssetPath =
                        AssetManager::class.java.getDeclaredMethod(
                            "addAssetPath",
                            String::class.java
                        )
                    addAssetPath.isAccessible = true
                    d((assets == null).toString() + " " + apkPath)

                    cookie = addAssetPath.invoke(assets, apkPath) as Int
                    if (cookie == null || cookie == 0) {
                        mView.showError("cookie == 0 ", null)
                    } else {
                        //mView.showError("cookie  " + cookie, null);
                    }

                    //parseManifest(parser);
                } catch (e: Exception) {
                    handleException(e)
                    mView.showError("XML", e)
                }
            }
            val xml = if (cookie == null || cookie == 0) {
                //mView.showError("{0}", null);
                assets!!.openXmlResourceParser(xmlFile)
            } else {
                assets!!.openXmlResourceParser(cookie, xmlFile)
            }
            val xmlText = getHTMLText(xml, resources)
            loadDataWithPatternHTML(xmlText.toString())
        } catch (ioe: Exception) {
            mView.showError("Reading XML", ioe)
        }
    }

    private fun getHTMLText(xrp: XmlResourceParser, currentResources: Resources?): CharSequence {
        val sb = StringBuffer()

        //sb.append("<pre>");
        var indent0 = 0
        try {
            var eventType = xrp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (XmlPullParser.START_TAG == eventType) {
                    if ((xrp.name == TAG_MANIFEST)) {
                        sb.append("<div class=\"folder\" id=\"folder0\">")
                    } else {
                    }
                    indent0 += 1
                    sb.append("<div class=\"line " + xrp.name + "\"><span class=\"folder-button fold\"></span>")
                    insertSpacesHTML(sb, indent0)
                    sb.append("<span class=\"html-tag_a\">&lt;" + xrp.name + "</span>")
                    sb.append("<span>" + getHTMLAttribs(xrp, currentResources) + "&gt;</span>")
                } else if (XmlPullParser.END_TAG == eventType) {
                    indent0 -= 1
                    sb.append("\n")
                    insertSpacesHTML(sb, indent0)
                    sb.append("<span class=\"html-tag_a\">&lt;/" + xrp.name + "&gt;</span>")
                    sb.append("</div>")

                    if ((xrp.name == TAG_MANIFEST)) {
                        sb.append("</div>")
                    } else {
                    }
                }
                when (eventType) {
                }
                eventType = xrp.nextToken()
            }
        } catch (ioe: IOException) {
            mView.showError("Reading XML", ioe)
        } catch (xppe: XmlPullParserException) {
            mView.showError("Parsing XML", xppe)
        }
        return sb
    }

    fun getHTMLAttribs(xrp: XmlResourceParser, currentResources: Resources?): CharSequence {
        val sb = StringBuffer()
        for (i in 0 until xrp.attributeCount) {
            val name = xrp.getAttributeName(i)
            val value = resolveValue(name, xrp.getAttributeValue(i), currentResources)
            sb.append(10.toChar())
                .append("<span class=\"html-attribute\">\n<span class=\"html-attribute-name\">$name</span>=\"<span class=\"html-attribute-value\">$value</span>\"</span>")
            //DLog.d("@@@"+(char)10 +  name+ "=\"" + value + "\"");
        }
        return sb
    }

    fun insertSpacesHTML(sb: StringBuffer?, num: Int) {
        if (sb == null) return
        for (i in 0 until num) {
            //sb.append(" ");
            sb.append("<span> </span>")
        }
    }

    private fun loadDataWithPatternHTML(s: String) {
        try {
            val inputStream = context.assets.open("pattern.html")
            val br = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            //            byte[] buffer = new byte[inputStream.available()];
//            inputStream.read(buffer);
//            inputStream.close();
//            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            val sb = StringBuilder()
            var str: String?
            while ((br.readLine().also { str = it }) != null) {
                sb.append(str)
            }
            br.close()
            var encoded = sb.toString()
            encoded = encoded.replace("@_DATA_@", s)
            mView.loadDataWithPatternHTML(encoded) //loadDataWithPatternHTML
        } catch (e: Exception) {
            handleException(e)
        }
    }

    companion object {
        const val ANDROID_MANIFEST_FILENAME: String = "AndroidManifest.xml"
        const val NETWORK_SECURITY_CONFIG: String = "res/xml/network_security_config.xml"


        @Throws(XmlPullParserException::class, IOException::class)
        fun getXMLText(xrp: XmlResourceParser, currentResources: Resources?): CharSequence {
            val buffer = StringBuffer()
            var indent = 0
            var eventType = xrp.eventType
            var name: String? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                // for buffer
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (name != null) {
                            //close previose
                            buffer.append(">")
                        }

                        indent += 1
                        name = xrp.name
                        buffer.append("\n")
                        insertXMLSpaces(buffer, indent)
                        buffer.append("<").append(name)
                        buffer.append(getAttribs(xrp, currentResources))
                    }

                    XmlPullParser.END_TAG -> {
                        if (xrp.name == name) {
                            //close previose
                            indent -= 1
                            buffer.append(" />")
                        } else {
                            indent -= 1
                            buffer.append("\n")
                            insertXMLSpaces(buffer, indent)
                            buffer.append("</").append(xrp.name).append(">")
                        }
                        name = null
                    }

                    XmlPullParser.TEXT -> buffer.append("").append(xrp.text)
                    XmlPullParser.CDSECT -> buffer.append("<!CDATA[").append(xrp.text).append("]]>")
                    XmlPullParser.PROCESSING_INSTRUCTION -> buffer.append("<?").append(xrp.text)
                        .append("?>")

                    XmlPullParser.COMMENT -> buffer.append("<!--").append(xrp.text).append("-->")
                }
                eventType = xrp.nextToken()
            }
            return buffer
        }


        fun insertXMLSpaces(sb: StringBuffer?, num: Int) {
            if (sb == null) return
            for (i in 0 until num) {
                sb.append(" ")
            }
        }

        fun getAttribs(xrp: XmlResourceParser, currentResources: Resources?): CharSequence {
            val sb = StringBuffer()
            for (i in 0 until xrp.attributeCount) {
                val name = xrp.getAttributeName(i)
                val value = resolveValue(name, xrp.getAttributeValue(i), currentResources)
                sb.append(" ").append(name).append("=\"").append(value).append("\"")
                //DLog.d("@@@"+(char)10 +  name+ "=\"" + value + "\"");
            }
            return sb
        }
    }
}
