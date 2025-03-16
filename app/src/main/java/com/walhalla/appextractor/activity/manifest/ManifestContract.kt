package com.walhalla.appextractor.activity.manifest;

import android.graphics.drawable.Drawable;

public interface ManifestContract {

    interface View {
        void setTitleWithIcon(String title, String packageName, Drawable icon);
    }

    interface Presenter {
    }

}
