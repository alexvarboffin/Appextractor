package com.walhalla.appextractor.activity.resources;

import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.activity.string.StringItem;

public class ResItem {

    public final Drawable drawable;

    public ResItem(String name, ResType type) {
        this.fullPath = name;
        this.type = type;
        this.drawable = null;
    }

    public final String fullPath;
    public final ResType type;

    public ResItem(String name, Drawable drawable, ResType type) {
        this.fullPath = name;
        this.type = type;
        this.drawable = drawable;
    }

    public static boolean isImages(ResItem item) {
        return item.fullPath.endsWith(".jpg") || item.fullPath.endsWith(".png");
    }

    public static boolean isImages(String fullPath) {
        return fullPath.endsWith(".jpg") || fullPath.endsWith(".png");
    }

    public static boolean isXml(StringItem resource) {
        //return resource.value.startsWith("res/xml");
        return resource.value.endsWith(".xml");
    }
}
