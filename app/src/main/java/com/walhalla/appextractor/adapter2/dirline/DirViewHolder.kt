package com.walhalla.appextractor.adapter2.dirline

import android.graphics.Color
import android.view.View
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.databinding.ItemDirBinding
import com.walhalla.appextractor.sdk.DirLine
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class DirViewHolder //this.presenter = presenter;
    (val binding: ItemDirBinding, presenter: AppDetailInfoAdapter?) :
    ExpandableAdapter.ViewHolder(binding.root) {
    //private final AppDetailInfoAdapter presenter;
    fun bind(`object`: DirLine, position: Int) {
        if (position % 2 > 0) {
            binding.lLayout1.setBackgroundColor(Color.WHITE)
        }
        binding.text1.text = `object`.name
        binding.text2.text = `object`.value


        //        Drawable img = ResourcesCompat.getDrawable(this.binding.text1.getContext().getResources(), object.icon, null);
//        this.binding.text1.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        binding.pathIcon.setImageResource(`object`.icon)

        //this.text2.setBackgroundColor(Color.YELLOW);
        binding.searchBtn.setOnClickListener { v: View? -> }
    }
}
