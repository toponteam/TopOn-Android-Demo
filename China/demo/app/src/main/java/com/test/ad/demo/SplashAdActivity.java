package com.test.ad.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SplashAdActivity extends Activity {
    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_splash_toutiao,
            DemoApplicaion.mPlacementId_splash_gdt,
            DemoApplicaion.mPlacementId_splash_baidu,
            DemoApplicaion.mPlacementId_splash_sigmob,
            DemoApplicaion.mPlacementId_splash_mintegral
    };

    String unitGroupName[] = new String[]{
            "Toutiao",
            "GDT",
            "Baidu",
            "Sigmob",
            "Mintegral"
    };

    int mCurrentSelectIndex;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        spinner = (Spinner) findViewById(R.id.splash_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SplashAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                unitGroupName);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(SplashAdActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentSelectIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        findViewById(R.id.show_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashAdActivity.this, SplashAdShowActivity.class);
                intent.putExtra("unitId", unitIds[mCurrentSelectIndex]);
                startActivity(intent);
            }
        });
    }

}
