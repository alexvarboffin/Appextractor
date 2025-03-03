package com.walhalla.appextractor.activity.string;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.walhalla.appextractor.BuildConfig;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.BaseActivity;
import com.walhalla.appextractor.activity.debug.DemoData;
import com.walhalla.appextractor.activity.resources.ResourcesPagerFragment;
import com.walhalla.appextractor.fragment.QCallback;
import com.walhalla.appextractor.model.PackageMeta;

public class StrActivity extends BaseActivity {


    public static final String KEY_OBJ_NAME = StrActivity.class.getSimpleName();

    private PackageMeta meta;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_assets_resources);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar aaa = getSupportActionBar();
        if (aaa != null) {
            aaa.setDisplayShowCustomEnabled(true);
            aaa.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            aaa.setDisplayShowTitleEnabled(false);
            aaa.setDisplayShowHomeEnabled(true);
        }

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(KEY_OBJ_NAME)) {
            meta = intent.getParcelableExtra(KEY_OBJ_NAME);
        }

        meta= DemoData.demoData(this, meta);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, StringsPagerFragment.newInstance(meta))
                //StrFragment.newInstance(meta,"")
                .commit();
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
    public void shareText(String value) {

    }
}
