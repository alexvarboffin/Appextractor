package com.walhalla.appextractor.adapter.viewholder

import com.walhalla.appextractor.databinding.ItemLoggerErrorBinding
import com.walhalla.appextractor.model.LogViewModel

class LogErrorViewHolder(private val binding: ItemLoggerErrorBinding) : BaseVh<LogViewModel>(binding.root) {

    override fun bind(a: LogViewModel, position: Int) {
        binding.image.setImageResource(a.icon)
        binding.text1.setText(a.text)
    }

}