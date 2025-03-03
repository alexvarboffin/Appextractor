package com.walhalla.appextractor

object Config {
    const val __CLOUD_BACKUP_LOCATION_DROP_BOX: String = "APK_BACKUP"
    const val __CLOUD_BACKUP_LOCATION_LOCAL: String = "/"

    /**_APK_BACKUP_/ */
    const val CLOUD_BACKUP_LOCATION_GOOGLE_DRIVE: String = __CLOUD_BACKUP_LOCATION_DROP_BOX

    //Variants
    //"APK_BKP_" + Build.DEVICE;
    const val ALERT_EXTR_STORAGE_CB_KEY: String = "uu"
    const val EXTRA_PATH: String = "extra.path"


    //private static final String DROPBOX_ACCESS_TOKEN = "53bdyrbc9g8swow";
    //    {".tag":"path","path":{".tag":"conflict","conflict":"file"}}
    const val CHILD_FOLDER_NAME: String = "APK"
    const val PREV: String = "Apk_Chief_"
}
