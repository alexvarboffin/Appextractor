package com.walhalla.db;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;

public class DbxRequestConfigFactory {
    private static DbxRequestConfig sDbxRequestConfig;

    public static DbxRequestConfig getRequestConfig() {
        if (sDbxRequestConfig == null) {
            sDbxRequestConfig = DbxRequestConfig.newBuilder("com_walhalla_appextractor")
                    .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build();
        }
        return sDbxRequestConfig;
    }
}
