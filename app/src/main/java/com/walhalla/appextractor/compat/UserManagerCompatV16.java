package com.walhalla.appextractor.compat;

import java.util.ArrayList;
import java.util.List;

public class UserManagerCompatV16 extends UserManagerCompat {
    UserManagerCompatV16() {
    }

    @Override
    public void enableAndResetCache() {
    }

    @Override
    public CharSequence getBadgedLabelForUser(CharSequence charSequence, UserHandleCompat userHandleCompat) {
        return charSequence;
    }

    @Override
    public long getSerialNumberForUser(UserHandleCompat userHandleCompat) {
        return 0;
    }

    @Override
    public long getUserCreationTime(UserHandleCompat userHandleCompat) {
        return 0;
    }

    @Override
    public UserHandleCompat getUserForSerialNumber(long j2) {
        return UserHandleCompat.myUserHandle();
    }

    @Override
    public List<UserHandleCompat> getUserProfiles() {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(UserHandleCompat.myUserHandle());
        return arrayList;
    }

    @Override
    public boolean isDemoUser() {
        return false;
    }

    @Override
    public boolean isQuietModeEnabled(UserHandleCompat userHandleCompat) {
        return false;
    }

    @Override
    public boolean isUserUnlocked(UserHandleCompat userHandleCompat) {
        return true;
    }
}
