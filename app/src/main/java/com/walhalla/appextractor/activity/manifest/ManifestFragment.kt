package com.walhalla.appextractor.activity.manifest

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.walhalla.abcsharedlib.Share
import com.walhalla.appextractor.R
import com.walhalla.appextractor.Tools
import com.walhalla.appextractor.databinding.ManifestExplorerBinding
import com.walhalla.appextractor.fragment.BaseFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.presenter.BaseManifestPresenter.ManifestCallback
import com.walhalla.appextractor.utils.ShareUtils.KEY_FILE_PROVIDER
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.ui.plugins.MimeType
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ManifestFragment : BaseFragment(), ManifestCallback {
    private var meta: PackageMeta? = null

    private var presenter: ManifestPresenterXml? = null


    private var bind: ManifestExplorerBinding? = null


    private var apkPath: String? = null

    private var xmlFileName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = arguments!!.getParcelable(ARG_PARAM1)
            apkPath = arguments!!.getString(KEY_OBJ_APK)

            if (arguments!!.containsKey(KEY_OBJ_XML_FILENAME)) {
                xmlFileName = arguments!!.getString(KEY_OBJ_XML_FILENAME)
            }
        }
        if (xmlFileName == null) {
            xmlFileName = ManifestPresenterXml.ANDROID_MANIFEST_FILENAME
        }
        presenter = ManifestPresenterXml(activity, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = ManifestExplorerBinding.inflate(inflater, container, false)
        return bind!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind!!.webView.settings.builtInZoomControls = true
        bind!!.webView.settings.displayZoomControls = true
        if (!TextUtils.isEmpty(apkPath)) {
            bind!!.text1.text = apkPath
        }

        bind!!.xml.setOnClickListener { v: View? ->
            presenter!!.renderXML(xmlFileName)
        }
        bind!!.txt.setOnClickListener { v: View? ->
            presenter!!.renderSimpleText(xmlFileName)
        }

        bind!!.copy.setOnClickListener { v: View? ->
            val tmp = presenter!!.mOutgetText(xmlFileName)
            d("@@$tmp")
            Tools.copyToClipboard(tmp, activity)
        }

        bind!!.share.setOnClickListener { v: View? ->
            val tmp = presenter!!.mOutgetText(xmlFileName)
            shareFile(
                activity!!,
                tmp,
                ManifestPresenterXml.ANDROID_MANIFEST_FILENAME,
                "Manifest Explorer"
            )
        }

        if (meta != null && savedInstanceState == null) {
            presenter!!.configForPackage(meta!!.packageName, apkPath)
            //this.mOut.setText("");
            presenter!!.renderXML(xmlFileName)
        }
    }

    override fun fab() {
    }

    //    Длина extra.length() == 130124
    //    но уже получаю
    //    android.os.TransactionTooLargeException: data parcel size 523504 bytes
    fun shareFile(context: Context, extra: String, fileName: String, chooserTitle: String?) {
        var chooserTitle = chooserTitle
        d("{share} " + extra.length)

        if (chooserTitle == null) {
            chooserTitle = context.resources.getString(R.string.app_name)
        }
        val text = meta!!.packageName
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, text)


        //intent.putExtra(Intent.EXTRA_EMAIL, "alexvarboffin@gmail.com");//Work only with intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        //intent.setType("*/*");
        if (extra.length < 50000) {
            intent.putExtra(Share.comPinterestEXTRA_DESCRIPTION, text)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // Сохраняем текст во временный файл в temp папку
        try {
            // Создаем временный файл в папке "cache" вашего приложения
            val tempFile = File(context.cacheDir, fileName)
            val fos = FileOutputStream(tempFile)
            fos.write(extra.toByteArray())
            fos.close()

            // Получаем URI файла через FileProvider
            val fileUri = FileProvider.getUriForFile(
                context,
                context.packageName + KEY_FILE_PROVIDER,
                tempFile
            )

            // Передаем файл через Intent
            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
            intent.setType(MimeType.TEXT_PLAIN)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Добавляем тему сообщения
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))

            // Открываем выбор приложений для отправки
            val chooser = Intent.createChooser(intent, chooserTitle)
            val resInfoList = context.packageManager.queryIntentActivities(
                chooser,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (!resInfoList.isEmpty()) {
                context.startActivity(chooser)
            } else {
                d("Not found activity...")
            }
        } catch (e: IOException) {
            handleException(e)
        }
    }

    override fun showError(text: String, t: Throwable) {
//        DLog.e("ManifestExplorer " + text + " : " + ((t != null) ? t.getMessage() : ""));
//        //Toast.makeText(this, "Error: " + text + " : " + t, Toast.LENGTH_LONG).show();
//        String s = "Error: " + text + " : " + t + " ✘";
//        Toast toast = Toasty.error(getActivity(), s.toUpperCase(), Toast.LENGTH_SHORT);
//        toast.show();
        if (t is FileNotFoundException) {
            bind!!.webView.loadDataWithBaseURL(
                null,
                ("""${nameFormat(apkPath)}
 does not contain this file
 $xmlFileName"""), MimeType.TEXT_PLAIN, "UTF-8", null
            )
        } else {
            bind!!.webView.loadDataWithBaseURL(
                null, (""
                        + "Error: " + text + " : " + t), MimeType.TEXT_PLAIN, "UTF-8", null
            )
        }
    }

    private fun nameFormat(apkPath: String?): String? {
        if (TextUtils.isEmpty(apkPath)) {
            return apkPath
        }
        val tmp = apkPath!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return tmp[tmp.size - 1]
    }

    override fun showManifestContent(s: String) {
        bind!!.webView.loadDataWithBaseURL(null, "" + s, MimeType.TEXT_PLAIN, "UTF-8", null)
    }

    override fun loadDataWithPatternHTML(encoded: String) {
        bind!!.webView.loadDataWithBaseURL(null, encoded, "text/html", "UTF-8", null)
    }

    companion object {
        private const val ARG_PARAM1 = "key_arg_0"


        const val KEY_OBJ_APK: String = "key_obj_apk"
        private val KEY_OBJ_XML_FILENAME = ManifestFragment::class.java.simpleName + "var3"
        @JvmStatic
        fun newInstance(meta: PackageMeta?, apkPath: String?, xmlFileName: String?): BaseFragment {
            val fragment = ManifestFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, meta)
            args.putString(KEY_OBJ_APK, apkPath)
            args.putString(KEY_OBJ_XML_FILENAME, xmlFileName)
            fragment.arguments = args
            return fragment
        }
    }
}
