package com.walhalla.core.settings;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.preference.DialogPreference;
import android.util.AttributeSet;

import com.walhalla.appextractor.R;

public class FolderChooser extends DialogPreference {

    private int mColor;

    public FolderChooser(Context context) {
        this(context, null);
    }

    @Override
    public void setDialogTitle(CharSequence dialogTitle) {
        super.setDialogTitle(null);
    }

    public FolderChooser(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public FolderChooser(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);

    }

    public FolderChooser(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
        persistInt(color);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    public void setPersistent(boolean persistent) {
        super.setPersistent(true);
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.dialog_director_chooser;
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setColor(restorePersistedValue ? getPersistedInt(mColor) : (int) defaultValue);
    }
}
