package com.walhalla.appextractor.adapter2.infoapk;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.AppDetailInfoAdapter;
import com.walhalla.appextractor.adapter2.dirline.DirLine;
import com.walhalla.appextractor.databinding.ItemInfoApkBinding;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class InfoApkHolder extends ExpandableAdapter.ViewHolder {


    public final ItemInfoApkBinding binding;
    //private final AppDetailInfoAdapter presenter;

    public void bind(InfoApkLine object, int position) {
        if (position % 2 > 0) {
            binding.lLayout1.setBackgroundColor(Color.WHITE);
        }
        binding.text1.setText(object.name);
        binding.text2.setText(object.value);

//        Drawable img = ResourcesCompat.getDrawable(binding.text1.getContext().getResources(), object.icon, null);
//        binding.text1.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        binding.pathIcon.setImageResource(object.icon);

        //this.text2.setBackgroundColor(Color.YELLOW);
//        binding.searchBtn.setOnClickListener(v -> {
//            //presenter.onItemClicked(v, object);
//        });

        //@@
    }

    public InfoApkHolder(ItemInfoApkBinding binding, AppDetailInfoAdapter presenter) {
        super(binding.getRoot());
        this.binding = binding;

        //this.presenter = presenter;
    }
}
