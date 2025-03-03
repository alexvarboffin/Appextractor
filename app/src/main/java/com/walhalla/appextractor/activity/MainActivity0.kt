package com.walhalla.appextractor.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import com.walhalla.appextractor.R
import com.walhalla.appextractor.Troubleshooting
import com.walhalla.appextractor.abba.IntentReaper
import com.walhalla.ui.DLog.d
import com.walhalla.ui.plugins.Launcher.openBrowser
import com.walhalla.ui.plugins.Launcher.rateUs
import com.walhalla.ui.plugins.Module_U.aboutDialog
import com.walhalla.ui.plugins.Module_U.feedback
import com.walhalla.ui.plugins.Module_U.moreApp
import com.walhalla.ui.plugins.Module_U.shareThisApp
import java.io.File
import java.util.Objects

class MainActivity0 : AppCompatActivity() {
    private var reaper: IntentReaper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSupportActionBar(findViewById(R.id.toolbar))
        reaper = IntentReaper(this)

        val extension = "apk"
        val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)


        //String type = getContentResolver().getType(path);

        //getSupportActionBar().setSubtitle("" + mimetype);

        //Util.installApp(this, );

//        for (String mime : Mimiq.mime) {
//            makeMime("application/vnd.android.package-archive");
//        }


        //reaper.makeMimeApk(apk);
        reaper!!.makemimeProxy()
    }

    var apk: File =
        File("/storage/emulated/0/Download/com.PillIdentifierandDrugList.app_v900020286.apk")

    //File apk = new File("/data/app/SmokeTestApp/SmokeTestApp.apk");
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        reaper!!.wrapper(menu, apk)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (nav_handler(item.itemId)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NonConstantResourceId")
    private fun nav_handler(id: Int): Boolean {
        if (id == R.id.action_about) {
            aboutDialog(this)
            return true
//            case R.id.action_home:
//                mBinding.include.container.setCurrentItem(0);
//                return true;
        } else if (id == R.id.action_privacy_policy) {
            openBrowser(this, getString(R.string.url_privacy_policy))
            return true
        } else if (id == R.id.action_rate_app) {
            rateUs(this)
            return true
        } else if (id == R.id.action_share_app) {
            shareThisApp(this)
            return true
        } else if (id == R.id.action_discover_more_app) {
            moreApp(this)
            return true

//            case R.id.action_exit:
//                this.finish();
//                System.exit(0);
//                return true;
        } else if (id == R.id.action_feedback) {
            feedback(this)
            return true
        } else if (id == R.id.action_log_out) { //GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, ScopeHandler.makeOptions());
            //googleSignInClient.signOut();
            return true
        } else if (id == R.id.action_permissions) {
            openBrowser(this, "https://myaccount.google.com/permissions")
            return true

//            case R.id.action_more_app_01:
//                Module_U.moreApp(this, "com.walhalla.ttloader");
//                return true;
//
//            case R.id.action_more_app_02:
//                Module_U.moreApp(this, "com.walhalla.vibro");
//                return true;
        }
        return false
    }


    private fun showFileList() {
        val b = Troubleshooting.defLocation().listFiles()
        for (file in Objects.requireNonNull(b)) {
            d("@@@@@" + file.absolutePath)
        }
    }
    //    private void makeMime(String mime) {
    //        PackageManager pm = getPackageManager();
    //        for (String s : Mimiq.actions) {
    //            DLog.d("=========" + s + "==============");
    //
    //            Intent intent = new Intent(s);
    //            if (mime != null) {
    //                intent.setType(mime);
    //            }
    //
    //            List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(intent, 0);
    //            for (ResolveInfo info : resolvedActivityList) {
    //                DLog.d("\t" + info.toString());
    //                Intent serviceIntent = intent;
    //                //serviceIntent.setPackage(info.activityInfo.packageName);
    //
    //                if (pm.resolveService(serviceIntent, 0) != null) {
    //                    //packagesSupportingCustomTabs.add(info);
    //                    DLog.d("\t\t-----0------" + info);
    //                } else if (pm.resolveActivity(serviceIntent, 0) != null) {
    //                    
    //                }
    //            }
    //        }
    //    }
}
