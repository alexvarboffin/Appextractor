package com.walhalla.appextractor.activity.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager

import com.dropbox.core.android.Auth
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.walhalla.appextractor.ApkUtils
import com.walhalla.appextractor.BuildConfig
import com.walhalla.appextractor.EasyPermissions
import com.walhalla.appextractor.R
import com.walhalla.appextractor.Util
import com.walhalla.appextractor.activity.debug.GooglePlayCategoryLauncher
import com.walhalla.appextractor.activity.detail.AppDetailInfoActivity
import com.walhalla.appextractor.databinding.MainBinding
import com.walhalla.appextractor.fragment.LogFragment
import com.walhalla.appextractor.model.LFileViewModel
import com.walhalla.appextractor.model.LogViewModel
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.storage.LocalStorage
import com.walhalla.appextractor.utils.PackageMetaUtils
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.getAppVersion
import com.walhalla.ui.DLog.handleException
import com.walhalla.ui.observer.RateAppModule
import com.walhalla.ui.plugins.Launcher.openBrowser
import com.walhalla.ui.plugins.Launcher.rateUs
import com.walhalla.ui.plugins.Module_U.aboutDialog
import com.walhalla.ui.plugins.Module_U.feedback
import com.walhalla.ui.plugins.Module_U.moreApp
import com.walhalla.ui.plugins.Module_U.shareThisApp
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Locale

class MainActivity : AppCompatActivity(), MainView {
    private var mBinding: MainBinding? = null

    private var mRateAppModule: RateAppModule? = null

    @JvmField
    var permissionResolver: EasyPermissions? = null
    private var toast: Toast? = null

    private var packageInfo: PackageMeta? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var mSectionsPagerAdapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "YourApp:WakeLockTag"
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        mBinding = MainBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        setSupportActionBar(mBinding!!.toolbar)


        //getSupportActionBar().setSubtitle(Dlog.getAppVersion(this));


        //test0(getPackageName());
        //test0("com.kgbook.makal");
        //test0("com.freevpnplanet");


//        Module_U.checkUpdate(this);
        permissionResolver = EasyPermissions(this)

        val testDevices: MutableList<String> = ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
        testDevices.add("B9F431FA79D446517D20E015CC3D013E")

        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDevices)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(
            this
        ) { initializationStatus: InitializationStatus? -> }

        //mbinding.toolbar.setNavigationIcon(R.mipmap.ic_launcher_round);
        mBinding!!.toolbar.setNavigationOnClickListener { v: View? ->
            aboutDialog(this)
        }
        if (supportActionBar != null) {
            supportActionBar!!.setSubtitle(getAppVersion(this))
        }


        //        getSupportActionBar().setDisplayShowHomeEnabled(true);


