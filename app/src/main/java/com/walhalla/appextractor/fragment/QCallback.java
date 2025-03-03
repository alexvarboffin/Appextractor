package com.walhalla.appextractor.fragment;


import com.walhalla.appextractor.BaseUtilsCallback;

public interface QCallback extends BaseUtilsCallback {

    void showProgress();

    void hideProgress();

    //void successResult(Result<TwitterSession> result);

    void handleException(Exception exception);

    void success(int text);


}
