package com.walhalla.appextractor.activity.string;

import androidx.annotation.StringRes;

import com.walhalla.appextractor.resources.StringItemViewModel;

import java.util.List;

public interface MvpContract {
    interface View {
        void showResourceRawText(StringItemViewModel resource, String content);

        void showError(String message);

        void showSuccess(List<StringItemViewModel> list);

        void successToast(@StringRes int res, String aaa);

        void errorToast(@StringRes int res, String aaa);

    }
}
