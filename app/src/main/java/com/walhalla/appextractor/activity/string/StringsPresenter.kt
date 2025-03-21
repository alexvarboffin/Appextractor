package com.walhalla.appextractor.activity.string;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.walhalla.appextractor.resources.ResType;
import com.walhalla.appextractor.resources.StringItemViewModel;
import com.walhalla.appextractor.sdk.ResourcesToolForPlugin;
import com.walhalla.ui.DLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StringsPresenter {

    private final Context context;
    private final MvpContract.View mView;

    String mPackageName = null;
    private AssetManager am = null;
    private Resources mResources = null;

    public StringsPresenter(Context m, MvpContract.View mView) {
        this.context = m;
        this.mView = mView;
    }


    public void loadManifestContent(String packageName, String resourceType) {
        this.mPackageName = packageName;

        AssetManager initAM = am;
        Resources initRes = mResources;
        try {
            am = context.createPackageContext(packageName, 0).getAssets();
            mResources = new Resources(am, context.getResources().getDisplayMetrics(), null);
            //resources = new Resources(am, null, null);
        } catch (PackageManager.NameNotFoundException name) {
            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
            am = initAM;
            mResources = initRes;
        } catch (RuntimeException unexpected) {
            DLog.d("@@@ error configuring for package: " + packageName + " " + unexpected.getMessage());
            am = initAM;
            mResources = initRes;
        }
        List<StringItemViewModel> m = new ArrayList<>();
        //loadApplicationResources(this, m, getPackageName());
        loadApplicationResources(context, m, packageName, resourceType);
        mView.showSuccess(m);
    }

    @SuppressLint("DiscouragedApi")
    private int getResourceId(String sourceName, String sourceType) {
        if (mResources == null || TextUtils.isEmpty(mPackageName) || TextUtils.isEmpty(sourceName)) {
            return -1;
        }
        return mResources.getIdentifier(sourceName, sourceType, mPackageName);
    }


    private void loadApplicationResources(Context context, List<StringItemViewModel> iconPackResources,
                                          String packageName, String resourceType) {/*from w w w.  j a  va 2s. co  m*/

        ResType draw = ResourcesToolForPlugin.getIconBySourceType(resourceType);
        Field[] drawableItems;
        try {

            //Всегда ClassNotFoundException
            // Context appContext = context.createPackageContext(packageName,0);

            Context appContext = context.createPackageContext(packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
            );
            //drawableItems = Class.forName(packageName + ".R$drawable", true, appContext.getClassLoader()).getFields();
            drawableItems = Class.forName(packageName + ".R$" + resourceType, true, appContext.getClassLoader()).getFields();

        } catch (ClassNotFoundException e) {
            mView.showError("@ww@" + e.toString());
            return;
        } catch (Exception e) {
            DLog.d("@ww@" + e.toString());
            return;
        }
        for (Field f : drawableItems) {
            String name = f.getName();
            String icon = name.toLowerCase();
            name = name.replaceAll("_", ".");
            String stringValue = "";
            int stringId = getResourceId(icon, resourceType);
            if (stringId > 0) {
                try {
                    stringValue = mResources.getString(stringId);
                } catch (Resources.NotFoundException e) {
                    stringValue = "Resources.NotFoundException " + stringId;
                }
            }

            //iconPackResources.add(new ResItem(name + " " + icon + " " + stringValue, null));
            //@@iconPackResources.add(new StringItem(icon, stringValue, null));

            int activityIndex = name.lastIndexOf(".");
            if (activityIndex <= 0 || activityIndex == name.length() - 1) {
                continue;
            }

            String iconPackage = name.substring(0, activityIndex);
            if (TextUtils.isEmpty(iconPackage)) {
                continue;
            }
            //iconPackResources.add(new ResItem(iconPackage + " " + icon, null));
            //@@iconPackResources.add(new StringItem(icon, stringValue, null));

            String iconActivity = name.substring(activityIndex + 1);
            if (TextUtils.isEmpty(iconActivity)) {
                continue;
            }
            //iconPackResources.add(new ResItem(iconPackage + "." + iconActivity + " " + icon, null));
            iconPackResources.add(new StringItemViewModel(icon, stringValue, draw));
        }
    }

}
