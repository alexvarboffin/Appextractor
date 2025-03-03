package com.walhalla.appextractor.activity.detail;

import android.graphics.drawable.Drawable;

public interface DetailContract {

    interface View {
        void setTitleWithIcon(String title, String packageName, Drawable icon);
    }

    interface Presenter {
    }

}
