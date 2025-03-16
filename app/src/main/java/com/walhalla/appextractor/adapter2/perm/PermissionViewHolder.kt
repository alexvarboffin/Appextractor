package com.walhalla.appextractor.adapter2.perm

import android.graphics.Color
import android.view.View
import com.walhalla.appextractor.R
import com.walhalla.appextractor.databinding.ItemLinePermissionBinding
import com.walhalla.appextractor.sdk.PermissionLine
import com.walhalla.appextractor.utils.PermissionUtils.protectionLevelToString
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class PermissionViewHolder(
    val binding: ItemLinePermissionBinding,
    private val presenter: PermissionViewHolderCallback
) :
    ExpandableAdapter.ViewHolder(binding.root) {
    interface PermissionViewHolderCallback {
        fun onItemClicked(`object`: PermissionLine)
    }

    fun bindPermItem(`object`: PermissionLine, position: Int) {
        if (position % 2 > 0) {
            binding.lLayout1.setBackgroundColor(Color.WHITE)
        }
        if (`object` != null) {
            binding.text1.text = `object`.res0
            val n = if (`object`.isGranted) R.drawable.ic_granted else R.drawable.ic_denied
            binding.lock.setImageResource(n)


            val mm = protectionLevelToString(`object`.protectionLevel)
            binding.protectionLevel.text = mm
        }

        binding.searchBtn.setOnClickListener { v: View? ->
            presenter.onItemClicked(`object`)
        }
    }
}
