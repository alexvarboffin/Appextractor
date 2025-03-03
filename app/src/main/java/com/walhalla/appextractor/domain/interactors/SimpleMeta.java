package com.walhalla.appextractor.domain.interactors;

public class SimpleMeta {

    public String packageName;
    public String label;

    public SimpleMeta(String label, String packageName) {
        this.packageName = packageName;
        this.label = label;
    }
}
