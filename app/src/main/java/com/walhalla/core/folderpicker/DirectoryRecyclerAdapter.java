//
//
//package com.walhalla.core.folderpicker;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import com.walhalla.appextractor.R;
//import com.walhalla.appextractor.databinding.ContentDirectoryChooserBinding;
//import com.walhalla.ui.DLog;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by vijai on 01-12-2016.
// */
//
//public class DirectoryRecyclerAdapter
//        extends RecyclerView.Adapter<DirectoryRecyclerAdapter.ItemViewHolder> {
//
//    private final OnDirectoryClickedListerner onDirectoryClickedListerner;
//    private Context context;
//    private List<Object> arr;
//
//    public DirectoryRecyclerAdapter(Context context, OnDirectoryClickedListerner listerner, ArrayList<Object> directories) {
//        this.context = context;
//        this.onDirectoryClickedListerner = listerner;
//        this.arr = directories;
//    }
//
//    public DirectoryRecyclerAdapter(Context context, OnDirectoryClickedListerner listerner) {
//        this.context = context;
//        this.onDirectoryClickedListerner = listerner;
//    }
//
//    @NonNull
//    @Override
//    public DirectoryRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ContentDirectoryChooserBinding mBinding;
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        mBinding = ContentDirectoryChooserBinding.inflate(inflater);
//        return new ItemViewHolder(mBinding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final DirectoryRecyclerAdapter.ItemViewHolder holder, int position) {
//
//        Object obj = arr.get(position);
//        if (obj instanceof File) {
//            File file = (File) obj;
//            holder.mBinding.directory.setText(file.getName());
//            holder.mBinding.directory.setOnClickListener(view -> {
//                DLog.d("Item clicked: " + arr.get(holder.getAdapterPosition()));
//                onDirectoryClickedListerner.OnDirectoryClicked((File) arr.get(holder.getAdapterPosition()));
//            });
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return (arr == null) ? 0 : arr.size();
//    }
//
//    public void swap(List<File> directories) {
//        if (arr == null) {
//            arr = new ArrayList<>();
//        } else {
//            arr.clear();
//        }
//        arr.addAll(directories);
//        notifyDataSetChanged();
//    }
//
//    public interface OnDirectoryClickedListerner {
//        void OnDirectoryClicked(File directory);
//    }
//
//    static class ItemViewHolder extends RecyclerView.ViewHolder {
//
//        private ContentDirectoryChooserBinding mBinding;
//
//        ItemViewHolder(ContentDirectoryChooserBinding binding) {
//            super(binding.getRoot());
//            mBinding = binding;
//        }
//    }
//}
