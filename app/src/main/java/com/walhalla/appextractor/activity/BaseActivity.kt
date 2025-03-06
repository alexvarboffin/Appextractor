package com.walhalla.appextractor.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.fragment.QCallback;
import com.walhalla.ui.BuildConfig;
import com.walhalla.ui.DLog;

import es.dmoral.toasty.Toasty;


public abstract class BaseActivity extends AppCompatActivity implements QCallback {


    @Override
    public void copyToBuffer(String value) {
        if(BuildConfig.DEBUG){
            String[] m = value.split("\n");
            for (String s : m) {
                DLog.d("<BUFFER> " + s);
            }
        }
        String tmp = String.format(getString(R.string.data_to_clipboard), value);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("NewValue", "" + value);
            clipboard.setPrimaryClip(clip);
            successMessage(this, tmp.toUpperCase());
        }
    }
    public void successMessage(Context context, String message) {
        if (context != null) {
            Toasty.custom(context, message,
                            ContextCompat.getDrawable(context, R.drawable.ic_details_settings),
                            ContextCompat.getColor(context, R.color.colorPrimaryDark),
                            ContextCompat.getColor(context, R.color.white), Toasty.LENGTH_SHORT,
                            true, true)
                    .show();
        }
    }

}
