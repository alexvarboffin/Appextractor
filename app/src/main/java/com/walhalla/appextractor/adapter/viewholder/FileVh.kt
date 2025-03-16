package com.walhalla.appextractor.adapter.viewholder;

import android.graphics.Color;
import android.view.View;

import com.walhalla.appextractor.Util;
import com.walhalla.appextractor.databinding.ItemLoggerFileBinding;
import com.walhalla.appextractor.model.LFileViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FileVh extends BaseVh<LFileViewModel>
        implements View.OnClickListener {

    private final FileVhCallback listener;
    public final ItemLoggerFileBinding mBinding;

    public interface FileVhCallback {

        void onClick0(View v, int adapterPosition);
    }

    public FileVh(ItemLoggerFileBinding binding, FileVhCallback listener) {
        super(binding.getRoot());
        this.mBinding = binding;
        this.mBinding.getRoot().setOnClickListener(this);
        this.listener = listener;
        //Log.d("@hash=" + this.hashCode());
    }


    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick0(v, getAdapterPosition());//clicker...
        }
    }

    @Override
    public void bind(LFileViewModel model, int position) {
        if (position % 2 > 0) {
            this.mBinding.getRoot().setBackgroundColor(Color.WHITE);
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
            String size = Util.getFileSizeMegaBytes(model.file);
            long aa = model.file.lastModified();
            mBinding.image.setImageResource(model.icon);
            mBinding.text1.setText(model.getText());
            mBinding.fileSize.setText(size);
            mBinding.text3.setText(getDate(aa));
        }
    }

    public String getDate(long aa) {
        if (aa > 0) {
            try {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//            Date netDate = (new Date(timeStamp));
//            return sdf.format(netDate);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String fut = sdf.format(new Date(aa));
                return "" + fut;
            } catch (Exception ex) {
                return "";
            }
        }
        return "";
    }
}