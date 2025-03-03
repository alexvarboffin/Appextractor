package com.walhalla.appextractor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


/**
 * adb shell am broadcast -a com.walhalla.appextractor.EXTRACT --es apk_file "credti.nakartu.blue.muge.com" -n com.walhalla.appextractor/.IntentReceiver
 adb shell am broadcast --es apk_file "com.example.app" -n com.walhalla.appextractor/.IntentReceiver
 */
public class IntentReceiver extends BroadcastReceiver {

    final String tag = "@@@";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag, "@@@@@@@@@@@@@");
        try {
            String data = intent.getStringExtra("apk_file");
            Log.d(tag, data);
            Toast.makeText(context, data.subSequence(0, data.length()), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Intercepted", Toast.LENGTH_LONG).show();
        }
    }
}