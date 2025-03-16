package com.walhalla.appextractor.activity.manifest

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.manifest.ManifestFragment.Companion.newInstance
import com.walhalla.appextractor.fragment.BaseFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.ui.DLog.d
import java.util.Arrays
import java.util.TreeSet

class ManifestPagerFragment : BaseFragment(), View.OnClickListener {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    private var meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta? = null
    private var apkPath: Array<String> = emptyArray()
    private var xmlFileName: String? = null

    private var p: BaseFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = requireArguments().getParcelable(ARG_PARAM1)
            apkPath = requireArguments().getStringArray(KEY_OBJ_APK_PATH)?: emptyArray()
            if (requireArguments().containsKey(KEY_OBJ_XML_FILENAME)) {
                xmlFileName = requireArguments().getString(KEY_OBJ_XML_FILENAME)
                //Toast.makeText(getActivity(), "@" + xmlFileName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_viwpager, container, false)
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
        val adapter = ViewpagerAdapter(
            childFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

        //adapter.addFragment(new FirstFragment(), "auth");
        //adapter.addFragment(new DemoFragment(), "demo");
        makeUI(requireActivity(), meta!!, adapter)


        //adapter.addFragment(new GalleryFragment(), getString(R.string.abc_tab_gallery));
        //adapter.addFragment(CollageFragment.newInstance("", ""), "Collage");
        //bb
        viewPager?.setAdapter(adapter)
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val tmp = adapter.getItem(position)
                //if (tmp instanceof BaseFragment && position > 0) {
                p = tmp as BaseFragment
                //}
                //DLog.d(fragment.getClass().getSimpleName());
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        tabLayout = requireActivity().findViewById(R.id.tabLayout)
        tabLayout?.setupWithViewPager(viewPager)
    }


    private fun makeUI(context: Context, meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta, adapter: ViewpagerAdapter) {
        if (apkPath == null || apkPath!!.size == 0) {
            try {
                val applicationInfo = context.packageManager.getApplicationInfo(
                    meta.packageName,
                    PackageManager.GET_META_DATA
                )
                val temp: MutableSet<String> = TreeSet()
                temp.add(applicationInfo.sourceDir)
                temp.add(applicationInfo.publicSourceDir)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (applicationInfo.splitPublicSourceDirs != null) {
                        temp.addAll(Arrays.asList(*applicationInfo.splitPublicSourceDirs))
                    }
                }
                apkPath = temp.toTypedArray()
            } catch (e: PackageManager.NameNotFoundException) {
            }
        }

        for (s in apkPath!!) {
            val q = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val name = q[q.size - 1].replace(".apk", "")
                .replace("split_config.", "")
            d("@@@$name")
            adapter.addFragment(newInstance(meta, s, xmlFileName), name)
        }
    }

    override fun onResume() {
        super.onResume()
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


    private class ViewpagerAdapter(private val fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        private val mFragmentList = ArrayList<BaseFragment>()
        private val mFragmentTitleList = ArrayList<String>()


        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
            //            viewPager!!.currentItem;
//            return when(position){
//
//                0-> download();
//                1->gallery();
//                else -> gallery();
//            }
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: BaseFragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): String? {
            return mFragmentTitleList[position]
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
//        if (getItem(position) != object) {
//            DLog.d("22222222222" + getItem(position).getClass().getSimpleName());
//        }
            super.setPrimaryItem(container, position, `object`)
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val KEY_OBJ_APK_PATH = "param2"
        private val KEY_OBJ_XML_FILENAME = ManifestPagerFragment::class.java.simpleName + "var3"

        fun newInstance(
            meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta?,
            apkPath: Array<String>?,
            xmlFileName: String?
        ): ManifestPagerFragment {
            val fragment = ManifestPagerFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, meta)
            args.putStringArray(KEY_OBJ_APK_PATH, apkPath)
            args.putString(KEY_OBJ_XML_FILENAME, xmlFileName)
            fragment.arguments = args
            return fragment
        }
    }
}