package com.walhalla.appextractor.activity.debug;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.walhalla.appextractor.R;

import com.walhalla.appextractor.fragment.QCallback;

public class DebugActivity extends AppCompatActivity implements QCallback {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_debug);

        if (bundle == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, DebugPagerFr.newInstance())
                    .commit();
        }
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
