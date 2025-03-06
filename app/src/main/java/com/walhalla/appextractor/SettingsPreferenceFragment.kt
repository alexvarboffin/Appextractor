package com.walhalla.appextractor

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.walhalla.appextractor.storage.LocalStorage
import com.walhalla.core.settings.FolderChooser
import com.walhalla.ui.DLog.getAppVersion
import com.walhalla.ui.plugins.Module_U.aboutDialog

/**
 * android.preference.PreferenceFragment
 * android.support.v7.preference.PreferenceFragmentCompat
 */
class SettingsPreferenceFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener {
    //    @Override
    //    public View onCreateView(
    //            @NonNull LayoutInflater inflater,
    //            @Nullable ViewGroup container,
    //            @Nullable Bundle savedInstanceState) {
    //
    //        return inflater.inflate(R.layout.fragment_preference_list_content, container, false);
    //    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.fragment_settings_pref)

        //        FolderChooser mFolderChooser = (FolderChooser) findPreference(KEY_APP_LOCATION);
//        bindPreferenceSummaryToValue(mFolderChooser);
        val ch_GD = findPreference<CheckBoxPreference>(KEY_GOOGLE_DRIVE_STORAGE)
        if (ch_GD != null) {
            bindPreferenceSummaryToValue(ch_GD)
        }

        val ch_DB = findPreference<CheckBoxPreference>(KEY_DROP_BOX_STORAGE)
        if (ch_DB != null) {
            bindPreferenceSummaryToValue(ch_DB)
        }

        val sw_TG = findPreference<SwitchPreference>(KEY_TELEGRAM_STORAGE)
        if (sw_TG != null) {
            bindPreferenceSummaryToValue(sw_TG)
        }

        val t_T = findPreference<EditTextPreference>(KEY_TELEGRAM_TOKEN)
        if (t_T != null) {
            bindPreferenceSummaryToValue(t_T)
        }
        val t_id = findPreference<EditTextPreference>(KEY_TELEGRAM_CHAT_ID)
        if (t_id != null) {
            bindPreferenceSummaryToValue(t_id)
        }
        findPreference<Preference>("key_about_version")!!.summary =
            getAppVersion(
                requireContext()
            )
        val pref = findPreference<Preference>("key_about")
        if (pref != null) {
            pref.setSummary(R.string.app_name_full)
            pref.onPreferenceClickListener =
                Preference.OnPreferenceClickListener { preference: Preference? ->
                    aboutDialog(requireContext())
                    false
                }
        }
    }


    override fun onStart() {
        super.onStart()
        //getPrefs();
    }

    override fun onDetach() {
        super.onDetach()
        //mCallback = null;
    }

    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
//        //addPreferencesFromResource(R.xml.fragment_settings_pref);
//        // Load the preferences from an XML resource
//        setPreferencesFromResource(R.xml.fragment_settings_pref, rootKey);
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        var fragment: DialogFragment

        //        if (preference instanceof FolderChooser) {
//            fragment = FolderChooserCompat.newInstance(preference.getKey());
//            fragment.setTargetFragment(this, 0);
//            if (getFragmentManager() != null) {
//                fragment.show(getFragmentManager(), null);
//            }
//        } else {
        super.onDisplayPreferenceDialog(preference)
        //        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val stringValue = newValue.toString()
        if (preference is FolderChooser) {
            preference.summary = stringValue
        } else if (preference is CheckBoxPreference) {
            preference.isChecked = newValue as Boolean
        } else if (preference is SwitchPreference) {
            val switchPreference = preference
            switchPreference.isChecked = newValue as Boolean

            if (KEY_TELEGRAM_STORAGE == switchPreference.key) {
                val block_1 = findPreference<PreferenceCategory>("block-1")
                block_1!!.isEnabled = switchPreference.isChecked
            }
        } else if (preference is EditTextPreference) {
            val editTextPreference = preference
            editTextPreference.text = newValue as String
            editTextPreference.summary = newValue
        } else {
            preference.summary = "Wtf"
        }

        return true
    }

    //    @Override
    //    public void onAttach(Context c) {
    //        super.onAttach(c);
    //        if (c instanceof MyPreferenceCallBack) {
    //            mCallback = (MyPreferenceCallBack) c;
    //        } else {
    //            throw new RuntimeException(c.toString() + " must implement MyPreferanceCallBack");
    //        }
    //    }
    //    @Override
    //    public void onAttach(Activity a) {
    //        super.onAttach(a);
    //        if (a instanceof MyPreferenceCallBack) {
    //            mCallback = (MyPreferenceCallBack) a;
    //        } else {
    //            throw new RuntimeException(a.toString() + " must implement MyPreferanceCallBack");
    //        }
    //    }
    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     */
    private fun bindPreferenceSummaryToValue(preference: Preference) {
        preference.onPreferenceChangeListener = this


        //Устанавливаем настройки из сохраненных в LocalStorage
        val obj = LocalStorage.getInstance(requireContext()).settingsValue(preference.key)
        //String obj = mPreferences.getString(preference.getKey(),"");//Crash if boolean
        /*listener*/
        if (obj != null) {
            this.onPreferenceChange(preference, obj)
        }
    }

    companion object {
        //public static final String KEY_APP_LOCATION = "key_save_location";
        const val KEY_GOOGLE_DRIVE_STORAGE: String = "key_google_drive_storage"
        const val KEY_DROP_BOX_STORAGE: String = "key_drop_box_storage"

        //Telegram
        const val KEY_TELEGRAM_STORAGE: String = "key_telegram_storage"
        const val KEY_TELEGRAM_TOKEN: String = "key_telegram_token"
        const val KEY_TELEGRAM_CHAT_ID: String = "key_telegram_chat_id"
    }
}
