package com.walhalla.appextractor.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.XmlResourceParser
import com.walhalla.appextractor.activity.manifest.ManifestPresenterXml
import com.walhalla.ui.DLog.d
import java.io.FileNotFoundException
import java.io.IOException

object XmlUtils {
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
    fun isPinningEnabled(context: Context, packageName: String): Boolean {
        var am: AssetManager?
        var parser: XmlResourceParser?
        try {
            val otherContext = context.createPackageContext(packageName, 0)
            am = otherContext.assets
            parser = am.openXmlResourceParser(ManifestPresenterXml.NETWORK_SECURITY_CONFIG)
            return true
        } catch (name: PackageManager.NameNotFoundException) {
            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
            am = null
            parser = null
            d("@@@ sslotused")
            return false
        } catch (unexpected: RuntimeException) {
            d("@@@ error configuring for package: " + packageName + " " + unexpected.message)
            am = null
            parser = null
        } catch (e: FileNotFoundException) {
            //DLog.d("FileNotFoundException: " + packageName + " " + e.getClass().getSimpleName());
        } catch (e: IOException) {
            d("@@@ error: " + packageName + " " + e.javaClass.simpleName)
            am = null
            parser = null
        }
        return false
    } //    public static boolean findNetworkSecurityConfig(Context context, String packageName) {
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
