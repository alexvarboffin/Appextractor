package com.walhalla.appextractor.adapter.viewholder

import android.graphics.Color
import android.view.View
import com.walhalla.appextractor.Util.getFileSizeMegaBytes
import com.walhalla.appextractor.databinding.ItemLoggerFileBinding
import com.walhalla.appextractor.model.LFileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileVh(val mBinding: ItemLoggerFileBinding, listener: FileVhCallback?) :
    BaseVh<LFileViewModel>(mBinding.root), View.OnClickListener {
    private val listener: FileVhCallback?

    interface FileVhCallback {
        fun onClick0(v: View, adapterPosition: Int)
    }

    init {
        mBinding.root.setOnClickListener(this)
        this.listener = listener
        //Log.d("@hash=" + this.hashCode());
    }


    override fun onClick(v: View) {
        listener?.onClick0(v, adapterPosition)
    }

    override fun bind(model: LFileViewModel, position: Int) {
        if (position % 2 > 0) {
            mBinding.root.setBackgroundColor(Color.WHITE)
        }


        //        if (position % Const.colors.length > 0) {
//            //@drawable/ic_log_file_bg
//            int jk = position;
//            //if(jk>colors.length){
//            jk = position % Const.colors.length;
//            //}
//            //this.mBinding.imgContainer.setBackgroundColor(itemView.getResources().getColor(Const.colors[jk]));
//        }
        if (model != null) {
            val size = getFileSizeMegaBytes(model.file)
            val aa = model.file.lastModified()
            mBinding.image.setImageResource(model.icon)
            mBinding.text1.text = model.text
            mBinding.fileSize.text = size
            mBinding.text3.text = getDate(aa)
        }
    }

    fun getDate(aa: Long): String {
        if (aa > 0) {
            try {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//            Date netDate = (new Date(timeStamp));
//            return sdf.format(netDate);
                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                val fut = sdf.format(Date(aa))
                return "" + fut
            } catch (ex: Exception) {
                return ""
            }
        }
        return ""
    }
}