package com.walhalla.appextractor.activity.resources;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.detail.DetailPresenter;
import com.walhalla.appextractor.fragment.PagerFragment;
import com.walhalla.appextractor.fragment.QCallback;
import com.walhalla.appextractor.model.PackageMeta;

import java.util.ArrayList;
import java.util.List;

public class ResourcesActivity extends AppCompatActivity implements QCallback {
    private static final String KEY_OBJ_NAME = "key_obj_name"; //"app_component"
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
        if (intent != null) {
            meta = intent.getParcelableExtra(KEY_OBJ_NAME);
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, ResourcesPagerFragment.newInstance(meta))
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
    public void copyToBuffer(String value) {

    }

    @Override
    public void shareText(String value) {

    }
}
