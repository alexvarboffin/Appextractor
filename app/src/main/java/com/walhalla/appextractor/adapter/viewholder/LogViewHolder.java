package com.walhalla.appextractor.adapter.viewholder;

import android.view.View;

import com.walhalla.appextractor.databinding.ItemLoggerBinding;

import com.walhalla.appextractor.model.LogViewModel;

public class LogViewHolder extends BaseVh<LogViewModel> implements View.OnClickListener {

    private final LoggerAdapter.ChildItemClickListener listener;
    private final ItemLoggerBinding mBinding;

    public LogViewHolder(ItemLoggerBinding binding, LoggerAdapter.ChildItemClickListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;
        this.mBinding.getRoot().setOnClickListener(this);
        this.listener = listener;

        //Log.d("@hash=" + this.hashCode());
    }


    @Override
    public void onClick(View view) {
        listener.onClick0(view, getAdapterPosition());
    }

    @Override
    public void bind(LogViewModel operation, int position) {
        if (operation != null) {
            mBinding.text1.setText(operation.getText());
            mBinding.imageView1.setImageResource(operation.getIcon());
        }
    }
}
