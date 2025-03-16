package com.walhalla.appextractor.activity.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.BaseActivity;
import com.walhalla.appextractor.activity.debug.DemoData;
import com.walhalla.appextractor.activity.manifest.ManifestActivity;

import com.walhalla.appextractor.databinding.ActivityAppDetailInfo0Binding;
import com.walhalla.appextractor.fragment.PagerFragment;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.ui.DLog;

public class AppDetailInfoActivity extends BaseActivity implements DetailContract.View {

    private static final String KEY_OBJ_NAME = "key_obj_name"; //"app_component"

    final ActivityResultLauncher<Intent> launcher0 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    DLog.d("@@@@" + result.getResultCode());
                    Intent data = result.getData();
                    if (data != null) {
                        DLog.d("@@@@" + data);
                        Toast.makeText(AppDetailInfoActivity.this, "" + data, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    DLog.d("@CODE@" + result.getResultCode());
                }
            });


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

    private PackageMeta meta;

    private DetailPresenter presenter;
    private ActivityAppDetailInfo0Binding binding;


    public static void newIntent(Context context, PackageMeta info) {
//        if (context.getPackageName().equals(info.packageName)) {
//            LauncherAppsCompat.getInstance(context).showAppDetailsForProfile(info, UserHandleCompat.myUserHandle());
//            return;
//        }
        Intent intent = new Intent(context, AppDetailInfoActivity.class);
        intent.putExtra(KEY_OBJ_NAME, info);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityAppDetailInfo0Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ActionBar aaa = getSupportActionBar();
        if (aaa != null) {
            aaa.setDisplayShowCustomEnabled(true);
            aaa.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            aaa.setDisplayShowTitleEnabled(false);
            aaa.setDisplayShowHomeEnabled(true);
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

        makeUI();
    }

    // com.google.android.youtube

    private void makeUI() {
        Intent intent = this.getIntent();
        if (intent != null) {
            meta = intent.getParcelableExtra(KEY_OBJ_NAME);
        }

        meta = DemoData.demoData(this, meta);

        if (meta == null) {
            this.finish();
            return;
            //"android"
            //"com.android.settings"
            //componentName = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        }

        if (TextUtils.isEmpty(meta.packageName)) {
            this.finish();
            return;
        }

        presenter = new DetailPresenter(this, meta, this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, PagerFragment.newInstance(meta))
                .commit();

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


    @Override
    public void setTitleWithIcon(String title, String packageName, Drawable icon) {
        binding.appName.setText(title);
        binding.subTitle.setText(packageName);
        binding.icon.setImageDrawable(icon);

        binding.appName.setOnClickListener(v -> {
            copyToBuffer(title);
        });
        binding.subTitle.setOnClickListener(v -> {
            copyToBuffer(packageName);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (R.id.action_manifest == menuItem.getItemId()) {
            ManifestActivity.newIntent(this, meta, null, null);
            return true;
        } else if (menuItem.getItemId() != R.id.system_info) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.meta == null) {
            Toast.makeText(this, "@@@@", Toast.LENGTH_SHORT).show();
            return false;
        }
        //LauncherAppsCompat.getInstance(this).showAppDetailsForProfile(this.componentName, UserHandleCompat.myUserHandle());
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + meta.packageName));
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void shareText(String value) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DLog.d("@@@@@@@@@@" + data + " " + requestCode + " " + resultCode);
    }


    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void handleException(Exception exception) {

    }

    @Override
    public void success(int text) {

    }


}
