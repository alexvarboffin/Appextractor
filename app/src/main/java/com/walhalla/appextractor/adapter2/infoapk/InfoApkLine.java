package com.walhalla.appextractor.adapter2.infoapk;


import androidx.annotation.DrawableRes;

import com.walhalla.appextractor.adapter2.base.ViewModel;

public class InfoApkLine implements ViewModel {

    public final String name;
    public final String value;
    public final int icon;

    public InfoApkLine(String name, String value, @DrawableRes int icon) {
        this.name = name;
        this.value = value;
        this.icon = icon;
    }
}
