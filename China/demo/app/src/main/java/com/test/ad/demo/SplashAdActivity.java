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
            DemoApplicaion.mPlacementId_splash_all
            ,DemoApplicaion.mPlacementId_splash_gdt
            , DemoApplicaion.mPlacementId_splash_toutiao
            ,DemoApplicaion.mPlacementId_splash_baidu
            ,DemoApplicaion.mPlacementId_splash_sigmob
    };

    String unitGroupName[] = new String[]{
            "All",
            "GDT",
            "Toutiao",
            "Baidu",
            "sigmob"
    };

    int mCurrentSelectIndex;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        spinner = (Spinner) findViewById(R.id.splash_spinner);
        // 声明一个ArrayAdapter用于存放简单数据
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SplashAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                unitGroupName);
        // 把定义好的Adapter设定到spinner中
        spinner.setAdapter(adapter);
        // 为第一个Spinner设定选中事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // 在选中之后触发
                Toast.makeText(SplashAdActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentSelectIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 这个一直没有触发，我也不知道什么时候被触发。
                //在官方的文档上说明，为back的时候触发，但是无效，可能需要特定的场景
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
