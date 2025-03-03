package com.walhalla.appextractor.activity.string;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.BaseUtilsCallback;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.resources.ResItem;
import com.walhalla.appextractor.activity.resources.ResType;

import com.walhalla.appextractor.adapter.LogErrorViewHolder;
import com.walhalla.appextractor.adapter.viewholder.EmptyViewHolder;
import com.walhalla.appextractor.adapter.viewholder.RecyclerViewSimpleTextViewHolder;
import com.walhalla.appextractor.databinding.ItemLoggerEmptyBinding;
import com.walhalla.appextractor.databinding.ItemLoggerErrorBinding;
import com.walhalla.appextractor.databinding.ItemStringBinding;
import com.walhalla.appextractor.model.EmptyViewModel;
import com.walhalla.appextractor.model.LErrorViewModel;

import com.walhalla.appextractor.model.ViewModel;

import java.lang.reflect.Field;
import java.util.List;

public class StringsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public interface OnItemClickListener extends BaseUtilsCallback {
        void xmlViewerRequest(String value);
    }

    private final List<ViewModel> items;
    private OnItemClickListener mView;

    public StringsAdapter(List<ViewModel> resources) {
        this.items = resources;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mView = listener;
    }

    @Override
    public int getItemViewType(int position) {
        final ViewModel rrr = items.get(position);
        if (rrr instanceof StringItem) {
            return StringItem.TYPE_ITEM_STRING;
        } else if (rrr instanceof EmptyViewModel) {
            return EmptyViewModel.TYPE_EMPTY;
        } else if (rrr instanceof LErrorViewModel) {
            return LErrorViewModel.TYPE_ERROR;
        }
        return -1;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //context = parent.getContext();
        RecyclerView.ViewHolder holder;


        switch (viewType) {

            case StringItem.TYPE_ITEM_STRING:
                @NonNull ItemStringBinding binding = ItemStringBinding.inflate(inflater, parent, false);
                return new StringsViewHolder(binding);

            case EmptyViewModel.TYPE_EMPTY:
                ItemLoggerEmptyBinding binding0 = ItemLoggerEmptyBinding.inflate(inflater, parent, false);
                return new EmptyViewHolder(binding0, null);

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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {


        switch (viewHolder.getItemViewType()) {

            case StringItem.TYPE_ITEM_STRING:
                StringsViewHolder holder = ((StringsViewHolder) viewHolder);
                StringItem resource = (StringItem) items.get(position);
                binderReceiverView(holder.binding, resource);
                holder.binding.overflowMenu.setOnClickListener(view -> showPopupMenu(view, resource));
                break;

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


    private void showPopupMenu(View v, StringItem resource) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();
        inflater.inflate(R.menu.popup_string_xml, menu);
        Object menuHelper;
        Class<?>[] argTypes;
        try {
            @SuppressLint("DiscouragedPrivateApi") Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
        }
        if (!ResItem.isXml(resource)) {
            menu.findItem(R.id.actionXmlViewer).setVisible(false);
        }
        popup.setOnMenuItemClickListener(menuItem -> {

            int itemId = menuItem.getItemId();
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
            if (itemId == R.id.actionXmlViewer) {
                if (mView != null) {
                    mView.xmlViewerRequest(resource.value);
                }
            } else if (itemId == R.id.actionCopyName) {
                if (mView != null) {
                    mView.copyToBuffer(resource.name);
                }
            } else if (itemId == R.id.actionCopyValue) {
                if (mView != null) {
                    mView.copyToBuffer(resource.value);
                }
            }
            return false;
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void swap(List<StringItem> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    public void swap(ViewModel data) {
        items.clear();
        items.add(data);
        notifyDataSetChanged();
    }

    private void binderReceiverView(ItemStringBinding binding, StringItem var0) {
        binding.name.setText(var0.name);
        binding.value.setText(var0.value);

        if (var0.type == ResType.DIR) {
            binding.icon.setVisibility(View.VISIBLE);
            binding.icon.setImageResource(R.drawable.ic_folder_blue_36dp);
            binding.overflowMenu.setVisibility(View.GONE);
        } else if (var0.type == ResType.XML) {
            binding.icon.setVisibility(View.VISIBLE);
            binding.icon.setImageResource(R.drawable.ic_res_xml);
        }

        else if (var0.type == ResType.STRING) {
            binding.icon.setVisibility(View.VISIBLE);
            binding.icon.setImageResource(R.drawable.file_any_type);
        }


        else {
            if (var0.drawable != null) {
                binding.overflowMenu.setVisibility(View.VISIBLE);
                binding.icon.setVisibility(View.VISIBLE);
                binding.icon.setImageResource(var0.drawable);
            } else {
                binding.overflowMenu.setVisibility(View.VISIBLE);
                //binding.icon.setVisibility(View.GONE);
                //binding.icon.setImageDrawable(null);
                binding.icon.setImageResource(R.drawable.ic_folder_blue_36dp);
            }
        }
//            itemView.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onItemClick(var0);
//                }
//            });

        binding.name.setOnClickListener(v -> {
            if (mView != null) {
                mView.copyToBuffer(var0.name);
            }
        });

        binding.value.setOnClickListener(v -> {
            if (mView != null) {
                mView.copyToBuffer(var0.value);
            }
        });
    }

    public static class StringsViewHolder extends RecyclerView.ViewHolder {

        private final ItemStringBinding binding;

        StringsViewHolder(@NonNull ItemStringBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}