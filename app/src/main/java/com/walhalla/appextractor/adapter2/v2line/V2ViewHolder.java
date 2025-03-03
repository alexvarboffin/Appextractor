package com.walhalla.appextractor.adapter2.v2line;

import android.graphics.Color;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.AppDetailInfoAdapter;
import com.walhalla.appextractor.databinding.ItemV2LineBinding;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class V2ViewHolder extends ExpandableAdapter.ViewHolder {

    private final ItemV2LineBinding bind;
    //private final AppDetailInfoAdapter presenter;

    public void bind(V2Line object, int position, AppDetailInfoAdapter.DetailAdapterCallback clb) {
        if (position % 2 > 0) {
            bind.lLayout1.setBackgroundColor(Color.WHITE);
        }
        bind.text1.setText(object.key);
        bind.text2.setText(object.value);
        if (object.drawable == null) {
            bind.icon.setImageResource(R.drawable.ic_item_key);
        } else {
            bind.icon.setImageResource(object.drawable);
        }

        //this.text2.setBackgroundColor(Color.YELLOW);

        bind.searchBtn.setOnClickListener(v -> {
            //presenter.onItemClicked(v, object);
        });

        bind.lLayout1.setOnClickListener(v -> {
            if (clb != null) {
                clb.shareText(object.key + "\n" + object.value);
            }
        });
        bind.text2.setOnClickListener(v -> {
            if (clb != null) {
                clb.shareText(object.value);
            }
        });
    }

    public V2ViewHolder(ItemV2LineBinding binding, AppDetailInfoAdapter presenter) {
        super(binding.getRoot());
        this.bind = binding;
        //this.presenter = presenter;
    }
}
