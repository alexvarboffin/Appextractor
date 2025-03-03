package com.walhalla.appextractor.model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class PackageMeta implements Parcelable {

    public String packageName;
    public String label;
    public boolean hasSplits;
    public boolean hasPinning;
    public boolean isSystemApp;
    public long versionCode;
    public String versionName;
    public Uri iconUri;
    public long installTime;
    public long updateTime;

    public String size;
    public String sourceDir;

    public long firstInstallTime;
    public long lastUpdateTime;
    public int category;

    //Extended
    public boolean isGranted;

    public PackageMeta(String packageName, String label) {
        this.packageName = packageName;
        this.label = label;
    }


    public static class Builder {
        private final PackageMeta mPackageMeta;

        public Builder(String packageName) {
            mPackageMeta = new PackageMeta(packageName, "?");
        }

        public Builder setLabel(String label) {
            mPackageMeta.label = label;
            return this;
        }

        public Builder setHasSplits(boolean hasSplits) {
            mPackageMeta.hasSplits = hasSplits;
            return this;
        }

        public Builder setIsSystemApp(boolean isSystemApp) {
            mPackageMeta.isSystemApp = isSystemApp;
            return this;
        }

        public Builder setVersionCode(long versionCode) {
            mPackageMeta.versionCode = versionCode;
            return this;
        }

        public Builder setVersionName(String versionName) {
            mPackageMeta.versionName = versionName;
            return this;
        }

        public Builder setIcon(int iconResId) {
            if (iconResId == 0) {
                mPackageMeta.iconUri = null;
                return this;
            }

            mPackageMeta.iconUri = new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(mPackageMeta.packageName)
                    .path(String.valueOf(iconResId))
                    .build();

            return this;
        }

        public Builder setIconUri(Uri iconUri) {
            mPackageMeta.iconUri = iconUri;
            return this;
        }


        public Builder setInstallTime(long installTime) {
            mPackageMeta.installTime = installTime;
            return this;
        }

        public Builder setUpdateTime(long updateTime) {
            mPackageMeta.updateTime = updateTime;
            return this;
        }

        public PackageMeta build() {
            return mPackageMeta;
        }

        public Builder setHasPining(boolean pining) {
            mPackageMeta.hasPinning = pining;
            return this;
        }
    }


//    public static PackageMeta forPackage(Context context, String packageName) {
//        try {
//            PackageManager pm = context.getPackageManager();
//
//            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
//            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
//
//            return new PackageMeta.Builder(applicationInfo.packageName)
//                    .setLabel(applicationInfo.loadLabel(pm).toString())
//                    .setHasSplits(applicationInfo.splitPublicSourceDirs != null && applicationInfo.splitPublicSourceDirs.length > 0)
//                    .setIsSystemApp((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
//                    .setVersionCode(Utils.apiIsAtLeast(Build.VERSION_CODES.P) ? packageInfo.getLongVersionCode() : packageInfo.versionCode)
//                    .setVersionName(packageInfo.versionName)
//                    .setIcon(applicationInfo.icon)
//                    .setInstallTime(packageInfo.firstInstallTime)
//                    .setUpdateTime(packageInfo.lastUpdateTime)
//                    .build();
//
//        } catch (PackageManager.NameNotFoundException e) {
//            return null;
//        }
//    }

    protected PackageMeta(Parcel in) {
        packageName = in.readString();
        label = in.readString();
        hasSplits = in.readByte() != 0;
        isSystemApp = in.readByte() != 0;
        versionCode = in.readLong();
        versionName = in.readString();
        iconUri = in.readParcelable(Uri.class.getClassLoader());
        installTime = in.readLong();
        updateTime = in.readLong();
        size = in.readString();
        sourceDir = in.readString();
        firstInstallTime = in.readLong();
        lastUpdateTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeString(label);
        dest.writeByte((byte) (hasSplits ? 1 : 0));
        dest.writeByte((byte) (isSystemApp ? 1 : 0));
        dest.writeLong(versionCode);
        dest.writeString(versionName);
        dest.writeParcelable(iconUri, flags);
        dest.writeLong(installTime);
        dest.writeLong(updateTime);
        dest.writeString(size);
        dest.writeString(sourceDir);
        dest.writeLong(firstInstallTime);
        dest.writeLong(lastUpdateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PackageMeta> CREATOR = new Creator<PackageMeta>() {
        @Override
        public PackageMeta createFromParcel(Parcel in) {
            return new PackageMeta(in);
        }

        @Override
        public PackageMeta[] newArray(int size) {
            return new PackageMeta[size];
        }
    };
}
