package com.walhalla.appextractor.adapter2.viewholders

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.walhalla.appextractor.R
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.sdk.ServiceLine
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class ServiceViewHolder(view: View, private val presenter: AppDetailInfoAdapter) :
    ExpandableAdapter.ViewHolder(view) {
    private val icon: ImageView = view.findViewById(R.id.icon)
    val class_name: TextView = view.findViewById(R.id.className)

    val label: TextView = view.findViewById(R.id.activityLabel)

    private val layout: ViewGroup = view.findViewById(R.id.lLayout1)
    private val launch: Button = view.findViewById(R.id.launchActivity)

    fun bind(`object`: ServiceLine, position: Int) {
        if (position % 2 > 0) {
            layout.setBackgroundColor(Color.WHITE)
        }
        icon.setImageDrawable(`object`.icon)
        label.text = `object`.label
        class_name.text = `object`.class_name

        //this.text2.setBackgroundColor(Color.YELLOW);
        if (`object`.exported) {
            launch.visibility = View.VISIBLE
            launch.setOnClickListener { v: View? ->
                presenter.onLaunchService(`object`.class_name)
            }
        }
    }
}