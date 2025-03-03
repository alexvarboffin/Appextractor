package com.walhalla.appextractor.fragment;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.activity.resources.p001.AssetsFragment;
import com.walhalla.appextractor.activity.string.ResourcesToolForPlugin;
import com.walhalla.appextractor.activity.string.StrFragment;
import com.walhalla.appextractor.model.PackageMeta;

import java.util.ArrayList;


public class PagerFragment extends BaseFragment
        implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final String ARG_PARAM1 = "param1";

    private PackageMeta meta;

    private BaseFragment p;

    public PagerFragment() {
        // Required empty public constructor
    }

    public static PagerFragment newInstance(PackageMeta meta) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, meta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meta = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viwpager, container, false);
    }

    @Override
    public void fab() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        //fab.setOnClickListener(this);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setSubtitle(R.string.app_description);
        }

        viewPager = view.findViewById(R.id.view_pager);
        ViewpagerAdapter adapter = new ViewpagerAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        //adapter.addFragment(new FirstFragment(), "auth");
        //adapter.addFragment(new DemoFragment(), "demo");

        adapter.addFragment(FR0.newInstance(meta), "INFO");
        adapter.addFragment(MetaFragment.newInstance(meta), "META");

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
        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.STRING), "string");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.STYLE), "style");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.STYLEABLE), "styleable");
//        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.TRANSITION), "transition");
        adapter.addFragment(StrFragment.newInstance(meta, ResourcesToolForPlugin.XML), "xml");




        adapter.addFragment(AssetsFragment.newInstance(meta), "Assets");

        //adapter.addFragment(new GalleryFragment(), getString(R.string.abc_tab_gallery));
        //adapter.addFragment(CollageFragment.newInstance("", ""), "Collage");
        //bb
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment tmp = adapter.getItem(position);
                //if (tmp instanceof BaseFragment && position > 0) {
                p = (BaseFragment) tmp;
                //}
                //DLog.d(fragment.getClass().getSimpleName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            if (p == null) {
                p = (BaseFragment) getChildFragmentManager().getFragments().get(0);
            }

            p.fab();

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




    private final static class ViewpagerAdapter extends FragmentPagerAdapter {

        private final FragmentManager fm;
        private final ArrayList<BaseFragment> mFragmentList = new ArrayList<>();
        private final ArrayList<String> mFragmentTitleList = new ArrayList<>();

        public ViewpagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            this.fm = fm;
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
//            viewPager!!.currentItem;
//            return when(position){
//
//                0-> download();
//                1->gallery();
//                else -> gallery();
//            }
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(BaseFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public String getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        if (getItem(position) != object) {
//            DLog.d("22222222222" + getItem(position).getClass().getSimpleName());
//        }
            super.setPrimaryItem(container, position, object);
        }
    }
}