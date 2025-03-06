package com.walhalla.appextractor.activity.appscanner

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.appscanner.MimeTabAdapter.MimeViewHolder

/**
 * ServiceIntentScanner
 *
 * Этот класс предназначен для сканирования устройства на наличие служб, соответствующих определённым Intent действиям.
 *
 * Он используется для поиска и отображения информации о службах, которые могут обрабатывать определённые действия Intent.
 * Это полезно для анализа и отладки приложений, а также для понимания того, какие службы доступны на устройстве для
 * обработки различных намерений.
 *
 * Пример использования:
 * ServiceIntentScanner scanner = new ServiceIntentScanner(context);
 * scanner.scanAndDisplayServices();
 */
class MimeTabAdapter(private val mContext: Context) :
    RecyclerView.Adapter<MimeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MimeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
        return MimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MimeViewHolder, position: Int) {
        val mimeType = "234"
        holder.bindRecyclerView(mimeType)
    }

    override fun getItemCount(): Int {
        return 1
    }

    class MimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mRecyclerView: RecyclerView =
            itemView.findViewById(R.id.recycler_view)
        private val mAdapter: ActivityListAdapter

        init {
            mRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context)
            mAdapter = ActivityListAdapter()
            mRecyclerView.adapter = mAdapter
        }

        fun bindRecyclerView(mimeType: String?) {
            val activityList = getServices(ALL_ACTIONS, itemView.context)
            if (activityList != null) {
                mAdapter.setActivityList(activityList)
            }
        }

        companion object {
            fun getServices(actions: HashSet<String?>, context: Context): List<ResolveInfo> {
                val list: MutableList<ResolveInfo> = ArrayList()
                val pm = context.packageManager
                for (action in actions) {
                    val intent = Intent(action)
                    val resolveInfoList = pm.queryIntentServices(intent, 0)
                    if (!resolveInfoList.isEmpty()) {
                        list.addAll(resolveInfoList)
                    }
                }
                return list
            }
        }
    }

    companion object {
        val ALL_ACTIONS: HashSet<String?> = object : HashSet<String?>() {
            init {
                // Действия для работы с активностями
                add(Intent.ACTION_VIEW)
                add(Intent.ACTION_ATTACH_DATA)
                add(Intent.ACTION_EDIT)
                add(Intent.ACTION_INSERT_OR_EDIT)
                add(Intent.ACTION_PICK)
                add(Intent.ACTION_CHOOSER)
                add(Intent.ACTION_GET_CONTENT)
                add(Intent.ACTION_DIAL)
                add(Intent.ACTION_CALL)
                //            add(Intent.ACTION_CALL_PRIVILEGED);
                add(Intent.ACTION_SEND)
                add(Intent.ACTION_SENDTO)
                add(Intent.ACTION_ANSWER)
                add(Intent.ACTION_INSERT)
                add(Intent.ACTION_DELETE)
                add(Intent.ACTION_RUN)
                add(Intent.ACTION_SYNC)
                add(Intent.ACTION_PICK_ACTIVITY)
                add(Intent.ACTION_SEARCH)
                add(Intent.ACTION_WEB_SEARCH)
                add(Intent.ACTION_FACTORY_TEST)

                // Действия для работы с устройством
                add(Intent.ACTION_POWER_CONNECTED)
                add(Intent.ACTION_POWER_DISCONNECTED)
                add(Intent.ACTION_SHUTDOWN)
                add(Intent.ACTION_REBOOT)
                add(Intent.ACTION_HEADSET_PLUG)
                //            add(Intent.ACTION_USB_DEVICE_ATTACHED);
//            add(Intent.ACTION_USB_DEVICE_DETACHED);
                add(Intent.ACTION_MEDIA_MOUNTED)
                add(Intent.ACTION_MEDIA_UNMOUNTED)
                add(Intent.ACTION_MEDIA_REMOVED)
                add(Intent.ACTION_MEDIA_BAD_REMOVAL)
                add(Intent.ACTION_CAMERA_BUTTON)
                add(Intent.ACTION_BATTERY_CHANGED)
                add(Intent.ACTION_BATTERY_LOW)
                add(Intent.ACTION_BATTERY_OKAY)
                add(Intent.ACTION_DEVICE_STORAGE_LOW)
                add(Intent.ACTION_DEVICE_STORAGE_OK)
                add(Intent.ACTION_TIME_TICK)
                add(Intent.ACTION_TIME_CHANGED)
                add(Intent.ACTION_TIMEZONE_CHANGED)
                add(Intent.ACTION_DATE_CHANGED)
                add(Intent.ACTION_LOCALE_CHANGED)

                // Другие стандартные действия
                add(Intent.ACTION_SEND_MULTIPLE)
                add(Intent.ACTION_INSTALL_PACKAGE)
                add(Intent.ACTION_UNINSTALL_PACKAGE)
                add(Intent.ACTION_PACKAGE_ADDED)
                add(Intent.ACTION_PACKAGE_REMOVED)
                add(Intent.ACTION_PACKAGE_CHANGED)
                add(Intent.ACTION_PACKAGE_REPLACED)
                add(Intent.ACTION_MY_PACKAGE_REPLACED)
                add(Intent.ACTION_PACKAGE_INSTALL)
                add(Intent.ACTION_PACKAGE_FIRST_LAUNCH)
                add(Intent.ACTION_PACKAGE_FULLY_REMOVED)
                add(Intent.ACTION_UID_REMOVED)
                add(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                add(Intent.ACTION_MEDIA_SCANNER_STARTED)
                add(Intent.ACTION_MEDIA_SCANNER_FINISHED)
                add(Intent.ACTION_NEW_OUTGOING_CALL)
                add(Intent.ACTION_USER_PRESENT)
                add(Intent.ACTION_SCREEN_OFF)
                add(Intent.ACTION_SCREEN_ON)
                add(Intent.ACTION_BOOT_COMPLETED)
                add(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                add(Intent.ACTION_USER_INITIALIZE)
                //            add(Intent.ACTION_USER_ADDED);
//            add(Intent.ACTION_USER_REMOVED);
//            add(Intent.ACTION_USER_STARTED);
//            add(Intent.ACTION_USER_STOPPED);
//            add(Intent.ACTION_USER_SWITCHED); add(Intent.ACTION_OPEN_FOLDER)
                add(Intent.ACTION_USER_UNLOCKED)
                add(Intent.ACTION_ASSIST)
                add(Intent.ACTION_VOICE_COMMAND)
                add(Intent.ACTION_SEARCH_LONG_PRESS)
                add(Intent.ACTION_WEB_SEARCH)
                add(Intent.ACTION_ALL_APPS)
                add(Intent.ACTION_OPEN_DOCUMENT)
                add(Intent.ACTION_CREATE_DOCUMENT)
                add(Intent.ACTION_OPEN_DOCUMENT_TREE)
                add(Intent.ACTION_GET_CONTENT)
                add(Intent.ACTION_GET_CONTENT)

                add(Intent.ACTION_PICK_ACTIVITY)
                add(Intent.ACTION_SEARCH)
                add(Intent.ACTION_APPLICATION_PREFERENCES)
                add(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED)
                add(Intent.ACTION_MANAGE_NETWORK_USAGE)
                add(Intent.ACTION_INSTALL_FAILURE)
                //            add(Settings.ACTION_VIEW_DOWNLOADS);
//            add(Settings.ACTION_OPEN_SETTINGS);
                add(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                //add(Settings.ACTION_MANAGE_PERMISSIONS);
                add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                add(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                add(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                add(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                add(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                add(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                add(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                add(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                //            add(Intent.ACTION_SHOW_APP_NOTIFICATION_SETTINGS);
//            add(Intent.ACTION_ZEN_MODE_SETTINGS);
                add(Intent.ACTION_QUICK_CLOCK)
                //            add(Intent.ACTION_VOICE_SETTINGS);
//            add(Intent.ACTION_PRINT_SETTINGS);
//            add(Intent.ACTION_PRINT);
//            add(Intent.ACTION_PRINT_PREVIEW);
//            add(Intent.ACTION_SEARCH_SETTINGS);
//            add(Intent.ACTION_NETWORK_OPERATOR_SETTINGS);
//            add(Intent.ACTION_DATA_ROAMING_SETTINGS);
//            add(Intent.ACTION_PRIVACY_SETTINGS);
//            add(Intent.ACTION_WIFI_SETTINGS);
//            add(Intent.ACTION_WIFI_IP_SETTINGS);
//            add(Intent.ACTION_BLUETOOTH_SETTINGS);
//            add(Intent.ACTION_DATE_SETTINGS);
//            add(Intent.ACTION_SOUND_SETTINGS);
//            add(Intent.ACTION_DISPLAY_SETTINGS);
//            add(Intent.ACTION_LOCALE_SETTINGS);
//            add(Intent.ACTION_INPUT_METHOD_SETTINGS);
//            add(Intent.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);
//            add(Intent.ACTION_SHOW_REGULATORY_INFO);
//            add(Intent.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
//            add(Intent.ACTION_DEVICE_INFO_SETTINGS);
//            add(Intent.ACTION_MEMORY_CARD_SETTINGS);
//            add(Intent.ACTION_INTERNAL_STORAGE_SETTINGS);
//            add(Intent.ACTION_USAGE_ACCESS_SETTINGS);
//            add(Intent.ACTION_APPLICATION_DETAILS_SETTINGS);
//            add(Intent.ACTION_DEVICE_INFO_SETTINGS);
//            add(Intent.ACTION_HARD_KEYBOARD_SETTINGS);
//            add(Intent.ACTION_SHOW_INPUT_METHOD_PICKER);
//            add(Intent.ACTION_USER_DICTIONARY_SETTINGS);
//            add(Intent.ACTION_SEARCH_SETTINGS);
//            add(Intent.ACTION_DEVICE_SEARCH_SETTINGS);
//            add(Intent.ACTION_VOICE_INPUT_SETTINGS);
//            add(Intent.ACTION_USER_CREDENTIALS_SETTINGS);
//            add(Intent.ACTION_DATA_USAGE_SETTINGS);
//            add(Intent.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
//            add(Intent.ACTION_REQUEST_SET_AUTOFILL_SERVICE);
//            add(Intent.ACTION_BACKGROUND_DATA_SETTING_CHANGED);
//            add(Intent.ACTION_SYNC_SETTINGS);
//            add(Intent.ACTION_ADD_ACCOUNT);
//            add(Intent.ACTION_ADD_SUBSCRIPTION);
//            add(Intent.ACTION_CONFIGURE_ACCOUNT);
//            add(Intent.ACTION_SYNC_SETUP);
//            add(Intent.ACTION_WIFI_ADD_NETWORKS);
//            add(Intent.ACTION_WIFI_NETWORK_SUGGESTION_PICKER);
//            add(Intent.ACTION_WIFI_P2P_SETTINGS);
//            add(Intent.ACTION_WIFI_DISPLAY_SETTINGS);
//            add(Intent.ACTION_NFC_SETTINGS);
//            add(Intent.ACTION_NFCSHARING_SETTINGS);
//            add(Intent.ACTION_WIRELESS_SETTINGS);
//            add(Intent.ACTION_HARDWARE_TEST);
//            add(Intent.ACTION_DEVICE_IDENTITY_SETTINGS);
//            add(Intent.ACTION_USER_ENCRYPTION_SETTINGS);
//            add(Intent.ACTION_TRUST_AGENT_SETTINGS);
//            add(Intent.ACTION_MEDIA_PLAY_FROM_SEARCH);
//            add(Intent.ACTION_GLOBAL_SEARCH);  add(Intent.ACTION_LOCK_TASK_MODE_CHANGED);
//            add(Intent.ACTION_APP_DISAMBIGUATION);add(Intent.ACTION_APPLICATION_DELEGATION_SCHEME_URL); add(Intent.ACTION_CALL_EMERGENCY);
//            add(Intent.ACTION_CALL_PRIVILEGED);        add(Intent.ACTION_CHANGE_DEFAULT);
//            add(Intent.ACTION_CHANGE_NETWORK_STATE);
//            add(Intent.ACTION_CLEAR_DNS_CACHE);add(Intent.ACTION_DEFAULT_SOUND_SETTINGS);
//            add(Intent.ACTION_DEFAULT_VOICE_CALL_PACKAGE_CHANGED);add(Intent.ACTION_DROPBOX_ENTRY_ADDED);
//            add(Intent.ACTION_DROPBOX_ENTRY_CHANGED);
//            add(Intent.ACTION_DROPBOX_ENTRY_DELETED);
//            add(Intent.ACTION_DYNAMIC_SENSOR_CHANGED); add(Intent.ACTION_PERSISTENT_ACTIVITY);
//            add(Intent.ACTION_PREFERRED_ACTIVITY_CHANGED);   add(Intent.ACTION_REMOTE_INTENT);
//            add(Intent.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//            add(Intent.ACTION_RESOLVE_EPHEMERAL_PACKAGE);
                add(Intent.ACTION_ASSIST)
                add(Intent.ACTION_VOICE_COMMAND)
                add(Intent.ACTION_SEARCH_LONG_PRESS)
                add(Intent.ACTION_WEB_SEARCH)
                add(Intent.ACTION_APPLICATION_PREFERENCES)
                add(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED)
                add(Intent.ACTION_LOCALE_CHANGED)

                add(Intent.ACTION_BUG_REPORT)

                add(Intent.ACTION_DEFAULT)
                add(Intent.ACTION_DIAL)
                add(Intent.ACTION_ANSWER)
                add(Intent.ACTION_CALL_BUTTON)

                add(Intent.ACTION_CALL)

                add(Intent.ACTION_CONFIGURATION_CHANGED)
                add(Intent.ACTION_CREATE_SHORTCUT)
                add(Intent.ACTION_DATE_CHANGED)

                add(Intent.ACTION_DELETE)
                add(Intent.ACTION_DEVICE_STORAGE_LOW)
                add(Intent.ACTION_DEVICE_STORAGE_OK)
                add(Intent.ACTION_DOCK_EVENT)
                add(Intent.ACTION_DREAMING_STARTED)
                add(Intent.ACTION_DREAMING_STOPPED)

                add(Intent.ACTION_EDIT)
                add(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE)
                add(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE)
                add(Intent.ACTION_FACTORY_TEST)
                add(Intent.ACTION_MEDIA_EJECT)
                add(Intent.ACTION_MEDIA_SCANNER_FINISHED)
                add(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                add(Intent.ACTION_MEDIA_SCANNER_STARTED)
                add(Intent.ACTION_MEDIA_SHARED)
                add(Intent.ACTION_MEDIA_UNMOUNTABLE)
                add(Intent.ACTION_MEDIA_UNMOUNTED)
                add(Intent.ACTION_MY_PACKAGE_REPLACED)
                add(Intent.ACTION_NEW_OUTGOING_CALL)
                add(Intent.ACTION_PACKAGE_ADDED)
                add(Intent.ACTION_PACKAGE_CHANGED)
                add(Intent.ACTION_PACKAGE_DATA_CLEARED)
                add(Intent.ACTION_PACKAGE_FIRST_LAUNCH)
                add(Intent.ACTION_PACKAGE_FULLY_REMOVED)
                add(Intent.ACTION_PACKAGE_INSTALL)
                add(Intent.ACTION_PACKAGE_NEEDS_VERIFICATION)
                add(Intent.ACTION_PACKAGE_REMOVED)
                add(Intent.ACTION_PACKAGE_REPLACED)
                add(Intent.ACTION_PACKAGE_RESTARTED)
                add(Intent.ACTION_PACKAGE_VERIFIED)
                add(Intent.ACTION_PACKAGES_SUSPENDED)
                add(Intent.ACTION_PACKAGES_UNSUSPENDED)

                add(Intent.ACTION_PROCESS_TEXT)
                add(Intent.ACTION_PROVIDER_CHANGED)
                add(Intent.ACTION_QUICK_CLOCK)
                add(Intent.ACTION_REBOOT)

                add(Intent.ACTION_RUN)
                add(Intent.ACTION_SCREEN_OFF)
                add(Intent.ACTION_SCREEN_ON)
                add(Intent.ACTION_SEARCH)
                add(Intent.ACTION_SEARCH_LONG_PRESS)
                add(Intent.ACTION_SEND)
                add(Intent.ACTION_SENDTO)
                add(Intent.ACTION_SEND_MULTIPLE)
                add(Intent.ACTION_SET_WALLPAPER)
                add(Intent.ACTION_SHOW_APP_INFO)
                //add(Intent.ACTION_SHOW_NOTICE);
                add(Intent.ACTION_SHUTDOWN)
                add(Intent.ACTION_SYNC)
                add(Intent.ACTION_SYSTEM_TUTORIAL)
                add(Intent.ACTION_TIMEZONE_CHANGED)
                add(Intent.ACTION_TIME_CHANGED)
                add(Intent.ACTION_TIME_TICK)
                add(Intent.ACTION_UID_REMOVED)
                add(Intent.ACTION_UNINSTALL_PACKAGE)
                add(Intent.ACTION_USER_BACKGROUND)
                add(Intent.ACTION_USER_FOREGROUND)
                add(Intent.ACTION_USER_INITIALIZE)
                add(Intent.ACTION_USER_PRESENT)
                add(Intent.ACTION_USER_UNLOCKED)
                add(Intent.ACTION_VIEW)
                add(Intent.ACTION_VOICE_COMMAND)
                add(Intent.ACTION_WALLPAPER_CHANGED)
                add(Intent.ACTION_WEB_SEARCH)
                //            add(Intent.ACTION_WIFI_DEVICE_ENABLING_DISABLING);
//            add(Intent.ACTION_WIFI_DISPLAY_STATUS_CHANGED);
//            add(Intent.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);
//            add(Intent.ACTION_WINDOW_CONFIGURATION_CHANGED);
                add(Intent.ACTION_PACKAGE_REMOVED)
                add(Intent.ACTION_PACKAGE_REPLACED)
            }
        }
    }
}
