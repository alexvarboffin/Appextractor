package com.walhalla.appextractor.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;

import com.walhalla.appextractor.activity.manifest.ManifestPresenterXml;
import com.walhalla.ui.DLog;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class XmlUtils {

//    public static String getXMLText(XmlResourceParser xrp, Resources resources) throws XmlPullParserException, IOException{
//        StringBuilder text = new StringBuilder();
//        while (xrp.getEventType() != XmlPullParser.END_DOCUMENT) {
//            if (xrp.getEventType() == XmlPullParser.TEXT) {
//
//                text.append(xrp.getText());// Если текущее событие - текстовое содержимое
//            }else {
//                text.append(xrp.getEventType());
//            }
//            xrp.next();
//        }
//        return text.toString();
//    }

    public static boolean isPinningEnabled(Context context, String packageName) {
        AssetManager am;
        XmlResourceParser parser;
        try {
            Context otherContext = context.createPackageContext(packageName, 0);
            am = otherContext.getAssets();
            parser = am.openXmlResourceParser(ManifestPresenterXml.NETWORK_SECURITY_CONFIG);
            return true;
        } catch (PackageManager.NameNotFoundException name) {
            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
            am = null;
            parser = null;
            DLog.d("@@@ sslotused");
            return false;
        } catch (RuntimeException unexpected) {
            DLog.d("@@@ error configuring for package: " + packageName + " " + unexpected.getMessage());
            am = null;
            parser = null;
        } catch (FileNotFoundException e) {
            //DLog.d("FileNotFoundException: " + packageName + " " + e.getClass().getSimpleName());
        } catch (IOException e) {
            DLog.d("@@@ error: " + packageName + " " + e.getClass().getSimpleName());
            am = null;
            parser = null;
        }
        return false;
    }

//    public static boolean findNetworkSecurityConfig(Context context, String packageName) {
//        AssetManager am;
//        try {
//            Context otherContext = context.createPackageContext(packageName, 0);
//            am = otherContext.getAssets();
//            XmlResourceParser parser = am.openXmlResourceParser(ManifestPresenterXml.NETWORK_SECURITY_CONFIG);
//            try {
//                List<String> aa0 = parseXml(parser);
//                if(aa0.size()>0){
//                    DLog.d("============================");
//                    DLog.d(packageName);
//                    DLog.d("============================");
//                    for (String s : aa0) {
//                        DLog.d("https://"+s);
//                    }
//                }
//            } catch (IOException ioe) {
//                DLog.handleException(ioe);
//            } catch (XmlPullParserException xppe) {
//                DLog.handleException(xppe);
//            }
//            return true;
//        } catch (PackageManager.NameNotFoundException name) {
//            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
//            am = null;
//        } catch (RuntimeException unexpected) {
//            DLog.d("@@@ error configuring for package: " + packageName + " " + unexpected.getMessage());
//            am = null;
//        } catch (FileNotFoundException e) {
//            //DLog.d("FileNotFoundException: " + packageName + " " + e.getClass().getSimpleName());
//        } catch (IOException e) {
//            DLog.d("@@@ error: " + packageName + " " + e.getClass().getSimpleName());
//        }
//        return false;
//    }
}
