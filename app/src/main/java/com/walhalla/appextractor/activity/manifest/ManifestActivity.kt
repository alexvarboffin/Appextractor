package com.walhalla.appextractor.activity.manifest;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.walhalla.appextractor.BuildConfig;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.Tools;
import com.walhalla.appextractor.activity.debug.DemoData;
import com.walhalla.appextractor.activity.detail.DetailPresenter;
import com.walhalla.appextractor.activity.resources.ResourcesPagerFragment;
import com.walhalla.appextractor.databinding.ManifestTabsExplorerBinding;
import com.walhalla.appextractor.fragment.QCallback;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.appextractor.presenter.BaseManifestPresenter;


public class ManifestActivity extends AppCompatActivity implements QCallback, ManifestContract.View {


    public static final String KEY_OBJ_NAME = "key_obj_iii";
    public static final String KEY_OBJ_APK_PATH = "key_obj_apkpath";
    public static final String KEY_OBJ_XML_FILENAME = "key_obj_xmlfile";

    private PackageMeta meta;
    private MainManifestPresenter presenter;
    private String[] apkPath;
    private String xmlFileName;

    private ManifestTabsExplorerBinding binding;


    public static void newIntent(Context context, PackageMeta packageInfo, String[] apkPath, String xmlFileName) {
        Intent intent = new Intent(context, ManifestActivity.class);
        intent.putExtra(KEY_OBJ_NAME, packageInfo);
        intent.putExtra(KEY_OBJ_APK_PATH, apkPath);
        intent.putExtra(KEY_OBJ_XML_FILENAME, xmlFileName);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //ArrayAdapter<String> spinnerArrayAdapter;
        super.onCreate(savedInstanceState);

        binding = ManifestTabsExplorerBinding.inflate(getLayoutInflater());
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
        makeUI();

    }

    private void makeUI() {
        if (getIntent() != null && getIntent().hasExtra(KEY_OBJ_NAME)) {
            meta = this.getIntent().getParcelableExtra(KEY_OBJ_NAME);
        }
        if (getIntent() != null && getIntent().hasExtra(KEY_OBJ_APK_PATH)) {
            apkPath = getIntent().getStringArrayExtra(KEY_OBJ_APK_PATH);
        }
        if (getIntent() != null && getIntent().hasExtra(KEY_OBJ_XML_FILENAME)) {
            xmlFileName = getIntent().getStringExtra(KEY_OBJ_XML_FILENAME);
        }

        if (meta == null) {
            meta = DemoData.demoData(this, meta);
        } else {
//                int pos = spinnerArrayAdapter.getPosition(packageName);
//                if (pos > -1)
//                {
//                    this.spinner.setSelection(pos);
//                }
        }

        presenter = new MainManifestPresenter(this, meta, this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, ManifestPagerFragment.newInstance(meta, apkPath, xmlFileName))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.manifest_explorer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.system_info) {
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
//        else if (menuItem.getItemId() == R.id.menu_copy_manifest_txt0) {
//            Tools.copyToClipboard(presenter.mOutgetText("AndroidManifest.xml"), this);
//            return true;
//        } else if (menuItem.getItemId() == R.id.menu_share_text0) {
//            Module_U.shareText(this, presenter.mOutgetText("AndroidManifest.xml"), "Manifest Explorer");
//            return true;
//        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void showError(String text, Throwable t) {
        Log.e("ManifestExplorer", text + " : " + ((t != null) ? t.getMessage() : ""));
        Toast.makeText(this, "Error: " + text + " : " + t, Toast.LENGTH_LONG).show();
    }


    private String ParseReleaseTag(XmlResourceParser xml) {
        return "@@@@@@@@@@@@@@";
    }

    private String GetStyle() {
        return "";
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void copyToBuffer(String value) {

    }

    @Override
    public void shareText(String value) {

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
}