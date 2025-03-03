package com.walhalla.appextractor.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.UserHandle;

import java.util.HashMap;
import java.util.List;

@TargetApi(21)
public class UserManagerCompatVL extends UserManagerCompatV17 {
    private static final String USER_CREATION_TIME_KEY = "user_creation_time_";
    private final Context mContext;
    private final PackageManager mPm;

    UserManagerCompatVL(Context context) {
        super(context);
        this.mPm = context.getPackageManager();
        this.mContext = context;
    }

    @Override
    public void enableAndResetCache() {
        synchronized (this) {
//            this.mUsers = new LongArrayMap<>();
//            this.mUserToSerialMap = new HashMap<>();
//            List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
//            if (userProfiles != null) {
//                for (UserHandle userHandle : userProfiles) {
//                    long serialNumberForUser = this.mUserManager.getSerialNumberForUser(userHandle);
//                    UserHandleCompat fromUser = UserHandleCompat.fromUser(userHandle);
//                    this.mUsers.put(serialNumberForUser, fromUser);
//                    this.mUserToSerialMap.put(fromUser, Long.valueOf(serialNumberForUser));
//                }
//            }
        }
    }

    @Override
    public CharSequence getBadgedLabelForUser(CharSequence charSequence, UserHandleCompat userHandleCompat) {
        return userHandleCompat == null ? charSequence : this.mPm.getUserBadgedLabel(charSequence, userHandleCompat.getUser());
    }

    @Override
    public long getUserCreationTime(UserHandleCompat userHandleCompat) {
        return 0;
    }

}
