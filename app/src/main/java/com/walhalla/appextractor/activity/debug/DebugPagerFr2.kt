package com.walhalla.appextractor.activity.debug

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.walhalla.appextractor.R
import com.walhalla.appextractor.fragment.BaseFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.ui.DLog.d

class DebugPagerFr2 : BaseFragment(), View.OnClickListener {
    private val tabLayout: TabLayout? = null
    private lateinit var viewPager: ViewPager2


    private var p: BaseFragment? = null
    private var mm: HashMap<Int?, MutableList<PackageMeta>>? = null
    private var adapter: ViewpagerAdapter? = null
    private var keyIndex: HashMap<Int, Int?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_viwpager2, container, false)
    }

    override fun fab() {
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        //fab.setOnClickListener(this);
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            //actionBar.setSubtitle(R.string.app_description);
        }

        viewPager = view.findViewById(R.id.view_pager)
        adapter = ViewpagerAdapter(
            childFragmentManager, lifecycle
        )

        mm = test1()


        keyIndex = HashMap()
        var j = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for ((key) in mm!!) {
                val label = ApplicationInfo.getCategoryTitle(
                    context,
                    key!!
                ).toString()
                d("{mm}" + j + "/" + mm!!.size + " " + key + " " + label)
                adapter!!.addFragment(DebugFR1.newInstance(), label)
                keyIndex!![j] = key
                ++j
            }
        }


        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                d("{}{}{}$position")
                val fragment = adapter!!.createFragment(position) as DebugFR1
                val key = keyIndex!![position]
                val nn: List<PackageMeta> = mm!![key]!!
                fragment.setData(nn)
            }
        })
        viewPager.setAdapter(adapter)

        //        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Fragment tmp = adapter.getItem(position);
//                //if (tmp instanceof BaseFragment && position > 0) {
//                p = (BaseFragment) tmp;
//                //}
//                //DLog.d(fragment.getClass().getSimpleName());
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
        val tabLayout = activity!!.findViewById<TabLayout>(R.id.tabLayout)
        val tabLayoutMediator = TabLayoutMediator(
            tabLayout, viewPager,
            TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                // Установка текста во вкладку
                tab.setText(adapter!!.getPageTitle(position))
            })
        tabLayoutMediator.attach()
    }

    override fun onResume() {
        super.onResume()

        //        for (int i = 0; i < adapter.getItemCount(); i++) {
//            DebugFR1 fragment = (DebugFR1) adapter.createFragment(i);
//            Integer key = keyIndex.get(i);
//            List<PackageMeta> nn = mm.get(key);
//            fragment.setData(nn); // Обновление данных во фрагменте
//        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        if (v.id == R.id.fab) {
            if (p == null) {
                p = childFragmentManager.fragments[0] as BaseFragment
            }

            p!!.fab()

            //                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            TweetComposer.Builder builder = new TweetComposer.Builder(this)
//                    .text("Just setting up my Fabric!");
//            builder.show();

            //TweetUtils.https://api.twitter.com/1.1/followers/list.json
            //FriendsFollowersResources.
//                mainPresenter.updateUserData("BabangidaVEVO", new MainPresenter.Callback<Render[]>() {
//                    @Override
//                    public void successResult(Render[] data) {
//                        Bitmap[] bitmaps = new Bitmap[(Const.layers[0] + Const.layers[1] + Const.layers[2])];
//                        new Crippy().render(data, bitmaps);
//                    }
//                });
        }
    }

    private fun test1(): HashMap<Int?, MutableList<PackageMeta>> {
        val map = HashMap<Int?, MutableList<PackageMeta>>()

        val pm = activity!!.packageManager
        @SuppressLint("QueryPermissionsNeeded") val packageInfos =
            pm.getInstalledPackages(PackageManager.GET_META_DATA)
        for (packageInfo in packageInfos) {
            //publishProgress(packageInfo);

            val applicationInfo = packageInfo.applicationInfo
            var hasSplits = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                hasSplits =
                    applicationInfo!!.splitPublicSourceDirs != null && applicationInfo.splitPublicSourceDirs!!.size > 0
            }
            val meta = PackageMeta.Builder(
                applicationInfo!!.packageName
            )
                .setLabel(
                    applicationInfo.loadLabel(pm).toString()
                ) //                    .setHasSplits(hasSplits)
                //                    .setIsSystemApp((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                //                    .setVersionCode(ApkUtils.apiIsAtLeast(Build.VERSION_CODES.P) ? packageInfo.getLongVersionCode() : packageInfo.versionCode)
                //                    .setVersionName(packageInfo.versionName)
                //                    .setIcon(applicationInfo.icon)
                //                    .setInstallTime(packageInfo.firstInstallTime)
                //                    .setUpdateTime(packageInfo.lastUpdateTime)
                .build()

            //            File file = new File(applicationInfo.publicSourceDir);
//            long longsize = file.length();
//            String size;
//            if (longsize > 1024 && longsize <= 1024 * 1024) {
//                size = (longsize / 1024 + " KB");
//            } else if (longsize > 1024 * 1024 && longsize <= 1024 * 1024 * 1024) {
//                size = (longsize / (1024 * 1024) + " MB");
//            } else {
//                size = (longsize / (1024 * 1024 * 1024) + " GB");
//            }
//            meta.size = size;
            meta.sourceDir = applicationInfo.sourceDir
            meta.packageName = packageInfo.packageName
            meta.firstInstallTime = packageInfo.firstInstallTime
            meta.lastUpdateTime = packageInfo.lastUpdateTime

            //meta.category = applicationInfo.category;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var metaList = map[applicationInfo.category]
                if (metaList == null) {
                    metaList = ArrayList()
                }
                metaList.add(meta)
                map[applicationInfo.category] = metaList
            }
        }
        return map
    }

    class ViewpagerAdapter(private val fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {
        private val mFragmentList = ArrayList<BaseFragment>()
        private val mFragmentTitleList = ArrayList<String>()


        override fun createFragment(position: Int): Fragment {
            return mFragmentList[position]
            //            viewPager!!.currentItem;
//            return when(position){
//
//                0-> download();
//                1->gallery();
//                else -> gallery();
//            }
        }

        override fun getItemCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: BaseFragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }


        fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        //        @Override
        //        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //        if (getItem(position) != object) {}
        //            super.setPrimaryItem(container, position, object);
        //        }
        override fun containsItem(itemId: Long): Boolean {
            // Check if the adapter contains a fragment with the specified itemId
            return super.containsItem(itemId)
        }
    }

    companion object {
        //    public static DebugPagerFr newInstance(PackageMeta meta) {
        //        DebugPagerFr fragment = new DebugPagerFr();
        //        Bundle args = new Bundle();
        //        args.putParcelable(ARG_PARAM1, meta);
        //        fragment.setArguments(args);
        //        return fragment;
        //    }
        fun newInstance(): DebugPagerFr2 {
            return DebugPagerFr2()
        }
    }
}