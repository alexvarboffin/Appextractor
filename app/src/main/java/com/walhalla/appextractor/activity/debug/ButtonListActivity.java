package com.walhalla.appextractor.activity.debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.walhalla.appextractor.databinding.ActivityPagerBinding;

import java.util.ArrayList;
import java.util.List;


public class ButtonListActivity extends AppCompatActivity {


    private ActivityPagerBinding binding;
    private String[] tabTitles;
    private ViewPagerAdapter tabAdapter;


    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "YourApp:WakeLockTag");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        binding = ActivityPagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        makeTabs(binding.tabLayout);


        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        var0();

        //binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(tabTitles[position])).attach();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                invalidateFragmentMenus(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //binding.viewPager.setOffscreenPageLimit(4);
    }

    private void invalidateFragmentMenus(int position) {
//        v1
//        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
//            mPagerAdapter.getItem(i).setHasOptionsMenu(i == position);
//        }
//        if (getActivity() != null) {
//            getActivity().invalidateOptionsMenu(); //or respectively its support method.
//        }
//        for (int i = 0; i < tabAdapter.getItemCount(); i++) {
//            //int item = mBinding.viewpager.getCurrentItem();
//            tabAdapter.getItem(i).setHasOptionsMenu(/*i == item && */i == position);
//            DLog.d("000000 " + i + " " + position);
//        }
        invalidateOptionsMenu(); //or respectively its support method.
    }

    private void makeTabs(TabLayout tabLayout) {
//        tabLayout.addTab(tabLayout.newTab().setText("Channel"));
//        tabLayout.addTab(tabLayout.newTab().setText("Category"));
//        tabLayout.addTab(tabLayout.newTab().setText("Favorites"));
    }

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();// Включаем WakeLock

    }

    @Override
    protected void onPause() {
        super.onPause();

        wakeLock.release();// Отключаем WakeLock при приостановке активности
    }

    private void var0() {
        tabTitles = new String[]{
                "0", "1", "2", "3"
        };
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new PlaylistFragment());
//        fragmentList.add(new AllChannelFragment());
//        fragmentList.add(new CategoryFragment());
//        fragmentList.add(new FavoritesFragment());
        tabAdapter = new ViewPagerAdapter(this, getSupportFragmentManager(), fragmentList);
        binding.viewPager.setAdapter(tabAdapter);
    }

    public static class ViewPagerAdapter extends FragmentStateAdapter {

        Context context;
        private final List<Fragment> fragmentList;

        public ViewPagerAdapter(FragmentActivity context, FragmentManager fm, List<Fragment> fragments) {
            super(context);
            this.context = context;
            this.fragmentList = fragments;
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }

//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        if (position == 0) {
//            return new ALatestFragment();
//        } else if (position == 1) {
//            return new CategoryFragment();
//        } else if (position == 2) {
//            return new FavoritesFragment();
//        }
//        return new Fragment();
//    }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

//    @Override
//    public int getCount() {
//        return totalTabs;
//    }
    }


}
