package com.walhalla.appextractor.adapter2.activity;

import android.content.IntentFilter;
import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.adapter2.base.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ActivityLine implements ViewModel {

    public Drawable icon;
    public String label;
    public String className;
    public boolean exported;

    public ActivityLine() {
    }


    public List<IntentFilter> intentFilters = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("{name:%s, intent-filter.size():%s}", className, intentFilters.size());
    }
}