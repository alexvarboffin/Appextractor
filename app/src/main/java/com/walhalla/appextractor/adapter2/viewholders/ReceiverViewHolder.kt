package com.walhalla.appextractor.adapter2.viewholders

import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.databinding.ItemReceiverBinding
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class ReceiverViewHolder(
    val binding: ItemReceiverBinding,
    private val presenter: AppDetailInfoAdapter
) :
    ExpandableAdapter.ViewHolder(binding.root) 