package com.walhalla.appextractor.adapter2.headerCollapsed;

import android.view.View;
import android.widget.TextView;

import com.walhalla.appextractor.R;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class HeaderCollapsedVH extends
        //RecyclerView.ViewHolder
        ExpandableAdapter.ViewHolder {

    public final TextView textView;

    public HeaderCollapsedVH(View view) {
        super(view);
        textView = view.findViewById(R.id.textView);
    }

    public void bind(HeaderCollapsedObject o) {
        textView.setText(o.title);
        if (o.icon != null) {
            textView.setCompoundDrawablesWithIntrinsicBounds(o.icon, 0, 0, 0);
        }
    }
}