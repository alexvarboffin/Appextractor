package com.walhalla.appextractor.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.walhalla.abcsharedlib.Share;
import com.walhalla.appextractor.DownloadProgressExample;
import com.walhalla.appextractor.Troubleshooting;
import com.walhalla.appextractor.Util;
import com.walhalla.appextractor.core.RBCWrapperDelegate;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.ui.DLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ShareUtils {

    public static void shareApkFile(Context context, PackageMeta meta, String filePath) {

        try {
            String packageName0 = context.getPackageName();
            File file0 = extractWithoutRoot(new File(filePath));
            Uri path = FileProvider.getUriForFile(context, packageName0 + Share.KEY_FILE_PROVIDER, file0);

            if (path != null) {
                String description = meta.packageName;
                shareFileApk(context, packageName0, description, path);
            }
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }

    private static void shareFileApk(Context context, String packageName0, String description, Uri fileUri) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/octet-stream");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, description);
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

        // Предоставление временного доступа к файлу для других приложений
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Запуск выбора приложения для отправки (включая Gmail)
        Intent choozer = Intent.createChooser(emailIntent, "Send email using:");
        choozer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(choozer);
    }

    private static File extractWithoutRoot(File file) throws Exception {
        //DLog.d("@@@" + meta.packageName+" "+meta.sourceDir);

        File out = new File(Troubleshooting.defLocation(), "base.apk");
        out = Util.buildDstPath(out);
        if (out.exists()) {
            boolean res = out.delete();
        }
        try {
//            File tmp = new File(out.getAbsolutePath());
//            if (BuildConfig.DEBUG) {
//                String s = Util.getFileSizeMegaBytes(file) + "\t" + Util.getFileSizeMegaBytes(out);
//                DLog.d("ExtractWithoutRoot: --> " + s);
//                DLog.d("--> " + file.getAbsolutePath() + "-->" + file.length());
//                DLog.d("--> " + tmp.getAbsolutePath() + "-->" + tmp.length());
//                for (File f : new File(Troubleshooting.defLocation(), __CLOUD_BACKUP_LOCATION_LOCAL).listFiles()) {
//                    DLog.d("F->" + f.getName() + "\t" + f.length());
//                }
//            }

            if (out.exists() && out.isFile() && out.length() == file.length()) {
                DLog.d("skip file...");
            } else {
                copyFile(file, out);
            }
        } catch (Exception ex) {
            DLog.d("@@@@@@@@@@@@@@" + ex.getMessage() + " " + ex.getClass().getSimpleName());
            throw new Exception(ex.getMessage());
        }
        if (!out.exists()) {
            DLog.d("cannot extract file [no root]");
            throw new Exception("cannot extract file [no root]");
        }
        return out;
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            boolean tmp = destFile.createNewFile();
            DLog.d("CRATE_FILE: -> " + tmp);
        }

        DLog.d("@" + sourceFile.getAbsolutePath() + "|" + destFile.getAbsolutePath());

        FileChannel source;
        FileChannel destination;

        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        String fileSize = Util.getFileSizeMegaBytes(sourceFile);


//        long size = source.transferTo(0, source.size(), destination);
//        DLog.d("@ size->" + size);

//        long size = destination.transferFrom(source, 0, Long.MAX_VALUE);
//        DLog.d("@ size->" + size);


        try {
            DownloadProgressExample.RBCWrapper rbc =
                    new DownloadProgressExample.RBCWrapper(source, source.size(),
                            new RBCWrapperDelegate() {
                                @Override
                                public void rbcProgressCallback(int position, int totalFileSize, DownloadProgressExample.RBCWrapper rbc, double progress) {
                                    DLog.d("position->" + position);
                                    //DLog.d("@@@" + index + "/" + totalFileSize + "\t" + sourceFile.getAbsolutePath() + "\t" + fileSize + "\t" + progress);
//                                    mMainThread.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            mView.rbcProgressCallback(index, totalFileSize, sourceFile, fileSize, progress);
//                                        }
//                                    });
                                }
                            });
            //CRASH __> long size = destination.transferFrom(rbc, 0, Long.MAX_VALUE);
            long size = destination.transferFrom(rbc, 0, source.size());
            DLog.d("@ size->" + size);
        } catch (IOException a) {
            DLog.d("@@@@@@@" + a.getMessage());
        } catch (IllegalArgumentException e) {//android 5.1
            DLog.d("xxxxxxxx" + e.getMessage());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        DLog.d("S:" + sourceFile.length() + " " + ", D: " + destFile.length());
    }
}
