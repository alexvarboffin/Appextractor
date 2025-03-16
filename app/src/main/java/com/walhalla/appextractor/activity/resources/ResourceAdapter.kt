package com.walhalla.appextractor.activity.resources;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.databinding.ItemResourceBinding;

import java.lang.reflect.Field;
import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {
    private final List<ResItem> items;
    private ResourceViewHolder.OnItemClickListener mView;

    public ResourceAdapter(List<ResItem> resources) {
        this.items = resources;
    }

    public void setOnItemClickListener(ResourceViewHolder.OnItemClickListener listener) {
        this.mView = listener;
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        @NonNull ItemResourceBinding binding = ItemResourceBinding.inflate(inflater, parent, false);
        return new ResourceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        ResItem resource = items.get(position);
        holder.bind(resource);
        holder.binding.overflowMenu.setOnClickListener(view -> showPopupMenu(view, position));
    }

    private void showPopupMenu(View v, int adapterPosition) {
        ResItem resource = items.get(adapterPosition);
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();

        inflater.inflate(R.menu.abc_popup_resource_image, menu);
        Object menuHelper;
        Class<?>[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {

        }

        if (ResItem.isImages(resource)) {
            menu.findItem(R.id.action_read_file).setVisible(false);
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
            if (itemId == R.id.action_save_icon) {
                if (mView != null) {
                    mView.saveIconRequest(resource);
                }
            } else if (itemId == R.id.action_share_icon) {
                if (mView != null) {
                    mView.exportIconRequest(resource);
                }
            } else if (itemId == R.id.action_read_file) {
                if (mView != null) {
                    mView.readAssetRequest(resource);
                }
            } else if (itemId == R.id.action_zip_all_assets) {
                if (mView != null) {
                    mView.zipAllAssetsRequest(resource);
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

    public void swap(List<ResItem> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    public static class ResourceViewHolder extends RecyclerView.ViewHolder {
        public interface OnItemClickListener {
            void readAssetRequest(ResItem resource);

            void saveIconRequest(ResItem resource);

            void exportIconRequest(ResItem resource);

            void zipAllAssetsRequest(ResItem resource);
        }

        private final ItemResourceBinding binding;

        ResourceViewHolder(@NonNull ItemResourceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ResItem resource) {
            binding.resourceText.setText(resource.fullPath);
            if (resource.type == ResType.DIR) {
                binding.icon0.setVisibility(View.VISIBLE);
                binding.icon0.setImageResource(R.drawable.ic_folder_blue_36dp);
                binding.overflowMenu.setVisibility(View.GONE);

                binding.icon1.setVisibility(View.GONE);

            } else {
                if (resource.drawable != null) {
                    binding.overflowMenu.setVisibility(View.VISIBLE);
                    binding.icon1.setVisibility(View.VISIBLE);
                    binding.icon1.setImageDrawable(resource.drawable);

                    binding.icon0.setVisibility(View.VISIBLE);
                } else {
                    binding.overflowMenu.setVisibility(View.VISIBLE);

                    binding.icon1.setVisibility(View.GONE);
                    binding.icon1.setImageDrawable(null);
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
