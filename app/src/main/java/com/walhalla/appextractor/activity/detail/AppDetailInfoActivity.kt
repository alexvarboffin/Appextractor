package com.walhalla.appextractor.activity.detail

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.BaseActivity
import com.walhalla.appextractor.activity.debug.DemoData
import com.walhalla.appextractor.activity.manifest.ManifestActivity
import com.walhalla.appextractor.databinding.ActivityAppDetailInfo0Binding
import com.walhalla.appextractor.fragment.ResourcePagerFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.ui.DLog.d

class AppDetailInfoActivity : BaseActivity(), DetailContract.View {
    val launcher0: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            d("@@@@" + result.resultCode)
            val data = result.data
            if (data != null) {
                d("@@@@$data")
                Toast.makeText(
                    this@AppDetailInfoActivity,
                    "" + data,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            d("@CODE@" + result.resultCode)
        }
    }


    //    private ComponentName componentName;
    //
    //    public static void newIntent(Context context, ComponentName componentName) {
    //        if (context.getPackageName().equals(componentName.getPackageName())) {
    //            LauncherAppsCompat.getInstance(context).showAppDetailsForProfile(componentName, UserHandleCompat.myUserHandle());
    //            return;
    //        }
    //        Intent intent = new Intent(context, AppDetailInfoActivity.class);
    //        intent.putExtra(KEY_OBJ_NAME, componentName);
    //        context.startActivity(intent);
    //    }
    private var meta: PackageMeta? = null

    private var presenter: DetailPresenter? = null
    private var binding: ActivityAppDetailInfo0Binding? = null


    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        binding = ActivityAppDetailInfo0Binding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)

        val aaa = supportActionBar
        if (aaa != null) {
            aaa.setDisplayShowCustomEnabled(true)
            aaa.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
            aaa.setDisplayShowTitleEnabled(false)
            aaa.setDisplayShowHomeEnabled(true)
        }

        //icons.put(0, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_android, null));

