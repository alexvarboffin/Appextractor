package com.walhalla.appextractor.model;

public class EmptyViewModel implements ViewModel {

    public static final int TYPE_EMPTY = 1;
    private final String txt;

    public int getIcon() {
        return icon;
    }

    private final int icon;

    public EmptyViewModel(int icon, String msg) {
        this.txt = msg;
        this.icon = icon;
    }

    public String getText() {
        return txt;
    }

    @Override
    public long getID() {
        return TYPE_EMPTY;
    }
}
