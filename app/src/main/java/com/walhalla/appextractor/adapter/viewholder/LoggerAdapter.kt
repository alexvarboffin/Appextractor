package com.walhalla.appextractor.adapter.viewholder

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.Util
import com.walhalla.appextractor.abba.IntentReaper
import com.walhalla.appextractor.databinding.ItemLoggerEmptyBinding
import com.walhalla.appextractor.databinding.ItemLoggerErrorBinding
import com.walhalla.appextractor.databinding.ItemLoggerFileBinding
import com.walhalla.appextractor.databinding.LogItemSuccessBinding

import com.walhalla.appextractor.model.LFileViewModel
import com.walhalla.appextractor.model.LogType
import com.walhalla.appextractor.model.LogViewModel
import com.walhalla.appextractor.model.ViewModel
import com.walhalla.ui.DLog
import java.io.File


class LoggerAdapter(context: Context, items: MutableList<LogViewModel>, private val mmmm: Callback) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val reaper: IntentReaper
    private val pm: PackageManager


    interface ChildItemClickListener {
        fun onClick0(v: View, position: Int)

        fun removeFileRequest(file: File)
    }

    private var context: Context? = null


//    private val TYPE_FILE = 2
//    private val TYPE_SUCCESS = 4

    //private final int TYPE_OPERATION = 3;


    //private final View.OnClickListener listener;
    private var childItemClickListener: ChildItemClickListener? = null

    private var items: MutableList<LogViewModel>? = null


    //    public LoggerAdapter() {
    //        //this.listener = listener;
    //    }
    private val _local_listener_: FileVh.FileVhCallback = object : FileVh.FileVhCallback {
        override fun onClick0(v: View, position: Int) {
//            if (childItemClickListener != null) {
//                childItemClickListener.onClick0(v, position);
//            }

            val model = items[position]
            if (model is LFileViewModel) {
                val o: LFileViewModel = (model as LFileViewModel)
                DLog.d("@@@" + o.file.getAbsolutePath())
            }
        }
        //        @Override
        //        public void removeFileRequest(File file) {
        //            if (childItemClickListener != null) {
        //                childItemClickListener.removeFileRequest(file);
        //            }
        //        }
    }

    init {
        if (items != null) {
            this.items = items
        } else {
            this.items = ArrayList<LogViewModel>()
        }
        this.pm = context.packageManager
        this.reaper = IntentReaper(context)

        //reaper.makeMimeDir();
        reaper.makeMimeApk()
    }

    fun setChildItemClickListener(listener: ChildItemClickListener?) {
        childItemClickListener = listener
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun getItemId(position: Int): Long {
        return items!![position].type.longId()
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    override fun getItemViewType(position: Int): Int {
//        if (items.get(position) instanceof LogViewModel) {
//            return TYPE_OPERATION;
//        } else
//        if (items.get(position) instanceof LogViewModel) {
//            return TYPE_S;
//        } else

        val model = items!![position].id
        return model
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        val holder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            LogType.Success.id -> {
                val mBinding = LogItemSuccessBinding.inflate(inflater, parent, false)
                return LogSuccessViewHolder(mBinding, childItemClickListener)
            }

            LogType.File.id -> {
                val mBinding0 = ItemLoggerFileBinding.inflate(inflater, parent, false)
                return FileVh(mBinding0, _local_listener_)
            }

            LogType.Empty.id -> {
                val binding = ItemLoggerEmptyBinding.inflate(inflater, parent, false)
                return LogEmptyViewHolder(binding, childItemClickListener)
            }

            LogType.Error.id -> {
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


    @SuppressLint("DiscouragedPrivateApi")
    private fun showPopupMenu(v: View, adapterPosition: Int) {
        val obj: LFileViewModel = items!![adapterPosition] as LFileViewModel
        val popup = PopupMenu(v.context, v)
        val inflater = popup.menuInflater
        val menu = popup.menu

        val parent_folder: String = obj.file.getParent()
        try {
            val uri = Uri.parse(parent_folder)
            if (Util.check_Android30FileBrowser_AndroidLysesoftFileBrowser(
                    uri,
                    context!!, pm, false
                )
            ) {
                val sub =
                    menu.add(com.walhalla.appextractor.R.string.action_open_file_parent_folder)
                        .setIcon(com.walhalla.appextractor.R.drawable.ic_action_launch_app)
                sub.setOnMenuItemClickListener { item: MenuItem? ->
                    /**storage/emulated/0/Download/APK_BACKUP */
                    //SharedObjects.externalMemory().getAbsolutePath()
                    Util.openFolder(context!!, parent_folder)
                    false
                }
            }
        } catch (e: Exception) {
            DLog.handleException(e)
        }

        //        try {
//            //Uri uri = Uri.parse(apkUri); //<!--- NOT WORK, EMPTY RESULT
//            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", obj.file);
//        } catch (Exception e) {
//            DLog.handleException(e);
//        }
        //Util.installApp(context, mModel.file);
        val file: File = obj.file
        reaper.wrapper(menu, file)
        if (file.canRead()) {
            menu.add(
                0,
                Menu.FIRST,
                Menu.NONE,
                com.walhalla.appextractor.R.string.action_delete_file
            )
                .setIcon(com.walhalla.appextractor.R.drawable.ic_action_uninstall_app)
                .setOnMenuItemClickListener { item: MenuItem? ->
                    childItemClickListener!!.removeFileRequest(file)
                    false
                }
        }
        inflater.inflate(com.walhalla.appextractor.R.menu.poppup_logger, menu)
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
        popup.setOnMenuItemClickListener(MMCL(mmmm, obj, context))
        popup.show()
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {


        val it: LogViewModel = items!![position]

        when (viewHolder.itemViewType) {
            LogType.Success.id -> {
                val holder = viewHolder as LogSuccessViewHolder
                holder.bind(it, position)
            }

            LogType.File.id -> {
                val holder0 = viewHolder as FileVh
                holder0.bind(it as LFileViewModel, position)
                holder0.mBinding.overflowMenu.setOnClickListener { view: View ->
                    showPopupMenu(
                        view,
                        position
                    )
                }
            }

            LogType.Empty.id -> {
                val vh1 = viewHolder as LogEmptyViewHolder
                vh1.bind(it, position)
            }

            LogType.Error.id -> {
                val holder: LogErrorViewHolder = viewHolder as LogErrorViewHolder
                holder.binding.image.setImageResource(it.icon)
                holder.binding.text1.setText(it.text)
            }

            else -> {
                val vh = viewHolder as RecyclerViewSimpleTextViewHolder
                vh.bind(it, position)
            }
        }
    }


    fun swapList(data: MutableList<ViewModel>) {
        items!!.clear()
        items!! + (data)
        notifyDataSetChanged()
    }

    fun swap(data: LogViewModel) {
        items!!.clear()
        items!!.add(data)
        notifyDataSetChanged()
    }


    fun add(data: LogViewModel) {
        items!!.add(data)
        notifyDataSetChanged()
    }

    interface Callback {
        fun removeFileRequest(file: File)
    }


    class MMCL internal constructor(
        private val callBack: Callback, category: LFileViewModel,
        private val context: Context?
    ) :
        PopupMenu.OnMenuItemClickListener {
        private val mModel: LFileViewModel = category
        private val pm: PackageManager? = null


        @SuppressLint("NonConstantResourceId")
        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
//            if (menuItem.getItemId() == R.id.action_delete_file) {
//                callBack.removeFileRequest(mModel.file);
//            }
            return false
        }
    }
}
