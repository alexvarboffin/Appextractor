package com.walhalla.appextractor.adapter2.service;

import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.adapter2.base.ViewModel;

public class ServiceLine implements ViewModel {

    public final Drawable icon;
    public final String label;
    public final String class_name;
    public final boolean exported;

    public ServiceLine(Drawable icon, String label, String class_name, boolean exported) {
        this.icon = icon;
        this.label = label;
        this.class_name = class_name;
        this.exported=exported;
    }
}