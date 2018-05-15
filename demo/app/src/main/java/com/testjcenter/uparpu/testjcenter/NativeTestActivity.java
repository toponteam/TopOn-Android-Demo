package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.view.View;


public class NativeTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_test);


        findViewById(R.id.show_real_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NativeTestActivity.this, NativeAdActivity.class));
            }
        });
    }



}
