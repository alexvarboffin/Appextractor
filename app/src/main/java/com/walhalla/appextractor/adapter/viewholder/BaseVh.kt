package com.walhalla.appextractor.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.model.ViewModel

abstract class BaseVh<T : ViewModel>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    abstract fun bind(viewModel: T, position: Int)
}
