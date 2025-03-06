package com.walhalla.appextractor

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.BuildConfig
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.ui.plugins.MimeType
import es.dmoral.toasty.Toasty
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Util {
    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    @JvmStatic
    fun getDate(meta: PackageMeta): String {
        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//            Date netDate = (new Date(meta));
//            return sdf.format(netDate);
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            val fut = sdf.format(Date(meta.firstInstallTime))
            val lut = sdf.format(Date(meta.lastUpdateTime))

            return "First/Last update time:\n$fut\t$lut"
        } catch (ex: Exception) {
            return "xx"
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
    fun getFolderSize(folderOrFile: File): Long {
        var length: Long = 0
        if (folderOrFile.isFile) {
            length = folderOrFile.length()
        } else {
            // listFiles() is used to list the
            // contents of the given folder
            val files = folderOrFile.listFiles()

            if (files != null) {
                val count = files.size
                // loop for traversing the directory
                for (i in 0 until count) {
                    length += if (files[i].isFile) {
                        files[i].length()
                    } else {
                        getFolderSize(files[i])
                    }
                }
            }
        }
        return length
    }

    @JvmStatic
    fun getFileSizeMegaBytes(file: File): String {
        return String.format(
            Locale.CANADA,
            "%.2f MB",
            getFolderSize(file).toDouble() / (1024 * 1024)
        )
    }

    fun loadDialog(context: Context, icon: Int): ProgressDialog {
        val draw =
            ResourcesCompat.getDrawable(context.resources, R.drawable.custom_progressbar, null)
        val pd = ProgressDialog(context)
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pd.isIndeterminate = false //If we need onProgress bar update
        pd.setCanceledOnTouchOutside(false)
        pd.setCancelable(false)
        pd.setProgressDrawable(draw)
        pd.setIcon(icon)
        pd.setTitle(R.string.alert_dialog_title)
        pd.setMessage(context.getString(R.string.alert_dialog_text))
        pd.max = 100
        return pd
    }

    /**data/app/SmokeTestApp/SmokeTestApp.apk
     * storage/emulated/0/Download */
    @JvmStatic
    fun openFolder(context: Context, var0: String?) {
        //        if (!var0.isDirectory()) {
//            Toast.makeText(context, R.string.access_error, Toast.LENGTH_SHORT).show();
//            return;
//        }


        if (var0 != null) {
            //Warning! Do this if it's directory
            //No need FileProvider


            val uri = Uri.parse(var0) //It's ok

            //Uri uri = Uri.fromFile(new File(var0)); //Exception

//                    Intent intent = null;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                        intent.setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR);
//                    }
            val pm = context.packageManager

            // if you reach this place, it means there is no any file
            // explorer app installed on your device

//            intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "*/*");
//            context.startActivity(intent);
            if (check_Android30FileBrowser_AndroidLysesoftFileBrowser(uri, context, pm, true)) {
                return
            }


            //api 24 not open folder? not have filebrowser((
        }
    }

    private fun checkResolver(intent: Intent, pm: PackageManager) {
        val resolvedActivityList = pm.queryIntentActivities(intent, 0)
        for (info in resolvedActivityList) {
            d("------------[][][$info")

            val serviceIntent = intent //new Intent();
            //serviceIntent.setAction(Intent.ACTION_VIEW);
            //serviceIntent.setPackage(info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                //packagesSupportingCustomTabs.add(info);
                d("-----0------$info")
            } else if (pm.resolveActivity(serviceIntent, 0) != null) {
                d("-----1------$info")
            }
        }
    }


    private fun resolwe(intent: Intent, pm: PackageManager) {
        val resolvedActivityList = pm.queryIntentActivities(intent, 0)
        for (info in resolvedActivityList) {
            d("------------[][][$info")

            val serviceIntent = Intent()
            serviceIntent.setAction(Intent.ACTION_VIEW)
            //serviceIntent.setPackage(info.activityInfo.packageName);
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                //packagesSupportingCustomTabs.add(info);
                d("-----0------$info")
            } else if (pm.resolveActivity(serviceIntent, 0) != null) {
                d("-----1------$info")
            }
        }
    }


    @JvmStatic
    fun check_Android30FileBrowser_AndroidLysesoftFileBrowser(
        uri: Uri, context: Context, pm: PackageManager, launch: Boolean
    ): Boolean {
        val m = arrayOf(
            "vnd.android.document/directory",  //=WORK in Android 30=
            "vnd.android.cursor.dir/lysesoft.andexplorer.director",  //"vnd.android.cursor.dir/*"
        )

        for (type in m) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, type)
            val mm = intent.resolveActivityInfo(pm, 0)
            if (mm != null) {
                d("[@@]$uri $mm $type")
                if (launch) {
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        handleException(e)
                    }
                }
                return true
            }
        }
        return false
    }


    private fun shareScreenshotToTwitter(context: Context, message: String?, file: File) {
        var message = message
        try {
            if (file.exists() && !file.isDirectory) {
            }
            val packageName = context.packageName
            val contentUri = FileProvider.getUriForFile(
                context,
                "$packageName.fileprovider", file
            )
            if (contentUri != null) {
                val www = Intent(Intent.ACTION_SEND)
                www.setType(MimeType.TEXT_PLAIN)
                if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                    message =
                            //"Hey my friend check out this app\n https://play.google.com/store/apps/details?id="
                        ""
                }
                www.putExtra(Intent.EXTRA_TEXT, "$message \n")
                //        www.putExtra(Intent.EXTRA_TEXT, new Intent(Intent.ACTION_VIEW,
                //                Uri.parse("https://play.google.com/store/apps/details?id="
                //                        + context.getPackageName()))
                //        );
//            www.putExtra(Intent.EXTRA_TEXT, new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id="
//                            + context.getPackageName()))
//            );
                // temp permission for receiving app to read this file
                www.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)


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
                www.putExtra(Intent.EXTRA_STREAM, contentUri)
                www.setType("*/*")

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

                    val intent = Intent(Intent.ACTION_SEND)

                    //--> intent.setType(MimeType.TEXT_PLAIN);
                    if (message.trim { it <= ' ' }.isEmpty()) {
                        message = ""
                    }

                    intent.putExtra(Intent.EXTRA_TEXT, "$message \n")


                    //        intent.putExtra(Intent.EXTRA_TEXT, new Intent(Intent.ACTION_VIEW,
                    //                Uri.parse("https://play.google.com/store/apps/details?id="
                    //                        + context.getPackageName()))
                    //        );
                    val type = context.contentResolver.getType(contentUri)
                    d("::TYPE:: $type")

                    intent.putExtra(Intent.EXTRA_EMAIL, "22@hhhh")
                    intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))

                    intent.setType("*/*")

                    //intent.setData(contentUri); //False To:field in gmail

                    // temp permission for receiving app to read this file
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    //intent.setDataAndType(contentUri, "image/jpeg"); //Not application/octet-stream type
                    intent.putExtra(Intent.EXTRA_STREAM, contentUri)

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
                    context.startActivity(Intent.createChooser(intent, "Choose an app"))
                } catch (e: StringIndexOutOfBoundsException) {
                    handleException(e)
                }
            }
        } catch (e: StringIndexOutOfBoundsException) {
            if (BuildConfig.DEBUG) {
                d("@@@: " + file.absolutePath)
            }
        } catch (a: IllegalArgumentException) {
            if (BuildConfig.DEBUG) {
                d("@@@: " + a.localizedMessage)
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun get_out_filename(meta: PackageMeta): String {
        //        long versionCode;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            versionCode = meta.getLongVersionCode();
//        } else {
//            versionCode = meta.versionCode;
//        }

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            ("/Download/"
                    + Config.__CLOUD_BACKUP_LOCATION_LOCAL + meta.packageName + "_v" + meta.versionCode + ".apk")
        } else {
            ("/"
                    + Config.__CLOUD_BACKUP_LOCATION_LOCAL + meta.packageName + "_v" + meta.versionCode + ".apk")
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun buildDstPath(path: File): File {
        if ((!path.parentFile.exists() && !path.parentFile.mkdirs()) || !path.parentFile.isDirectory) {
            throw IOException("Cannot create directory: " + path.parentFile.absolutePath)
        }
        if (!path.exists()) return path

        var dst = path
        val fname = path.name
        val index = fname.lastIndexOf(".")
        val ext = fname.substring(index)
        val name = fname.substring(0, index)

        var i = 0
        while (dst.exists()) {
            dst = File(path.parentFile, "$name-$i$ext")
            i++
        }

        return dst
    }


    @Throws(Exception::class)
    fun extractWithRoot(meta: PackageMeta): String {
        val src = File(meta.sourceDir)
        val path = System.getenv("EXTERNAL_STORAGE") + get_out_filename(meta)

        val dst = buildDstPath(File(path))
        if (dst.exists()) {
            val res = dst.delete()
        }

        var p: Process? = null
        val err = StringBuilder()
        try {
            p = Runtime.getRuntime()
                .exec("su -c cat " + src.absolutePath + " > " + dst.absolutePath)
            p.waitFor()

            if (p.exitValue() != 0) {
                val reader = BufferedReader(InputStreamReader(p.errorStream))
                var line: String? = ""
                while ((reader.readLine().also { line = it }) != null) {
                    err.append(line)
                    err.append("\n")
                }

                throw Exception(err.toString())
            }
        } catch (e: IOException) {
            throw Exception(e.message)
        } catch (e: InterruptedException) {
            throw Exception(e.message)
        } finally {
            if (p != null) {
                try {
                    p.destroy()
                } catch (e: Exception) {
                    handleException(e)
                }
            }
        }
        if (!dst.exists()) {
            throw Exception("cannot exctract file [root]")
        }
        return dst.absolutePath
    }

    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun makeURI(context: Context, file: File): Uri {
        if (file.isDirectory) {
            return Uri.fromFile(file) //Not use FileProvider is Directory
        }

        return if (Build.VERSION.SDK_INT >= 23) {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
            )

            //java.lang.IllegalArgumentException: Failed to find configured root that contains
            //            try {
            //                return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            //            } catch (java.lang.IllegalArgumentException e) {
            //                DLog.handleException(e);
            //                return Uri.fromFile(file);
            //            }
        } else {
            Uri.fromFile(file)
        }
    }


    fun installApp(context: Context, file: File) {
        d(file.absolutePath)

        val pm = context.packageManager
        try {
            val toInstall = File(file.path)
            val v0: Intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //old -->
                //Uri apkUri = Uri.fromFile(mModel.file);

                val apkUri = makeURI(context, toInstall)
                v0 = Intent(Intent.ACTION_VIEW)
                v0.setDataAndType(apkUri, "application/vnd.android.package-archive")
                v0.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                v0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                v0.setData(apkUri)
            } else {
                val apkUri = Uri.fromFile(toInstall)
                v0 = Intent(Intent.ACTION_VIEW)
                v0.setDataAndType(apkUri, "application/vnd.android.package-archive")
                v0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val resolvedActivityList = pm.queryIntentActivities(v0, 0)
            for (info in resolvedActivityList) {
                d("------------[][][$info")

                val serviceIntent = v0 //new Intent();
                //serviceIntent.setAction(Intent.ACTION_VIEW);
                //serviceIntent.setPackage(info.activityInfo.packageName);
                // Check if this package also resolves the Custom Tabs service.
                if (pm.resolveService(serviceIntent, 0) != null) {
                    //packagesSupportingCustomTabs.add(info);
                    d("[INSTALLER 0]$info")
                } else if (pm.resolveActivity(serviceIntent, 0) != null) {
                    d("[INSTALLER 1]$info")
                }
            }
            context.startActivity(v0)
        } catch (e: Exception) {
            handleException(e)
            Toasty.error(context, "" + e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }


    fun handleInstallerViewer(context: Context, info: ResolveInfo, action: String?, uri: Uri?) {
        val intent = Intent(action)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        intent.setPackage(packageName(info))
        context.startActivity(intent)
    }

    /**
     * It's error
     *
     *
     * intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
     * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     *
     *
     * Success
     * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
     * | Intent.FLAG_GRANT_READ_URI_PERMISSION);
     */
    @JvmStatic
    fun packageName(info: ResolveInfo): String? {
        var packageName: String? = null
        if (info.activityInfo != null) {
            packageName = info.activityInfo.packageName
        }
        if (info.serviceInfo != null) {
            packageName = info.serviceInfo.packageName
        }
        return packageName
    }
}
