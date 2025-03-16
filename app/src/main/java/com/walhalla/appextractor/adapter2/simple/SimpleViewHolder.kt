package com.walhalla.appextractor.adapter2.simple;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.AppDetailInfoAdapter;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class SimpleViewHolder extends
        //RecyclerView.ViewHolder
        ExpandableAdapter.ViewHolder {

    private final ViewGroup layout;
    private final View btn;
    private final AppDetailInfoAdapter presenter;

    public void bind(SimpleLine object, int position) {
        if (position % 2 > 0) {
            this.layout.setBackgroundColor(Color.WHITE);
        }
        this.text1.setText(object.res0);
        this.text2.setText(object.value);
        btn.setOnClickListener(v -> {
            presenter.onItemClicked(v, object);
        });
    }

    private final TextView text1;
    private final TextView text2;

    public SimpleViewHolder(View view, AppDetailInfoAdapter presenter) {
        super(view);
        text1 = view.findViewById(R.id.text1);
        text2 = view.findViewById(R.id.text2);
        layout = view.findViewById(R.id.lLayout1);
        btn = view.findViewById(R.id.searchBtn);
        this.presenter = presenter;
    }

}
