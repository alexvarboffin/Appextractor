package com.walhalla.appextractor.compat;

import android.annotation.TargetApi;
import android.content.Context;

@TargetApi(24)
public class UserManagerCompatVN extends UserManagerCompatVM {
    UserManagerCompatVN(Context context) {
        super(context);
    }

    @Override
    public boolean isQuietModeEnabled(UserHandleCompat userHandleCompat) {
        return this.mUserManager.isQuietModeEnabled(userHandleCompat.getUser());
    }

    @Override
    public boolean isUserUnlocked(UserHandleCompat userHandleCompat) {
        try {
            return this.mUserManager.isUserUnlocked(userHandleCompat.getUser());
        } catch (SecurityException unused) {
            return false;
        }
    }
}
