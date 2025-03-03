package com.walhalla.appextractor.activity.main;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.walhalla.appextractor.R;
import com.walhalla.appextractor.SettingsPreferenceFragment;
import com.walhalla.appextractor.fragment.LogFragment;
import com.walhalla.appextractor.fragment.extractor.ExtractorFragment;

class ViewPagerAdapter extends FragmentStatePagerAdapter/*FragmentPagerAdapter */ {


    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(androidx.fragment.app.FragmentManager manager) {
        super(manager);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position + 1);

        switch (position) {
            case 0:
                return new ExtractorFragment();
            case 1:
                return LogFragment.newInstance();
            case 2:
                return new SettingsPreferenceFragment();
            default:
                return new Fragment();

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFragment(String title) {
        mFragmentTitleList.add(title);
    }
}
