package com.walhalla.appextractor.adapter2.receiver;

import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.adapter2.base.ViewModel;

public class ReceiverLine implements ViewModel {

    public final Drawable icon;
    public final String label;
    public final String receiverName;
    public final boolean exported;
    public final boolean enabled;
    public String pkg;
    //public final String authority;

    public ReceiverLine(Drawable icon,
                        String label, String class_name,
                        boolean exported,
                        boolean enabled,
                        String pkg
    ) {
        this.icon = icon;
        this.label = label;
        this.receiverName = class_name;
        this.exported = exported;
        this.enabled = enabled;
        this.pkg = pkg;
    }
}