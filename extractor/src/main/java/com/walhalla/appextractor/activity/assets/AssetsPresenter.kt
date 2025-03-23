package com.walhalla.appextractor.activity.assets

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.walhalla.abcsharedlib.Share
import com.walhalla.extractor.R

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AssetsPresenter(private val context: Context, private val view: MvpContract.View) : MvpContract.Presenter {
    private var packageName: String? = null
    private var am: AssetManager? = null
    private var resources: Resources? = null
    private var xmlResourceId = 0


    fun configForPackage(packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) {
            //packageName = "com.zaimi.online.na.kartu.Buckridge";
            //packageName = "com.walhalla.appextractor";
        }
        println(packageName)

        this.packageName = packageName

        val initAM: AssetManager? = null
        val initRes: Resources? = null
        try {
            am = context.createPackageContext(packageName, 0).assets
            try {
                val list: MutableList<ResItem> = ArrayList()
                treeViewer(am!!, list, "")

//                treeViewer(am, list, "dexopt");
//                treeViewer(am, list, "images");
//                treeViewer(am, list, "");
//                treeViewer(am, list, "");
//                treeViewer(am, list, "");
//                treeViewer(am, list, "");
//                treeViewer(am, list, "");
//                treeViewer(am, list, "");
//                treeViewer(am, list, "");
//                treeViewer(am, list, "");
                view.showSuccess(list)
            } catch (e: Exception) {
                handleException(e)
            }

            val list = Resources(am, context.resources.displayMetrics, null)
            xmlResourceId = list.getIdentifier(
                "strings",
                "xml",
                packageName
            ) // Получение идентификатора ресурса

            println("@@=$xmlResourceId ")
            try {
                //XmlResourceParser xml = assetManager.openXmlResourceParser("AndroidManifest.xml");


                val xml = list.getXml(xmlResourceId) // Получение XmlResourceParser


                //CharSequence xmlText = getXMLText(xml, strings);
//                String txt0 = convertStreamToReadableString("<manifest\n" + "    versionCode=\"221208\"/>");

                //this.mOut.append(xmlText);
//            this.mOut.loadData(
//                    "<manifest\n" +
//                            "    versionCode=\"221208\"/>"
//
//                    , "text/xml; charset=UTF-8", null);


                //mView.showManifestContent(xmlText.toString());//loadDataWithPatternXML
            } catch (ioe: Exception) {
                handleException(ioe)
                //mView.showError("Reading XML", ioe);
            }

            //Resources resources = new Resources(am, context.getResources().getDisplayMetrics(), null);
            //raw000();
        } catch (name: PackageManager.NameNotFoundException) {
            //Toast.makeText(this, "Error, couldn't create package context: " + name.getLocalizedMessage(), Toast.LENGTH_LONG);
            am = initAM
            resources = initRes
            return false
        } catch (unexpected: RuntimeException) {
            Log.e("@@@", "error configuring for package: " + packageName + " " + unexpected.message)
            am = initAM
            resources = initRes
            return false
        }
        return true
    }

    private fun raw000() {
        val fields = android.R.raw::class.java.fields
        println("@@@@@@@@@@" + fields.size)
        for (field in fields) {
            try {
                val resourceId = field.getInt(null)
                val resourceName = context.resources.getResourceEntryName(resourceId)
                println("Raw Resource $resourceName")
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    @Throws(IOException::class)
    private fun treeViewer(am: AssetManager, list: MutableList<ResItem>, rootPath: String) {
        val resourceFiles =
            am.list(rootPath) // Получение списка всех ресурсов в корневом каталоге assets
        for (resourceFile in resourceFiles!!) {
            val fullPath = if (("" == rootPath)) resourceFile else "$rootPath/$resourceFile"

            if (!fullPath.isEmpty()) {
                try {
                    val inputStream = this.am!!.open(fullPath)
                    //AssetFileDescriptor assetFileDescriptor = am.openFd(fullPath);
                    //AssetFileDescriptor assetFileDescriptor = am.openNonAssetFd(fullPath);
                    //DLog.d("{@@@@" + fullPath);
                    var drawable: Drawable? = null

                    //@@detect typew
                    if (ResItem.isImages(fullPath)) {
                        drawable = Drawable.createFromStream(inputStream, null)
                        list.add(ResItem(fullPath, drawable, ResType.Images))
                    } else {
                        list.add(ResItem(fullPath, drawable, ResType.File))
                    }

                    // Полученное содержимое файла
                    //assetFileDescriptor.close();
                } catch (e: IOException) {
                    handleException(e)
                    list.add(ResItem("" + fullPath, ResType.Dir)) //FileNotFoundException
                    treeViewer(am, list, fullPath)
                }
            }
        }
    }

    override fun loadManifestContent(packageName: String) {
        configForPackage(packageName)
    }


    override fun readAssetRequest(context: Context, resource: ResItem) {
        try {
            val inputStream = am!!.open(resource.fullPath)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val content = StringBuilder()
            var line: String?
            while ((reader.readLine().also { line = it }) != null) {
                content.append(line)
                content.append("\n")
            }
            reader.close()
            inputStream.close()

            //Полученное содержимое файла
            val fileContent = content.toString()
            view.showResourceRawText(resource, fileContent)
        } catch (e: IOException) {
            view.showError(e.message?:"${e}")
        }
    }

    override fun zipAllAssetsRequest(activity: Context, r0: ResItem) {
        val name = "assets.zip" //+app_name
        val outputZipPath =
            File((Environment.getExternalStorageDirectory().absolutePath + "/Download/"), name)
        if (outputZipPath.exists()) {
            val b = outputZipPath.delete()
        }

        println("@@$outputZipPath")

        try {
            zipAssetsFolder(am!!, outputZipPath.absolutePath)


            val description =
                "" + name + " file extracted by " + context.resources.getString(R.string.app_name)
            Share.shareFile(context, context.packageName, description, outputZipPath)
            println("success: $name")
        } catch (e: IOException) {
            handleException(e)
            view.successToast(R.string.err_unable_saved_file, name)
        }
    }

    override fun exportIconRequest(context: Context, resource: ResItem) {
        val tmp =
            resource.fullPath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val name = tmp[tmp.size - 1]
        val filePath =
            File((Environment.getExternalStorageDirectory().absolutePath + "/Download/"), name)
        if (filePath.exists()) {
            val b = filePath.delete()
        }
        println("@@$filePath")

        try {
            val inputStream = am!!.open(resource.fullPath)
            val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.newOutputStream(filePath.toPath())
            } else {
                FileOutputStream(filePath)
            }
            val buffer = ByteArray(1024)
            var length: Int
            while ((inputStream.read(buffer).also { length = it }) > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream.close()
            val description = "" + name + " file extracted by " + context.resources.getString(R.string.app_name)
            Share.shareFile(context, context.packageName, description, filePath)
            println("success: $name")
        } catch (e: IOException) {
            handleException(e)
            view.successToast(R.string.err_unable_saved_file, name)
        }
    }


    override fun saveAsset(context: Context, resource: ResItem) {
//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/");
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//        File iconDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk Extractor/", "/App Icons/");
//        if (!iconDir.exists()) {
//            iconDir.mkdir();
//        }
        //final PackageManager pm = getPackageManager();
        //String name = resource.applicationInfo.loadLabel(pm).toString();
        val tmp =
            resource.fullPath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val name = tmp[tmp.size - 1]
        val filePath =
            File((Environment.getExternalStorageDirectory().absolutePath + "/Download/"), name)
        if (filePath.exists()) {
            val b = filePath.delete()
        }
        println("@pp@$filePath")

        try {
            val inputStream = am!!.open(resource.fullPath)
            val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.newOutputStream(filePath.toPath())
            } else {
                FileOutputStream(filePath)
            }
            val buffer = ByteArray(1024)
            var length: Int
            while ((inputStream.read(buffer).also { length = it }) > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream.close()

//            OutputStream outputStream = new FileOutputStream(filePath);
//            if (bitmap != null) {
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//                outputStream.flush();
//                outputStream.close();
//            }
            view.successToast(R.string.savedfile, name)
            println("success: $name")
        } catch (e: IOException) {
            handleException(e)
            view.successToast(R.string.err_unable_saved_file, name)
        }
    }

    private fun handleException(e: Exception) {
        println(e)
    }

    companion object {
        @Throws(IOException::class)
        fun zipAssetsFolder(am: AssetManager, outputZipPath: String?) {
            val list = am.list("")


            ZipOutputStream(FileOutputStream(outputZipPath)).use { zipOutputStream ->
                for (fileName in list!!) {
                    val filePath = fileName

                    if ("dexopt" != fileName && "webkit" != fileName && "images" != fileName
                    ) {
                        try {
                            val inputStream = am.open(filePath)
                            zipFile(fileName, inputStream, zipOutputStream)
                            inputStream.close()
                        } catch (e: FileNotFoundException) {
                            handleException(e)
                        }
                    }
                }
            }
        }

        private fun handleException(e: Exception) {
            println(e)
        }

        @Throws(IOException::class)
        private fun zipFile(
            fileName: String,
            inputStream: InputStream,
            zipOutputStream: ZipOutputStream
        ) {
            val buffer = ByteArray(1024)
            var count: Int

            val zipEntry = ZipEntry(fileName)
            zipOutputStream.putNextEntry(zipEntry)

            while ((inputStream.read(buffer).also { count = it }) != -1) {
                zipOutputStream.write(buffer, 0, count)
            }
            zipOutputStream.closeEntry()
        }

        @Throws(IOException::class)
        private fun zipFolder(
            path: String,
            inputStream: InputStream,
            zipOutputStream: ZipOutputStream
        ) {
            val buffer = ByteArray(1024)
            var count: Int

            val zipEntry = ZipEntry(path)
            zipOutputStream.putNextEntry(zipEntry)

            while ((inputStream.read(buffer).also { count = it }) != -1) {
                zipOutputStream.write(buffer, 0, count)
            }

            zipOutputStream.closeEntry()
        }
    }
}

