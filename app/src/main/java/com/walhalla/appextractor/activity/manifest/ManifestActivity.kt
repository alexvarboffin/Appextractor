package com.walhalla.appextractor.activity.manifest

import android.content.Context
import android.content.Intent
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.debug.DemoData
import com.walhalla.appextractor.databinding.ManifestTabsExplorerBinding
import com.walhalla.appextractor.fragment.QCallback
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.manifest.ManifestContract
import androidx.core.net.toUri
import com.walhalla.manifest.ManifestViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManifestActivity : AppCompatActivity(), QCallback {

    private val viewModel: ManifestViewModel by viewModels()
    private lateinit var binding: ManifestTabsExplorerBinding
    private var meta: PackageMeta? = null
    private var apkPath: Array<String>? = null
    private var xmlFileName: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ManifestTabsExplorerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = null
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
        }
        
        makeUI()
    }

    private fun makeUI() {
        meta = intent.getParcelableExtra(KEY_OBJ_NAME)
        apkPath = intent.getStringArrayExtra(KEY_OBJ_APK_PATH)
        xmlFileName = intent.getStringExtra(KEY_OBJ_XML_FILENAME)

        if (meta == null) {
            meta = DemoData.demoData(this, meta)
        }

        lifecycleScope.launch {
            viewModel.data.collectLatest { state ->
                binding.appName.text = state.title
                binding.subTitle.text = state.packageName
                binding.icon.setImageDrawable(state.icon)
                
                binding.appName.setOnClickListener { copyToBuffer(state.title) }
                binding.subTitle.setOnClickListener { copyToBuffer(state.packageName) }
            }
        }

        meta?.let { packageMeta ->
            viewModel.doStuff(packageManager, packageMeta)
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.container, ManifestPagerFragment.instanceXmlViewer(meta, apkPath, xmlFileName))
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
            intent.setData(("package:" + meta!!.packageName).toUri())
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



    companion object {
        const val KEY_OBJ_NAME: String = "key_obj_iii"
        const val KEY_OBJ_APK_PATH: String = "key_obj_apkpath"
        const val KEY_OBJ_XML_FILENAME: String = "key_obj_xmlfile"

        fun newIntent(
            context: Context,
            packageInfo: PackageMeta?,
            apkPath: Array<String>?,
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