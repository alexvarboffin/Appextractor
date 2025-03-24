package com.walhalla.appextractor.adapter2.viewholders

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.walhalla.appextractor.R

import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.sdk.SimpleLine
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class SimpleViewHolder(view: View, private val presenter: AppDetailInfoAdapter) :
    ExpandableAdapter.ViewHolder(view) {
    private val layout: ViewGroup = view.findViewById(R.id.lLayout1)
    private val btn: View = view.findViewById(R.id.searchBtn)

    fun bind(o: SimpleLine, position: Int) {
        if (position % 2 > 0) {
            layout.setBackgroundColor(Color.WHITE)
        }
        text1.setText(o.name)
        text2.text = o.value
        btn.setOnClickListener { v: View? ->
            presenter.onItemClicked(v, o)
        }
    }

    private val text1: TextView = view.findViewById(R.id.text1)
    private val text2: TextView = view.findViewById(R.id.text2)
}
