package com.walhalla.appextractor.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.databinding.ItemBackupAppFeatureBinding;
import com.walhalla.appextractor.common.AppFeature;

import java.util.ArrayList;
import java.util.List;

public class BackupAppFeatureAdapter extends
        RecyclerView.Adapter<BackupAppFeatureAdapter.ViewHolder0> {

    private List<AppFeature> mFeatures = new ArrayList<>();

    public BackupAppFeatureAdapter() {
        // Конструктор пустой, потому что мы не используем контекст в адаптере
    }

    public void setFeatures(List<AppFeature> newFeatures) {
        // Используем DiffUtil для обновления списка с минимальными изменениями
        AppFeatureDiffCallback diffCallback = new AppFeatureDiffCallback(mFeatures, newFeatures);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        mFeatures = newFeatures;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder0 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBackupAppFeatureBinding binding = ItemBackupAppFeatureBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder0(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder0 holder, int position) {
        holder.bind(mFeatures.get(position));
    }

    @Override
    public int getItemCount() {
        return mFeatures == null ? 0 : mFeatures.size();
    }

    static class ViewHolder0 extends RecyclerView.ViewHolder {

        private final ItemBackupAppFeatureBinding binding;

        ViewHolder0(@NonNull ItemBackupAppFeatureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AppFeature feature) {
            binding.getRoot().setText(feature.toText());
        }
    }

    static class AppFeatureDiffCallback extends DiffUtil.Callback {

        private final List<AppFeature> oldFeatures;
        private final List<AppFeature> newFeatures;

        public AppFeatureDiffCallback(List<AppFeature> oldFeatures, List<AppFeature> newFeatures) {
            this.oldFeatures = oldFeatures;
            this.newFeatures = newFeatures;
        }

        @Override
        public int getOldListSize() {
            return oldFeatures.size();
        }

        @Override
        public int getNewListSize() {
            return newFeatures.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldFeatures.get(oldItemPosition).toText() == newFeatures.get(newItemPosition).toText();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            AppFeature oldFeature = oldFeatures.get(oldItemPosition);
            AppFeature newFeature = newFeatures.get(newItemPosition);
            return oldFeature.equals(newFeature);
        }
    }

}
