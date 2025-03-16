package com.walhalla.appextractor.adapter2.infoapk

import android.graphics.Color
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.databinding.ItemInfoApkBinding
import com.walhalla.appextractor.sdk.InfoApkLine
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class InfoApkHolder //this.presenter = presenter;
    (val binding: ItemInfoApkBinding, presenter: AppDetailInfoAdapter?) :
    ExpandableAdapter.ViewHolder(binding.root) {
    //private final AppDetailInfoAdapter presenter;
    fun bind(`object`: InfoApkLine, position: Int) {
        if (position % 2 > 0) {
            binding.lLayout1.setBackgroundColor(Color.WHITE)
        }
        binding.text1.text = `object`.name
        binding.text2.text = `object`.value

        //        Drawable img = ResourcesCompat.getDrawable(binding.text1.getContext().getResources(), object.icon, null);
//        binding.text1.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        binding.pathIcon.setImageResource(`object`.icon)

        //this.text2.setBackgroundColor(Color.YELLOW);
//        binding.searchBtn.setOnClickListener(v -> {
//            //presenter.onItemClicked(v, object);
//        });

        //@@
    }
}
