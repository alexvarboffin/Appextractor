package com.walhalla.appextractor.model;

public abstract class LogViewModel implements ViewModel {

    private final String txt;

    public int getIcon() {
        return icon;
    }

    private final int icon;

    public LogViewModel(int icon, String msg) {
        this.txt = msg;
        this.icon = icon;
    }

    public String getText() {
        return txt;
    }
}
