package com.walhalla.appextractor.compat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

public class ComV19 {
    public static Drawable getDrawable(Context context, int res) {
        Drawable draw;
        if (Build.VERSION.SDK_INT > 19) {
            draw = ContextCompat.getDrawable(context, res);
        } else {
            //Вектор падает на 4.4 sdk 19
            draw = AppCompatResources.getDrawable(context, res);
        }
        return draw;
    }
}
