package com.walhalla.appextractor.adapter2.dirline;


import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;

import com.walhalla.appextractor.adapter2.base.ViewModel;

public class DirLine implements ViewModel {

    public final String name;
    public final String value;
    public final int icon;

    public DirLine(String name, String value, @DrawableRes int icon) {
        this.name = name;
        this.value = value;
        this.icon = icon;
    }
}
