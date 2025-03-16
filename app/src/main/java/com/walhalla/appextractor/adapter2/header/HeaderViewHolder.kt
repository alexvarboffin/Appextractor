package com.walhalla.appextractor.adapter2.header

import com.walhalla.appextractor.databinding.ItemHeaderBinding
import com.walhalla.appextractor.sdk.HeaderObject
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class HeaderViewHolder(private val binding: ItemHeaderBinding) : ExpandableAdapter.ViewHolder(
    binding.root
) {
    fun bind(tmp: HeaderObject) {
        binding.textView1.text = tmp.title
        if (tmp.icon > 0) {
            //this.binding.textView1.setCompoundDrawablesWithIntrinsicBounds(tmp.icon, 0, 0, 0);
            binding.image.setImageResource(tmp.icon)
        }
    }
}