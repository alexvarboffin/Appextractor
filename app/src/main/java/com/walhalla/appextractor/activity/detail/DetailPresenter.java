package com.walhalla.appextractor.activity.detail;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.ui.DLog;

public class DetailPresenter implements DetailContract.Presenter {

    private final DetailContract.View mView;
    private final Context context;

    public DetailPresenter(Context context, PackageMeta meta, DetailContract.View view) {
        final PackageManager manager = context.getPackageManager();
        this.context = context;
        this.mView = view;
        doStuff(manager, meta);
    }

    private void doStuff(PackageManager packageManager, PackageMeta meta) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(meta.packageName, 0);
            CharSequence title = applicationInfo.loadLabel(packageManager);
            Drawable icon = applicationInfo.loadIcon(packageManager);
            mView.setTitleWithIcon(title.toString(), applicationInfo.packageName, icon);

        } catch (PackageManager.NameNotFoundException e) {
            DLog.handleException(e);
        }
    }
}
