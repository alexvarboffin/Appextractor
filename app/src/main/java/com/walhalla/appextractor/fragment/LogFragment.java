package com.walhalla.appextractor.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.walhalla.appextractor.BuildConfig;
import com.walhalla.appextractor.Config;
import com.walhalla.appextractor.Troubleshooting;
import com.walhalla.appextractor.model.EmptyViewModel;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.model.LFileViewModel;
import com.walhalla.appextractor.adapter.viewholder.LoggerAdapter;
import com.walhalla.appextractor.databinding.FragmentLogBinding;
import com.walhalla.appextractor.model.ViewModel;
import com.walhalla.ui.DLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogFragment extends Fragment
        implements LoggerAdapter.ChildItemClickListener, LoggerAdapter.Callback {

    private LoggerAdapter aaa;
    private FragmentLogBinding mBinding;

    public static LogFragment newInstance() {
        LogFragment pageFragment = new LogFragment();
//        Bundle arguments = new Bundle();
//        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
//        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (aaa == null) {
            aaa = new LoggerAdapter(getContext(), new ArrayList<>(), this);
        }
        aaa.setChildItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLogBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        mBinding.recyclerView.setLayoutManager(manager);

        if(getContext()!=null){
//            DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//            itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_divider));
//            mBinding.recyclerView.addItemDecoration(itemDecorator);
            mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        }

        //mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        mBinding.recyclerView.setAdapter(aaa);

        //loggerAdapter.swap(new LErrorViewModel(0,""));
        aaa.swap(new EmptyViewModel(R.drawable.ic_main_logo_110, getString(R.string.data_empty)));
        //((MainActivity) getActivity()).setLogTag(getTag());


        //Demo
//        File file = new File("/storage/emulated/0/Download/com.google.android.youtube_v1512439272.apk");
//        DLog.d("File exist? => " + file.exists());
//        DLog.d("File r? => " + file.canRead());
//
//        Uri path = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID
//                + ".fileprovider", file);
//        if (path != null) {
//            String type = getActivity().getContentResolver().getType(path);
//            DLog.d("::TYPE:: " + type);
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.putExtra(Intent.EXTRA_EMAIL, "22@hhhh");
//            intent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(com.walhalla.ui.R.string.app_name));
//            intent.setType(type);
//            //intent.setData(path); //False To:field in gmail
//            intent.putExtra(Intent.EXTRA_STREAM, path);
//            // temp permission for receiving app to read this file
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            getActivity().startActivity(Intent.createChooser(intent, "Choose an app"));
//        }
    }


    @Override
    public void onClick0(View v, int position) {
        DLog.d("");
    }

    public void showData(List<ViewModel> data) {
        aaa.swapList(data);
    }

    public void makeLog(ViewModel message) {

        //DLog.d("@@@@@@@@@@@@@@@@@@@" + (aaa.getItemCount() > 0));
        //DLog.d("@@@@@@@@@@@@@@@@@@@" + (aaa.getItemId(0)));

        if (aaa != null) {
            if (aaa.getItemCount() > 0) {
                if (aaa.getItemId(0) == EmptyViewModel.TYPE_EMPTY) {
                    aaa.swap(message);//Clear and add
                    return;
                }
            }
            aaa.add(message);//add new
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updatelist();
    }

    private void updatelist() {
        List<ViewModel> data = new ArrayList<>();
        File[] aa = Troubleshooting.defLocation()
                .listFiles(pathname ->
                        pathname.getName().endsWith(".apk")
                                ||pathname.getName().startsWith(Config.PREV));
        if (aa != null) {
            for (File file : aa) {
                data.add(new LFileViewModel(
                        file,
                        file.getName(),
                        R.drawable.ic_android)
                );
            }
        }

        if (BuildConfig.DEBUG) {

            File file = new File("/data/app/SmokeTestApp/SmokeTestApp.apk");
            data.add(new LFileViewModel(
                    file,
                    file.getName(),
                    R.drawable.ic_android)
            );
        }
        showData(data);
    }

    @Override
    public void removeFileRequest(File file) {
        if (file.delete()) {
            Toast.makeText(getContext(), "Success!!!", Toast.LENGTH_SHORT).show();
        }
        updatelist();
    }
}
