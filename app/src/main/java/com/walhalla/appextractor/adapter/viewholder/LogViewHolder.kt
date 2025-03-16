package com.walhalla.appextractor.adapter.viewholder

import android.view.View
import com.walhalla.appextractor.adapter.viewholder.LoggerAdapter.ChildItemClickListener
import com.walhalla.appextractor.databinding.ItemLoggerBinding
import com.walhalla.appextractor.model.LogViewModel

class LogViewHolder(private val mBinding: ItemLoggerBinding, listener: ChildItemClickListener) :
    BaseVh<LogViewModel>(mBinding.root), View.OnClickListener {
    private val listener: ChildItemClickListener

    init {
        mBinding.root.setOnClickListener(this)
        this.listener = listener

        //Log.d("@hash=" + this.hashCode());
    }


    override fun onClick(view: View) {
        listener.onClick0(view, adapterPosition)
    }

    override fun bind(operation: LogViewModel, position: Int) {
        if (operation != null) {
            mBinding.text1.text = operation.text
            mBinding.imageView1.setImageResource(operation.icon)
        }
    }
}
