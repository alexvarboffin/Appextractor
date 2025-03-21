package com.walhalla.appextractor.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.walhalla.appextractor.AppListAdapterCallback
import com.walhalla.appextractor.R
import com.walhalla.appextractor.utils.Util.getDate
import com.walhalla.appextractor.activity.detail.AppDetailInfoActivity
import com.walhalla.appextractor.activity.manifest.ManifestActivity
import com.walhalla.appextractor.adapter.appInfo.AppVh
import com.walhalla.appextractor.databinding.ItemAppBinding
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.ui.DLog.d
import java.util.Collections
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

class ApkListAdapter(private val mView: AppListAdapterCallback, private val context: Context) :
    RecyclerView.Adapter<AppVh>() {
//        reaper= new IntentReaper(context);
//        reaper.makeMimeApk();
    private val mFeatureViewPool =
        RecyclerView.RecycledViewPool()


    private val tFactory = ThreadFactory { r: Runnable? ->
        val t = Thread(r)
        t.isDaemon = true
        t
    }


    private val selectedItemColor: Int
    private val unselectedItemColor: Int

    //private final IntentReaper reaper;
    private val items: MutableList<PackageMeta> = ArrayList()
    private val list_original: MutableList<PackageMeta> = ArrayList()
    private val executorServiceNames: ExecutorService = Executors.newFixedThreadPool(3, tFactory)
    private val executorServiceIcons: ExecutorService = Executors.newFixedThreadPool(3, tFactory)
    private val handler = Handler()

    private val pm: PackageManager = mView.provideActivity().packageManager

    var names_to_load: Int = 0
    private val cache_appName: MutableMap<String?, String?> = Collections.synchronizedMap(
        LinkedHashMap(10, 1.5f, true)
    )

    //private final Map<String, Drawable> cache_appIcon = Collections.synchronizedMap(new LinkedHashMap<>(10, 1.5f, true));
    private val cache_appIcon: MutableMap<String?, Any> =
        Collections.synchronizedMap(LinkedHashMap(10, 1.5f, true))

    private var search_pattern: String? = null
    private var appNames: Array<String> = emptyArray()

    init {
        mFeatureViewPool.setMaxRecycledViews(0, 16)
        selectedItemColor = ContextCompat.getColor(context, R.color.color_selected)
        unselectedItemColor = ContextCompat.getColor(
            context,  //android.R.color.transparent
            R.color.color_unselected
        )
    }


    private inner class AppNameLoader(private val package_info: PackageMeta) : Runnable {
        override fun run() {
            cache_appName[package_info.packageName] =
                package_info.label /*package_info.applicationInfo.loadLabel(pm)*/
            handler.post {
                names_to_load--
                if (names_to_load == 0) {
                    mView.hideProgressBar()
                    //@@@ executorServiceNames.shutdown();
                }
            }
        }
    }

    internal inner class GuiLoader(
        private val applicationViewHolder: AppVh,
        private val packageMeta: PackageMeta
    ) : Runnable {
        override fun run() {
            var first = true
            do {
                try {
                    val appName = GENERATE_APP_NAME(packageMeta)
                    if (appName != null) {
                        cache_appName[packageMeta.packageName] = appName
                    }

                    //final Drawable icon = package_info.applicationInfo.loadIcon(pm);
                    val icon: Any =
                        (if (packageMeta.icon != null) packageMeta.icon else R.drawable.placeholder_app_icon)!!
                    cache_appIcon[packageMeta.packageName] = icon
                    handler.post {
                        applicationViewHolder.setAppName(appName!!, search_pattern)
                        //applicationViewHolder.mBinding.imgIcon.setImageDrawable00(icon);
                        Glide.with(applicationViewHolder.mBinding.imgIcon)
                            .load(icon)
                            .placeholder(R.drawable.placeholder_app_icon)
                            .into(applicationViewHolder.mBinding.imgIcon)
                    }
                } catch (ex: OutOfMemoryError) {
                    cache_appIcon.clear()
                    cache_appName.clear()
                    if (first) {
                        first = false
                        continue
                    }
                }
                break
            } while (true)
        }
    }


    /**
     * Generate apk Name
     *
     * @param meta
     * @return
     */
    private fun GENERATE_APP_NAME(meta: PackageMeta): String {
        return if (cache_appName.containsKey(meta.packageName))
            cache_appName[meta.packageName]?:"Unknown"
        else meta.label /*(String) meta.applicationInfo.loadLabel(pm)*/
    }


    /**
     * View holder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AppVh {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemAppBinding.inflate(inflater, viewGroup, false)
        return AppVh(binding, mFeatureViewPool, this)
    }

    override fun onBindViewHolder(holder: AppVh, position: Int) {
        val item = items[position]
        holder.setPackageName(item.packageName, search_pattern, position)
        holder.mBinding.version.text = getDate(item)


        //String uid = String.valueOf(item.applicationInfo.uid);
        holder.mBinding.size.text = prettyFileSize(item.fileSize)

        if (cache_appIcon.containsKey(item.packageName) && cache_appName.containsKey(item.packageName)) {
            holder.setAppName(cache_appName[item.packageName]!!, search_pattern)
            //holder.mBinding.imgIcon.setImageDrawable00(cache_appIcon.get(item.packageName));
            Glide.with(holder.mBinding.imgIcon)
                .load(cache_appIcon[item.packageName])
                .placeholder(R.drawable.placeholder_app_icon)
                .into(holder.mBinding.imgIcon)
        } else {
            holder.setAppName(item.packageName, search_pattern)
            holder.mBinding.imgIcon.setImageDrawable(null)
            executorServiceIcons.submit(GuiLoader(holder, item))
        }


        //Onclick
        holder.mBinding.overflowMenu.setOnClickListener { view: View ->
            showPopupMenu(
                view,
                position
            )
        }
        holder.mBinding.root.setOnClickListener { view: View? ->
            val item1 = getItem(position)
            if (Companion.selected.contains(item1)) {
                Companion.selected.remove(item1)
                unhighlightView(holder)
            } else {
                Companion.selected.add(item1)
                highlightView(holder)
            }
            //mAdapter.appListAdapterCallback.doExtractRequest(item, mBinding.txtAppName.getText().toString());
            mView.count(Companion.selected.size)
        }

        if (Companion.selected.contains(item)) {
            highlightView(holder)
        } else {
            unhighlightView(holder)
        }

        if (item.isGranted) {
            highlightGrantedPermission(holder)
        }

        holder.bindTo(item)
    }

    private fun highlightView(holder: AppVh) {
        holder.itemView.setBackgroundColor(selectedItemColor)
        holder.mBinding.rl2.setBackgroundColor(selectedItemColor)
    }

    private fun highlightGrantedPermission(holder: AppVh) {
        holder.itemView.setBackgroundColor(selectedItemColor)
        holder.mBinding.rl2.setBackgroundColor(selectedItemColor)
    }

    private fun unhighlightView(holder: AppVh) {
        holder.itemView.setBackgroundColor(unselectedItemColor)
        holder.mBinding.rl2.setBackgroundColor(unselectedItemColor)
    }

    private fun getItem(pos: Int): PackageMeta {
        return items[pos]
    }

    override fun getItemCount(): Int {
        return items.size
    }


    fun setSearchPattern(pattern: String) {
        search_pattern = pattern.lowercase(Locale.getDefault())
        filterListByPattern()
        this.notifyDataSetChanged()
    }

    private fun filterListByPattern() {
        items.clear()
        for (meta in list_original) {
            var add = true
            do {
                if (search_pattern == null || search_pattern!!.isEmpty()) {
                    break // empty search pattern: add everything
                }
                if (meta.packageName.lowercase(Locale.getDefault()).contains(
                        search_pattern!!
                    )
                ) {
                    break // search in package name
                }
                if (cache_appName.containsKey(meta.packageName) && cache_appName[meta.packageName]!!
                        .lowercase(Locale.getDefault()).contains(search_pattern!!)
                ) {
                    break // search in application name
                }
                add = false
            } while (false)

            if (add) items.add(meta)
        }
    }

    //Button pressed or permission Granted
    fun doExtractClick() {
        appNames = Array(selected.size) { "" }
        for (i in selected.indices) {
            appNames[i] = GENERATE_APP_NAME(selected[i])?:""
        }
        mView.nowExtractOneSelected(selected, appNames)
    }

    @SuppressLint("NonConstantResourceId")
    private fun showPopupMenu(v: View, adapterPosition: Int) {
        val packageInfo = items[adapterPosition]
        val popup = PopupMenu(mView.provideActivity(), v)
        val inflater = popup.menuInflater

        val menu = popup.menu

//        MenuPopupHelper menuHelper = new MenuPopupHelper(
//                mView.getActivity(), (MenuBuilder) menu, v);
//        menuHelper.setForceShowIcon(true);
//        menuHelper.show();

        //reaper.wrapper(menu, new File(packageInfo.applicationInfo.sourceDir));
        inflater.inflate(R.menu.abc_popup_app, menu)
        val menuHelper: Any
        val argTypes: Array<Class<*>?>
        try {
            @SuppressLint("DiscouragedPrivateApi") val fMenuHelper =
                PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.isAccessible = true
            menuHelper = fMenuHelper[popup]
            argTypes = arrayOf(Boolean::class.javaPrimitiveType)
            menuHelper?.javaClass?.getDeclaredMethod("setForceShowIcon", *argTypes)
                ?.invoke(menuHelper, true)
        } catch (ignored: Exception) {
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            val itemId = menuItem.itemId
            if (itemId == R.id.action_open_link) {
                d("Open on Google Play" + packageInfo.packageName)
                mView.openPackageOnGooglePlay(packageInfo.packageName)
                return@setOnMenuItemClickListener true
            } else if (itemId == R.id.action_extract) {
                clearSelected()
                selectOne(packageInfo)
                appNames = arrayOf(
                    GENERATE_APP_NAME(packageInfo)?:"" //cache_appName.get(info.packageName)
                )
                //DLog.d("@@@" + Arrays.toString(appName) + "\t|\t" + cache_appName.get(info.packageName));
                //this.mView.nowExtractOneSelected(selected, appnames);
                mView.menuExtractSelected(v)
            } else if (itemId == R.id.action_launch_app) {
                mView.launchApp(context, packageInfo.packageName)
                return@setOnMenuItemClickListener true
            } else if (itemId == R.id.action_share_app) {
                mView.shareToOtherApp(GENERATE_APP_NAME(packageInfo))
                return@setOnMenuItemClickListener true
            } else if (itemId == R.id.action_uninstall_app) {
                mView.uninstallApp(packageInfo.packageName)
                return@setOnMenuItemClickListener true
            } else if (itemId == R.id.action_copy_package_name) {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                if (clipboard != null) {
                    val clip = ClipData.newPlainText(
                        "packageName", "" + packageInfo.packageName
                    )
                    clipboard.setPrimaryClip(clip)
                    if (mView != null) {
                        mView.successMessage(
                            context.getString(R.string.copied_to_clipboard)
                                .uppercase(Locale.getDefault())
                        )
                    }
                }
                return@setOnMenuItemClickListener true
            } else if (itemId == R.id.action_app_info) {
                AppDetailInfoActivity.newIntent(context, packageInfo)
                return@setOnMenuItemClickListener true
            } else if (itemId == R.id.action_manifest) {
                ManifestActivity.newIntent(context, packageInfo, null, null)
                return@setOnMenuItemClickListener true
            } else if (itemId == R.id.action_save_icon) {
                if (mView != null) {
                    mView.saveIconRequest(packageInfo)
                }
            } else if (itemId == R.id.action_share_icon) {
                if (mView != null) {
                    mView.exportIconRequest(packageInfo)
                }
//                case R.id.action_share_link:
////                    ---akeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
//                    //getPresenter().onItemClicked(menuItem.getItemId(), category);
//                    return true;
            }
            false
        }
        popup.show()
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
    fun addAll0(items: List<PackageMeta>) {
        this.items.clear()
        list_original.clear()
        Companion.selected.clear()

        this.items.addAll(items)
        list_original.addAll(items)
        notifyDataSetChanged()
    }

    private fun selectOne(info: PackageMeta) {
        Companion.selected.add(info)
        mView.count(Companion.selected.size)
    }

    fun addItem(item: PackageMeta) {
        names_to_load++
        executorServiceNames.submit(AppNameLoader(item))
        list_original.add(item)
        filterListByPattern()
        notifyDataSetChanged()
    }

    fun clearAll0(isNotify: Boolean) {
        items.clear()
        list_original.clear()
        Companion.selected.clear()
        if (isNotify) notifyDataSetChanged()
    }

    fun clearSelected() {
        Companion.selected.clear()
        notifyDataSetChanged()
        mView.count(Companion.selected.size)
    }

    fun selectAll() {
        Companion.selected.clear()
        Companion.selected.addAll(items)
        notifyDataSetChanged()
        mView.count(Companion.selected.size)
    }

    val selected: List<PackageMeta>
        get() = Companion.selected

    companion object {
        private val selected: MutableList<PackageMeta> = ArrayList()
    }
}