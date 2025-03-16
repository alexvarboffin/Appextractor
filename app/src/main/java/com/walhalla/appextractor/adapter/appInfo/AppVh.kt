package com.walhalla.appextractor.adapter.appInfo

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.walhalla.appextractor.R
import com.walhalla.appextractor.adapter.ApkListAdapter
import com.walhalla.appextractor.adapter.BackupAppFeatureAdapter
import com.walhalla.appextractor.databinding.ItemAppBinding
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.model.backup.SimpleAppFeature
import com.walhalla.appextractor.model.common.AppFeature
import java.util.Locale

class AppVh(
    val mBinding: ItemAppBinding,
    mFeatureViewPool: RecyclerView.RecycledViewPool?, //private BackupPackagesFilterConfig mFilterConfig;
    val mAdapter: ApkListAdapter
) :
    RecyclerView.ViewHolder(mBinding.root) {
    private val mFeatureAdapter: BackupAppFeatureAdapter

    init {
        val featureRecycler: RecyclerView = mBinding.rvBackupAppFeatures
        val layoutManager = FlexboxLayoutManager(itemView.context, FlexDirection.ROW, FlexWrap.WRAP)
        layoutManager.justifyContent = JustifyContent.FLEX_START
        featureRecycler.layoutManager = layoutManager

        featureRecycler.setRecycledViewPool(mFeatureViewPool)
        mFeatureAdapter = BackupAppFeatureAdapter()
        featureRecycler.adapter = mFeatureAdapter
        featureRecycler.isFocusable = false
    }


    //    /**
    //     * Click listener for popup menu items
    //     */
    //    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
    //
    //        @Override
    //        public boolean onMenuItemClick(MenuItem item) {
    //            return false;
    //        }
    //    }
    fun setAppName(name: String, highlight: String?) {
        setAndHighlight(mBinding.txtAppName, name, highlight)
    }

    fun setPackageName(name: String, highlight: String?, position: Int) {
        if (position % 2 > 0) {
            mBinding.root.setBackgroundColor(Color.WHITE)
        }
        setAndHighlight(mBinding.txtPackageName, name, highlight)
    }

    private fun setAndHighlight(view: TextView, value: String, pattern: String?) {
        var value = value
        view.text = value
        if (pattern == null || pattern.isEmpty()) return  // nothing to highlight
        value = value.lowercase(Locale.getDefault())
        var offset = 0
        var index = value.indexOf(pattern, offset)
        while (index >= 0 && offset < value.length) {
            val span: Spannable = SpannableString(view.text)
            span.setSpan(
                ForegroundColorSpan(Color.BLUE),
                index,
                index + pattern.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.text = span
            offset += index + pattern.length
            index = value.indexOf(pattern, offset)
        }
    }

    fun bindTo(packageMeta: PackageMeta) {
        mFeatureAdapter.setFeatures(createContextualFeatures(packageMeta))
    }


    private fun createContextualFeatures(packageMeta: PackageMeta): List<AppFeature> {
//        if (mFilterConfig == null)
//            return Collections.emptyList();

        val features = ArrayList<AppFeature>()

        val res = itemView.resources

        //        switch (mFilterConfig.getSort()) {
//            case NAME:
//                break;
//            case INSTALL_TIME:
//                features.add(new SimpleAppFeature(res.getString(R.string.backup_app_feature_install_date, mInstallOrUpdateDateSdf.format(packageMeta.installTime))));
//                break;
//            case UPDATE_TIME:
//                features.add(new SimpleAppFeature(res.getString(R.string.backup_app_feature_update_date, mInstallOrUpdateDateSdf.format(packageMeta.updateTime))));
//                break;
//        }
        if ( //mFilterConfig.getSplitApkFilter() == BackupPackagesFilterConfig.SimpleFilterMode.WHATEVER &&
            packageMeta.hasSplits
        ) features.add(SimpleAppFeature(res.getString(R.string.backup_app_feature_split)))

        if ( //mFilterConfig.getSystemAppFilter() == BackupPackagesFilterConfig.SimpleFilterMode.WHATEVER &&
            packageMeta.isSystemApp
        ) {
            features.add(SimpleAppFeature(res.getString(R.string.backup_app_feature_system_app)))
        }

        if (packageMeta.hasPinning) {
            features.add(SimpleAppFeature("SSL Pinning"))
        }
        return features
    }
}