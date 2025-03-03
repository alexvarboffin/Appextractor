package com.walhalla.appextractor.presenter;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.webkit.WebView;

import com.walhalla.appextractor.activity.manifest.ManifestPresenterXml;
import com.walhalla.appextractor.model.PackageMeta;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public interface ManifestPresenter {

    String TAG_MANIFEST = "manifest";

    boolean configForPackage(String packageName, String apkPath0);

}
