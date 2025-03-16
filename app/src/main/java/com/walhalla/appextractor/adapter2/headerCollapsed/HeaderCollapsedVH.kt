package com.walhalla.appextractor.adapter2.headerCollapsed

import android.view.View
import android.widget.TextView
import com.walhalla.appextractor.R
import com.walhalla.appextractor.sdk.HeaderCollapsedObject
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class HeaderCollapsedVH(view: View) : ExpandableAdapter.ViewHolder(view) {
    val textView: TextView =
        view.findViewById(R.id.textView)

    fun bind(o: HeaderCollapsedObject) {
        textView.text = o.title
        if (o.icon != null) {
            textView.setCompoundDrawablesWithIntrinsicBounds(o.icon, 0, 0, 0)
        }
    }
}