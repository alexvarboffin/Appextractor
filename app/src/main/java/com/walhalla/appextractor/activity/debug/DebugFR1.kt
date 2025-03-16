package com.walhalla.appextractor.activity.debug;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.AppListAdapterCallback;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.detail.DetailsF0;
import com.walhalla.appextractor.adapter.ApkListAdapter;
import com.walhalla.appextractor.adapter2.base.ViewModel;
import com.walhalla.appextractor.databinding.DebugListBinding;
import com.walhalla.appextractor.fragment.BaseFragment;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.appextractor.presenter.MetaPresenter;
import com.walhalla.ui.DLog;

import java.util.List;

import pokercc.android.expandablerecyclerview.ExpandableRecyclerView;

public class DebugFR1 extends BaseFragment implements AppListAdapterCallback {

    private static final String ARG_PARAM1 = "param1";

    private MetaPresenter mPresenter;
    private DebugListBinding binding;


    public static DebugFR1 newInstance() {
        return new DebugFR1();
    }

    private ApkListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new ApkListAdapter(this, getContext());
        binding = DebugListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        this.binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.binding.recyclerView.setAdapter(adapter);
//        mPresenter = new MetaPresenter(context, meta, this);
//        mPresenter.doStuff(context);
        DLog.d("@@@@@@@@");
    }

    public void setData(List<PackageMeta> nn) {
        if (MNonNull(adapter)) {
            adapter.addAll0(nn);
        }
    }

    private boolean MNonNull(ApkListAdapter adapter) {
        boolean b = adapter == null;
        DLog.d("@@@" + b);
        return !b;
    }

    @Override
    public void fab() {

    }

    @Override
    public void nowExtractOneSelected(List<PackageMeta> info, String[] appName) {

    }

    @Override
    public void openPackageOnGooglePlay(String packageName) {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void launchApp(Context context, String packageName) {

    }

    @Override
    public void uninstallApp(String packageName) {

    }

    @Override
    public void count(int size) {

    }

    @Override
    public void shareToOtherApp(String generate_app_name) {

    }

    @Override
    public void menuExtractSelected(View v) {

    }

    @Override
    public void saveIconRequest(PackageMeta packageInfo) {

    }

    @Override
    public void exportIconRequest(PackageMeta packageInfo) {

    }

    @Override
    public void successMessage(String message) {

    }


}
