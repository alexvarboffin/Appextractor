//package com.walhalla.appextractor.activity.debug;
//
//import android.content.ContentProviderClient;
//import android.content.Context;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.ProviderInfo;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.RemoteException;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//;
//import com.walhalla.appextractor.utils.LauncherUtils;
//import com.walhalla.ui.DLog;
//
//import java.util.Arrays;
//
//public class AllChannelFragment extends DemoFragment {
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return makeUI(getContext());
//    }
//
//    private View makeUI(Context context) {
//
//        if (infos == null) {
//            PackageManager pm = context.getPackageManager();
//            fullMeta(pm);
//        }
//
//        Button b = new Button(context);
//        b.setText("xxx" + infos.size());
//        b.setOnClickListener(v -> {
//            String[] proj = new String[]{BookmarkColumns.TITLE, BookmarkColumns.URL};
//            Uri uriCustom = Uri.parse("content://com.android.chrome.browser/bookmarks");
//            String sel = BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1 = bookmark
//            Cursor mCur = getActivity().getContentResolver().query(uriCustom, proj, sel, null, null);
//            mCur.moveToFirst();
//            @SuppressWarnings("unused")
//            String title = "";
//            @SuppressWarnings("unused")
//            String url = "";
//
//            if (mCur.moveToFirst() && mCur.getCount() > 0) {
//                boolean cont = true;
//                while (mCur.isAfterLast() == false && cont) {
//                    title = mCur.getString(mCur.getColumnIndex(BookmarkColumns.TITLE));
//                    url = mCur.getString(mCur.getColumnIndex(BookmarkColumns.URL));
//                    DLog.d("title" + title);
//                    DLog.d("url" + url);
//                    // Do something with title and url
//                    mCur.moveToNext();
//                }
//            }
//            try0("content://com.android.chrome.ChromeBrowserProvider/",
//                    new String[]{"bookmarks", "history", "searches", "*", "", "test"});
//
//            try0("content://com.android.chrome.browser/",
//                    new String[]{"bookmarks", "history", "searches", "*", "", "test"});
//
////            for (PackageInfo info : infos) {
////                ProviderInfo[] m = info.providers;
////                if (null != m) {
////                    DLog.d("===============");
////                    for (ProviderInfo providerInfo : m) {
////                        // &&providerInfo.enabled
////                        if (providerInfo.exported) {
////                            DLog.d("@@@@@@@@@@@@" + providerInfo.authority + " [" + providerInfo.exported + "] [" + providerInfo.enabled + "]");
////                            if (!TextUtils.isEmpty(providerInfo.authority)) {
//////                                //Я хочу обратиться к провайдеру чтобы узнать его структуру
//////                                //providerInfo.authority
//////                                ProviderLine pro = new ProviderLine(
//////                                        null, null, providerInfo.authority
//////                                );
//////                                LauncherUtils.onLaunchProvider(getActivity(), pro, new LauncherUtils.LauCallback() {
//////                                    @Override
//////                                    public void showError(String s) {
//////                                        DLog.d("WWW " + providerInfo.authority + " " + s);
//////                                    }
//////                                });
////
////
////                                // Получение URI провайдера
////                                String authority = providerInfo.authority;
////                                Uri uri = Uri.parse("content://" + authority);
////
////                                try {
////                                    //Cursor cursor = null;
////                                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
////
////
//////                                    ContentProviderClient client = getActivity().getContentResolver().acquireContentProviderClient(uri);
//////                                    if (client != null) {
//////                                        try {
//////                                            cursor = client.query(uri, null, null, null, null);
////////                                            if (cursor != null) {
////////                                                // Работа с курсором
////////                                                cursor.close();
////////                                            }
//////                                        } catch (RemoteException e) {
//////                                            e.printStackTrace();
//////                                        } finally {
//////                                            client.release();
//////                                        }
//////                                    }
////
////
////                                    if (cursor != null) {
////                                        String[] columnNames = cursor.getColumnNames();
////                                        Log.d("@@@", "Columns for " + authority + ": " + Arrays.toString(columnNames));
////
////                                        // Пример получения данных из первой строки
////                                        if (cursor.moveToFirst()) {
////                                            do {
////                                                for (String columnName : columnNames) {
////                                                    try {
////                                                        int columnIndex = cursor.getColumnIndex(columnName);
////                                                        if (columnIndex != -1) {
////                                                            String columnValue = cursor.getString(columnIndex);
////                                                            Log.d("@@@", columnName + ": " + columnValue);
////                                                        }
////                                                    }catch (Exception e){
////                                                        DLog.d("========================>>>>"+e);
////                                                    }
////                                                }
////                                            } while (cursor.moveToNext());
////                                        }
////
////                                        cursor.close();
////                                    } else {
////                                        Log.e("@@@", "Cursor is null for authority: " + authority);
////                                    }
////                                } catch (Exception e) {
////
////
////                                    if (e instanceof java.lang.SecurityException) {
////                                        if (e.getLocalizedMessage().contains("Permission Denial")) {
////                                            //Log.e("@@@", "bbb Failed to query provider: " + authority, e);
////                                        } else {
////                                            Log.e("@@@", "Failed to query provider: " + authority, e);
////                                        }
////                                    } else {
////                                        Log.e("@@@", "Failed to query provider: " + authority, e);
////                                    }
////                                }
////                            }
////                        }
////                    }
////                }
////            }
////
//////            ProviderLine pro = new ProviderLine(
//////                    null, null, "content://com.bybit.app.CCInitProvider/"
//////            );
//////            LauncherUtils.onLaunchProvider(getActivity(), pro, new LauncherUtils.LauCallback() {
//////                @Override
//////                public void showError(String s) {
//////                    Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
//////                }
//////            });
//        });
//        return b;
//    }
//
//    private void try0(String s, String[] strings) {
//        Uri uri = null;
//        for (String string : strings) {
//            uri = Uri.parse(s + "/" + string);
//            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//            if (cursor != null) {
//                int m = cursor.getCount();
//                try {
//                    String[] columnNames = cursor.getColumnNames();
//                    // Проверяем, содержит ли курсор хотя бы одну строку
//                    if (cursor.moveToFirst()) {
//
//                        do {
//                            // Проходим по всем строкам и столбцам
//                            for (String columnName : columnNames) {
//                                int columnIndex = cursor.getColumnIndex(columnName);
//                                String columnValue = cursor.getString(columnIndex);
//                                Log.d("@@@", columnName + ": " + columnValue);
//                            }
//                        } while (cursor.moveToNext()); // Перемещаемся к следующей строке
//                    } else {
//                        Log.d("@@@", uri + " - No data available. " + Arrays.toString(columnNames) + " " + m);
//                    }
//                } finally {
//                    cursor.close(); // Закрываем курсор
//                }
//            } else {
//                DLog.d("@@@" + uri + "  " + "NULL");
//            }
//        }
//    }
//}
