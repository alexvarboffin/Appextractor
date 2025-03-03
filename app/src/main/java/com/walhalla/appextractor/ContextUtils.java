package com.walhalla.appextractor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.walhalla.ui.DLog;

public class ContextUtils {
    public static void test0(Context context) {
        int resourceId = 0x7f080158;
        String packageName = "com.walhalla.mtprotolist";
        Context otherContext;
        try {
            otherContext = context.createPackageContext(packageName, 0);
            String resourceName = otherContext.getResources().getResourceEntryName(resourceId);
            boolean m = isDrawableResourceExist(otherContext, resourceName);
            DLog.d("Resource name: " + resourceName + " " + m);
        } catch (Resources.NotFoundException e) {
            DLog.d("Resource not found for ID: " + resourceId);
        } catch (PackageManager.NameNotFoundException e) {
            DLog.d("@@ PACKAGE NOT FOUND @@@ " + packageName + " " + resourceId);
        }
    }

    public static boolean isDrawableResourceExist(Context context, String resourceName) {
        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        return resourceId != 0;
    }
}
