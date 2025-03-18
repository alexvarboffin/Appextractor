package com.walhalla.appextractor.activity.debug

import android.R
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.walhalla.appextractor.utils.MarketUtils.openGooglePlayCategory

class GooglePlayCategoryLauncher : AppCompatActivity() {
    private val items = arrayOf(
        "GAME",
        "FAMILY",
        "ART_AND_DESIGN",
        "AUTO_AND_VEHICLES",
        "BEAUTY",
        "BOOKS_AND_REFERENCE",
        "BUSINESS",
        "COMICS",
        "COMMUNICATION",
        "DATING",
        "EDUCATION",
        "ENTERTAINMENT",
        "EVENTS",
        "FINANCE",
        "FOOD_AND_DRINK",
        "HEALTH_AND_FITNESS",
        "HOUSE_AND_HOME",
        "LIBRARIES_AND_DEMO",
        "LIFESTYLE",
        "MAPS_AND_NAVIGATION",
        "MEDICAL",
        "MUSIC_AND_AUDIO",
        "NEWS_AND_MAGAZINES",
        "PARENTING",
        "PERSONALIZATION",
        "PHOTOGRAPHY",
        "PRODUCTIVITY",
        "SHOPPING",
        "SOCIAL",
        "SPORTS",
        "TOOLS",
        "TRAVEL_AND_LOCAL",
        "VIDEO_PLAYERS",
        "WEATHER",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listView = ListView(this)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        listView.layoutParams = layoutParams

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, items)
        listView.adapter = adapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                val category = items[position]
                openGooglePlayCategory(
                    this@GooglePlayCategoryLauncher,
                    category
                )
            }
        setContentView(listView)
    }
}