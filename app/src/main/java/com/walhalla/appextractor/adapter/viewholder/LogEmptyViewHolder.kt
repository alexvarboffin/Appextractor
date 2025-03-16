package com.walhalla.appextractor.adapter.viewholder

import android.view.View

import com.walhalla.appextractor.databinding.ItemLoggerEmptyBinding
import com.walhalla.appextractor.model.LogViewModel


class LogEmptyViewHolder(
    private val mBinding: ItemLoggerEmptyBinding,
    val listener: LoggerAdapter.ChildItemClickListener?
) :
    BaseVh<LogViewModel>(mBinding.root), View.OnClickListener {


    init {
        mBinding.root.setOnClickListener(this)

        //Log.d("@hash=" + this.hashCode());
    }


    override fun onClick(view: View) {
        listener?.onClick0(view, adapterPosition)
    }

    override fun bind(operation: LogViewModel, position: Int) {
        if (operation != null) {
            mBinding.text1.setText(operation.text)
            mBinding.image.setImageResource(operation.icon)
        }
    }
}
