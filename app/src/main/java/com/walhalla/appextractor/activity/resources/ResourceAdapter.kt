package com.walhalla.appextractor.activity.resources

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.resources.ResourceAdapter.ResourceViewHolder
import com.walhalla.appextractor.databinding.ItemResourceBinding
import com.walhalla.appextractor.activity.assets.OnResourceItemClickListener
import com.walhalla.appextractor.activity.assets.ResItem
import com.walhalla.appextractor.activity.assets.ResType

class ResourceAdapter(private val items: MutableList<ResItem>) : RecyclerView.Adapter<ResourceViewHolder>() {

    private var mView: OnResourceItemClickListener? = null

    fun setOnItemClickListener(listener: OnResourceItemClickListener?) {
        this.mView = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemResourceBinding.inflate(inflater, parent, false)
        return ResourceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = items[position]
        holder.bind(resource)
        holder.binding.overflowMenu.setOnClickListener { view: View ->
            showPopupMenu(
                view,
                position
            )
        }
    }

    private fun showPopupMenu(v: View, adapterPosition: Int) {
        val resource = items[adapterPosition]
        val popup = PopupMenu(v.context, v)
        val inflater = popup.menuInflater
        val menu = popup.menu

        inflater.inflate(R.menu.abc_popup_resource_image, menu)
        val menuHelper: Any
        val argTypes: Array<Class<*>?>
        try {
            val fMenuHelper = PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.isAccessible = true
            menuHelper = fMenuHelper[popup]
            argTypes = arrayOf(Boolean::class.javaPrimitiveType)
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
                .invoke(menuHelper, true)
        } catch (e: Exception) {
        }

        if (ResItem.isImages(resource)) {
            menu.findItem(R.id.action_read_file).setVisible(false)
        }

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            val itemId = menuItem.itemId
            //                case R.id.action_copy_package_name:
//                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                    if (clipboard != null) {
//                        ClipData clip = ClipData.newPlainText("packageName", "" + packageInfo.packageName);
//                        clipboard.setPrimaryClip(clip);
//                        if (mView != null) {
//                            mView.successMessage(context.getString(R.string.copied_to_clipboard).toUpperCase());
//                            DLog.d("" + packageInfo.packageName);
//                        }
//                    }
//                    return true;
            if (itemId == R.id.action_save_icon) {
                if (mView != null) {
                    mView!!.saveIconRequest(resource)
                }
            } else if (itemId == R.id.action_share_icon) {
                if (mView != null) {
                    mView!!.exportIconRequest(resource)
                }
            } else if (itemId == R.id.action_read_file) {
                if (mView != null) {
                    mView!!.readAssetRequest(resource)
                }
            } else if (itemId == R.id.action_zip_all_assets) {
                if (mView != null) {
                    mView!!.zipAllAssetsRequest(resource)
                }
            }
            false
        }
        popup.show()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun swap(list: List<ResItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ResourceViewHolder internal constructor(internal val binding: ItemResourceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(resource: ResItem) {
            binding.resourceText.text = resource.fullPath
            if (resource.type === ResType.Dir) {
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
//            itemView.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onItemClick(resource);
//                }
//            });
        }
    }

}
