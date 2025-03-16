package com.walhalla.appextractor.activity.resources.p002;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.BuildConfig;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.resources.ResItem;
import com.walhalla.appextractor.activity.resources.ResourceAdapter;
import com.walhalla.appextractor.databinding.ActivityResourcesBinding;
import com.walhalla.appextractor.fragment.BaseFragment;
import com.walhalla.appextractor.model.PackageMeta;

import java.util.ArrayList;
import java.util.List;

public class ResourcesFragment extends BaseFragment implements ManifestContract2.View {

    private static final String ARG_PARAM1 = "key_arg_0";
    private ResourceAdapter adapter;
    private ManifestContract2.Presenter presenter;
    private PackageMeta meta;
    private ActivityResourcesBinding binding;


    public static ResourcesFragment newInstance(PackageMeta meta) {
        ResourcesFragment fragment = new ResourcesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, meta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ResourceAdapter(new ArrayList<>());
        presenter = new ManifestPresenter2(getActivity(), this);

        if (getArguments() != null && getArguments().containsKey(ARG_PARAM1)) {
            meta = getArguments().getParcelable(ARG_PARAM1);
        }
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

        adapter.setOnItemClickListener(new ResourceAdapter.ResourceViewHolder.OnItemClickListener() {
            @Override
            public void readAssetRequest(ResItem resource) {
            }

            @Override
            public void saveIconRequest(ResItem resource) {

            }

            @Override
            public void exportIconRequest(ResItem resource) {

            }

            @Override
            public void zipAllAssetsRequest(ResItem resource) {

            }
        });

    }

    @Override
    public void fab() {

    }

    @Override
    public void showManifestContent(String content) {
        //textView.setText(content);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccess(List<ResItem> list) {
        adapter.swap(list);
    }
}