//        adapter = new AppDetailInfoAdapter(object -> {
//            mPresenter.openSettingsRequest(this);
//        }, this);
//
//        this.recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
//        this.recyclerView.setAdapter(adapter);
        makeUI()
    }

    // com.google.android.youtube
    private fun makeUI() {
        val intent = this.intent
        if (intent != null) {
            meta = intent.getParcelableExtra(KEY_OBJ_NAME)
        }

        meta = DemoData.demoData(this, meta)

        if (meta == null) {
            this.finish()
            return
            //"android"
            //"com.android.settings"
            //componentName = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        }

        if (TextUtils.isEmpty(meta!!.packageName)) {
            this.finish()
            return
        }

        presenter = DetailPresenter(this, meta!!, this)
        supportFragmentManager.beginTransaction()
            .add(R.id.container, ResourcePagerFragment.newInstance(meta))
            .commit()


        //        final PackageManager packageManager = this.getPackageManager();
//        mPresenter = new DetailsPresenter(this, meta, packageManager, this);
//        //PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
//
//        //packageManager.getPackageArchiveInfo()
//        mPresenter.doStuff(this);


//            LinearLayout r0z = this.mCard();
//            this.newline("@@@@@@@@@@@", r0z);
//            this.newline("@@@@@@@@@@@", r0z);
    }


    //    private LinearLayout mCard() {
    //        CardView cardView = new CardView(this);
    //        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-1, -2);
    //        marginLayoutParams.topMargin = Utilities.pxFromDp(2.0f, getResources().getDisplayMetrics());
    //        int i2 = -Utilities.pxFromDp(5.0f, getResources().getDisplayMetrics());
    //        marginLayoutParams.rightMargin = i2;
    //        marginLayoutParams.leftMargin = i2;
    //        cardView.setLayoutParams(marginLayoutParams);
    //        cardView.setCardElevation((float) Utilities.pxFromDp(2.0f, getResources().getDisplayMetrics()));
    //        cardView.setCardBackgroundColor(-1);
    //        cardView.setUseCompatPadding(true);
    //        LinearLayout linearLayout = new LinearLayout(this);
    //        linearLayout.setOrientation(1);
    //        linearLayout.setPadding(0, 0, 0, Utilities.pxFromDp(4.0f, getResources().getDisplayMetrics()));
    //        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
    //        cardView.addView(linearLayout);
    //        this.recyclerView.addView(cardView);
    //        return linearLayout;
    //    }
    //    private void header(String str, String str2, ViewGroup viewGroup, View.OnClickListener onClickListener) {
    //        TextView textView = new TextView(this);
    //        textView.setTextSize(2, 14.0f);
    //        textView.setTextColor(-13421773);
    //        textView.setText(getString(R.string.key_value_pair, new Object[]{str, "--MB"}));
    //        LinearLayout linearLayout = new LinearLayout(this);
    //        linearLayout.setOrientation(0);
    //        linearLayout.setGravity(16);
    //        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
    //        int pxFromDp = Utilities.pxFromDp(3.0f, getResources().getDisplayMetrics());
    //        layoutParams.topMargin = pxFromDp;
    //        layoutParams.bottomMargin = pxFromDp;
    //        layoutParams.leftMargin = Utilities.pxFromDp(16.0f, getResources().getDisplayMetrics());
    //        linearLayout.addView(textView);
    //        TextView textView2 = new TextView(this);
    //        textView2.setTextSize(2, 12.0f);
    //        textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
    //        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
    //        layoutParams2.leftMargin = Utilities.pxFromDp(20.0f, getResources().getDisplayMetrics());
    //        textView2.setOnClickListener(onClickListener);
    //        textView2.setText(str2);
    //        textView2.setPadding(30, 5, 30, 5);
    //        //@@       textView2.setBackgroundResource(R.drawable.shape_blue_board_btn);
    //        linearLayout.addView(textView2, layoutParams2);
    //        viewGroup.addView(linearLayout, layoutParams);
    //    }
    //    private void header(String str, String str2, ViewGroup viewGroup) {
    //        TextView textView = new TextView(this);
    //        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
    //        int pxFromDp = Utilities.pxFromDp(3.0f, getResources().getDisplayMetrics());
    //        layoutParams.topMargin = pxFromDp;
    //        layoutParams.bottomMargin = pxFromDp;
    //        layoutParams.leftMargin = Utilities.pxFromDp(16.0f, getResources().getDisplayMetrics());
    //        textView.setTextSize(2, 14.0f);
    //        if (TextUtils.equals(getString(R.string.unknown), str2)) {
    //            textView.setTextColor(SupportMenu.CATEGORY_MASK);
    //        } else {
    //            textView.setTextColor(-13421773);
    //        }
    //        if (TextUtils.isEmpty(str2)) {
    //            textView.setText(str);
    //        } else {
    //            textView.setText(getString(R.string.key_value_pair, new Object[]{str, str2}));
    //        }
    //        viewGroup.addView(textView, layoutParams);
    //    }

    override fun setTitleWithIcon(title: String?, packageName: String?, icon: Drawable?) {
        binding!!.appName.text = title
        binding!!.subTitle.text = packageName
        binding!!.icon.setImageDrawable(icon)

        binding!!.appName.setOnClickListener { v: View? ->
            if (title != null) {
                copyToBuffer(title)
            }
        }
        binding!!.subTitle.setOnClickListener { v: View? ->
            if (packageName != null) {
                copyToBuffer(packageName)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_app_info, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (R.id.action_manifest == menuItem.itemId) {
            ManifestActivity.newIntent(this, meta, null, null)
            return true
        } else if (menuItem.itemId != R.id.system_info) {
            return super.onOptionsItemSelected(menuItem)
        }
        if (this.meta == null) {
            Toast.makeText(this, "@@@@", Toast.LENGTH_SHORT).show()
            return false
        }
        //LauncherAppsCompat.getInstance(this).showAppDetailsForProfile(this.componentName, UserHandleCompat.myUserHandle());
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(Uri.parse("package:" + meta!!.packageName))
        startActivity(intent)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    override fun shareText(value: String) {
    }

    //    android:name=""
    //    android:exported="true"
    //    android:taskAffinity="put your package name here">
    //            <intent-filter>
    //                        <action android:name="android.intent.action.VIEW" />
    //                        <category android:name="android.intent.category.DEFAULT" />
    //                        <category android:name="android.intent.category.BROWSABLE" />
    //                        <data android:scheme="@string/fb_login_protocol_scheme" />
    //            </intent-filter>
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        d("@@@@@@@@@@$data $requestCode $resultCode")
    }


    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun handleException(exception: Exception) {
    }

    override fun success(text: Int) {
    }


    companion object {
        private const val KEY_OBJ_NAME = "key_obj_name" //"app_component"

        fun newIntent(context: Context, info: PackageMeta?) {
//        if (context.getPackageName().equals(info.packageName)) {
//            LauncherAppsCompat.getInstance(context).showAppDetailsForProfile(info, UserHandleCompat.myUserHandle());
//            return;
//        }
            val intent = Intent(
                context,
                AppDetailInfoActivity::class.java
            )
            intent.putExtra(KEY_OBJ_NAME, info)
            context.startActivity(intent)
        }
    }
}
