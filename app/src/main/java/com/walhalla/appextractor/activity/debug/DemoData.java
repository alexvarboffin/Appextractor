package com.walhalla.appextractor.activity.debug;

import android.content.Context;

import com.walhalla.appextractor.BuildConfig;
import com.walhalla.appextractor.model.PackageMeta;

public class DemoData {

    public static String[] firebaseHosts = new String[]{
//            "https://~.firebaseio.com/.json",
//            "https://~.firebasedatabase.app/.json",
//
//            "https://~-default-rtdb.europe-west1.firebasedatabase.app/.json",
//            "https://~-default-rtdb.us-central1.firebasedatabase.app/.json",//+
//            "https://~-default-rtdb.asia-southeast1.firebasedatabase.app/.json",
//            //"https://~-default-rtdb.europe-west1.firebaseio.com/.json",
//            "https://~-default-rtdb.firebaseio.com/.json"//+
    };

    public static PackageMeta demoData(Context context, PackageMeta meta) {
        if (meta == null && BuildConfig.DEBUG) {
            //meta = new PackageMeta("ua.com.testds.free", "vpn");
            //meta = new PackageMeta("com.google.android.youtube", "jokermask");
            //meta = new PackageMeta("com.creditna.kartu.bluapp", "jokermask");
            //meta = new PackageMeta("com.ang.creditonline", "jokermask");
            //meta = new PackageMeta("ua.com.testds.free", "jokermask");
            //meta = new PackageMeta("org.freevpn", "jokermask");
            //meta = new PackageMeta("com.oppo.ota", "jokermask");
            //meta = new PackageMeta("app.source.getcontact", "jokermask");
            //meta = new PackageMeta("com.softmedya.footballprediction", "jokermask");
            //meta = new PackageMeta("video.like", "jokermask");
            meta = new PackageMeta("com.instagram.android", "jokermask");

            //meta = new PackageMeta(context.getPackageName(), "jokermask");

        }
        return meta;
    }
}
