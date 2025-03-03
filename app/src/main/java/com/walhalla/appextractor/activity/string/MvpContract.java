package com.walhalla.appextractor.activity.string;

import androidx.annotation.StringRes;

import java.util.List;

public interface MvpContract {
    interface View {
        void showResourceRawText(StringItem resource, String content);

        void showError(String message);

        void showSuccess(List<StringItem> list);

        void successToast(@StringRes int res, String aaa);

        void errorToast(@StringRes int res, String aaa);

    }
}
