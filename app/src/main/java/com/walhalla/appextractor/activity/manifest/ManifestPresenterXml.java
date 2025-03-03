package com.walhalla.appextractor.activity.manifest;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.walhalla.appextractor.presenter.BaseManifestPresenter;
import com.walhalla.ui.DLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ManifestPresenterXml extends BaseManifestPresenter {


    public static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";
    public static final String NETWORK_SECURITY_CONFIG = "res/xml/network_security_config.xml";


    //String[] mPkgs = null;

    String packageName = null;
    private AssetManager assets = null;
    private Resources resources = null;
    private String apkPath = null;


    public ManifestPresenterXml(Context m, ManifestCallback mView) {
        super(m, mView);
    }

    public String getPackageName() {
        return this.packageName;
    }


    public static CharSequence getXMLText(@NonNull XmlResourceParser xrp, Resources currentResources)
            throws XmlPullParserException, IOException {


        StringBuffer buffer = new StringBuffer();
        int indent = 0;
        int eventType = xrp.getEventType();
        String name = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            // for buffer
            switch (eventType) {
                case XmlPullParser.START_TAG:

                    if (name != null) {
                        //close previose
                        buffer.append(">");
                    }

                    indent += 1;
                    name = xrp.getName();
                    buffer.append("\n");
                    insertXMLSpaces(buffer, indent);
                    buffer.append("<").append(name);
                    buffer.append(getAttribs(xrp, currentResources));

                    //DLog.d("1122"+xrp.getText());//null

                    break;
                case XmlPullParser.END_TAG:

                    if (xrp.getName().equals(name)) {
                        //close previose
                        indent -= 1;
                        buffer.append(" />");
                    } else {
                        indent -= 1;
                        buffer.append("\n");
                        insertXMLSpaces(buffer, indent);
                        buffer.append("</").append(xrp.getName()).append(">");
                    }
                    name = null;
                    break;

                case XmlPullParser.TEXT:
                    buffer.append("").append(xrp.getText());
                    break;

                case XmlPullParser.CDSECT:
                    buffer.append("<!CDATA[").append(xrp.getText()).append("]]>");
                    break;

                case XmlPullParser.PROCESSING_INSTRUCTION:
                    buffer.append("<?").append(xrp.getText()).append("?>");
                    break;

                case XmlPullParser.COMMENT:
                    buffer.append("<!--").append(xrp.getText()).append("-->");
                    break;
            }
            eventType = xrp.nextToken();
        }
        return buffer;
    }


    public static void insertXMLSpaces(StringBuffer sb, int num) {
        if (sb == null) return;
        for (int i = 0; i < num; i++) {
            sb.append(" ");
        }
    }

    public static CharSequence getAttribs(XmlResourceParser xrp, Resources currentResources) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < xrp.getAttributeCount(); i++) {
            String name = xrp.getAttributeName(i);
            String value = resolveValue(name, xrp.getAttributeValue(i), currentResources);
            sb.append(" ").append(name).append("=\"").append(value).append("\"");
            //DLog.d("@@@"+(char)10 +  name+ "=\"" + value + "\"");
        }
        return sb;
    }


    public String mOutgetText(XmlResourceParser xrp, Resources currentResources) {

        String aa = "";
        try {
            aa = getXMLText(xrp, currentResources).toString();
        } catch (IOException ioe) {
            mView.showError("Reading XML", ioe);
        } catch (XmlPullParserException xppe) {
            mView.showError("Parsing XML", xppe);
        }
        return aa;
    }

    public boolean configForPackage(String packageName, String apkPath0) {
        if (packageName == null || packageName.equals("")) {
            packageName = "android";
        }
        this.packageName = packageName;
        this.apkPath = apkPath0;

        AssetManager initAM = assets;
        Resources initRes = resources;

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

            Context otherAppContext = context.getApplicationContext().createPackageContext(packageName,
                    0
                    //Context.CONTEXT_IGNORE_SECURITY
                    //Context.CONTEXT_INCLUDE_CODE//Requesting code from com.google.android.youtube (with uid 10162) to be run in process com.walhalla.appextractor (with uid 10730)
            );
            assets = otherAppContext.getAssets();
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

            Resources otherAppResources = otherAppContext.getResources();
//            int resourceId = otherAppResources.getIdentifier("app_name", "string", packageName);
//            String text = otherAppResources.getString(resourceId);
//            DLog.d("@@@" + text);


            //am = context.createPackageContext(packageName, Context.CONTEXT_RESTRICTED).getAssets();

            //default
            //resources = new Resources(am, context.getResources().getDisplayMetrics(), null);

            //resources = new Resources(am, null, null);
            //resources = new Resources(am, null, null);

            resources = otherAppResources;
//            DLog.d("@@@@@@@@@@@" + applicationInfo.sourceDir);
//            DLog.d("@@@@@@@@@@@" + applicationInfo.publicSourceDir);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                DLog.d("@@@@@@@@@@@" + Arrays.toString(applicationInfo.splitPublicSourceDirs));
//            }


        } catch (PackageManager.NameNotFoundException name) {
            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
            assets = initAM;
            resources = initRes;
            return false;
        } catch (RuntimeException unexpected) {
            DLog.d("----------------------->" + packageName + " " + unexpected.getMessage());
            assets = initAM;
            resources = initRes;
            return false;
        }
        return true;
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

    public String mOutgetText(String binaryXmlFileName) {

        String filePath = "/";
        Integer cookie = 0;
        XmlResourceParser xml;

        try {

            if (!TextUtils.isEmpty(apkPath)) {
                try {
                    Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
                    addAssetPath.setAccessible(true);
                    DLog.d((assets == null) + " " + apkPath);

                    cookie = (Integer) addAssetPath.invoke(assets, apkPath);
                    if (cookie == null || cookie == 0) {
                        mView.showError("cookie == 0 ", null);
                    } else {
                        //mView.showError("cookie  " + cookie, null);
                    }

                    //parseManifest(parser);
                } catch (Exception e) {
                    DLog.handleException(e);
                    mView.showError("XML", e);
                }
            }
            if (cookie == null || cookie == 0) {
                //mView.showError("{0}", null);
                xml = assets.openXmlResourceParser(binaryXmlFileName);
            } else {
                xml = assets.openXmlResourceParser(cookie, binaryXmlFileName);
            }

//            am.openXmlResourceParser(cookie, binaryXmlFileName);

            CharSequence xmlText = getXMLText(xml, resources);
//this.mOut.append(xmlText);
//            this.mOut.loadData(
//                    "<manifest\n" +
//                            "    versionCode=\"221208\"/>"
//
//                    , "text/xml; charset=UTF-8", null);
            return xmlText.toString();
        } catch (Exception ioe) {
            mView.showError("Reading XML", ioe);
        }
        return "";
    }

    public void renderSimpleText(String xmlFile) {
        try {
            //int xmlResourceId = resources.getIdentifier("AndroidManifest", "xml", packageName);
            //XmlResourceParser xml = resources.getXml(xmlResourceId);
            Integer cookie = 0;
            XmlResourceParser xml;

            if (!TextUtils.isEmpty(apkPath)) {
                try {

                    //java.lang.NullPointerException: null receiver

                    Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
                    addAssetPath.setAccessible(true);
                    cookie = (Integer) addAssetPath.invoke(assets, apkPath);


                    if (cookie == null || cookie == 0) {
                        mView.showError("cookie == 0 ", null);
                    } else {
                        //mView.showError("cookie  " + cookie, null);
                    }

                    //parseManifest(parser);
                } catch (Exception e) {
                    DLog.handleException(e);
                    mView.showError("@@@@@@@@@@@@@@@@@@@@@@@@@@@", e);
                }
            }
            if (cookie == 0) {
                //mView.showError("{0}", null);
                xml = assets.openXmlResourceParser(xmlFile);
            } else {
                xml = assets.openXmlResourceParser(cookie, xmlFile);
                if (xml == null) {
                    xml = assets.openXmlResourceParser(xmlFile);
                }
            }

//            ApplicationInfo appInfo = getPackageManager().getApplicationInfo("ваше_пакетное_имя_приложения", PackageManager.GET_META_DATA);
//            String apkPath = appInfo.sourceDir; // Путь к базовому APK
//            am.addAssetPath(apkPath);
//            XmlResourceParser xmlResourceParser = assetManager.openXmlResourceParser(lllll);

            //String txt = "/sdcard/" + getPackageName() + ".txt";

            CharSequence xmlText = getXMLText(xml, resources);
            //String txt0 = convertStreamToReadableString("<manifest\n" + "    versionCode=\"221208\"/>");

            //this.mOut.append(xmlText);
//            this.mOut.loadData(
//                    "<manifest\n" +
//                            "    versionCode=\"221208\"/>"
//
//                    , "text/xml; charset=UTF-8", null);

            mView.showManifestContent(xmlText.toString());//loadDataWithPatternXML
        } catch (Exception ioe) {
            mView.showError("Reading XML", ioe);
        }
    }


    //==========================================================================================
    public void renderXML(String xmlFile) {
        try {
            Integer cookie = 0;
            XmlResourceParser xml;

            if (!TextUtils.isEmpty(apkPath)) {
                try {
                    Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
                    addAssetPath.setAccessible(true);
                    DLog.d((assets == null) + " " + apkPath);

                    cookie = (Integer) addAssetPath.invoke(assets, apkPath);
                    if (cookie == null || cookie == 0) {
                        mView.showError("cookie == 0 ", null);
                    } else {
                        //mView.showError("cookie  " + cookie, null);
                    }

                    //parseManifest(parser);
                } catch (Exception e) {
                    DLog.handleException(e);
                    mView.showError("XML", e);
                }
            }
            if (cookie == null || cookie == 0) {
                //mView.showError("{0}", null);
                xml = assets.openXmlResourceParser(xmlFile);
            } else {
                xml = assets.openXmlResourceParser(cookie, xmlFile);
            }
            CharSequence xmlText = getHTMLText(xml, resources);
            loadDataWithPatternHTML(xmlText.toString());
        } catch (Exception ioe) {
            mView.showError("Reading XML", ioe);
        }
    }

    private CharSequence getHTMLText(XmlResourceParser xrp, Resources currentResources) {
        StringBuffer sb = new StringBuffer();
        //sb.append("<pre>");

        int indent0 = 0;
        try {
            int eventType = xrp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (XmlPullParser.START_TAG == eventType) {
                    if ((xrp.getName().equals(TAG_MANIFEST))) {
                        sb.append("<div class=\"folder\" id=\"folder0\">");
                    } else {

                    }
                    indent0 += 1;
                    sb.append("<div class=\"line " + xrp.getName() + "\"><span class=\"folder-button fold\"></span>");
                    insertSpacesHTML(sb, indent0);
                    sb.append("<span class=\"html-tag_a\">&lt;" + xrp.getName() + "</span>");
                    sb.append("<span>" + getHTMLAttribs(xrp, currentResources) + "&gt;</span>");
                } else if (XmlPullParser.END_TAG == eventType) {

                    indent0 -= 1;
                    sb.append("\n");
                    insertSpacesHTML(sb, indent0);
                    sb.append("<span class=\"html-tag_a\">&lt;/" + xrp.getName() + "&gt;</span>");
                    sb.append("</div>");

                    if ((xrp.getName().equals(TAG_MANIFEST))) {
                        sb.append("</div>");
                    } else {

                    }
                }
                switch (eventType) {


//                    case XmlPullParser.TEXT:
//                        sb.append("<span>" + xrp.getText() + "</span>");
//                        break;
//
//                    case XmlPullParser.CDSECT:
//                        sb.append("<span>" + xrp.getText() + "</span>");
//                        break;
//
//                    case XmlPullParser.PROCESSING_INSTRUCTION:
//                        sb.append("<span>" + xrp.getText() + "</span>");
//                        break;
//
//                    case XmlPullParser.COMMENT:
//                        sb.append("<!--" + xrp.getText() + "-->");
//                        break;
                }
                eventType = xrp.nextToken();
            }
        } catch (IOException ioe) {
            mView.showError("Reading XML", ioe);
        } catch (XmlPullParserException xppe) {
            mView.showError("Parsing XML", xppe);
        }
        return sb;
    }

    public CharSequence getHTMLAttribs(XmlResourceParser xrp, Resources currentResources) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < xrp.getAttributeCount(); i++) {
            String name = xrp.getAttributeName(i);
            String value = resolveValue(name, xrp.getAttributeValue(i), currentResources);
            sb.append((char) 10).append("<span class=\"html-attribute\">\n" + "<span class=\"html-attribute-name\">" + name + "</span>=\"<span class=\"html-attribute-value\">" + value + "</span>\"</span>");
            //DLog.d("@@@"+(char)10 +  name+ "=\"" + value + "\"");
        }
        return sb;
    }

    public void insertSpacesHTML(StringBuffer sb, int num) {
        if (sb == null) return;
        for (int i = 0; i < num; i++) {
            //sb.append(" ");
            sb.append("<span> </span>");
        }
    }

    private void loadDataWithPatternHTML(String s) {
        try {
            InputStream inputStream = context.getAssets().open("pattern.html");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//            byte[] buffer = new byte[inputStream.available()];
//            inputStream.read(buffer);
//            inputStream.close();
//            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
            String encoded = sb.toString();
            encoded = encoded.replace("@_DATA_@", s);
            mView.loadDataWithPatternHTML(encoded);//loadDataWithPatternHTML
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }
}
