package com.walhalla.appextractor.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.model.PackageMeta;

public class PackageMetaUtils {

    public static Bitmap drw(Context context, String packageName) {
        Bitmap bitmap = null;
        final PackageManager pm = context.getPackageManager();

        //am = context.createPackageContext(packageName, 0).getAssets();
        //Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            Drawable drawable = applicationInfo.loadIcon(pm);
            //AdaptiveIconDrawable
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                bitmap = bitmapDrawable.getBitmap();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//26
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    final Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            return bitmap;
        }
        return bitmap;
    }
}
