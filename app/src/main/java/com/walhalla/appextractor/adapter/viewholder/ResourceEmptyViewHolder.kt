package com.walhalla.appextractor.adapter.viewholder

import com.walhalla.appextractor.resources.StringItemViewModel
import com.walhalla.appextractor.databinding.ItemLoggerEmptyBinding

class ResourceEmptyViewHolder(
    private val mBinding: ItemLoggerEmptyBinding
) :
    BaseVh<StringItemViewModel>(mBinding.root){

    override fun bind(operation: StringItemViewModel, position: Int) {
        if (operation != null) {
            mBinding.text1.setText(operation.text)
            operation.icon?.let { mBinding.image.setImageResource(it) }
        }
    }
}
