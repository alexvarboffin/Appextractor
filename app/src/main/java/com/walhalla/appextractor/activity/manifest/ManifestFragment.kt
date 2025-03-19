package com.walhalla.appextractor.activity.manifest

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.walhalla.appextractor.Tools
import com.walhalla.appextractor.databinding.ManifestExplorerBinding
import com.walhalla.appextractor.fragment.BaseFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.presenter.BaseManifestPresenter.ManifestCallback
import com.walhalla.appextractor.utils.IntentUtil
import com.walhalla.manifest.ManifestPresenterXml
import com.walhalla.ui.DLog.d
import com.walhalla.ui.plugins.MimeType
import java.io.FileNotFoundException

class ManifestFragment : BaseFragment(), ManifestCallback {
    private var meta: PackageMeta? = null

    private var presenter: ManifestPresenterXml? = null


    private var bind: ManifestExplorerBinding? = null


    private lateinit var apkPath: String

    private lateinit var xmlFileName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = requireArguments().getParcelable(ARG_PARAM1)
            apkPath = requireArguments().getString(KEY_OBJ_APK)?:""

            if (requireArguments().containsKey(KEY_OBJ_XML_FILENAME)) {
                xmlFileName = requireArguments().getString(KEY_OBJ_XML_FILENAME)?:""
            }
        }
        if (xmlFileName.isEmpty()) {
            xmlFileName = ManifestPresenterXml.ANDROID_MANIFEST_FILENAME
        }
        presenter = ManifestPresenterXml(requireActivity(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            Tools.copyToClipboard(tmp, requireActivity())
        }

        bind!!.share.setOnClickListener { v: View? ->

            val tmp = presenter!!.mOutgetText(xmlFileName)
            IntentUtil.shareTextLikeFile(
                requireActivity(), meta!!, tmp, ManifestPresenterXml.ANDROID_MANIFEST_FILENAME,
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



    override fun showError(text: String, t: Throwable?) {
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
