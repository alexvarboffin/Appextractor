package com.walhalla.appextractor.compat;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Process;
import android.os.UserHandle;

import com.walhalla.appextractor.activity.Utilities;

public class UserHandleCompat {
    private UserHandle mUser;

    private UserHandleCompat(UserHandle userHandle) {
        this.mUser = userHandle;
    }

    public static UserHandleCompat fromIntent(Intent intent) {
        UserHandle userHandle;
        if (!Utilities.ATLEAST_LOLLIPOP || (userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER")) == null) {
            return null;
        }
        return fromUser(userHandle);
    }

    public static UserHandleCompat fromUser(UserHandle userHandle) {
        if (userHandle == null) {
            return null;
        }
        return new UserHandleCompat(userHandle);
    }

    @TargetApi(17)
    public static UserHandleCompat myUserHandle() {
        if (Utilities.ATLEAST_JB_MR1) {
            return new UserHandleCompat(Process.myUserHandle());
        }
        return new UserHandleCompat();
    }

    public void addToIntent(Intent intent, String str) {
        UserHandle userHandle;
        if (Utilities.ATLEAST_LOLLIPOP && (userHandle = this.mUser) != null) {
            intent.putExtra(str, userHandle);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UserHandleCompat)) {
            return false;
        }
        if (Utilities.ATLEAST_JB_MR1) {
            return this.mUser.equals(((UserHandleCompat) obj).mUser);
        }
        return true;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public int hashCode() {
        if (Utilities.ATLEAST_JB_MR1) {
            return this.mUser.hashCode();
        }
        return 0;
    }

    public String toString() {
        return Utilities.ATLEAST_JB_MR1 ? this.mUser.toString() : "";
    }

    private UserHandleCompat() {
    }
}
