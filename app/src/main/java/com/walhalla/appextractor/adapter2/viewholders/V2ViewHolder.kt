package com.walhalla.appextractor.adapter2.viewholders

import android.graphics.Color
import android.view.View
import com.walhalla.appextractor.R
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter.DetailAdapterCallback
import com.walhalla.appextractor.databinding.ItemV2LineBinding
import com.walhalla.appextractor.sdk.V2Line
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class V2ViewHolder //this.presenter = presenter;
    (private val bind: ItemV2LineBinding, presenter: AppDetailInfoAdapter?) :
    ExpandableAdapter.ViewHolder(bind.root) {
    //private final AppDetailInfoAdapter presenter;
    fun bind(`object`: V2Line, position: Int, clb: DetailAdapterCallback?) {
        if (position % 2 > 0) {
            bind.lLayout1.setBackgroundColor(Color.WHITE)
        }
        bind.text1.text = `object`.key
        bind.text2.text = `object`.value
        if (`object`.drawable == null) {
            bind.icon.setImageResource(R.drawable.ic_item_key)
        } else {
            bind.icon.setImageResource(`object`.drawable!!)
        }

        //this.text2.setBackgroundColor(Color.YELLOW);
        bind.searchBtn.setOnClickListener { v: View? -> }

        bind.lLayout1.setOnClickListener { v: View? ->
            clb?.shareText(
                """
                    ${`object`.key}
                    ${`object`.value}
                    """.trimIndent()
            )
        }
        bind.text2.setOnClickListener { v: View? ->
            clb?.shareText(`object`.value)
        }
    }
}
