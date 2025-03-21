package com.walhalla.appextractor.adapter.viewholder

import android.R
import android.view.View
import android.widget.TextView
import com.walhalla.appextractor.model.ViewModel
import com.walhalla.appextractor.resources.StringItemViewModel


//class RecyclerViewSimpleTextViewHolder(view: View) : BaseVh<StringItemViewModel>(view) {
//    private val text1: TextView = view.findViewById(R.id.text1)
//
//    override fun bind(viewModel: StringItemViewModel, position: Int) {
//        text1.text = viewModel.text
//    }
//}


class RecyclerViewSimpleTextViewHolder(view: View) : BaseVh<ViewModel>(view) {
    private val text1: TextView =
        view.findViewById(R.id.text1)

    override fun bind(viewModel: ViewModel, position: Int) {
        text1.text = viewModel.toString()
    }
}