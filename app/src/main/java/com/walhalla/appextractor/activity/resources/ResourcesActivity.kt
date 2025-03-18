package com.walhalla.appextractor.activity.resources

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.walhalla.appextractor.R
import com.walhalla.appextractor.fragment.QCallback
import com.walhalla.appextractor.model.PackageMeta

class ResourcesActivity : AppCompatActivity(), QCallback {
    private var meta: PackageMeta? = null

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_assets_resources)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val aaa = supportActionBar
        if (aaa != null) {
            aaa.setDisplayShowCustomEnabled(true)
            aaa.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
            aaa.setDisplayShowTitleEnabled(false)
            aaa.setDisplayShowHomeEnabled(true)
        }

        val intent = this.intent
        if (intent != null) {
            meta = intent.getParcelableExtra(KEY_OBJ_NAME)
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.container, ResourcesPagerFragment.newInstance(meta))
            .commit()
    }


    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun handleException(exception: Exception) {
    }

    override fun success(text: Int) {
    }

    override fun copyToBuffer(value: String) {
    }

    override fun shareText(value: String) {
    }

    companion object {
        private const val KEY_OBJ_NAME = "key_obj_name" //"app_component"
    }
}
