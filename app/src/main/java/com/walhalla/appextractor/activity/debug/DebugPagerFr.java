package com.walhalla.appextractor.activity.debug;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.fragment.BaseFragment;
import com.walhalla.appextractor.model.PackageMeta;
import com.walhalla.ui.DLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DebugPagerFr extends BaseFragment
        implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;


    private BaseFragment p;
    private HashMap<String, List<PackageMeta>> mm;
    private DebugPagerFr2.ViewpagerAdapter adapter;
    private HashMap<Integer, String> keyIndex;

    public DebugPagerFr() {
        // Required empty public constructor
    }

//    public static DebugPagerFr newInstance(PackageMeta meta) {
//        DebugPagerFr fragment = new DebugPagerFr();
//        Bundle args = new Bundle();
//        args.putParcelable(ARG_PARAM1, meta);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static DebugPagerFr newInstance() {
        return new DebugPagerFr();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viwpager2, container, false);
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
        adapter = new DebugPagerFr2.ViewpagerAdapter(getChildFragmentManager(), getLifecycle());

        mm = test1();


        keyIndex = new HashMap<>();
        int j = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (Map.Entry<String, List<PackageMeta>> entry : mm.entrySet()) {

                String key = entry.getKey();
                String label = key;
                DLog.d("{mm}" + j + "/" + mm.size() + " " + key + " " + label);
                adapter.addFragment(DebugFR1.newInstance(), label);
                keyIndex.put(j, key);
                ++j;
            }
        }


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                DLog.d("{}{}{}" + position);
                DebugFR1 fragment = (DebugFR1) adapter.createFragment(position);
                String key = keyIndex.get(position);
                List<PackageMeta> nn = mm.get(key);
                fragment.setData(nn);
            }
        });
        viewPager.setAdapter(adapter);
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

        TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Установка текста во вкладку
            tab.setText(adapter.getPageTitle(position));
            // Или если вам нужны другие настройки для вкладки, например, иконки:
            // tab.setText(adapter.getPageTitle(position)).setIcon(R.drawable.ic_tab_icon);
        });
        tabLayoutMediator.attach();


    }

    @Override
    public void onResume() {
        super.onResume();

//        for (int i = 0; i < adapter.getItemCount(); i++) {
//            DebugFR1 fragment = (DebugFR1) adapter.createFragment(i);
//            Integer key = keyIndex.get(i);
//            List<PackageMeta> nn = mm.get(key);
//            fragment.setData(nn); // Обновление данных во фрагменте
//        }
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

    private HashMap<String, List<PackageMeta>> test1() {
        HashMap<String, List<PackageMeta>> map = new HashMap<>();

        PackageManager pm = getActivity().getPackageManager();
        @SuppressLint("QueryPermissionsNeeded")
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo packageInfo : packageInfos) {

            //publishProgress(packageInfo);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            boolean hasSplits = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                hasSplits = applicationInfo.splitPublicSourceDirs != null && applicationInfo.splitPublicSourceDirs.length > 0;
            }
            PackageMeta meta = new PackageMeta.Builder(applicationInfo.packageName)
                    .setLabel(applicationInfo.loadLabel(pm).toString())
//                    .setHasSplits(hasSplits)
//                    .setIsSystemApp((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
//                    .setVersionCode(ApkUtils.apiIsAtLeast(Build.VERSION_CODES.P) ? packageInfo.getLongVersionCode() : packageInfo.versionCode)
//                    .setVersionName(packageInfo.versionName)
//                    .setIcon(applicationInfo.icon)
//                    .setInstallTime(packageInfo.firstInstallTime)
//                    .setUpdateTime(packageInfo.lastUpdateTime)
                    .build();

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
            meta.sourceDir = applicationInfo.sourceDir;
            meta.packageName = packageInfo.packageName;
            meta.firstInstallTime = packageInfo.firstInstallTime;
            meta.lastUpdateTime = packageInfo.lastUpdateTime;
            //meta.category = applicationInfo.category;

            String[] permissions = packageInfo.requestedPermissions;
            String[] keys = new String[]{
                    "READ_PHONE_NUMBERS",
                    "CALL_PHONE", "READ_PHONE_STATE", "READ_CALL_LOG", "READ_CONTACTS"
            };

            if (null != permissions) {
                for (String permission : permissions) {
                    for (String key : keys) {
                        if (permission.equals("android.permission." + key)) {
                            //Log.d("AppWithReadPhoneNumber", packageInfo.packageName);
                            boolean isGranted = pm.checkPermission(permission, packageInfo.packageName) == PackageManager.PERMISSION_GRANTED;
                            meta.isGranted = isGranted;
                            List<PackageMeta> metaList = map.get(key);
                            if (metaList == null) {
                                metaList = new ArrayList<>();
                            }
                            metaList.add(meta);
                            map.put(key, metaList);
                            break;
                        }
                    }
                }
            }
        }
        return map;
    }
}