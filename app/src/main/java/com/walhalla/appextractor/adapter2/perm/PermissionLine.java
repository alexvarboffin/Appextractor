package com.walhalla.appextractor.adapter2.perm;


import com.walhalla.appextractor.adapter2.base.ViewModel;

public class PermissionLine implements ViewModel {

    public final String res0;

    public boolean isGranted() {
        return granted;
    }

    private final boolean granted;

    private final int protectionLevel;

    public PermissionLine(String name, boolean value, int protectionLevel) {
        this.res0 = name;
        this.granted = value;
        this.protectionLevel = protectionLevel;
    }

    public int getProtectionLevel() {
        return protectionLevel;
    }
}
