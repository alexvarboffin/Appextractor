package com.walhalla.appextractor.activity.manifest;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.walhalla.appextractor.activity.detail.DetailContract;
import com.walhalla.appextractor.model.PackageMeta;

public class MainManifestPresenter implements ManifestContract.Presenter {

    private final ManifestContract.View mView;
    private final Context context;

    public MainManifestPresenter(Context context, PackageMeta meta, ManifestContract.View view) {
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
            throw new RuntimeException(e);
        }
    }
}
