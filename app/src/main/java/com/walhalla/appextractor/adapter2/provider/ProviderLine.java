package com.walhalla.appextractor.adapter2.provider;

import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.adapter2.base.ViewModel;

public class ProviderLine implements ViewModel {

    public final Drawable icon;
    public final String label;
    public final String class_name;
    public final boolean exported;
    public final boolean enabled;
    public final String authority;

    public ProviderLine(Drawable icon, String label, String class_name,
                        boolean exported,
                        boolean enabled,
                        String authority) {
        this.icon = icon;
        this.label = label;
        this.class_name = class_name;
        this.exported = exported;
        this.enabled = enabled;
        this.authority = authority;
    }

    public ProviderLine(String label, String class_name,
                        String authority) {
        this(null, label, class_name, true, true, authority);
    }
}