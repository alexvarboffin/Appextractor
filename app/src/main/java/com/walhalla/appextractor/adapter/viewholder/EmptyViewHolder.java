package com.walhalla.appextractor.adapter.viewholder;

import android.view.View;

import com.walhalla.appextractor.databinding.ItemLoggerEmptyBinding;
import com.walhalla.appextractor.model.EmptyViewModel;

public class EmptyViewHolder extends BaseVh<EmptyViewModel> implements View.OnClickListener {

    private LoggerAdapter.ChildItemClickListener listener;
    private final ItemLoggerEmptyBinding mBinding;

    public EmptyViewHolder(ItemLoggerEmptyBinding binding, LoggerAdapter.ChildItemClickListener listener) {
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
    public void bind(EmptyViewModel operation, int position) {
        if (operation != null) {
            mBinding.text1.setText(operation.getText());
            mBinding.image.setImageResource(operation.getIcon());
        }
    }
}
