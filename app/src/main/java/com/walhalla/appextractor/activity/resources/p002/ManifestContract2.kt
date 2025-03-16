package com.walhalla.appextractor.activity.resources.p002;

import com.walhalla.appextractor.activity.resources.ResItem;

import java.util.List;

public interface ManifestContract2 {
    interface View {
        void showManifestContent(String content);
        void showError(String message);

        void showSuccess(List<ResItem> list);
    }

    interface Presenter {
        void loadManifestContent(String packageName);
    }
}
