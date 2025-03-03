package com.walhalla.appextractor.adapter2.v2line;

import com.walhalla.appextractor.adapter2.base.ViewModel;

public class V2Line implements ViewModel {

    public final String key;
    public final String value;
    public final Integer drawable;

    public V2Line(String name, String value) {
        this(name, value, null);
    }

    public V2Line(String name, String value, Integer drawable) {
        this.key = name;
        this.value = value;
        this.drawable = drawable;
    }
}
