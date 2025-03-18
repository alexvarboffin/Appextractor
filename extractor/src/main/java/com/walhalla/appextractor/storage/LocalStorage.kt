package com.walhalla.appextractor.storage

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.preference.PreferenceManager

import com.walhalla.extractor.BuildConfig
import com.walhalla.extractor.R

class LocalStorage private constructor(context: Context) {
    //private final Context mContext;
    private val app_prefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    init {
        PreferenceManager.setDefaultValues(
            context,
            R.xml.fragment_settings_pref,
            false
        ) //<-- init settings
        //context.getSharedPreferences("PREF_DATA_44", Context.MODE_PRIVATE);
        //mContext = context;
    }

    fun sharedPreferences(): SharedPreferences {
        return app_prefs
    }

    fun settingsValue(propertyKey: String): Any? {
        return when (propertyKey) {
            PrefsConst.KEY_GOOGLE_DRIVE_STORAGE -> enableGoogleDrive()

            PrefsConst.KEY_DROP_BOX_STORAGE -> enableDropBox()

            PrefsConst.KEY_TELEGRAM_STORAGE -> enableTelegram()

            PrefsConst.KEY_TELEGRAM_TOKEN -> telegramToken()

            PrefsConst.KEY_TELEGRAM_CHAT_ID -> telegramChatId()

            else -> app_prefs.getString(propertyKey, "")
        }
    }


    fun telegramToken(): String? {
        return app_prefs.getString(PrefsConst.KEY_TELEGRAM_TOKEN, BuildConfig.KEY_TELEGRAM_TOKEN)
    }


    fun telegramChatId(): String? {
        return app_prefs.getString(
            PrefsConst.KEY_TELEGRAM_CHAT_ID,
            BuildConfig.KEY_TELEGRAM_CHAT_ID
        )
    }

    /**
     * Switch
     *
     * @return
     */
    fun enableTelegram(): Boolean {
        return app_prefs.getBoolean(PrefsConst.KEY_TELEGRAM_STORAGE, false)
    }


    //    public String saveLocation() {
    //        return this.app_prefs.getString(PrefsConst.KEY_APP_LOCATION, defLocation() + File.separator);
    //    }
    fun enableGoogleDrive(): Boolean {
        return app_prefs.getBoolean(PrefsConst.KEY_GOOGLE_DRIVE_STORAGE, false)
    }

    fun enableDropBox(): Boolean {
        return app_prefs.getBoolean(PrefsConst.KEY_DROP_BOX_STORAGE, false)
    }


    fun hasDropBoxToken(): Boolean {
        return !TextUtils.isEmpty(dropboxAccessToken())
    }

    fun dropboxAccessToken(): String? {
        return app_prefs.getString("access-token", null)
    }

    fun dropboxAccessToken(accessToken: String?) {
        app_prefs.edit().putString("access-token", accessToken).apply()
    }


    //        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
    //            return "/Download/apk/"
    //        } else {
    //            return "/apk/"
    //        }
    fun googleDriveFolderId(driveId: String?) {
        app_prefs.edit().putString("GD_FOLDER_ID", driveId).apply()
    }

    fun saveSettings(s: String, enabled: Boolean) {
        app_prefs.edit().putBoolean(s, enabled).apply()
    }

    fun saveSettings(key: String, value: String) {
        app_prefs.edit().putString(key, value).apply()
    }

    companion object {
        private var instance: LocalStorage? = null

        @Synchronized
        fun getInstance(context: Context): LocalStorage {
            if (instance == null) {
                instance = LocalStorage(context)
            }

            return instance!!
        }
    }
}
