package com.walhalla.appextractor.activity.main;

import androidx.annotation.StringRes;

import com.walhalla.appextractor.model.LogViewModel;
import com.walhalla.appextractor.model.PackageMeta;

import java.io.File;

public interface MainView {
    void debugHideProgress(int size);

    void printOutput(LogViewModel message);

    //void makeProgressBar(int size);

    void debugShowProgress(int i, int v0);

    void makeSnackBar(File file);

    void failureExtracted(@StringRes int id);

    void successToast(String s);

    void errorToast(String s);

    void saveIconRequest(PackageMeta packageInfo);
}
