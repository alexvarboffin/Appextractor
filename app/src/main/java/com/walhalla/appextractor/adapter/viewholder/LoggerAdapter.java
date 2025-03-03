package com.walhalla.appextractor.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.walhalla.appextractor.Util;
import com.walhalla.appextractor.abba.IntentReaper;
import com.walhalla.appextractor.adapter.LogErrorViewHolder;
import com.walhalla.appextractor.databinding.ItemLoggerEmptyBinding;
import com.walhalla.appextractor.databinding.ItemLoggerErrorBinding;
import com.walhalla.appextractor.databinding.ItemLoggerFileBinding;
import com.walhalla.appextractor.model.EmptyViewModel;
import com.walhalla.appextractor.model.LErrorViewModel;
import com.walhalla.appextractor.model.LFileViewModel;
import com.walhalla.appextractor.model.LSuccessViewModel;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.databinding.LogItemSuccessBinding;
import com.walhalla.appextractor.model.ViewModel;
import com.walhalla.ui.DLog;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class LoggerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final IntentReaper reaper;
    private final PackageManager pm;

    private final Callback mmmm;


    public interface ChildItemClickListener {
        void onClick0(View v, int position);

        void removeFileRequest(File file);
    }

    private Context context;


    private final int TYPE_FILE = 2;
    //private final int TYPE_OPERATION = 3;
    private final int TYPE_SUCCESS = 4;


    //private final View.OnClickListener listener;
    private ChildItemClickListener childItemClickListener;

    private final List<ViewModel> items;

