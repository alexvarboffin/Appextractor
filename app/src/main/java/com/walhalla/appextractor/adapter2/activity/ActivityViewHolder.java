package com.walhalla.appextractor.adapter2.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.AppDetailInfoAdapter;
import com.walhalla.appextractor.adapter.BackupAppFeatureAdapter;
import com.walhalla.appextractor.databinding.ItemActivityBinding;
import com.walhalla.appextractor.model.backup.SimpleAppFeature;
import com.walhalla.appextractor.model.common.AppFeature;

import java.util.ArrayList;
import java.util.List;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

public class ActivityViewHolder extends ExpandableAdapter.ViewHolder {

    public final ItemActivityBinding binding;
    private final AppDetailInfoAdapter presenter;
    private BackupAppFeatureAdapter mFeatureAdapter;


    public ActivityViewHolder(ItemActivityBinding binding, AppDetailInfoAdapter presenter) {
        super(binding.getRoot());
        this.binding = binding;
        this.presenter = presenter;
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(itemView.getContext(), FlexDirection.ROW, FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        this.binding.actions.setLayoutManager(layoutManager);

        //featureRecycler.setRecycledViewPool(mFeatureViewPool);
        mFeatureAdapter = new BackupAppFeatureAdapter();
        this.binding.actions.setAdapter(mFeatureAdapter);
        this.binding.actions.setFocusable(false);
    }

    public void bind(ActivityLine object, int position) {
        if (position % 2 > 0) {
            this.binding.lLayout1.setBackgroundColor(Color.WHITE);
        }
        this.binding.icon.setImageDrawable(object.icon);
        this.binding.activityLabel.setText(object.label);
        this.binding.className.setText(object.className);

        //this.text2.setBackgroundColor(Color.YELLOW);
        if (object.exported) {
            this.binding.launchActivity.setVisibility(View.VISIBLE);
            this.binding.launchActivity.setOnClickListener(v -> {
                presenter.onLaunchActivityRequest(object.className);
            });
        }
        mFeatureAdapter.setFeatures(createContextualFeatures(object.intentFilters));
    }

    private List<AppFeature> createContextualFeatures(List<IntentFilter> intentFilters) {
        ArrayList<AppFeature> features = new ArrayList<>();
        if (!intentFilters.isEmpty()) {

            for (IntentFilter filter : intentFilters) {
                StringBuilder sb = new StringBuilder();
                int countActions = filter.countActions();
                int countCategories = filter.countCategories();
                int dataTypes = filter.countDataTypes();

                for (int i = 0; i < countActions; i++) {
                    String action = filter.getAction(i);
                    action = action.replace("android.intent.action.", "");
                    sb.append(action).append(" ");
                }
                for (int i = 0; i < countCategories; i++) {
                    String caregory = filter.getCategory(i);
                    caregory = caregory.replace("android.intent.category.", "");
                    sb.append(caregory).append(" ");
                }

                for (int i = 0; i < dataTypes; i++) {
                    String dataType = filter.getDataType(i);
                    dataType = dataType.replace("android.intent.category.", "");
                    sb.append(dataType).append(" ");
                }
                features.add(new SimpleAppFeature(sb.toString()));
            }

        }
        return features;
    }

}