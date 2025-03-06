package com.walhalla.appextractor.activity.string;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.R;

import com.walhalla.appextractor.activity.manifest.ManifestActivity;
import com.walhalla.appextractor.databinding.TabStringsBinding;
import com.walhalla.appextractor.fragment.BaseFragment;
import com.walhalla.appextractor.fragment.QCallback;
import com.walhalla.appextractor.model.EmptyViewModel;
import com.walhalla.appextractor.model.PackageMeta;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StrFragment extends BaseFragment implements MvpContract.View, StringsAdapter.OnItemClickListener {

    public static final String KEY_OBJ_NAME = StrFragment.class.getSimpleName();
    public static final String KEY_RES_TYPE = "key.var1";

    private PackageMeta meta;
    String resourceType;
    private StringsPresenter presenter;
    private StringsAdapter adapter;
    private TabStringsBinding binding;


    public static StrFragment newInstance(PackageMeta packageInfo, String resourceType) {
        StrFragment f = new StrFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_OBJ_NAME, packageInfo);
        bundle.putString(KEY_RES_TYPE, resourceType);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meta = getArguments().getParcelable(KEY_OBJ_NAME);
            resourceType = getArguments().getString(KEY_RES_TYPE);
        }
        adapter = new StringsAdapter(new ArrayList<>());
        presenter = new StringsPresenter(getActivity(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TabStringsBinding.inflate(inflater, container, false);
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
        if (meta != null && savedInstanceState == null) {
            presenter.loadManifestContent(meta.packageName, resourceType);
        }
    }

    public void showError(CharSequence text, Throwable t) {
        Log.e("ManifestExplorer", text + " : " + ((t != null) ? t.getMessage() : ""));
        Toast.makeText(getActivity(), "Error: " + text + " : " + t, Toast.LENGTH_LONG).show();
    }


    @Override
    public void fab() {

    }


    @Override
    public void showResourceRawText(StringItem resource, String content) {

    }

    @Override
    public void showError(String message) {
        //message
        adapter.swap(new EmptyViewModel(0, message));
    }

    @Override
    public void showSuccess(List<StringItem> list) {
        adapter.swap(list);
    }

    @Override
    public void successToast(int res, String aaa) {

    }

    @Override
    public void errorToast(int res, String aaa) {

    }

    @Override
    public void copyToBuffer(String value) {
        if (callback != null) {
            callback.copyToBuffer(value);
        }
    }

    @Override
    public void shareText(String value) {

    }

    @Override
    public void xmlViewerRequest(String value) {
        //Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
        ManifestActivity.newIntent(getActivity(), meta, null, value);
    }
}
