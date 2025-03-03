package com.walhalla.appextractor.adapter2.service;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.AppDetailInfoAdapter;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class ServiceViewHolder extends ExpandableAdapter.ViewHolder {

    private final ImageView icon;
    public final TextView class_name;
    public final TextView label;

    private final ViewGroup layout;
    private final Button launch;
    private final AppDetailInfoAdapter presenter;

    public void bind(ServiceLine object, int position) {
        if (position % 2 > 0) {
            this.layout.setBackgroundColor(Color.WHITE);
        }
        this.icon.setImageDrawable(object.icon);
        this.label.setText(object.label);
        this.class_name.setText(object.class_name);

        //this.text2.setBackgroundColor(Color.YELLOW);
       if (object.exported) {
            launch.setVisibility(View.VISIBLE);
            launch.setOnClickListener(v -> {
                presenter.onLaunchService(object.class_name);
            });
        }

    }

    public ServiceViewHolder(View view, AppDetailInfoAdapter presenter) {
        super(view);
        this.icon = view.findViewById(R.id.icon);
        this.label = view.findViewById(R.id.activityLabel);
        this.class_name = view.findViewById(R.id.className);
        this.layout = view.findViewById(R.id.lLayout1);
        this.launch = view.findViewById(R.id.launchActivity);
        this.presenter = presenter;
    }
}