package com.walhalla.appextractor.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.assets.OnResourceItemClickListener
import com.walhalla.appextractor.activity.assets.ResItem
import com.walhalla.appextractor.activity.assets.ResType
import com.walhalla.appextractor.databinding.ItemResourceBinding
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class ExpandableResourceAdapter(private val items: MutableList<ResItem>) :
    ExpandableAdapter<ExpandableResourceAdapter.ResourceViewHolder>() {

    private var mView: OnResourceItemClickListener? = null

    fun setOnItemClickListener(listener: OnResourceItemClickListener?) {
        this.mView = listener
    }

    override fun getGroupCount(): Int = items.count { it.type == ResType.Dir }

    override fun getChildCount(groupPosition: Int): Int {
        val dirItems = items.filter { it.type == ResType.Dir }
        if (groupPosition >= dirItems.size) return 0

        val dirPath = dirItems[groupPosition].fullPath
        return items.count { it.type != ResType.Dir && it.fullPath.startsWith(dirPath) }
    }

    override fun onCreateGroupViewHolder(viewGroup: ViewGroup, viewType: Int): ResourceViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemResourceBinding.inflate(inflater, viewGroup, false)
        return ResourceViewHolder(binding)
    }

    override fun onCreateChildViewHolder(viewGroup: ViewGroup, viewType: Int): ResourceViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemResourceBinding.inflate(inflater, viewGroup, false)
        return ResourceViewHolder(binding)
    }

    override fun onGroupViewHolderExpandChange(
        holder: ResourceViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {

    }

    override fun onBindChildViewHolder(
        holder: ResourceViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val dirItems = items.filter { it.type == ResType.Dir }
        val dirPath = dirItems[groupPosition].fullPath
        val childItems = items.filter { it.type != ResType.Dir && it.fullPath.startsWith(dirPath) }
        val resource = childItems[childPosition]

        holder.bind(resource)
        holder.binding.overflowMenu.setOnClickListener { view ->
            showPopupMenu(view, resource)
        }
    }

    override fun onBindGroupViewHolder(
        holder: ResourceViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        val dirItems = items.filter { it.type == ResType.Dir }
        val resource = dirItems[groupPosition]
        holder.bind(resource)
    }

    private fun showPopupMenu(v: View, resource: ResItem) {
        val popup = PopupMenu(v.context, v)
        val inflater = popup.menuInflater
        val menu = popup.menu

        inflater.inflate(R.menu.abc_popup_resource_image, menu)

        try {
            val fMenuHelper = PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.isAccessible = true
            val menuHelper = fMenuHelper[popup]
            val argTypes = arrayOf(Boolean::class.javaPrimitiveType)
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
                .invoke(menuHelper, true)
        } catch (e: Exception) {
        }

        if (ResItem.isImages(resource)) {
            menu.findItem(R.id.action_read_file).isVisible = false
        }
        menu.findItem(R.id.actionXmlViewer).isVisible = false

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_save_icon -> mView?.saveIconRequest(resource)
                R.id.action_share_icon -> mView?.exportIconRequest(resource)
                R.id.action_read_file -> mView?.readAssetRequest(resource)
                R.id.action_zip_all_assets -> mView?.zipAllAssetsRequest(resource)
                R.id.actionXmlViewer -> mView?.xmlViewerRequest(resource.fullPath)
            }
            false
        }
        popup.show()
    }

    fun swap(list: List<ResItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ResourceViewHolder(val binding: ItemResourceBinding) : ViewHolder(binding.root) {
        fun bind(resource: ResItem) {
            binding.resourceText.text = resource.fullPath
            if (resource.type == ResType.Dir) {
                binding.icon0.visibility = View.VISIBLE
                binding.icon0.setImageResource(R.drawable.ic_folder_blue_36dp)
                binding.overflowMenu.visibility = View.GONE
                binding.icon1.visibility = View.GONE
            } else {
                if (resource.drawable != null) {
                    binding.overflowMenu.visibility = View.VISIBLE
                    binding.icon1.visibility = View.VISIBLE
                    binding.icon1.setImageDrawable(resource.drawable)
                    binding.icon0.visibility = View.VISIBLE
                } else {
                    binding.overflowMenu.visibility = View.VISIBLE
                    binding.icon1.visibility = View.GONE
                    binding.icon1.setImageDrawable(null)
                }
            }
        }
    }
} 