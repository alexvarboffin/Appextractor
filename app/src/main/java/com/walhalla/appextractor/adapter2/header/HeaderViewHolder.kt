package com.walhalla.appextractor.adapter2.header;

import android.widget.TextView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.databinding.ItemHeaderBinding;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class HeaderViewHolder extends
        //RecyclerView.ViewHolder
        ExpandableAdapter.ViewHolder {


    private final ItemHeaderBinding binding;


    public HeaderViewHolder(ItemHeaderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(HeaderObject tmp) {
        this.binding.textView1.setText(tmp.title);
        if (tmp.icon != null) {
            //this.binding.textView1.setCompoundDrawablesWithIntrinsicBounds(tmp.icon, 0, 0, 0);
            binding.image.setImageResource(tmp.icon);
        }
    }
}