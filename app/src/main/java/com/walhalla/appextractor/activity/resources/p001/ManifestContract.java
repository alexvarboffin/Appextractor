package com.walhalla.appextractor.activity.resources.p001;

import android.content.Context;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import com.walhalla.appextractor.activity.resources.ResItem;

import java.util.List;

public interface ManifestContract {
    interface View {
        void showResourceRawText(ResItem resource, String content);

        void showError(String message);

        void showSuccess(List<ResItem> list);

        void successToast(@StringRes int res, String aaa);

        void errorToast(@StringRes int res, String aaa);

    }

    interface Presenter {
        void loadManifestContent(String packageName);
        void saveAsset(Context context, ResItem resource);
        void exportIconRequest(Context context, ResItem resource);

        void readAssetRequest(Context context, ResItem resource);

        void zipAllAssetsRequest(FragmentActivity activity, ResItem resource);
    }
}
