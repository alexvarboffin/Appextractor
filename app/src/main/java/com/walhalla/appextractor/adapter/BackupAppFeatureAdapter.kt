package com.walhalla.appextractor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.adapter.BackupAppFeatureAdapter.ViewHolder0
import com.walhalla.appextractor.common.AppFeature
import com.walhalla.appextractor.databinding.ItemBackupAppFeatureBinding

class BackupAppFeatureAdapter : RecyclerView.Adapter<ViewHolder0>() {
    private var mFeatures: List<AppFeature> = ArrayList()

    fun setFeatures(newFeatures: List<AppFeature>) {
        // Используем DiffUtil для обновления списка с минимальными изменениями
        val diffCallback = AppFeatureDiffCallback(mFeatures, newFeatures)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mFeatures = newFeatures
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder0 {
        val binding =
            ItemBackupAppFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder0(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder0, position: Int) {
        holder.bind(mFeatures[position])
    }

    override fun getItemCount(): Int {
        return if (mFeatures == null) 0 else mFeatures.size
    }

    class ViewHolder0(private val binding: ItemBackupAppFeatureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: AppFeature) {
            binding.root.text = feature.toText()
        }
    }

    internal class AppFeatureDiffCallback(
        private val oldFeatures: List<AppFeature>?,
        private val newFeatures: List<AppFeature>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldFeatures!!.size
        }

        override fun getNewListSize(): Int {
            return newFeatures.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldFeatures!![oldItemPosition].toText() === newFeatures[newItemPosition].toText()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldFeature = oldFeatures!![oldItemPosition]
            val newFeature = newFeatures[newItemPosition]
            return oldFeature == newFeature
        }
    }
}
