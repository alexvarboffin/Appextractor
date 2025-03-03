package com.walhalla.appextractor.adapter2.cert;


import com.walhalla.appextractor.adapter2.base.ViewModel;

public class CertLine implements ViewModel {

    public final String res0;
    public final String value;

    public CertLine(String name, String value) {
        this.res0 = name;
        this.value = value;
    }
}
