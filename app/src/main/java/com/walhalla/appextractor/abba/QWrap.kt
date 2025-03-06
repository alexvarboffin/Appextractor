package com.walhalla.appextractor.abba;

import android.content.pm.ResolveInfo;

import java.util.List;
import java.util.Map;

public class QWrap {

    public String mime;
    public Map<String, List<ResolveInfo>> map;

    public QWrap(String mime, Map<String, List<ResolveInfo>> map) {
        this.mime = mime;
        this.map = map;
    }
}