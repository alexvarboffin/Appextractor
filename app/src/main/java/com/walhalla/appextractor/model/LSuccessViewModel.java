package com.walhalla.appextractor.model;

public class LSuccessViewModel extends LogViewModel{

    public LSuccessViewModel(int icon, String msg) {
        super(icon, msg);
    }

    @Override
    public long getID() {
        return -1;
    }
}
