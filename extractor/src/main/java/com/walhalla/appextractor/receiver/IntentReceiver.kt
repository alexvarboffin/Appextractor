package com.walhalla.appextractor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * adb shell am broadcast -a com.walhalla.appextractor.EXTRACT --es apk_file "credti.nakartu.blue.muge.com" -n com.walhalla.appextractor/.IntentReceiver
 * adb shell am broadcast --es apk_file "com.example.app" -n com.walhalla.appextractor/.IntentReceiver
 */
class IntentReceiver : BroadcastReceiver() {
    val tag: String = "@@@"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "@@@@@@@@@@@@@")
        try {
            val data = intent.getStringExtra("apk_file")
            Log.d(tag, data!!)
            Toast.makeText(context, data.subSequence(0, data.length), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Intercepted", Toast.LENGTH_LONG).show()
        }
    }
}