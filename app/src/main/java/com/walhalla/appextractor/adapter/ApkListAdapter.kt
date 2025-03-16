package com.walhalla.appextractor.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.walhalla.appextractor.AppListAdapterCallback;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.Util;
import com.walhalla.appextractor.activity.detail.AppDetailInfoActivity;
import com.walhalla.appextractor.activity.manifest.ManifestActivity;
import com.walhalla.appextractor.databinding.ItemAppBinding;
import com.walhalla.appextractor.adapter.appInfo.AppVh;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.ui.DLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ApkListAdapter extends RecyclerView.Adapter<AppVh> {


    private final RecyclerView.RecycledViewPool mFeatureViewPool;

    private final AppListAdapterCallback mView;
    private final Context context;


    private final ThreadFactory tFactory = r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    };


    private final int selectedItemColor;
    private final int unselectedItemColor;

    //private final IntentReaper reaper;

    private List<PackageMeta> items = new ArrayList<>();
    private static final List<PackageMeta> selected = new ArrayList<>();

    private final List<PackageMeta> list_original = new ArrayList<>();
    private final ExecutorService executorServiceNames = Executors.newFixedThreadPool(3, tFactory);
    private final ExecutorService executorServiceIcons = Executors.newFixedThreadPool(3, tFactory);
    private final Handler handler = new Handler();

    private final PackageManager pm;

    int names_to_load = 0;
    private final Map<String, String> cache_appName = Collections.synchronizedMap(new LinkedHashMap<>(10, 1.5f, true));

    //private final Map<String, Drawable> cache_appIcon = Collections.synchronizedMap(new LinkedHashMap<>(10, 1.5f, true));
    private final Map<String, Object> cache_appIcon = Collections.synchronizedMap(new LinkedHashMap<>(10, 1.5f, true));

    private String search_pattern;
    private String[] appNames;

    public ApkListAdapter(AppListAdapterCallback appListAdapterCallback, Context context) {
        this.pm = appListAdapterCallback.getActivity().getPackageManager();
        this.mView = appListAdapterCallback;
        this.context = context;

//        reaper= new IntentReaper(context);
//        reaper.makeMimeApk();

        mFeatureViewPool = new RecyclerView.RecycledViewPool();
        mFeatureViewPool.setMaxRecycledViews(0, 16);
        selectedItemColor = ContextCompat.getColor(context, R.color.color_selected);
        unselectedItemColor = ContextCompat.getColor(context,
                //android.R.color.transparent
                R.color.color_unselected
        );
    }


    private class AppNameLoader implements Runnable {
        private final PackageMeta package_info;

        AppNameLoader(PackageMeta info) {
            package_info = info;
        }

        @Override
        public void run() {
            cache_appName.put(package_info.packageName, (String) package_info.label);/*package_info.applicationInfo.loadLabel(pm)*/
            handler.post(() -> {
                names_to_load--;
                if (names_to_load == 0) {
                    mView.hideProgressBar();
                    //@@@ executorServiceNames.shutdown();
                }
            });
        }
    }

    class GuiLoader implements Runnable {

        private final AppVh applicationViewHolder;
        private final PackageMeta packageMeta;

        GuiLoader(AppVh h, PackageMeta meta) {
            applicationViewHolder = h;
            this.packageMeta = meta;
        }

        @Override
        public void run() {
            boolean first = true;
            do {
                try {
                    final String appName = GENERATE_APP_NAME(packageMeta);
                    if (appName != null) {
                        cache_appName.put(packageMeta.packageName, appName);
                    }

                    //final Drawable icon = package_info.applicationInfo.loadIcon(pm);
                    final Object icon = packageMeta.iconUri != null ? packageMeta.iconUri : R.drawable.placeholder_app_icon;
                    cache_appIcon.put(packageMeta.packageName, icon);
                    handler.post(() -> {
                        applicationViewHolder.setAppName(appName, search_pattern);
                        //applicationViewHolder.mBinding.imgIcon.setImageDrawable00(icon);
                        Glide.with(applicationViewHolder.mBinding.imgIcon)
                                .load(icon)
                                .placeholder(R.drawable.placeholder_app_icon)
                                .into(applicationViewHolder.mBinding.imgIcon);
                    });

                } catch (OutOfMemoryError ex) {
                    cache_appIcon.clear();
                    cache_appName.clear();
                    if (first) {
                        first = false;
                        continue;
                    }
                }
                break;
            } while (true);
        }
    }


    /**
     * Generate apk Name
     *
     * @param meta
     * @return
     */
    private String GENERATE_APP_NAME(PackageMeta meta) {
        return cache_appName.containsKey(meta.packageName)
                ? cache_appName.get(meta.packageName)
                : (String) meta.label; /*(String) meta.applicationInfo.loadLabel(pm)*/

    }


    /**
     * View holder
     */


    @NonNull
    @Override
    public AppVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ItemAppBinding binding = ItemAppBinding.inflate(inflater, viewGroup, false);
        return new AppVh(binding, mFeatureViewPool, this);
    }

    @Override
    public void onBindViewHolder(@NonNull AppVh holder, int position) {
        PackageMeta item = items.get(position);
        holder.setPackageName(item.packageName, search_pattern, position);
        holder.mBinding.version.setText(Util.getDate(item));


        //String uid = String.valueOf(item.applicationInfo.uid);
        holder.mBinding.size.setText(item.size);

        if (cache_appIcon.containsKey(item.packageName) && cache_appName.containsKey(item.packageName)) {
            holder.setAppName(cache_appName.get(item.packageName), search_pattern);
            //holder.mBinding.imgIcon.setImageDrawable00(cache_appIcon.get(item.packageName));
            Glide.with(holder.mBinding.imgIcon)
                    .load(cache_appIcon.get(item.packageName))
                    .placeholder(R.drawable.placeholder_app_icon)
                    .into(holder.mBinding.imgIcon);
        } else {
            holder.setAppName(item.packageName, search_pattern);
            holder.mBinding.imgIcon.setImageDrawable(null);
            executorServiceIcons.submit(new GuiLoader(holder, item));
        }


        //Onclick
        holder.mBinding.overflowMenu.setOnClickListener(view -> showPopupMenu(view, position));
        holder.mBinding.getRoot().setOnClickListener(view -> {
            final PackageMeta item1 = getItem(position);
            if (selected.contains(item1)) {
                selected.remove(item1);
                unhighlightView(holder);
            } else {
                selected.add(item1);
                highlightView(holder);
            }
            //mAdapter.appListAdapterCallback.doExtractRequest(item, mBinding.txtAppName.getText().toString());
            mView.count(selected.size());
        });

        if (selected.contains(item)) {
            highlightView(holder);
        } else {
            unhighlightView(holder);
        }

        if (item.isGranted) {
            highlightGrantedPermission(holder);
        }

        holder.bindTo(item);
    }

    private void highlightView(AppVh holder) {
        holder.itemView.setBackgroundColor(selectedItemColor);
        holder.mBinding.rl2.setBackgroundColor(selectedItemColor);
    }

    private void highlightGrantedPermission(AppVh holder) {
        holder.itemView.setBackgroundColor(selectedItemColor);
        holder.mBinding.rl2.setBackgroundColor(selectedItemColor);
    }

    private void unhighlightView(AppVh holder) {
        holder.itemView.setBackgroundColor(unselectedItemColor);
        holder.mBinding.rl2.setBackgroundColor(unselectedItemColor);
    }

    private PackageMeta getItem(int pos) {
        return items.get(pos);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void setSearchPattern(String pattern) {
        search_pattern = pattern.toLowerCase();
        filterListByPattern();
        this.notifyDataSetChanged();
    }

    private void filterListByPattern() {
        items.clear();
        for (PackageMeta meta : list_original) {
            boolean add = true;
            do {
                if (search_pattern == null || search_pattern.isEmpty()) {
                    break;// empty search pattern: add everything
                }
                if (meta.packageName.toLowerCase().contains(search_pattern)) {
                    break;// search in package name
                }
                if (cache_appName.containsKey(meta.packageName) && cache_appName.get(meta.packageName).toLowerCase().contains(search_pattern)) {
                    break;// search in application name
                }
                add = false;
            } while (false);

            if (add) items.add(meta);
        }
    }

    //Button pressed or permission Granted
    public void doExtractClick() {
        appNames = new String[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            appNames[i] = GENERATE_APP_NAME(selected.get(i));
        }
        this.mView.nowExtractOneSelected(selected, appNames);
    }

    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View v, int adapterPosition) {

        PackageMeta packageInfo = items.get(adapterPosition);
        PopupMenu popup = new PopupMenu(mView.getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();

        Menu menu = popup.getMenu();

//        MenuPopupHelper menuHelper = new MenuPopupHelper(
//                mView.getActivity(), (MenuBuilder) menu, v);
//        menuHelper.setForceShowIcon(true);
//        menuHelper.show();

        //reaper.wrapper(menu, new File(packageInfo.applicationInfo.sourceDir));

        inflater.inflate(R.menu.abc_popup_app, menu);
        Object menuHelper;
        Class<?>[] argTypes;
        try {
            @SuppressLint("DiscouragedPrivateApi") Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            if (menuHelper != null) {
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            }
        } catch (Exception ignored) {
        }
        popup.setOnMenuItemClickListener(menuItem -> {

            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_open_link) {
                DLog.d("Open on Google Play" + packageInfo.packageName);
                mView.openPackageOnGooglePlay(packageInfo.packageName);
                return true;
            } else if (itemId == R.id.action_extract) {
                clearSelected();
                selectOne(packageInfo);
                appNames = new String[]{GENERATE_APP_NAME(packageInfo)
                        //cache_appName.get(info.packageName)
                };
                //DLog.d("@@@" + Arrays.toString(appName) + "\t|\t" + cache_appName.get(info.packageName));
                //this.mView.nowExtractOneSelected(selected, appnames);
                this.mView.menuExtractSelected(v);
            } else if (itemId == R.id.action_launch_app) {
                mView.launchApp(context, packageInfo.packageName);
                return true;
            } else if (itemId == R.id.action_share_app) {
                mView.shareToOtherApp(GENERATE_APP_NAME(packageInfo));
                return true;
            } else if (itemId == R.id.action_uninstall_app) {
                mView.uninstallApp(packageInfo.packageName);
                return true;
            } else if (itemId == R.id.action_copy_package_name) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("packageName", "" + packageInfo.packageName);
                    clipboard.setPrimaryClip(clip);
                    if (mView != null) {
                        mView.successMessage(context.getString(R.string.copied_to_clipboard).toUpperCase());
                    }
                }
                return true;
            } else if (itemId == R.id.action_app_info) {
                AppDetailInfoActivity.newIntent(context, packageInfo);
                return true;
            } else if (itemId == R.id.action_manifest) {
                ManifestActivity.newIntent(context, packageInfo, null, null);
                return true;
            } else if (itemId == R.id.action_save_icon) {
                if (mView != null) {
                    mView.saveIconRequest(packageInfo);
                }
            } else if (itemId == R.id.action_share_icon) {
                if (mView != null) {
                    mView.exportIconRequest(packageInfo);
                }
                //                case R.id.action_share_link:
////                    ---akeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
//                    //getPresenter().onItemClicked(menuItem.getItemId(), category);
//                    return true;
            }
            return false;
        });
        popup.show();
    }

