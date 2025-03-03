package com.walhalla.appextractor.adapter2.receiver;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.AppDetailInfoAdapter;
import com.walhalla.appextractor.databinding.ItemReceiverBinding;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class ReceiverViewHolder extends ExpandableAdapter.ViewHolder {

    private final AppDetailInfoAdapter presenter;
    public final ItemReceiverBinding binding;


    public ReceiverViewHolder(ItemReceiverBinding binding, AppDetailInfoAdapter presenter) {
        super(binding.getRoot());
        this.binding = binding;
        this.presenter = presenter;
    }


}