//        drawer-layout
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mBinding.drawerLayout, mbinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mBinding.drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        mBinding.navView.setNavigationItemSelectedListener(menuItem -> {
//            nav_handler(menuItem.getItemId());
//            //action_how_to_use_app
//            //action_support_developer
//
//            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
//            return true;
//        });


//        FloatingActionButton fab = @BindView(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
//                printOutput("uuuu");
//            }
//        });
        mRateAppModule = RateAppModule(this)
        //WhiteScreen
        lifecycle.addObserver(mRateAppModule!!)


        //if (savedInstanceState == null) {
        mSectionsPagerAdapter = ViewPagerAdapter(
            supportFragmentManager
        )
        __tabs(mBinding!!.tabs)


        //} else {
        //-->           mbinding.container.setCurrentItem(savedInstanceState.getInt(KEY_TAB_POSITION, 0));
        //}

        //mbinding.toolbar.setOnClickListener(v -> signIn());


//        AdvertAdmobRepository repository = AdvertAdmobRepository.getInstance(
//                new AdvertConfig() {
//                    @Override
//                    public String application_id() {
//                        return getString(R.string.app_id);
//                    }
//
//                    @Override
//                    public SparseArray<String> banner_ad_unit_id() {
//                        SparseArray<String> map = new SparseArray<>();
//                        map.append(R.id.bottom_banner, getString(R.string.app_pub_1));
//                        return map;
//                    }
//
//                    @Override
//                    public String interstitial_ad_unit_id() {
//                        return null;
//                    }
//                }
//        );
//        getLifecycle().addObserver(repository);
//
//        AdvertInteractor advertInteractor = new AdvertInteractorImpl(
//                BackgroundExecutor.getInstance(),
//                //ThreadExecutor.getInstance(/*new Handler()*/)
//                MainThreadImpl.getInstance(), repository);
//        advertInteractor.selectView(mBinding.bottomBanner, new AdvertInteractor.ExtractorViewCallback<View>() {
//            @Override
//            public void onMessageRetrieved(int id, View message) {
//                ViewGroup viewGroup = findViewById(R.id.bottom_banner);
//                if (viewGroup != null) {
//                    try {
//                        //viewGroup.removeView(message);
//                        if (message.getParent() != null) {
//                            ((ViewGroup) message.getParent()).removeView(message);
//                        }
//                        viewGroup.addView(message);
//                    } catch (Exception e) {
//                        DLog.handleException(e);
//                    }
//                }
//            }
//
//            @Override
//            public void onRetrievalFailed(String error) {
//                Log.i(TAG, "onRetrievalFailed: " + error);
//            }
//        });
        mBinding!!.adView.loadAd(
            AdRequest.Builder() //                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //                .addTestDevice("28964E2506C9A8C6400A9E8FF42D3486")
                .build()
        )

        //Util.openFolder(this, "/storage/sdcard/Download/");
    }

    private fun test0(packageName: String) {
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            val meta = PackageMeta(info.packageName, "test")
            AppDetailInfoActivity.newIntent(this, meta)

            //ManifestExplorer.newIntent(this, meta);
        } catch (e: PackageManager.NameNotFoundException) {
            handleException(e)
        }
    }

    @SuppressLint("NonConstantResourceId")
    private fun nav_handler(id: Int): Boolean {
        if (id == R.id.action_about) {
            aboutDialog(this)
            return true

//            case R.id.action_home:
//                mbinding.container.setCurrentItem(0);
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
        } else if (id == R.id.action_accessibility) {
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (anfe: ActivityNotFoundException) {
                handleException(anfe)
            }
            return true
        } else if (id == R.id.action_GooglePlayCategoryLauncher) {
            try {
                val intent = Intent(
                    this,
                    GooglePlayCategoryLauncher::class.java
                )
                startActivity(intent)
            } catch (anfe: ActivityNotFoundException) {
                handleException(anfe)
            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            //showStoragePermissionRationale();
//            DLog.d("((***))");
//        }
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            //showStoragePermissionRationale();
//            DLog.d("***READ_EXTERNAL_STORAGE***@");
//        }
//            //Permission Granted
//            if (requestCode == PermissionResolver.REQUEST_CODE && grantResults.length > 0) {
//                List<Fragment> fragments = getSupportFragmentManager().getFragments();
//                for (Fragment fragment : fragments) {
//                    if (fragment instanceof ExtractorFragment) {
//                        ((ExtractorFragment) fragment).checkPermissionAndExtract(null);
//                    }
//                }
//            }
        when (requestCode) {
            REQUEST_CODE_SAVE_ICON -> if (packageInfo != null) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    run {
                        saveIcons0(this, packageInfo!!)
                    }
                }
            }
        }
    }


    override fun failureExtracted(@StringRes id: Int) {
        Toasty.error(this, id, Toast.LENGTH_SHORT, true).show()
    }

    override fun successToast(s: String) {
        val toast0 = Toasty.info(this, s.uppercase(Locale.getDefault()), Toast.LENGTH_SHORT)
        toast0.show()

        //        Toast toast0 = Toasty.error(this, s, Toast.LENGTH_SHORT, true);
//        toast0.show();
    }

    override fun errorToast(s: String) {
        val toast0 = Toasty.error(this, s, Toast.LENGTH_SHORT, true)
        toast0.show()
    }

    override fun saveIconRequest(packageInfo: PackageMeta) {
        this.packageInfo = packageInfo

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            saveIcons0(this, packageInfo)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_CODE_SAVE_ICON
            )
        }
    }

    fun saveIcons0(context: Context, meta: PackageMeta) {
//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/");
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//        File iconDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/", "/App Icons/");
//        if (!iconDir.exists()) {
//            iconDir.mkdir();
//        }
        //final PackageManager pm = getPackageManager();
        //String name = meta.applicationInfo.loadLabel(pm).toString();
        val name = meta.label
        val newFile = File(
            (Environment.getExternalStorageDirectory().absolutePath
                    + "/Download/"), "$name.png"
        )
        if (newFile.exists()) {
            val b = newFile.delete()
            d("@@$b")
        }
        val bitmap = PackageMetaUtils.drw(context, meta.packageName)
        try {
            val outputStream: OutputStream = FileOutputStream(newFile)
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            }
            successToast(
                resources
                    .getString(R.string.savedicon) + " " + name + " ✔"
            )
        } catch (e: FileNotFoundException) {
            //errorToast(getResources()
            //.getString(R.string.err_unable_saved_icon) + " " + name + " ✘");
            //DLog.handleException(e);
        } catch (e: IOException) {
            //errorToast(getResources()
            //.getString(R.string.err_unable_saved_icon) + " " + name + " ✘");
            //DLog.handleException(e);
        }
    }


    // [END drive_android_create_folder]
    //public class MainActivity extends AppCompatActivity implements
    //        ExtractorFragment.ExtractorViewCallback,
    //         {
    //
    //
    private fun __tabs(tabs: TabLayout) {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        mSectionsPagerAdapter!!.addFragment(getString(R.string.tab_text_1))
        mSectionsPagerAdapter!!.addFragment(getString(R.string.tab_text_3))
        mSectionsPagerAdapter!!.addFragment(getString(R.string.action_settings))

        mBinding!!.container.adapter = mSectionsPagerAdapter
        mBinding!!.container.addOnPageChangeListener(
            TabLayoutOnPageChangeListener(tabs)
        )
        mBinding!!.container.offscreenPageLimit = 3
        tabs.setupWithViewPager(mBinding!!.container)

        tabs.addOnTabSelectedListener(
            object : TabLayout.ViewPagerOnTabSelectedListener(mBinding!!.container) {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    super.onTabSelected(tab)
                    //                        int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.colorAccent);
                    val icon = tab.icon
                    icon?.setColorFilter(
                        resources.getColor(R.color.tabIconColor),
                        PorterDuff.Mode.SRC_IN
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    super.onTabUnselected(tab)
                    //int tabIconColor = ContextCompat.getColor(this, R.color.tabUnselectedIconColor);
                    val icon = tab.icon
                    icon?.setColorFilter(
                        resources.getColor(R.color.tabIconUnselected),
                        PorterDuff.Mode.SRC_IN
                    )
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    super.onTabReselected(tab)
                }
            })


        //Data
        val tab_icons = intArrayOf(
            R.drawable.ic_tab_archive_white,
            R.drawable.ic_tab_settings_white,
            R.drawable.ic_tab_baseline_log
        )


        for (i in 0 until tabs.tabCount) {
            val tab = tabs.getTabAt(i)
            if (tab != null) {
                //tab.setText(R.string.tab_text_1);
                try {
                    tab.setIcon(tab_icons[i])
                } catch (ignored: Exception) {
                }
                val icon = tab.icon
                icon?.setColorFilter(
                    resources.getColor(R.color.tabIconUnselected),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        mRateAppModule!!.appReloadedHandler()
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TAB_POSITION, mBinding!!.container.currentItem)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        if (BuildConfig.DEBUG) {
//            menu.add("[1]").setOnMenuItemClickListener(item -> {
//                startActivity(new Intent(MainActivity.this, DebugActivity.class));
//                return false;
//            });
//            menu.add("[2]").setOnMenuItemClickListener(item -> {
//                startActivity(new Intent(MainActivity.this, ButtonListActivity.class));
//                return false;
//            });
//            menu.add("[3]").setOnMenuItemClickListener(item -> {
//                startActivity(new Intent(MainActivity.this, GooglePlayCategoryLauncher.class));
//                return false;
//            });
//        }
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (nav_handler(item.itemId)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    //
    //
    //    @Override
    //    public void setLogTag(String tag) {
    //        Log.d(Config.TAG, "set-tag: " + tag);
    //    }
    //

    override fun printOutput(viewModel: LogViewModel) {
        val fragment = supportFragmentManager.fragments[1]
        if (fragment != null && fragment is LogFragment) {
            fragment.makeLog(viewModel)
        }

        //        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
//                .setAction("9999", null);
//        View snackbarView = snackbar.getView();
//        snackbarView.setBackgroundColor(Color.BLUE);
//        snackbar.show();
        val contextView = findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(
            contextView, viewModel.text, 7000
        )
            .setTextColor(Color.WHITE)
            .setBackgroundTint(Color.BLACK)
            .setAction(android.R.string.ok) { v: View? -> }
        snackbar.show()


        //getWindow().getDecorView().getRootView()
//        List<Fragment> xx = getSupportFragmentManager().getFragments();
////        System.out.makeLog("@@" + xx.toString());
////        //TabHolderFragment f = (TabHolderFragment) getSupportFragmentManager().findFragmentByTag("@tag");
////
////        //getChildFragmentManager().getFragments();
////
////        String fragmentTag = makeFragmentName(mViewPager.getId(), 2);
////        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
////        System.out.makeLog("@@"+fragment);


//        FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
//        if (adapter != null) {

//        LogFragment logFragment = (LogFragment) mSectionsPagerAdapter.getItem(2);
//        logFragment.makeLog(message);


//        List<Fragment> fragments = getChildFragmentManager().getFragments();
//        for (final Fragment fragment : fragments) {
//            if (fragment != null) {
//                ((TabForecastFragment) fragment).showError(error);
//            }
//        }
//        }
    }

    override fun makeSnackBar(file: File) {
        val text = String.format(this.getString(R.string.toast_extracted), file.path)
        val mm = Util.getFileSizeMegaBytes(file)
        printOutput(
            LFileViewModel(
                file, R.drawable.ic_baseline_sd_card_24, """
     $text
     $mm
     """.trimIndent()
            )
        )

        //---akeText(getContext(), text, Toast.LENGTH_LONG).show();
        val contextView = findViewById<View>(android.R.id.content)

        val snackbar = Snackbar.make(
            contextView, """
     $text
     $mm
     """.trimIndent(), 7000
        )
            .setTextColor(Color.WHITE)
            .setBackgroundTint(Color.BLACK)
            .setAction(android.R.string.ok) { v: View? -> }
        snackbar.show()
        if (BuildConfig.DEBUG) {
            d("==> adb pull " + file.path + " .")
        }
    }


    //    @Override
    //    public void makeProgressBar(int size) {
    //        //mbinding.toolbar.setSubtitle("@@ 0/" + size);
    //    }
    override fun debugHideProgress(size: Int) {
        if (BuildConfig.DEBUG) {
            mBinding!!.toolbar.subtitle = "@@"
        }
    }

    override fun debugShowProgress(i: Int, v0: Int) {
        if (BuildConfig.DEBUG) {
            mBinding!!.toolbar.subtitle = "@@ $i/$v0"
        }
    }


    //    /**
    //     * A placeholder fragment containing a simple view.
    //     */
    ////    public static class PlaceholderFragment extends Fragment {
    ////        /**
    ////         * The fragment argument representing the section number for this
    ////         * fragment.
    ////         */
    ////        private static final String ARG_SECTION_NUMBER = "section_number";
    ////
    ////        public PlaceholderFragment() {
    ////        }
    ////
    ////        /**
    ////         * Returns a new instance of this fragment for the given section
    ////         * number.
    ////         */
    ////        public static PlaceholderFragment newInstance(int sectionNumber) {
    ////            PlaceholderFragment fragment = new PlaceholderFragment();
    ////            Bundle args = new Bundle();
    ////            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
    ////            fragment.setArguments(args);
    ////            return fragment;
    ////        }
    ////
    ////        @Override
    ////        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
    ////                                 Bundle savedInstanceState) {
    ////            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    //////            TextView textView = rootView.@BindView(R.id.section_label);
    //////            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
    ////            return rootView;
    ////        }
    ////    }
    //    @Override
    //    public void onBackPressed() {
    //        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
    //            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
    //        } else {
    //            super.onBackPressed();
    //        }
    //    }
    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        //Pressed back => return to home screen

        val count = supportFragmentManager.backStackEntryCount
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(count > 0)
        }
        if (count > 0) {
            supportFragmentManager
                .popBackStack(
                    supportFragmentManager
                        .getBackStackEntryAt(0).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
        } else { //count == 0


//                Dialog
//                new AlertDialog.Builder(this)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle("Leaving this App?")
//                        .setMessage("Are you sure you want to close this application?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
            //super.onBackPressed();


            if (doubleBackToExitPressedOnce) {
                if (toast != null) {
                    toast!!.cancel()
                }
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            toast = Toasty.info(
                this,
                getString(R.string.press_again_to_exit)
                    .uppercase(Locale.getDefault()), Toast.LENGTH_LONG
            )
            toast?.show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == ApkUtils.KEY_UNINSTALL) {
            //result always 0 === Activity.RESULT_CANCELED

//            Handler handler = new Handler();
//            handler.postDelayed(() -> {
//                mSectionsPagerAdapter.notifyDataSetChanged();
//            }, 1000);
        } else {
            d("** " + requestCode + " " + resultCode + " " + (if ((data == null)) "NULL" else data.toString()))
        }

        if (requestCode == RESULT_OK) {
        }
    }

    public override fun onPause() {
        if (mBinding!!.adView != null) {
            mBinding!!.adView.pause()
        }
        super.onPause()
        wakeLock!!.release() // Отключаем WakeLock при приостановке активности
    }

    @SuppressLint("WakelockTimeout")
    public override fun onResume() {
        super.onResume()
        if (mBinding!!.adView != null) {
            mBinding!!.adView.resume()
        }
        wakeLock!!.acquire() // Включаем WakeLock

        val token = Auth.getOAuth2Token()
        if (!TextUtils.isEmpty(token)) {
            val mpm = LocalStorage.getInstance(this)
            mpm.dropboxAccessToken(token)
        }
    }

    public override fun onDestroy() {
        if (mBinding!!.adView != null) {
            mBinding!!.adView.destroy()
        }
        super.onDestroy()
    }

    companion object {
        private const val KEY_TAB_POSITION = "key:tab:pos"
        const val REQUEST_CODE_SAVE_ICON: Int = 143

        private fun makeFragmentName(viewId: Int, id: Long): String {
            return "android:switcher:$viewId:$id"
        }
    }
}
