package com.test.ad.demo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class AdNetworkSelectActivity extends Activity {

    private RelativeLayout mRlAdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_network_select);

        initView();
        initListener();
    }

    private void initView() {
        mRlAdx = findViewById(R.id.topon_adx);
    }

    private void initListener() {
        mRlAdx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdNetworkSelectActivity.this, InitSDkActivity.class));
                finish();
            }
        });
    }
}