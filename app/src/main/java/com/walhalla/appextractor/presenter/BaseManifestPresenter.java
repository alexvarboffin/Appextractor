package com.walhalla.appextractor.presenter;

import android.content.Context;
import android.content.res.Resources;

import com.walhalla.appextractor.activity.manifest.ManifestActivity;
import com.walhalla.ui.DLog;

import java.io.IOException;

public abstract class BaseManifestPresenter implements ManifestPresenter {

    protected final Context context;
    protected final ManifestCallback mView;

    public interface ManifestCallback {

        void showError(String readingXml, Throwable throwable);

        void showManifestContent(String toString);

        void loadDataWithPatternHTML(String encoded);
    }




    public BaseManifestPresenter(Context m, ManifestCallback mView) {
        this.context = m;
        this.mView = mView;
    }

    /**
     * returns the value, resolving it through the provided resources if it
     * appears to be a resource ID. Otherwise just returns what was provided.
     *
     * @param in String to resolve
     * @param r  Context appropriate resource (system for system, package's for
     *           package)
     * @return Resolved value, either the input, or some other string.
     */
    public static String resolveValue(String name, String in, Resources r) {
        if (in == null || !in.startsWith("@") || r == null)
            return in;
        int num = 0;
        try {
            num = Integer.parseInt(in.substring(1));
            if ("theme".equals(name)) {
                String tmp = r.getResourceName(num);
                if (tmp.startsWith("android:style")) {
                    return tmp;
                } else {
                    return tmp.split("/")[1];
                }
            }
            return r.getString(num);
        } catch (NumberFormatException e) {
            return in;
        } catch (RuntimeException e) {
            // formerly noted errors here, but simply not resolving works better
            DLog.d(e.getLocalizedMessage() + " " + name + " " + r.getResourceEntryName(num));
            return in;
        }
    }


}
