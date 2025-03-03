package com.walhalla.appextractor.adapter;

import com.walhalla.appextractor.databinding.ItemLoggerErrorBinding;
import com.walhalla.appextractor.model.LErrorViewModel;
import com.walhalla.appextractor.adapter.viewholder.BaseVh;

public class LogErrorViewHolder extends BaseVh<LErrorViewModel> {

    private final ItemLoggerErrorBinding binding;

    public LogErrorViewHolder(ItemLoggerErrorBinding v2) {
        super(v2.getRoot());
        this.binding = v2;
    }

    @Override
    public void bind(LErrorViewModel a, int position) {
        binding.image.setImageResource(a.getIcon());
        binding.text1.setText(a.getText());
    }
}