package com.walhalla.appextractor.activity.manifest;

import static com.walhalla.abcsharedlib.Share.KEY_FILE_PROVIDER;
import static com.walhalla.appextractor.activity.manifest.ManifestPresenterXml.ANDROID_MANIFEST_FILENAME;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.walhalla.abcsharedlib.Share;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.Tools;
import com.walhalla.appextractor.databinding.ManifestExplorerBinding;
import com.walhalla.appextractor.fragment.BaseFragment;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.appextractor.presenter.BaseManifestPresenter;
import com.walhalla.ui.DLog;
import com.walhalla.ui.plugins.MimeType;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ManifestFragment extends BaseFragment
        implements BaseManifestPresenter.ManifestCallback {

    private static final String ARG_PARAM1 = "key_arg_0";


    public static final String KEY_OBJ_APK = "key_obj_apk";
    private static final String KEY_OBJ_XML_FILENAME = ManifestFragment.class.getSimpleName() + "var3";
    private PackageMeta meta;

    private ManifestPresenterXml presenter;


    private ManifestExplorerBinding bind;


    private String apkPath;

    private String xmlFileName = null;


    public static BaseFragment newInstance(PackageMeta meta, String apkPath, String xmlFileName) {
        ManifestFragment fragment = new ManifestFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, meta);
        args.putString(KEY_OBJ_APK, apkPath);
        args.putString(KEY_OBJ_XML_FILENAME, xmlFileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meta = getArguments().getParcelable(ARG_PARAM1);
            apkPath = getArguments().getString(KEY_OBJ_APK);

            if (getArguments().containsKey(KEY_OBJ_XML_FILENAME)) {
                xmlFileName = getArguments().getString(KEY_OBJ_XML_FILENAME);
            }
        }
        if (xmlFileName == null) {
            xmlFileName = ANDROID_MANIFEST_FILENAME;
        }
        presenter = new ManifestPresenterXml(getActivity(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind = ManifestExplorerBinding.inflate(inflater, container, false);
        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind.webView.getSettings().setBuiltInZoomControls(true);
        bind.webView.getSettings().setDisplayZoomControls(true);
        if (!TextUtils.isEmpty(apkPath)) {
            bind.text1.setText(apkPath);
        }

        bind.xml.setOnClickListener(v -> {
            presenter.renderXML(xmlFileName);
        });
        bind.txt.setOnClickListener(v -> {
            presenter.renderSimpleText(xmlFileName);
        });

        bind.copy.setOnClickListener(v -> {
            String tmp = presenter.mOutgetText(xmlFileName);
            DLog.d("@@" + tmp);
            Tools.copyToClipboard(tmp, getActivity());
        });

        bind.share.setOnClickListener(v -> {
            String tmp = presenter.mOutgetText(xmlFileName);
            shareFile(getActivity(), tmp, ANDROID_MANIFEST_FILENAME, "Manifest Explorer");
        });

        if (meta != null && savedInstanceState == null) {
            presenter.configForPackage(meta.packageName, apkPath);
            //this.mOut.setText("");
            presenter.renderXML(xmlFileName);
        }
    }

    @Override
    public void fab() {

    }

    //    Длина extra.length() == 130124
//    но уже получаю
//    android.os.TransactionTooLargeException: data parcel size 523504 bytes
    public void shareFile(Context context, String extra, String fileName, String chooserTitle) {
        DLog.d("{share} " + extra.length());

        if (chooserTitle == null) {
            chooserTitle = context.getResources().getString(R.string.app_name);
        }
        String text = meta.packageName;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);



        //intent.putExtra(Intent.EXTRA_EMAIL, "alexvarboffin@gmail.com");//Work only with intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        //intent.setType("*/*");
        if (extra.length() < 50000) {
            intent.putExtra(Share.comPinterestEXTRA_DESCRIPTION, text);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Сохраняем текст во временный файл в temp папку
        try {
            // Создаем временный файл в папке "cache" вашего приложения
            File tempFile = new File(context.getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(extra.getBytes());
            fos.close();

            // Получаем URI файла через FileProvider
            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + KEY_FILE_PROVIDER, tempFile);

            // Передаем файл через Intent
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.setType(MimeType.TEXT_PLAIN);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Добавляем тему сообщения
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));

            // Открываем выбор приложений для отправки
            Intent chooser = Intent.createChooser(intent, chooserTitle);
            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
            if (!resInfoList.isEmpty()) {
                context.startActivity(chooser);
            } else {
                DLog.d("Not found activity...");
            }
        } catch (IOException e) {
            DLog.handleException(e);
        }
    }

    @Override
    public void showError(String text, Throwable t) {
//        DLog.e("ManifestExplorer " + text + " : " + ((t != null) ? t.getMessage() : ""));
//        //Toast.makeText(this, "Error: " + text + " : " + t, Toast.LENGTH_LONG).show();
//        String s = "Error: " + text + " : " + t + " ✘";
//        Toast toast = Toasty.error(getActivity(), s.toUpperCase(), Toast.LENGTH_SHORT);
//        toast.show();
        if (t instanceof FileNotFoundException) {
            bind.webView.loadDataWithBaseURL(null,
                    nameFormat(apkPath) + "\n" + " does not contain this file\n"
                            + " " + xmlFileName, MimeType.TEXT_PLAIN, "UTF-8", null);
        } else {
            bind.webView.loadDataWithBaseURL(null, ""
                    + "Error: " + text + " : " + t, MimeType.TEXT_PLAIN, "UTF-8", null);
        }
    }

    private String nameFormat(String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return apkPath;
        }
        String[] tmp = apkPath.split("/");
        return tmp[tmp.length - 1];
    }

    @Override
    public void showManifestContent(String s) {
        bind.webView.loadDataWithBaseURL(null, "" + s, MimeType.TEXT_PLAIN, "UTF-8", null);
    }

    @Override
    public void loadDataWithPatternHTML(String encoded) {
        bind.webView.loadDataWithBaseURL(null, encoded, "text/html", "UTF-8", null);
    }
}
