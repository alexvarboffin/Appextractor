package com.walhalla.appextractor.adapter2.perm;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.databinding.ItemLinePermissionBinding;
import com.walhalla.appextractor.utils.PermissionUtils;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class PermissionViewHolder extends
        //RecyclerView.ViewHolder
        ExpandableAdapter.ViewHolder {

    public final ItemLinePermissionBinding binding;

    public interface PermissionViewHolderCallback {

        void onItemClicked(PermissionLine object);
    }

    private final PermissionViewHolderCallback presenter;

    public void bindPermItem(final PermissionLine object, int position) {
        if (position % 2 > 0) {
            this.binding.lLayout1.setBackgroundColor(Color.WHITE);
        }
        if (object != null) {
            this.binding.text1.setText(object.res0);
            int n = (object.isGranted()) ? R.drawable.ic_granted : R.drawable.ic_denied;
            this.binding.lock.setImageResource(n);


            String mm = PermissionUtils.protectionLevelToString(object.getProtectionLevel());
            this.binding.protectionLevel.setText(mm);
        }

        binding.searchBtn.setOnClickListener(v -> {
            presenter.onItemClicked(object);
        });
    }


    public PermissionViewHolder(ItemLinePermissionBinding binding, PermissionViewHolderCallback presenter) {
        super(binding.getRoot());
        this.binding = binding;
        this.presenter = presenter;
    }
}
