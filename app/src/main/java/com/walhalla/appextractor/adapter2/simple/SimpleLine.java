package com.walhalla.appextractor.adapter2.simple;


import com.walhalla.appextractor.adapter2.base.ViewModel;

public class SimpleLine implements ViewModel {

    public final int res0;
    public final String value;

    public SimpleLine(int name, String value) {
        this.res0 = name;
        this.value = value;
    }
}
