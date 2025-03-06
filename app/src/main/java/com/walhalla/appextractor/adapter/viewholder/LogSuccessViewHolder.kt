package com.walhalla.appextractor.adapter.viewholder;

import android.view.View;

import com.walhalla.appextractor.databinding.LogItemSuccessBinding;
import com.walhalla.appextractor.model.LSuccessViewModel;

public class LogSuccessViewHolder extends BaseVh<LSuccessViewModel> implements View.OnClickListener {

    private LoggerAdapter.ChildItemClickListener listener;
    public final LogItemSuccessBinding mBinding;

    public LogSuccessViewHolder(LogItemSuccessBinding binding, LoggerAdapter.ChildItemClickListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;
        this.mBinding.getRoot().setOnClickListener(this);
        this.listener = listener;

        //Log.d("@hash=" + this.hashCode());
    }


    @Override
    public void onClick(View v) {
        listener.onClick0(v, getAdapterPosition());//clicker...
    }

    @Override
    public void bind(LSuccessViewModel model, int position) {
        if (model != null) {
            //File file = model.file;
            //String text = String.format(model.file, file.getPath());
            //mBinding.text1.setText(text);
            mBinding.image.setImageResource(model.getIcon());
            mBinding.text1.setText(model.getText());
        }
    }

}
