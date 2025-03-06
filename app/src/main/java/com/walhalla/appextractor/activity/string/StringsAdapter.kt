package com.walhalla.appextractor.activity.string

import android.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.BaseUtilsCallback
import com.walhalla.appextractor.activity.resources.ResItem.Companion.isXml
import com.walhalla.appextractor.activity.resources.ResType
import com.walhalla.appextractor.adapter.viewholder.EmptyViewHolder
import com.walhalla.appextractor.adapter.viewholder.LogErrorViewHolder
import com.walhalla.appextractor.adapter.viewholder.RecyclerViewSimpleTextViewHolder
import com.walhalla.appextractor.databinding.ItemLoggerEmptyBinding
import com.walhalla.appextractor.databinding.ItemLoggerErrorBinding
import com.walhalla.appextractor.databinding.ItemStringBinding
import com.walhalla.appextractor.model.LogType
import com.walhalla.appextractor.model.LogViewModel
import com.walhalla.appextractor.model.ViewModel

class StringsAdapter(private val items: MutableList<ViewModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnItemClickListener : BaseUtilsCallback {
        fun xmlViewerRequest(value: String?)
    }

    private var mView: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.mView = listener
    }

    override fun getItemViewType(position: Int): Int {
        val model = items[position]
        if (model is StringItem) {
            return StringItem.TYPE_ITEM_STRING
        } else if (model is LogViewModel) {
            return model.id
        }
        return -1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //context = parent.getContext();
        val holder: RecyclerView.ViewHolder


        when (viewType) {
            StringItem.TYPE_ITEM_STRING -> {
                val binding = ItemStringBinding.inflate(inflater, parent, false)
                return StringsViewHolder(binding)
            }

            EmptyViewModel.TYPE_EMPTY -> {
                val binding0 = ItemLoggerEmptyBinding.inflate(inflater, parent, false)
                return EmptyViewHolder(binding0, null)
            }

            LErrorViewModel.TYPE_ERROR -> {
                val b = ItemLoggerErrorBinding.inflate(inflater, parent, false)
                //View v2 = inflater.inflate(R.layout.item_logger_error, parent, false);
                holder = LogErrorViewHolder(b)
            }

            else -> {
                val v = inflater.inflate(R.layout.simple_list_item_1, parent, false)
                holder = RecyclerViewSimpleTextViewHolder(v)
            }
        }
        return holder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            StringItem.TYPE_ITEM_STRING -> {
                val holder = (viewHolder as StringsViewHolder)
                val resource = items[position] as StringItem
                binderReceiverView(holder.binding, resource)
                holder.binding.overflowMenu.setOnClickListener { view: View ->
                    showPopupMenu(
                        view,
                        resource
                    )
                }
            }

            EmptyViewModel.TYPE_EMPTY -> {
                val vh1 = viewHolder as EmptyViewHolder
                vh1.bind(items[position] as EmptyViewModel, position)
            }

            LErrorViewModel.TYPE_ERROR -> {
                val vh2 = viewHolder as LogErrorViewHolder
                vh2.bind(items[position] as LErrorViewModel, position)
            }

            else -> {
                val vh = viewHolder as RecyclerViewSimpleTextViewHolder
                vh.bind(items[position], position)
            }
        }
    }


    private fun showPopupMenu(v: View, resource: StringItem) {
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
                    mView!!.xmlViewerRequest(resource.value)
                }
            } else if (itemId == com.walhalla.appextractor.R.id.actionCopyName) {
                if (mView != null) {
                    mView!!.copyToBuffer(resource.name)
                }
            } else if (itemId == com.walhalla.appextractor.R.id.actionCopyValue) {
                if (mView != null) {
                    mView!!.copyToBuffer(resource.value)
                }
            }
            false
        }
        popup.show()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun swap(list: List<StringItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun swap(data: ViewModel) {
        items.clear()
        items.add(data)
        notifyDataSetChanged()
    }

    private fun binderReceiverView(binding: ItemStringBinding, var0: StringItem) {
        binding.name.text = var0.name
        binding.value.text = var0.value

        if (var0.type == ResType.DIR) {
            binding.icon.visibility = View.VISIBLE
            binding.icon.setImageResource(com.walhalla.appextractor.R.drawable.ic_folder_blue_36dp)
            binding.overflowMenu.visibility = View.GONE
        } else if (var0.type == ResType.XML) {
            binding.icon.visibility = View.VISIBLE
            binding.icon.setImageResource(com.walhalla.appextractor.R.drawable.ic_res_xml)
        } else if (var0.type == ResType.STRING) {
            binding.icon.visibility = View.VISIBLE
            binding.icon.setImageResource(com.walhalla.appextractor.R.drawable.file_any_type)
        } else {
            if (var0.drawable != null) {
                binding.overflowMenu.visibility = View.VISIBLE
                binding.icon.visibility = View.VISIBLE
                binding.icon.setImageResource(var0.drawable)
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
                mView!!.copyToBuffer(var0.value)
            }
        }
    }

    class StringsViewHolder internal constructor(val binding: ItemStringBinding) :
        RecyclerView.ViewHolder(binding.root)
}