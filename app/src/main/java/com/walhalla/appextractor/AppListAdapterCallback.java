package com.walhalla.appextractor;

import android.content.Context;
import android.view.View;

import com.walhalla.appextractor.model.PackageMeta;

import java.util.List;

public interface AppListAdapterCallback extends NotificationToast{

    void nowExtractOneSelected(List<PackageMeta> info, String[] appName);

    void openPackageOnGooglePlay(String packageName);

    void hideProgressBar();

    void launchApp(Context context, String packageName);

    Context getActivity();

    void uninstallApp(String packageName);

    void count(int size);

    void shareToOtherApp(String generate_app_name);

    void menuExtractSelected(View v);

    void saveIconRequest(PackageMeta packageInfo);

    void exportIconRequest(PackageMeta packageInfo);
}