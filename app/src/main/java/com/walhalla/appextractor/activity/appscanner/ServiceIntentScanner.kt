package com.walhalla.appextractor.activity.appscanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.walhalla.appextractor.databinding.ActivityMimeBinding

class ServiceIntentScanner : AppCompatActivity() {
    private var binding: ActivityMimeBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMimeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

//        binding.viewPager.setAdapter(new MimeTabAdapter(this));
//
//        // Связываем TabLayout с ViewPager2
//        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
//                (tab, position) -> tab.setText(MimeTabData.TAB_TITLES.get(position))
//        ).attach();
        binding!!.viewPager.adapter = MimeTabAdapter(this)

        // Связываем TabLayout с ViewPager2
        TabLayoutMediator(
            binding!!.tabLayout, binding!!.viewPager
        ) { tab: TabLayout.Tab, position: Int -> tab.setText("123") }.attach()
    }
}