package com.walhalla.appextractor.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.BaseUtilsCallback
import com.walhalla.appextractor.resources.ResItem.Companion.isXml
import com.walhalla.appextractor.resources.ResType
import com.walhalla.appextractor.resources.StringItemViewModel
import com.walhalla.appextractor.adapter.viewholder.LogEmptyViewHolder
import com.walhalla.appextractor.adapter.viewholder.ResourceEmptyViewHolder
import com.walhalla.appextractor.adapter.viewholder.LogErrorViewHolder
import com.walhalla.appextractor.adapter.viewholder.RecyclerViewSimpleTextViewHolder
import com.walhalla.appextractor.databinding.ItemLoggerEmptyBinding
import com.walhalla.appextractor.databinding.ItemLoggerErrorBinding
import com.walhalla.appextractor.databinding.ItemStringBinding

class ResourceAdapter(private val items: MutableList<StringItemViewModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener : BaseUtilsCallback {
        fun xmlViewerRequest(value: String?)
    }

    private var mView: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.mView = listener
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].id
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //context = parent.getContext();
        val holder: RecyclerView.ViewHolder


        when (viewType) {
            ResType.StringRes.id -> {
                val binding = ItemStringBinding.inflate(inflater, parent, false)
                return StringsViewHolder(binding)
            }

            ResType.Empty.id -> {
                val binding0 = ItemLoggerEmptyBinding.inflate(inflater, parent, false)
                return LogEmptyViewHolder(binding0, null)
            }

            ResType.Error.id -> {
                val b = ItemLoggerErrorBinding.inflate(inflater, parent, false)
                //View v2 = inflater.inflate(R.layout.item_logger_error, parent, false);
                holder = LogErrorViewHolder(b)
            }

            else -> {
                val v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                holder = RecyclerViewSimpleTextViewHolder(v)
            }
        }
        return holder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val it = items[position]

        when (viewHolder.itemViewType) {
            ResType.StringRes.id -> {
                val holder = (viewHolder as StringsViewHolder)
                val resource = it as StringItemViewModel
                binderReceiverView(holder.binding, resource)
                holder.binding.overflowMenu.setOnClickListener { view: View ->
                    showPopupMenu(
                        view,
                        resource
                    )
                }
            }

            ResType.Empty.id -> {
                val vh1 = viewHolder as ResourceEmptyViewHolder
                vh1.bind(items[position], position)
            }

            ResType.Error.id -> {
                val holder = viewHolder as LogErrorViewHolder
                it.icon?.let { it1 -> holder.binding.image.setImageResource(it1) }
                holder.binding.text1.setText(it.text)
            }

            else -> {
                val vh = viewHolder as RecyclerViewSimpleTextViewHolder
                vh.bind(items[position], position)
            }
        }
    }


    private fun showPopupMenu(v: View, resource: StringItemViewModel) {
        val popup = PopupMenu(v.context, v)
        val inflater = popup.menuInflater
        val menu = popup.menu
        inflater.inflate(com.walhalla.appextractor.R.menu.popup_string_xml, menu)
        val menuHelper: Any
        val argTypes: Array<Class<*>?>
        try {
            @SuppressLint("DiscouragedPrivateApi") val fMenuHelper =
                PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.isAccessible = true
            menuHelper = fMenuHelper[popup]
            argTypes = arrayOf(Boolean::class.javaPrimitiveType)
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
                .invoke(menuHelper, true)
        } catch (e: Exception) {
        }
        if (!isXml(resource)) {
            menu.findItem(com.walhalla.appextractor.R.id.actionXmlViewer).setVisible(false)
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
            if (itemId == com.walhalla.appextractor.R.id.actionXmlViewer) {
                if (mView != null) {
                    mView!!.xmlViewerRequest(resource.text)
                }
            } else if (itemId == com.walhalla.appextractor.R.id.actionCopyName) {
                if (mView != null) {
                    mView!!.copyToBuffer(resource.name)
                }
            } else if (itemId == com.walhalla.appextractor.R.id.actionCopyValue) {
                if (mView != null) {
                    mView!!.copyToBuffer(resource.text)
                }
            }
            false
        }
        popup.show()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun swap(list: List<StringItemViewModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun swap(data: StringItemViewModel) {
        items.clear()
        items.add(data)
        notifyDataSetChanged()
    }

    private fun binderReceiverView(binding: ItemStringBinding, var0: StringItemViewModel) {
        binding.name.text = var0.name
        binding.value.text = var0.text

        if (var0.type == ResType.Dir) {
            binding.icon.visibility = View.VISIBLE
            binding.icon.setImageResource(com.walhalla.appextractor.R.drawable.ic_folder_blue_36dp)
            binding.overflowMenu.visibility = View.GONE
        } else if (var0.type == ResType.Xml) {
            binding.icon.visibility = View.VISIBLE
            binding.icon.setImageResource(com.walhalla.appextractor.R.drawable.ic_res_xml)
        } else if (var0.type == ResType.StringRes) {
            binding.icon.visibility = View.VISIBLE
            binding.icon.setImageResource(com.walhalla.appextractor.R.drawable.file_any_type)
        } else {
            if (var0.icon != null) {
                binding.overflowMenu.visibility = View.VISIBLE
                binding.icon.visibility = View.VISIBLE
                binding.icon.setImageResource(var0.icon!!)
            } else {
                binding.overflowMenu.visibility = View.VISIBLE
                //binding.icon.setVisibility(View.GONE);
                //binding.icon.setImageDrawable(null);
                binding.icon.setImageResource(com.walhalla.appextractor.R.drawable.ic_folder_blue_36dp)
            }
        }

//            itemView.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onItemClick(var0);
//                }
//            });
        binding.name.setOnClickListener { v: View? ->
            if (mView != null) {
                mView!!.copyToBuffer(var0.name)
            }
        }

        binding.value.setOnClickListener { v: View? ->
            if (mView != null) {
                mView!!.copyToBuffer(var0.text)
            }
        }
    }

    class StringsViewHolder internal constructor(val binding: ItemStringBinding) :
        RecyclerView.ViewHolder(binding.root)
}