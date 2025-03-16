package com.walhalla.appextractor.adapter2.receiver;

import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter;
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