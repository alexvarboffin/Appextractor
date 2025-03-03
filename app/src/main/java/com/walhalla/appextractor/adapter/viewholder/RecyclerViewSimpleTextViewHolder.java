package com.walhalla.appextractor.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.walhalla.appextractor.model.ViewModel;

public class RecyclerViewSimpleTextViewHolder extends BaseVh {

    private TextView text1;

    public RecyclerViewSimpleTextViewHolder(View view) {
        super(view);
        text1 = view.findViewById(android.R.id.text1);
    }

    @Override
    public void bind(ViewModel viewModel, int position) {
        text1.setText(viewModel.toString());
    }
}