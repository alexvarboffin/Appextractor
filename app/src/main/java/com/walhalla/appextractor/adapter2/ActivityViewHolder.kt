package com.walhalla.appextractor.adapter2

import android.content.IntentFilter
import android.graphics.Color
import android.view.View
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.walhalla.appextractor.adapter.BackupAppFeatureAdapter
import com.walhalla.appextractor.databinding.ItemActivityBinding
import com.walhalla.appextractor.model.backup.SimpleAppFeature
import com.walhalla.appextractor.model.common.AppFeature
import com.walhalla.appextractor.sdk.ActivityLine
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class ActivityViewHolder(
    val binding: ItemActivityBinding,
    private val presenter: AppDetailInfoAdapter
) :
    ExpandableAdapter.ViewHolder(binding.root) {
    private val mFeatureAdapter: BackupAppFeatureAdapter


    init {
        val layoutManager = FlexboxLayoutManager(itemView.context, FlexDirection.ROW, FlexWrap.WRAP)
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.actions.layoutManager = layoutManager

        //featureRecycler.setRecycledViewPool(mFeatureViewPool);
        mFeatureAdapter = BackupAppFeatureAdapter()
        binding.actions.adapter = mFeatureAdapter
        binding.actions.isFocusable = false
    }

    fun bind(line: ActivityLine, position: Int) {
        if (position % 2 > 0) {
            binding.lLayout1.setBackgroundColor(Color.WHITE)
        }
        binding.icon.setImageDrawable(line.icon)
        binding.activityLabel.text = line.label
        binding.className.text = line.className

        //this.text2.setBackgroundColor(Color.YELLOW);
        if (line.exported) {
            binding.launchActivity.visibility = View.VISIBLE
            binding.launchActivity.setOnClickListener { v: View? ->
                presenter.onLaunchActivityRequest(
                    line.className!!
                )
            }
        }
        mFeatureAdapter.setFeatures(createContextualFeatures(line.intentFilters))
    }

    private fun createContextualFeatures(intentFilters: List<IntentFilter>): List<AppFeature> {
        val features = ArrayList<AppFeature>()
        if (!intentFilters.isEmpty()) {
            for (filter in intentFilters) {
                val sb = StringBuilder()
                val countActions = filter.countActions()
                val countCategories = filter.countCategories()
                val dataTypes = filter.countDataTypes()

                for (i in 0..<countActions) {
                    var action = filter.getAction(i)
                    action = action.replace("android.intent.action.", "")
                    sb.append(action).append(" ")
                }
                for (i in 0..<countCategories) {
                    var caregory = filter.getCategory(i)
                    caregory = caregory.replace("android.intent.category.", "")
                    sb.append(caregory).append(" ")
                }

                for (i in 0..<dataTypes) {
                    var dataType = filter.getDataType(i)
                    dataType = dataType.replace("android.intent.category.", "")
                    sb.append(dataType).append(" ")
                }
                features.add(SimpleAppFeature(sb.toString()))
            }
        }
        return features
    }
}