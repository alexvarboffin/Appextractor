package com.walhalla.appextractor.adapter2.headerCollapsed;

import com.walhalla.appextractor.adapter2.base.ViewModel;
import com.walhalla.appextractor.adapter2.cert.CertLine;
import com.walhalla.appextractor.adapter2.simple.SimpleLine;

import java.util.ArrayList;
import java.util.List;

public class HeaderCollapsedObject implements ViewModel {

    public final String title;
    public final Integer icon;

    public HeaderCollapsedObject(String name, Integer icon) {
        this.title = name;
        this.icon = icon;
    }


    public final List<ViewModel> list = new ArrayList<>();
}

