package com.walhalla.appextractor.activity.manifest

import android.content.Context
import android.content.Intent
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.debug.DemoData
import com.walhalla.appextractor.databinding.ManifestTabsExplorerBinding
import com.walhalla.appextractor.fragment.QCallback
import com.walhalla.appextractor.model.PackageMeta

class ManifestActivity : AppCompatActivity(), QCallback, ManifestContract.View {
    private var meta: PackageMeta? = null
    private var presenter: MainManifestPresenter? = null
    private var apkPath: Array<String>? = null
    private var xmlFileName: String? = null

    private var binding: ManifestTabsExplorerBinding? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        //ArrayAdapter<String> spinnerArrayAdapter;
        super.onCreate(savedInstanceState)

        binding = ManifestTabsExplorerBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        val aaa = supportActionBar
        if (aaa != null) {
            aaa.setDisplayShowCustomEnabled(true)
            aaa.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
            aaa.setDisplayShowTitleEnabled(false)
            aaa.setDisplayShowHomeEnabled(true)
        }
        makeUI()
    }

    private fun makeUI() {
        if (intent != null && intent.hasExtra(KEY_OBJ_NAME)) {
            meta = this.intent.getParcelableExtra(KEY_OBJ_NAME)
        }
        if (intent != null && intent.hasExtra(KEY_OBJ_APK_PATH)) {
            apkPath = intent.getStringArrayExtra(KEY_OBJ_APK_PATH)
        }
        if (intent != null && intent.hasExtra(KEY_OBJ_XML_FILENAME)) {
            xmlFileName = intent.getStringExtra(KEY_OBJ_XML_FILENAME)
        }

        if (meta == null) {
            meta = DemoData.demoData(this, meta)
        } else {
//                int pos = spinnerArrayAdapter.getPosition(packageName);
//                if (pos > -1)
//                {
//                    this.spinner.setSelection(pos);
//                }
        }

        presenter = MainManifestPresenter(this, meta!!, this)
        supportFragmentManager.beginTransaction()
            .add(R.id.container, ManifestPagerFragment.newInstance(meta, apkPath, xmlFileName))
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.manifest_explorer, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.system_info) {
            if (this.meta == null) {
                Toast.makeText(this, "@@@@", Toast.LENGTH_SHORT).show()
                return false
            }
            //LauncherAppsCompat.getInstance(this).showAppDetailsForProfile(this.componentName, UserHandleCompat.myUserHandle());
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.setData(Uri.parse("package:" + meta!!.packageName))
            startActivity(intent)
            return true
        }
        //        else if (menuItem.getItemId() == R.id.menu_copy_manifest_txt0) {
//            Tools.copyToClipboard(presenter.mOutgetText("AndroidManifest.xml"), this);
//            return true;
//        } else if (menuItem.getItemId() == R.id.menu_share_text0) {
//            Module_U.shareText(this, presenter.mOutgetText("AndroidManifest.xml"), "Manifest Explorer");
//            return true;
//        }
        return super.onOptionsItemSelected(menuItem)
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    fun showError(text: String, t: Throwable?) {
        Log.e("ManifestExplorer", text + " : " + (if (t != null) t.message else ""))
        Toast.makeText(this, "Error: $text : $t", Toast.LENGTH_LONG).show()
    }


    private fun ParseReleaseTag(xml: XmlResourceParser): String {
        return "@@@@@@@@@@@@@@"
    }

    private fun GetStyle(): String {
        return ""
    }


    override fun onResume() {
        super.onResume()
    }

    override fun copyToBuffer(value: String) {
    }

    override fun shareText(value: String) {
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun handleException(exception: Exception) {
    }

    override fun success(text: Int) {
    }

    override fun setTitleWithIcon(title: String?, packageName: String?, icon: Drawable?) {
        binding!!.appName.text = title
        binding!!.subTitle.text = packageName
        binding!!.icon.setImageDrawable(icon)

        binding!!.appName.setOnClickListener { v: View? ->
            copyToBuffer(
                title!!
            )
        }
        binding!!.subTitle.setOnClickListener { v: View? ->
            copyToBuffer(
                packageName!!
            )
        }
    }

    companion object {
        const val KEY_OBJ_NAME: String = "key_obj_iii"
        const val KEY_OBJ_APK_PATH: String = "key_obj_apkpath"
        const val KEY_OBJ_XML_FILENAME: String = "key_obj_xmlfile"

        fun newIntent(
            context: Context,
            packageInfo: PackageMeta?,
            apkPath: Array<String?>?,
            xmlFileName: String?
        ) {
            val intent = Intent(context, ManifestActivity::class.java)
            intent.putExtra(KEY_OBJ_NAME, packageInfo)
            intent.putExtra(KEY_OBJ_APK_PATH, apkPath)
            intent.putExtra(KEY_OBJ_XML_FILENAME, xmlFileName)
            context.startActivity(intent)
        }
    }
}