package com.walhalla.appextractor.activity.resources

import com.walhalla.appextractor.fragment.BaseFragment
import androidx.viewpager.widget.ViewPager
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.activity.resources.ResourcesPagerFragment
import android.view.LayoutInflater
import com.walhalla.appextractor.R
import com.walhalla.appextractor.activity.resources.p001.AssetsFragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

class ResourcesPagerFragment : BaseFragment(), android.view.View.OnClickListener {

    private lateinit var tabLayout: com.google.android.material.tabs.TabLayout
    private lateinit var viewPager: ViewPager

    private var meta: PackageMeta? = null

    private var p: BaseFragment? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = arguments?.getParcelable<PackageMeta>(ResourcesPagerFragment.Companion.ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): android.view.View? {
        return inflater.inflate(R.layout.fragment_viwpager, container, false)
    }

    override fun fab() {
    }


    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        //fab.setOnClickListener(this);
        val actionBar: androidx.appcompat.app.ActionBar? =
            (getActivity() as androidx.appcompat.app.AppCompatActivity).getSupportActionBar()
        if (actionBar != null) {
            //actionBar.setSubtitle(R.string.app_description);
        }

        viewPager = view.findViewById<ViewPager>(R.id.view_pager)
        val adapter: com.walhalla.appextractor.activity.resources.ResourcesPagerFragment.ViewpagerAdapter =
            com.walhalla.appextractor.activity.resources.ResourcesPagerFragment.ViewpagerAdapter(
                getChildFragmentManager(),
                androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            )

        //adapter.addFragment(new FirstFragment(), "auth");
        //adapter.addFragment(new DemoFragment(), "demo");

        //adapter.addFragment(ResourcesFragment.newInstance(meta), "Resources");
        adapter.addFragment(AssetsFragment.newInstance(meta), "Assets")


        //adapter.addFragment(new GalleryFragment(), getString(R.string.abc_tab_gallery));
        //adapter.addFragment(CollageFragment.newInstance("", ""), "Collage");
        //bb
        viewPager.setAdapter(adapter)
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: kotlin.Int,
                positionOffset: kotlin.Float,
                positionOffsetPixels: kotlin.Int
            ) {
            }

            override fun onPageSelected(position: kotlin.Int) {
                val tmp: androidx.fragment.app.Fragment? = adapter.getItem(position)
                //if (tmp instanceof BaseFragment && position > 0) {
                p = tmp as BaseFragment
                //}
                //DLog.d(fragment.getClass().getSimpleName());
            }

            override fun onPageScrollStateChanged(state: kotlin.Int) {
            }
        })
        tabLayout =requireActivity().findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
    }

    @android.annotation.SuppressLint("NonConstantResourceId")
    override fun onClick(v: android.view.View) {
        if (v.getId() == R.id.fab) {
            if (p == null) {
                p = getChildFragmentManager().getFragments().get(0) as BaseFragment
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


    private class ViewpagerAdapter(
        private val fm: androidx.fragment.app.FragmentManager,
        behavior: kotlin.Int
    ) :
        androidx.fragment.app.FragmentPagerAdapter(fm, behavior) {
        private val mFragmentList: java.util.ArrayList<BaseFragment> = java.util.ArrayList()
        private val mFragmentTitleList: java.util.ArrayList<kotlin.String> = java.util.ArrayList()


        override fun getItem(position: kotlin.Int): androidx.fragment.app.Fragment {
            return mFragmentList.get(position)
            //            viewPager!!.currentItem;
//            return when(position){
//
//                0-> download();
//                1->gallery();
//                else -> gallery();
//            }
        }

        override fun getCount(): kotlin.Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: BaseFragment, title: kotlin.String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: kotlin.Int): kotlin.String {
            return mFragmentTitleList.get(position)
        }

        override fun setPrimaryItem(
            container: android.view.ViewGroup,
            position: kotlin.Int,
            `object`: kotlin.Any
        ) {
//        if (getItem(position) != object) {
//            DLog.d("22222222222" + getItem(position).getClass().getSimpleName());
//        }
            super.setPrimaryItem(container, position, `object`)
        }
    }

    companion object {
        private const val ARG_PARAM1: kotlin.String = "param1"

        fun newInstance(meta: PackageMeta?): ResourcesPagerFragment {
            val fragment: ResourcesPagerFragment = ResourcesPagerFragment()
            val args: android.os.Bundle = android.os.Bundle()
            args.putParcelable(ResourcesPagerFragment.Companion.ARG_PARAM1, meta)
            fragment.setArguments(args)
            return fragment
        }
    }
}