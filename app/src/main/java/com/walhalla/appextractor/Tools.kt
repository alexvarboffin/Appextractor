package com.walhalla.appextractor;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import com.walhalla.appextractor.compat.ComV19;
import com.walhalla.ui.BuildConfig;
import com.walhalla.ui.DLog;

import es.dmoral.toasty.Toasty;

public class Tools {

    public static void copyToClipboard(String value, Activity activity) {
        if(BuildConfig.DEBUG){
            String[] m = value.split("\n");
            for (String s : m) {
                DLog.d("<BUFFER> " + s);
            }
        }
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("packageName", "" + value);
            clipboard.setPrimaryClip(clip);
        }
        String tmp = String.format(activity.getString(R.string.data_to_clipboard), value);
        Toasty.custom(activity, tmp,
                ComV19.getDrawable(activity, R.drawable.ic_info),
                ContextCompat.getColor(activity, R.color.colorPrimaryDark),
                ContextCompat.getColor(activity, R.color.white), Toasty.LENGTH_SHORT, true, true).show();
    }

//    public static String convertStreamToReadableString(String text)
//    {
//        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
//                "<html><head>" +
//                "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
//                "<head><body>" +
//                text +
//                "</body></html>";
//    }
    public static String convertStreamToReadableString(String text) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + text;
    }
}
