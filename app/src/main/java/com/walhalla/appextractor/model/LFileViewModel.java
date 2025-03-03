package com.walhalla.appextractor.model;

import java.io.File;

public class LFileViewModel extends LogViewModel {

    public final File file;

    public LFileViewModel(File file, String msg, int icon) {
        super(icon, msg);
        this.file = file;
    }

    @Override
    public long getID() {
        return -1;
    }
}
