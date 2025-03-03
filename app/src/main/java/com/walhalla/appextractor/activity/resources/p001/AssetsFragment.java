package com.walhalla.appextractor.activity.resources.p001;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.main.MainActivity;
import com.walhalla.appextractor.activity.resources.ResItem;
import com.walhalla.appextractor.activity.resources.ResourceAdapter;
import com.walhalla.appextractor.databinding.ActivityResourcesBinding;
import com.walhalla.appextractor.fragment.BaseFragment;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.ui.DLog;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AssetsFragment extends BaseFragment implements ManifestContract.View,
        ResourceAdapter.ResourceViewHolder.OnItemClickListener {

    private static final String ARG_PARAM1 = "key_arg_0";
    private ResourceAdapter adapter;
    private ManifestContract.Presenter presenter;
    private PackageMeta meta;
    private ActivityResourcesBinding binding;


    public static AssetsFragment newInstance(PackageMeta meta) {
        AssetsFragment fragment = new AssetsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, meta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meta = getArguments().getParcelable(ARG_PARAM1);
        }
        adapter = new ResourceAdapter(new ArrayList<>());
        presenter = new AssetsPresenter(getActivity(), this);

        if (meta != null && savedInstanceState == null) {
            presenter.loadManifestContent(meta.packageName);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityResourcesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        this.binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.binding.recyclerView.setAdapter(adapter);
        //TextView textView = findViewById(R.id.manifest_content);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void fab() {

    }

    @Override
    public void showResourceRawText(ResItem resource, String content) {

        //html support

        //textView.setText(content);
        //Toast.makeText(getActivity(), ""+content, Toast.LENGTH_SHORT).show();
        if (getActivity() != null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(resource.fullPath)
                    .setMessage(content)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    })
                    //.setNegativeButton(R.string.alert_root_no, (dialog, which) -> DLog.d("cancel"))
                    .show();
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccess(List<ResItem> list) {
        adapter.swap(list);
        DLog.d("@@@@@@@" + list.size());
    }

    @Override
    public void successToast(@StringRes int res, String name) {
        String s = getResources().getString(res) + " " + name + " ✔";
        Toast toast = Toasty.info(getActivity(), s.toUpperCase(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void errorToast(@StringRes int res, String name) {
        String s = getResources().getString(res) + " " + name + " ✘";
        Toast toast = Toasty.error(getActivity(), s.toUpperCase(), Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void readAssetRequest(ResItem resource) {
        presenter.readAssetRequest(getActivity(), resource);
    }

    @Override
    public void saveIconRequest(ResItem resource) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            presenter.saveAsset(getActivity(), resource);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_CODE_SAVE_ICON);
        }
    }

    @Override
    public void exportIconRequest(ResItem resource) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            presenter.exportIconRequest(getActivity(), resource);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 766);
        }
    }

    @Override
    public void zipAllAssetsRequest(ResItem resource) {
        presenter.zipAllAssetsRequest(getActivity(), resource);
    }

}