//    @SuppressLint("NonConstantResourceId")
//    private void showPopupMenu(View v, int adapterPosition) {
//
//        PackageInfo info = items.get(adapterPosition);
//        PopupMenu popup = new PopupMenu(mView.getActivity(), v);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu_album, popup.getMenu());
//        popup.setOnMenuItemClickListener(menuItem -> {
//
//            switch (menuItem.getItemId()) {
//
//                case R.id.action_open_link:
//                    //Open on Google Play
//                    mView.openOnGooglePlay(info.packageName);
//                    return true;
//
//                case R.id.action_extract:
//                    clearSelected();
//                    selectOne(info);
//                    appnames = new String[]{GENERATE_APP_NAME(info)
//                            //cache_appName.get(info.packageName)
//                    };
//                    //DLog.d("@@@" + Arrays.toString(appName) + "\t|\t" + cache_appName.get(info.packageName));
//                    //this.mView.nowExtractOneSelected(selected, appnames);
//                    this.mView.menuExtractSelected(v);
//                    break;
//
//                case R.id.action_launch_app:
//                    mView.launchApp(context, info.packageName);
//                    return true;
//
//                case R.id.action_share_app:
//                    mView.shareToOtherApp(GENERATE_APP_NAME(info));
//                    return true;
//
//                case R.id.action_uninstall_app:
//                    mView.uninstallApp(info.packageName);
//                    return true;
//
////                case R.id.action_share_link:
//////                    ---akeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
////                    //getPresenter().onItemClicked(menuItem.getItemId(), category);
////                    return true;
//                default:
//            }
//            return false;
//        });
//        popup.show();
//    }


    //Other function
    public void addAll0(List<PackageMeta> items) {
        this.items.clear();
        this.list_original.clear();
        this.selected.clear();

        this.items.addAll(items);
        this.list_original.addAll(items);
        notifyDataSetChanged();
    }

    private void selectOne(PackageMeta info) {
        selected.add(info);
        mView.count(selected.size());
    }

    public void addItem(PackageMeta item) {
        names_to_load++;
        executorServiceNames.submit(new AppNameLoader(item));
        list_original.add(item);
        filterListByPattern();
        notifyDataSetChanged();
    }

    public void clearAll0(boolean isNotify) {
        items.clear();
        list_original.clear();
        selected.clear();
        if (isNotify) notifyDataSetChanged();
    }

    public void clearSelected() {
        selected.clear();
        notifyDataSetChanged();
        mView.count(selected.size());
    }

    public void selectAll() {
        selected.clear();
        selected.addAll(items);
        notifyDataSetChanged();
        mView.count(selected.size());
    }

    public List<PackageMeta> getSelected() {
        return selected;
    }
}