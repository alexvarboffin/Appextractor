package com.walhalla.appextractor.adapter2.header;

import com.walhalla.appextractor.adapter2.base.ViewModel;

public class HeaderObject implements ViewModel {

    public final String title;
    public final Integer icon;

    public HeaderObject(String name, Integer icon) {
        this.title = name;
        this.icon = icon;
    }
}

