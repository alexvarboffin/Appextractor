package com.walhalla.appextractor.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.UserManager;

import java.util.HashMap;

@TargetApi(17)
public class UserManagerCompatV17 extends UserManagerCompatV16 {
    protected UserManager mUserManager;
    protected HashMap<UserHandleCompat, Long> mUserToSerialMap;
    //protected LongArrayMap<UserHandleCompat> mUsers;

    UserManagerCompatV17(Context context) {
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    @Override
    public void enableAndResetCache() {
        synchronized (this) {
//            this.mUsers = new LongArrayMap<>();
//            this.mUserToSerialMap = new HashMap<>();
//            UserHandleCompat myUserHandle = UserHandleCompat.myUserHandle();
//            long serialNumberForUser = this.mUserManager.getSerialNumberForUser(myUserHandle.getUser());
//            this.mUsers.put(serialNumberForUser, myUserHandle);
//            this.mUserToSerialMap.put(myUserHandle, Long.valueOf(serialNumberForUser));
        }
    }

    @Override
    public long getSerialNumberForUser(UserHandleCompat userHandleCompat) {
        long j2;
        synchronized (this) {
            if (this.mUserToSerialMap == null) {
                return this.mUserManager.getSerialNumberForUser(userHandleCompat.getUser());
            }
            Long l = this.mUserToSerialMap.get(userHandleCompat);
            if (l == null) {
                j2 = 0;
            } else {
                j2 = l.longValue();
            }
            return j2;
        }
    }

    @Override
    public UserHandleCompat getUserForSerialNumber(long j2) {
        return null;
    }
}
