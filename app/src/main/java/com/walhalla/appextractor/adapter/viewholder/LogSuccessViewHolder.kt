package com.walhalla.appextractor.adapter.viewholder

import android.view.View
import com.walhalla.appextractor.databinding.LogItemSuccessBinding
import com.walhalla.appextractor.model.LogType
import com.walhalla.appextractor.model.LogViewModel


class LogSuccessViewHolder(
    val mBinding: LogItemSuccessBinding,
    val listener: LoggerAdapter.ChildItemClickListener?
) :
    BaseVh<LogViewModel>(mBinding.root), View.OnClickListener {

        //private val listener: LoggerAdapter.ChildItemClickListener

    init {
        mBinding.root.setOnClickListener(this)
        //this.listener = listener

        //Log.d("@hash=" + this.hashCode());
    }


    override fun onClick(v: View) {
        listener?.onClick0(v, adapterPosition) //clicker...
    }

    override fun bind(model: LogViewModel, position: Int) {
        if (model != null) {
            //File file = model.file;
            //String text = String.format(model.file, file.getPath());
            //mBinding.text1.setText(text);
            mBinding.image.setImageResource(model.icon)
            mBinding.text1.setText(model.text)
        }
    }
}
