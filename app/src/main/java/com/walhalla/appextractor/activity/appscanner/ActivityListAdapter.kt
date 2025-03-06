package com.walhalla.appextractor.activity.appscanner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.walhalla.appextractor.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ActivityViewHolder> {

    private List<ResolveInfo> mActivityList = new ArrayList<>();

    public void setActivityList(List<ResolveInfo> activityList) {
        mActivityList = activityList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_list, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ResolveInfo resolveInfo = mActivityList.get(position);
        holder.bind(resolveInfo);
    }

    @Override
    public int getItemCount() {
        return mActivityList.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {

        private final TextView mActivityName;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            mActivityName = itemView.findViewById(R.id.activity_name);
        }

        public void bind(final ResolveInfo info) {
            final String activityName = componentName(info);
            if (info.activityInfo != null) {
                mActivityName.setText("{A} " + activityName);
            }
            if (info.serviceInfo != null) {
                mActivityName.setText("{S} " + activityName);
            }

            itemView.setOnClickListener(view -> launchActivity(view.getContext(), info));
        }

        public static String packageName(ResolveInfo info) {
            String zzz = null;
            if (info.activityInfo != null) {
                zzz = info.activityInfo.packageName;
            }
            if (info.serviceInfo != null) {
                zzz = info.serviceInfo.packageName;
            }
            return zzz;
        }

        public static String componentName(ResolveInfo info) {
            String zzz = null;
            if (info.activityInfo != null) {
                zzz = info.activityInfo.name;
            }
            if (info.serviceInfo != null) {
                zzz = info.serviceInfo.name;
            }
            return zzz;
        }

        private void launchActivity(@NonNull Context context, ResolveInfo info) {


            if (info.activityInfo != null) {
                Intent launchIntent = new Intent(Intent.ACTION_MAIN);
                launchIntent.setClassName(packageName(info), componentName(info));
                context.startActivity(launchIntent);
            }
            if (info.serviceInfo != null) {
                try {
                    Intent launchIntent = new Intent(Intent.ACTION_MAIN);
                    launchIntent.setClassName(packageName(info), componentName(info));
                    context.startService(launchIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "" + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
