package com.walhalla.appextractor.adapter2.dirline;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.AppDetailInfoAdapter;
import com.walhalla.appextractor.databinding.ItemDirBinding;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class DirViewHolder extends ExpandableAdapter.ViewHolder {

    public final ItemDirBinding binding;
    //private final AppDetailInfoAdapter presenter;

    public void bind(DirLine object, int position) {
        if (position % 2 > 0) {
            this.binding.lLayout1.setBackgroundColor(Color.WHITE);
        }
        this.binding.text1.setText(object.name);
        this.binding.text2.setText(object.value);


//        Drawable img = ResourcesCompat.getDrawable(this.binding.text1.getContext().getResources(), object.icon, null);
//        this.binding.text1.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        binding.pathIcon.setImageResource(object.icon);

        //this.text2.setBackgroundColor(Color.YELLOW);
        binding.searchBtn.setOnClickListener(v -> {
            //presenter.onItemClicked(v, object);
        });
    }

    public DirViewHolder(ItemDirBinding binding, AppDetailInfoAdapter presenter) {
        super(binding.getRoot());
        this.binding = binding;
        //this.presenter = presenter;
    }
}
