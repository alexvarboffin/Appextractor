package com.walhalla.appextractor.utils;

import static android.content.pm.PermissionInfo.PROTECTION_FLAG_INSTANT;
import static android.content.pm.PermissionInfo.PROTECTION_FLAG_PREINSTALLED;
import static android.content.pm.PermissionInfo.PROTECTION_FLAG_PRIVILEGED;
import static android.content.pm.PermissionInfo.PROTECTION_NORMAL;

import android.content.pm.PermissionInfo;

public class PermissionUtils {

    public static String protectionLevelToString(int protectionLevelKey) {
        String protectionLevel;
        switch (protectionLevelKey) {

            case PROTECTION_NORMAL:
                protectionLevel = "normal";
                break;
            case PermissionInfo.PROTECTION_DANGEROUS:
                protectionLevel = "dangerous";
                break;
            case PermissionInfo.PROTECTION_SIGNATURE:
                protectionLevel = "signature";
                break;
            case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
                protectionLevel = "signatureOrSystem";
                break;

            case PROTECTION_FLAG_PRIVILEGED:
                protectionLevel = "PRIVILEGED";
                break;

            case PROTECTION_FLAG_PREINSTALLED:
                protectionLevel = "PREINSTALLED";
                break;

            case PROTECTION_FLAG_INSTANT:
                protectionLevel = "INSTANT";
                break;

            default:
                protectionLevel = "<unknown> " + protectionLevelKey;
                break;
        }
        return protectionLevel;
    }
}
