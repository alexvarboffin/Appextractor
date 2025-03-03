package com.walhalla.appextractor.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.model.ViewModel;

public abstract class BaseVh<T extends ViewModel> extends RecyclerView.ViewHolder {

    public BaseVh(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(T viewModel, int position);
}
