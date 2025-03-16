package com.walhalla.appextractor.adapter2.cert;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter;
import com.walhalla.appextractor.sdk.CertLine;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class CertViewHolder extends ExpandableAdapter.ViewHolder {

    private final TextView text1;
    public final TextView text2;

    private final ViewGroup layout;
    private final View btn;
    //private final AppDetailInfoAdapter presenter;

    public void bind(CertLine object, int position) {
        if (position % 2 > 0) {
            this.layout.setBackgroundColor(Color.WHITE);
        }
        this.text1.setText(object.res0);
        this.text2.setText(object.value);
        //this.text2.setBackgroundColor(Color.YELLOW);

        btn.setOnClickListener(v -> {
            //presenter.onItemClicked(v, object);
        });
    }

    public CertViewHolder(View view, AppDetailInfoAdapter presenter) {
        super(view);
        text1 = view.findViewById(R.id.text1);
        text2 = view.findViewById(R.id.text2);
        layout = view.findViewById(R.id.lLayout1);
        btn = view.findViewById(R.id.searchBtn);
        //this.presenter = presenter;
    }
}
