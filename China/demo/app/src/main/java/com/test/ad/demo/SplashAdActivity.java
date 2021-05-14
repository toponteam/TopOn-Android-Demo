/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplashAdActivity extends Activity {

    String mCurrentPlacementName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Map<String, String> placementIdMap = PlacementIdUtil.getSplashPlacements(this);
        List<String> placementNameList = new ArrayList<>(placementIdMap.keySet());
        
        Spinner spinner = (Spinner) findViewById(R.id.splash_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SplashAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                placementNameList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(SplashAdActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentPlacementName = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        findViewById(R.id.show_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashAdActivity.this, SplashAdShowActivity.class);
                intent.putExtra("placementId", placementIdMap.get(mCurrentPlacementName));
                startActivity(intent);
            }
        });
    }

}
