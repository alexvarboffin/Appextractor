package com.walhalla.appextractor.activity.detail;

import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.adapter2.base.ViewModel;

import java.util.List;

public interface DetailsF0 {

    interface View {
        void snack();

        void swap(List<ViewModel> data);
    }

    interface Presenter {
        //void getAllStringResourcesFromPackage(Context context, String packageName);
    }
}
