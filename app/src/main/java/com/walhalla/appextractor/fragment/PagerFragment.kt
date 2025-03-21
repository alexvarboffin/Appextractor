package com.walhalla.appextractor.fragment

import android.annotation.SuppressLint
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
import com.walhalla.appextractor.MetaFragment
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.resources.p001.AssetsFragment
import com.walhalla.appextractor.activity.string.StrFragment.Companion.newInstance
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.ResourcesToolForPlugin

class PagerFragment : BaseFragment(), View.OnClickListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private var meta: PackageMeta? = null

    private var p: BaseFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = requireArguments().getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        adapter.addFragment(FR0.newInstance(meta), "INFO")
        adapter.addFragment(MetaFragment.newInstance(meta), "META")

        //        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.ANIM), "anim");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.ANIMATOR), "animator");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.ARRAY), "array");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.ATTR), "attr");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.BOOL), "bool");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.COLOR), "color");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.DIMEN), "dimen");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.DRAWABLE), "drawable");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.ID), "id");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.INTEGER), "integer");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.INTERPOLATOR), "interpolator");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.LAYOUT), "layout");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.MENU), "menu");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.RAW), "raw");
        adapter.addFragment(newInstance(meta, ResourcesToolForPlugin.STRING), "string")
        //        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.STYLE), "style");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.STYLEABLE), "styleable");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.TRANSITION), "transition");
        adapter.addFragment(newInstance(meta, ResourcesToolForPlugin.XML), "xml")




        adapter.addFragment(AssetsFragment.newInstance(meta), "Assets")

        //adapter.addFragment(new GalleryFragment(), getString(R.string.abc_tab_gallery));
        //adapter.addFragment(CollageFragment.newInstance("", ""), "Collage");
        //bb
        viewPager.setAdapter(adapter)
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
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
        tabLayout.setupWithViewPager(viewPager)
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

        fun newInstance(meta: PackageMeta?): PagerFragment {
            val fragment = PagerFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, meta)
            fragment.arguments = args
            return fragment
        }
    }
}