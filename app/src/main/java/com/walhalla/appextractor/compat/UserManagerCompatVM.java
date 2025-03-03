package com.walhalla.appextractor.compat;

import android.annotation.TargetApi;
import android.content.Context;

@TargetApi(23)
public class UserManagerCompatVM extends UserManagerCompatVL {
    UserManagerCompatVM(Context context) {
        super(context);
    }

    @Override
    public long getUserCreationTime(UserHandleCompat userHandleCompat) {
        return this.mUserManager.getUserCreationTime(userHandleCompat.getUser());
    }
}
