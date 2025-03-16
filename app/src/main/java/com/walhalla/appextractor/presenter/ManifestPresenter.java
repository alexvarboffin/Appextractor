package com.walhalla.appextractor.presenter;

public interface ManifestPresenter {

    String TAG_MANIFEST = "manifest";

    boolean configForPackage(String packageName, String apkPath0);

}
