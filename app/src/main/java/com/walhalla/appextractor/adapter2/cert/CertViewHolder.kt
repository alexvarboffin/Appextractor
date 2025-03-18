package com.walhalla.appextractor.adapter2.cert

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.walhalla.appextractor.R
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.sdk.CertLine
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class CertViewHolder(view: View, presenter: AppDetailInfoAdapter?) :
    ExpandableAdapter.ViewHolder(view) {
    private val text1: TextView =
        view.findViewById(R.id.text1)
    val text2: TextView = view.findViewById(R.id.text2)

    private val layout: ViewGroup =
        view.findViewById(R.id.lLayout1)
    private val btn: View = view.findViewById(R.id.searchBtn)

    //private final AppDetailInfoAdapter presenter;
    fun bind(`object`: CertLine, position: Int) {
        if (position % 2 > 0) {
            layout.setBackgroundColor(Color.WHITE)
        }
        text1.text = `object`.res0
        text2.text = `object`.value

        //this.text2.setBackgroundColor(Color.YELLOW);
        btn.setOnClickListener { v: View? -> }
    }

    init {
        //this.presenter = presenter;
    }
}
