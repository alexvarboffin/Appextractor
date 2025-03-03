package com.walhalla.appextractor.model;

public class LErrorViewModel extends LogViewModel {

    public static final int TYPE_ERROR = 5;

    public LErrorViewModel(int icon, String msg) {
        super(icon, msg);
    }

    @Override
    public long getID() {
        return TYPE_ERROR;
    }
}
