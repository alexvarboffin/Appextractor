package com.walhalla.appextractor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.multidex.BuildConfig;

import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.ui.DLog;
import com.walhalla.ui.plugins.MimeType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class Util {

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getDate(PackageMeta meta) {

        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//            Date netDate = (new Date(meta));
//            return sdf.format(netDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String fut = sdf.format(new Date(meta.firstInstallTime));
            String lut = sdf.format(new Date(meta.lastUpdateTime));

            return "First/Last update time:\n" + fut + "\t" + lut;
        } catch (Exception ex) {
            return "xx";
        }
    }
    /*
    StringBuilder sb = new StringBuilder();
        sb.append(p.sharedUserId).append((char)10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            sb.append(p.baseRevisionCode).append((char)10);
        }
        sb.append(p.firstInstallTime).append((char)10);
        sb.append(p.lastUpdateTime).append((char)10);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(p.installLocation).append((char)10);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sb.append(p.isApex).append((char)10);
        }

        sb.append(p.sharedUserLabel).append((char)10);
        sb.append(Arrays.toString(p.activities)).append((char)10);
        sb.append(Arrays.toString(p.gids)).append((char)10);
        sb.append(Arrays.toString(p.permissions)).append((char)10);
        sb.append(Arrays.toString(p.providers)).append((char)10);
        sb.append(Arrays.toString(p.receivers)).append((char)10);

        return sb.toString();*/

    public static long getFolderSize(File folderOrFile) {
        long length = 0;
        if (folderOrFile.isFile()) {
            length = folderOrFile.length();
        } else {
            // listFiles() is used to list the
            // contents of the given folder
            File[] files = folderOrFile.listFiles();

            if (files != null) {
                int count = files.length;
                // loop for traversing the directory
                for (int i = 0; i < count; i++) {
                    if (files[i].isFile()) {
                        length += files[i].length();
                    } else {
                        length += getFolderSize(files[i]);
                    }
                }
            }
        }
        return length;
    }

    public static String getFileSizeMegaBytes(File file) {
        return String.format(Locale.CANADA, "%.2f MB", (double) getFolderSize(file) / (1024 * 1024));
    }

    public static ProgressDialog loadDialog(Context context, int icon) {
        Drawable draw = ResourcesCompat.getDrawable(context.getResources(), R.drawable.custom_progressbar, null);
        ProgressDialog pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(false); //If we need onProgress bar update
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setProgressDrawable(draw);
        pd.setIcon(icon);
        pd.setTitle(R.string.alert_dialog_title);
        pd.setMessage(context.getString(R.string.alert_dialog_text));
        pd.setMax(100);
        return pd;
    }

    ///data/app/SmokeTestApp/SmokeTestApp.apk
    ///storage/emulated/0/Download

    public static void openFolder(Context context, String var0) {

//        if (!var0.isDirectory()) {
//            Toast.makeText(context, R.string.access_error, Toast.LENGTH_SHORT).show();
//            return;
//        }


        if (var0 != null) {
            //Warning! Do this if it's directory
            //No need FileProvider


            Uri uri = Uri.parse(var0);//It's ok
            //Uri uri = Uri.fromFile(new File(var0)); //Exception

//                    Intent intent = null;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                        intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR);
//                    }

            PackageManager pm = context.getPackageManager();
            // if you reach this place, it means there is no any file
            // explorer app installed on your device

//            intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "*/*");
//            context.startActivity(intent);

            if (check_Android30FileBrowser_AndroidLysesoftFileBrowser(uri, context, pm, true)) {
                return;
            }


            //api 24 not open folder? not have filebrowser((
        }
    }

    private static void checkResolver(Intent intent, PackageManager pm) {
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : resolvedActivityList) {
            DLog.d("------------[][][" + info.toString());

            Intent serviceIntent = intent;//new Intent();
            //serviceIntent.setAction(Intent.ACTION_VIEW);
            //serviceIntent.setPackage(info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                //packagesSupportingCustomTabs.add(info);
                DLog.d("-----0------" + info.toString());
            } else if (pm.resolveActivity(serviceIntent, 0) != null) {
                DLog.d("-----1------" + info.toString());
            }

        }
    }


    private static void resolwe(Intent intent, PackageManager pm) {
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : resolvedActivityList) {
            DLog.d("------------[][][" + info.toString());

            Intent serviceIntent = new Intent();
            serviceIntent.setAction(Intent.ACTION_VIEW);
            //serviceIntent.setPackage(info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                //packagesSupportingCustomTabs.add(info);
                DLog.d("-----0------" + info.toString());
            } else if (pm.resolveActivity(serviceIntent, 0) != null) {
                DLog.d("-----1------" + info.toString());
            }

        }
    }


    public static boolean check_Android30FileBrowser_AndroidLysesoftFileBrowser(
            Uri uri, Context context, PackageManager pm, boolean launch) {

        String[] m = new String[]{
                "vnd.android.document/directory", //=WORK in Android 30=
                "vnd.android.cursor.dir/lysesoft.andexplorer.director",
                //"vnd.android.cursor.dir/*"
        };

        for (String type : m) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);
            ActivityInfo mm = intent.resolveActivityInfo(pm, 0);
            if (mm != null) {
                DLog.d("[@@]" + uri + " " + mm + " " + type);
                if (launch) {
                    try {
                        context.startActivity(intent);
                    } catch (Exception e) {
                        DLog.handleException(e);
                    }
                }
                return true;
            }
        }
        return false;
    }


    private static void shareScreenshotToTwitter(Context context, String message, File file) {
        try {
            if (file.exists() && !file.isDirectory()) {
            }
            String packageName = context.getPackageName();
            Uri contentUri = FileProvider.getUriForFile(context, packageName + ".fileprovider", file);
            if (contentUri != null) {
                Intent www = new Intent(Intent.ACTION_SEND);
                www.setType(MimeType.TEXT_PLAIN);
                if (message == null || message.trim().isEmpty()) {
                    message = //"Hey my friend check out this app\n https://play.google.com/store/apps/details?id="
                            "";
                }
                www.putExtra(Intent.EXTRA_TEXT, message + " \n");
                //        www.putExtra(Intent.EXTRA_TEXT, new Intent(Intent.ACTION_VIEW,
                //                Uri.parse("https://play.google.com/store/apps/details?id="
                //                        + context.getPackageName()))
                //        );
//            www.putExtra(Intent.EXTRA_TEXT, new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id="
//                            + context.getPackageName()))
//            );
                // temp permission for receiving app to read this file
                www.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

//        switch (memoryType) {
//            case INTERNAL:
//
//                break;
//
//            case EXTERNAL:
//                break;
//
//            default:
//                break;
//        }
//        File imagePath = SharedObjects.imageCacheDir(context);
//        File file = new File(imagePath, imageName);
                //From card
                //File file = new File(ssssdddddd, imageName);
                //contentUri = Uri.fromFile(file);


                www.putExtra(Intent.EXTRA_STREAM, contentUri);
                www.setType("*/*");

//            Intent i = new Intent();
//            i.putExtra(Intent.EXTRA_TEXT, message);
//            i.setAction(Intent.ACTION_VIEW);
//            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(message)));
//            context.startActivity(i);
                //Toasty.info(context, "Twitter app isn't found", Toast.LENGTH_SHORT, true).show();
                try {

//            if (Build.VERSION.SDK_INT >= 23) {
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri contentUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() +  ".FileProvider", new File(filePath + fileName));
//
//            } else{
//                Uri contentUri =Uri.fromFile(new File(filePath + fileName))
//            }
                    //OR
                    //Intent shareIntent = new Intent();
                    //shareIntent.setAction(Intent.ACTION_SEND);
                    //OR

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    //--> intent.setType(MimeType.TEXT_PLAIN);

                    if (message.trim().isEmpty()) {
                        message = "";
                    }

                    intent.putExtra(Intent.EXTRA_TEXT, message + " \n");
                    //        intent.putExtra(Intent.EXTRA_TEXT, new Intent(Intent.ACTION_VIEW,
                    //                Uri.parse("https://play.google.com/store/apps/details?id="
                    //                        + context.getPackageName()))
                    //        );


                    String type = context.getContentResolver().getType(contentUri);
                    DLog.d("::TYPE:: " + type);

                    intent.putExtra(Intent.EXTRA_EMAIL, "22@hhhh");
                    intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));

                    intent.setType("*/*");
                    //intent.setData(contentUri); //False To:field in gmail

                    // temp permission for receiving app to read this file
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    //intent.setDataAndType(contentUri, "image/jpeg"); //Not application/octet-stream type
                    intent.putExtra(Intent.EXTRA_STREAM, contentUri);

                    //OR
                    //intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    //intent.setType("image/jpeg");

//                if (DEBUG) {
//                    DLog.d( "shareFile: " + intent.toString());
//                }
                    //if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                for (ResolveInfo resolveInfo : resInfoList) {
//                    String packageName = resolveInfo.activityInfo.packageName;
//                    context.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    //}
//                }
                    context.startActivity(Intent.createChooser(intent, "Choose an app"));
                } catch (StringIndexOutOfBoundsException e) {
                    DLog.handleException(e);
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            if (BuildConfig.DEBUG) {
                DLog.d("@@@: " + file.getAbsolutePath());
            }
        } catch (IllegalArgumentException a) {
            if (BuildConfig.DEBUG) {
                DLog.d("@@@: " + a.getLocalizedMessage());
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public static String get_out_filename(PackageMeta meta) {

//        long versionCode;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            versionCode = meta.getLongVersionCode();
//        } else {
//            versionCode = meta.versionCode;
//        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            return "/Download/"
                    + Config.__CLOUD_BACKUP_LOCATION_LOCAL + meta.packageName + "_v" + meta.versionCode + ".apk";
        } else {
            return "/"
                    + Config.__CLOUD_BACKUP_LOCATION_LOCAL + meta.packageName + "_v" + meta.versionCode + ".apk";
        }
    }

    public static File buildDstPath(File path) throws IOException {
        if ((!path.getParentFile().exists() && !path.getParentFile().mkdirs()) || !path.getParentFile().isDirectory()) {
            throw new IOException("Cannot create directory: " + path.getParentFile().getAbsolutePath());
        }
        if (!path.exists()) return path;

        File dst = path;
        String fname = path.getName();
        int index = fname.lastIndexOf(".");
        String ext = fname.substring(index);
        String name = fname.substring(0, index);

        for (int i = 0; dst.exists(); i++) {
            dst = new File(path.getParentFile(), name + "-" + String.valueOf(i) + ext);
        }

        return dst;
    }


    public static String extractWithRoot(PackageMeta meta) throws Exception {
        File src = new File(meta.sourceDir);
        String path = System.getenv("EXTERNAL_STORAGE") + get_out_filename(meta);

        File dst = buildDstPath(new File(path));
        if (dst.exists()) {
            boolean res = dst.delete();
        }

        Process p = null;
        StringBuilder err = new StringBuilder();
        try {
            p = Runtime.getRuntime().exec("su -c cat " + src.getAbsolutePath() + " > " + dst.getAbsolutePath());
            p.waitFor();

            if (p.exitValue() != 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    err.append(line);
                    err.append("\n");
                }

                throw new Exception(err.toString());
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        } catch (InterruptedException e) {
            throw new Exception(e.getMessage());
        } finally {
            if (p != null) {
                try {
                    p.destroy();
                } catch (Exception e) {
                    DLog.handleException(e);
                }
            }
        }
        if (!dst.exists()) {
            throw new Exception("cannot exctract file [root]");
        }
        return dst.getAbsolutePath();
    }

    public static Uri makeURI(Context context, @NonNull File file) throws java.lang.IllegalArgumentException {

        if (file.isDirectory()) {
            return Uri.fromFile(file);//Not use FileProvider is Directory
        }

        if (Build.VERSION.SDK_INT >= 23) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);

            //java.lang.IllegalArgumentException: Failed to find configured root that contains
//            try {
//                return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
//            } catch (java.lang.IllegalArgumentException e) {
//                DLog.handleException(e);
//                return Uri.fromFile(file);
//            }
        } else {
            return Uri.fromFile(file);
        }
    }


    public static void installApp(Context context, File file) {
        DLog.d(file.getAbsolutePath());

        PackageManager pm = context.getPackageManager();
        try {
            File toInstall = new File(file.getPath());
            Intent v0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                //old -->
                //Uri apkUri = Uri.fromFile(mModel.file);
                Uri apkUri = Util.makeURI(context, toInstall);
                v0 = new Intent(Intent.ACTION_VIEW);
                v0.setDataAndType(apkUri, "application/vnd.android.package-archive");
                v0.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                v0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                v0.setData(apkUri);
            } else {
                Uri apkUri = Uri.fromFile(toInstall);
                v0 = new Intent(Intent.ACTION_VIEW);
                v0.setDataAndType(apkUri, "application/vnd.android.package-archive");
                v0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(v0, 0);
            for (ResolveInfo info : resolvedActivityList) {
                DLog.d("------------[][][" + info.toString());

                Intent serviceIntent = v0;//new Intent();
                //serviceIntent.setAction(Intent.ACTION_VIEW);
                //serviceIntent.setPackage(info.activityInfo.packageName);
                // Check if this package also resolves the Custom Tabs service.
                if (pm.resolveService(serviceIntent, 0) != null) {
                    //packagesSupportingCustomTabs.add(info);
                    DLog.d("[INSTALLER 0]" + info.toString());
                } else if (pm.resolveActivity(serviceIntent, 0) != null) {
                    DLog.d("[INSTALLER 1]" + info.toString());
                }

            }
            context.startActivity(v0);
        } catch (Exception e) {
            DLog.handleException(e);
            Toasty.error(context, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public static void handleInstallerViewer(Context context, ResolveInfo info, String action, Uri uri) {
        Intent intent = new Intent(action);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setPackage(packageName(info));
        context.startActivity(intent);
    }

    /**
     * It's error
     * <p>
     * intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
     * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     * <p>
     * Success
     * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
     * | Intent.FLAG_GRANT_READ_URI_PERMISSION);
     */

    public static String packageName(ResolveInfo info) {
        String packageName = null;
        if (info.activityInfo != null) {
            packageName = info.activityInfo.packageName;
        }
        if (info.serviceInfo != null) {
            packageName = info.serviceInfo.packageName;
        }
        return packageName;
    }
}
