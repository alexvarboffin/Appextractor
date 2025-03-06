package com.walhalla.appextractor.activity.appscanner

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.R

class ActivityListAdapter : RecyclerView.Adapter<ActivityListAdapter.ActivityViewHolder>() {
    private var mActivityList: List<ResolveInfo> = ArrayList()

    fun setActivityList(activityList: List<ResolveInfo>) {
        mActivityList = activityList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_activity_list, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val resolveInfo = mActivityList[position]
        holder.bind(resolveInfo)
    }

    override fun getItemCount(): Int {
        return mActivityList.size
    }

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mActivityName: TextView =
            itemView.findViewById(R.id.activity_name)

        fun bind(info: ResolveInfo) {
            val activityName = componentName(info)
            if (info.activityInfo != null) {
                mActivityName.text = "{A} $activityName"
            }
            if (info.serviceInfo != null) {
                mActivityName.text = "{S} $activityName"
            }

            itemView.setOnClickListener { view: View ->
                launchActivity(
                    view.context,
                    info
                )
            }
        }

        private fun launchActivity(context: Context, info: ResolveInfo) {
            if (info.activityInfo != null) {
                val launchIntent = Intent(Intent.ACTION_MAIN)
                launchIntent.setClassName(
                    packageName(info)!!,
                    componentName(info)!!
                )
                context.startActivity(launchIntent)
            }
            if (info.serviceInfo != null) {
                try {
                    val launchIntent = Intent(Intent.ACTION_MAIN)
                    launchIntent.setClassName(
                        packageName(info)!!,
                        componentName(info)!!
                    )
                    context.startService(launchIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "" + e.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        companion object {
            fun packageName(info: ResolveInfo): String? {
                var zzz: String? = null
                if (info.activityInfo != null) {
                    zzz = info.activityInfo.packageName
                }
                if (info.serviceInfo != null) {
                    zzz = info.serviceInfo.packageName
                }
                return zzz
            }

            fun componentName(info: ResolveInfo): String? {
                var zzz: String? = null
                if (info.activityInfo != null) {
                    zzz = info.activityInfo.name
                }
                if (info.serviceInfo != null) {
                    zzz = info.serviceInfo.name
                }
                return zzz
            }
        }
    }
}
