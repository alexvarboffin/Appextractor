package com.walhalla.appextractor.activity.debug;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.walhalla.appextractor.MarketUtils;

public class GooglePlayCategoryLauncher extends AppCompatActivity {
    private final String[] items = {
            "GAME"                             ,
            "FAMILY"                           ,
            "ART_AND_DESIGN"                   ,
            "AUTO_AND_VEHICLES"                ,
            "BEAUTY"                           ,
            "BOOKS_AND_REFERENCE"              ,
            "BUSINESS"                         ,
            "COMICS"                           ,
            "COMMUNICATION"                    ,
            "DATING"                           ,
            "EDUCATION"                        ,
            "ENTERTAINMENT"                    ,
            "EVENTS"                           ,
            "FINANCE"                          ,
            "FOOD_AND_DRINK"                   ,
            "HEALTH_AND_FITNESS"               ,
            "HOUSE_AND_HOME"                   ,
            "LIBRARIES_AND_DEMO"               ,
            "LIFESTYLE"                        ,
            "MAPS_AND_NAVIGATION"              ,
            "MEDICAL"                          ,
            "MUSIC_AND_AUDIO"                  ,
            "NEWS_AND_MAGAZINES"               ,
            "PARENTING"                        ,
            "PERSONALIZATION"                  ,
            "PHOTOGRAPHY"                      ,
            "PRODUCTIVITY"                     ,
            "SHOPPING"                         ,
            "SOCIAL"                           ,
            "SPORTS"                           ,
            "TOOLS"                            ,
            "TRAVEL_AND_LOCAL"                 ,
            "VIDEO_PLAYERS"                    ,
            "WEATHER"                          ,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = new ListView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        listView.setLayoutParams(layoutParams);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String category = items[position];
            MarketUtils.openGooglePlayCategory(GooglePlayCategoryLauncher.this, category);
        });
        setContentView(listView);
    }
}