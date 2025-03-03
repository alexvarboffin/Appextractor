package com.walhalla.appextractor.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


import androidx.preference.PreferenceManager;

import com.walhalla.appextractor.BuildConfig;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.SettingsPreferenceFragment;


public class LocalStorage {

    private static LocalStorage instance = null;
    //private final Context mContext;
    private SharedPreferences app_prefs;

    private LocalStorage(Context context) {
        app_prefs = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceManager.setDefaultValues(context, R.xml.fragment_settings_pref, false);//<-- init settings
        //context.getSharedPreferences("PREF_DATA_44", Context.MODE_PRIVATE);
        //mContext = context;
    }

    public synchronized static LocalStorage getInstance(Context context) {
        if (instance == null) {
            instance = new LocalStorage(context);
        }

        return instance;
    }

    public SharedPreferences sharedPreferences() {
        return app_prefs;
    }

    public Object settingsValue(String propertyKey) {

        switch (propertyKey) {
//            case SettingsPreferenceFragment.KEY_APP_LOCATION:
//                return saveLocation();


            case SettingsPreferenceFragment.KEY_GOOGLE_DRIVE_STORAGE:
                return enableGoogleDrive();

            case SettingsPreferenceFragment.KEY_DROP_BOX_STORAGE:
                return enableDropBox();

            case SettingsPreferenceFragment.KEY_TELEGRAM_STORAGE:
                return enableTelegram();

            case SettingsPreferenceFragment.KEY_TELEGRAM_TOKEN:
                return telegramToken();

            case SettingsPreferenceFragment.KEY_TELEGRAM_CHAT_ID:
                return telegramChatId();

            default:
                return app_prefs.getString(propertyKey, "");
        }
    }


    public String telegramToken() {
        return app_prefs.getString(
                SettingsPreferenceFragment.KEY_TELEGRAM_TOKEN,
                BuildConfig.KEY_TELEGRAM_TOKEN
        );
    }


    public String telegramChatId() {
        return app_prefs.getString(
                SettingsPreferenceFragment.KEY_TELEGRAM_CHAT_ID,
                BuildConfig.KEY_TELEGRAM_CHAT_ID
        );
    }

    /**
     * Switch
     *
     * @return
     */
    public boolean enableTelegram() {
        return this.app_prefs.getBoolean(SettingsPreferenceFragment.KEY_TELEGRAM_STORAGE, false);
    }

//    public String saveLocation() {
//        return this.app_prefs.getString(SettingsPreferenceFragment.KEY_APP_LOCATION, defLocation() + File.separator);
//    }


    public boolean enableGoogleDrive() {
        return this.app_prefs.getBoolean(SettingsPreferenceFragment.KEY_GOOGLE_DRIVE_STORAGE, false);
    }

    public boolean enableDropBox() {
        return app_prefs.getBoolean(SettingsPreferenceFragment.KEY_DROP_BOX_STORAGE, false);
    }


    public boolean hasDropBoxToken() {
        return !TextUtils.isEmpty(dropboxAccessToken());
    }

    public String dropboxAccessToken() {
        return app_prefs.getString("access-token", null);
    }

    public void dropboxAccessToken(String accessToken) {
        this.app_prefs.edit().putString("access-token", accessToken).apply();
    }


//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
//            return "/Download/apk/"
//        } else {
//            return "/apk/"
//        }

    public void googleDriveFolderId(String driveId) {
        this.app_prefs.edit().putString("GD_FOLDER_ID", driveId).apply();
    }
}
