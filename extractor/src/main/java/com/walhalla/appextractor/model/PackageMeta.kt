package com.walhalla.appextractor.model

import android.content.ContentResolver
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable


class PackageMeta : Parcelable {

    var packageName: String

    var label: String?
    var hasSplits: Boolean = false
    var hasPinning: Boolean = false
    var isSystemApp: Boolean = false
    var versionCode: Long = 0
    var versionName: String? = null

    var iconUri: Uri? = null
    var installTime: Long = 0
    var updateTime: Long = 0


    var size: String? = null

    var sourceDir: String? = null


    var firstInstallTime: Long = 0

    var lastUpdateTime: Long = 0

    var category: Int = 0

    //Extended

    var isGranted: Boolean = false

    constructor(packageName: String, label: String?) {
        this.packageName = packageName
        this.label = label
    }


    class Builder(packageName: String) {
        private val mPackageMeta = PackageMeta(packageName, "?")

        fun setLabel(label: String?): Builder {
            mPackageMeta.label = label
            return this
        }

        fun setHasSplits(hasSplits: Boolean): Builder {
            mPackageMeta.hasSplits = hasSplits
            return this
        }

        fun setIsSystemApp(isSystemApp: Boolean): Builder {
            mPackageMeta.isSystemApp = isSystemApp
            return this
        }

        fun setVersionCode(versionCode: Long): Builder {
            mPackageMeta.versionCode = versionCode
            return this
        }

        fun setVersionName(versionName: String?): Builder {
            mPackageMeta.versionName = versionName
            return this
        }

        fun setIcon(iconResId: Int): Builder {
            if (iconResId == 0) {
                mPackageMeta.iconUri = null
                return this
            }

            mPackageMeta.iconUri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(mPackageMeta.packageName)
                .path(iconResId.toString())
                .build()

            return this
        }

        fun setIconUri(iconUri: Uri?): Builder {
            mPackageMeta.iconUri = iconUri
            return this
        }


        fun setInstallTime(installTime: Long): Builder {
            mPackageMeta.installTime = installTime
            return this
        }

        fun setUpdateTime(updateTime: Long): Builder {
            mPackageMeta.updateTime = updateTime
            return this
        }

        fun build(): PackageMeta {
            return mPackageMeta
        }

        fun setHasPining(pining: Boolean): Builder {
            mPackageMeta.hasPinning = pining
            return this
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

    protected constructor(`in`: Parcel) {
        packageName = `in`.readString() ?: ""
        label = `in`.readString()
        hasSplits = `in`.readByte().toInt() != 0
        isSystemApp = `in`.readByte().toInt() != 0
        versionCode = `in`.readLong()
        versionName = `in`.readString()
        iconUri = `in`.readParcelable(Uri::class.java.classLoader)
        installTime = `in`.readLong()
        updateTime = `in`.readLong()
        size = `in`.readString()
        sourceDir = `in`.readString()
        firstInstallTime = `in`.readLong()
        lastUpdateTime = `in`.readLong()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(packageName)
        dest.writeString(label)
        dest.writeByte((if (hasSplits) 1 else 0).toByte())
        dest.writeByte((if (isSystemApp) 1 else 0).toByte())
        dest.writeLong(versionCode)
        dest.writeString(versionName)
        dest.writeParcelable(iconUri, flags)
        dest.writeLong(installTime)
        dest.writeLong(updateTime)
        dest.writeString(size)
        dest.writeString(sourceDir)
        dest.writeLong(firstInstallTime)
        dest.writeLong(lastUpdateTime)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<PackageMeta> {
        override fun createFromParcel(parcel: Parcel): PackageMeta {
            return PackageMeta(parcel)
        }

        override fun newArray(size: Int): Array<PackageMeta?> {
            return arrayOfNulls(size)
        }
    }


}