//    public LoggerAdapter() {
//        //this.listener = listener;
//    }


    private final FileVh.FileVhCallback _local_listener_ = new FileVh.FileVhCallback() {

        @Override
        public void onClick0(View v, int position) {
//            if (childItemClickListener != null) {
//                childItemClickListener.onClick0(v, position);
//            }

            ViewModel aa = items.get(position);
            if(aa instanceof  LFileViewModel){
                LFileViewModel o = ((LFileViewModel) aa);
                DLog.d("@@@" + o.file.getAbsolutePath());
            }

        }

//        @Override
//        public void removeFileRequest(File file) {
//            if (childItemClickListener != null) {
//                childItemClickListener.removeFileRequest(file);
//            }
//        }
    };

    public LoggerAdapter(Context context, List<ViewModel> items, Callback callback) {
        this.mmmm = callback;
        if (items != null) {
            this.items = items;
        } else {
            this.items = new ArrayList<>();
        }
        this.pm = context.getPackageManager();
        this.reaper = new IntentReaper(context);

        //reaper.makeMimeDir();
        reaper.makeMimeApk();
    }

    public void setChildItemClickListener(ChildItemClickListener listener) {
        childItemClickListener = listener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getID();
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {

//        if (items.get(position) instanceof LogViewModel) {
//            return TYPE_OPERATION;
//        } else
//        if (items.get(position) instanceof LogViewModel) {
//            return TYPE_S;
//        } else

        final ViewModel rrr = items.get(position);
        if (rrr instanceof LSuccessViewModel) {
            return TYPE_SUCCESS;
        } else if (rrr instanceof EmptyViewModel) {
            return EmptyViewModel.TYPE_EMPTY;
        } else if (rrr instanceof LFileViewModel) {
            return TYPE_FILE;
        } else if (rrr instanceof LErrorViewModel) {
            return LErrorViewModel.TYPE_ERROR;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        RecyclerView.ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case TYPE_SUCCESS:
                LogItemSuccessBinding mBinding = LogItemSuccessBinding.inflate(inflater, parent, false);
                return new LogSuccessViewHolder(mBinding, childItemClickListener);

            case TYPE_FILE:
                ItemLoggerFileBinding mBinding0 = ItemLoggerFileBinding.inflate(inflater, parent, false);
                return new FileVh(mBinding0, _local_listener_);

//            case TYPE_OPERATION:
//                ItemLoggerBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_logger, parent, false);
//                return new LogViewHolder(binding, childItemClickListener);

            case EmptyViewModel.TYPE_EMPTY:
                ItemLoggerEmptyBinding binding = ItemLoggerEmptyBinding.inflate(inflater, parent, false);
                return new EmptyViewHolder(binding, childItemClickListener);

            case LErrorViewModel.TYPE_ERROR:
                ItemLoggerErrorBinding b = ItemLoggerErrorBinding.inflate(inflater, parent, false);
                //View v2 = inflater.inflate(R.layout.item_logger_error, parent, false);
                holder = new LogErrorViewHolder(b);
                break;

            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new RecyclerViewSimpleTextViewHolder(v);
                break;
        }
        return holder;
    }


    @SuppressLint("DiscouragedPrivateApi")
    private void showPopupMenu(View v, int adapterPosition) {
        LFileViewModel obj = (LFileViewModel) items.get(adapterPosition);
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();

        String parent_folder = obj.file.getParent();
        try {
            Uri uri = Uri.parse(parent_folder);
            if (Util.check_Android30FileBrowser_AndroidLysesoftFileBrowser(uri, context, pm, false)) {
                MenuItem sub = menu.add(R.string.action_open_file_parent_folder)
                        .setIcon(R.drawable.ic_action_launch_app);
                sub.setOnMenuItemClickListener(item -> {
                    ///storage/emulated/0/Download/APK_BACKUP
                    //SharedObjects.externalMemory().getAbsolutePath()
                    Util.openFolder(context, parent_folder);
                    return false;
                });
            }
        } catch (Exception e) {
            DLog.handleException(e);
        }
//        try {
//            //Uri uri = Uri.parse(apkUri); //<!--- NOT WORK, EMPTY RESULT
//            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", obj.file);
//        } catch (Exception e) {
//            DLog.handleException(e);
//        }
        //Util.installApp(context, mModel.file);

        File file = obj.file;
        reaper.wrapper(menu, file);
        if (file.canRead()) {
            menu.add(0, Menu.FIRST, Menu.NONE, R.string.action_delete_file)
                    .setIcon(R.drawable.ic_action_uninstall_app)
                    .setOnMenuItemClickListener(item -> {
                        childItemClickListener.removeFileRequest(file);
                        return false;
                    });
        }
        inflater.inflate(R.menu.poppup_logger, menu);
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {

        }
        popup.setOnMenuItemClickListener(new MMCL(mmmm, obj, context));
        popup.show();
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {

            case TYPE_SUCCESS:
                LogSuccessViewHolder holder = (LogSuccessViewHolder) viewHolder;
                holder.bind((LSuccessViewModel) items.get(position), position);
                //holder.mBinding.overflowMenu.setOnClickListener(view -> showPopupMenu(view, position));
                break;

            case TYPE_FILE:
                FileVh holder0 = (FileVh) viewHolder;
                holder0.bind((LFileViewModel) items.get(position), position);
                holder0.mBinding.overflowMenu.setOnClickListener(view -> showPopupMenu(view, position));
                break;

//            case TYPE_OPERATION:
//                LogViewHolder vh1 = (LogViewHolder) viewHolder;
//                vh1.bind((LogViewModel) items.get(position), position);
//                break;

            case EmptyViewModel.TYPE_EMPTY:
                EmptyViewHolder vh1 = (EmptyViewHolder) viewHolder;
                vh1.bind((EmptyViewModel) items.get(position), position);
                break;

            case LErrorViewModel.TYPE_ERROR:
                LogErrorViewHolder vh2 = (LogErrorViewHolder) viewHolder;
                vh2.bind((LErrorViewModel) items.get(position), position);
                break;

            default:
                RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) viewHolder;
                vh.bind(items.get(position), position);
                break;
        }
    }


    public void swapList(List<ViewModel> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    public void swap(ViewModel data) {
        items.clear();
        items.add(data);
        notifyDataSetChanged();
    }


    public void add(ViewModel data) {
        items.add(data);
        notifyDataSetChanged();
    }

    public interface Callback {
        void removeFileRequest(File file);
    }


    public static class MMCL implements PopupMenu.OnMenuItemClickListener {


        private final LFileViewModel mModel;
        private final Callback callBack;
        private final Context context;
        private PackageManager pm;


        MMCL(Callback callBack, LFileViewModel category, Context context) {
            this.mModel = category;
            this.callBack = callBack;
            this.context = context;

        }


        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
//            if (menuItem.getItemId() == R.id.action_delete_file) {
//                callBack.removeFileRequest(mModel.file);
//            }
            return false;
        }
    }
}

