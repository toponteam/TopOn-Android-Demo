package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.uparpu.nativead.splash.api.UpArpuNativeSplash;
import com.uparpu.nativead.splash.api.UpArpuNativeSplashListener;

public class NativeSplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_splash);
        FrameLayout splashView = findViewById(R.id.native_splash_view);
//        Map<String, Object> localMap = new HashMap<>();
//        //广点通需要加的
//        localMap.put(GDTUpArpuConst.ADTYPE, "1");
//        localMap.put(GDTUpArpuConst.AD_WIDTH, ADSize.FULL_WIDTH);
//        localMap.put(GDTUpArpuConst.AD_HEIGHT, ADSize.FULL_WIDTH);

//        config.mode = UpArpuNativeSplashConfig.ICON_IMAGE_MODE;
        UpArpuNativeSplash splash = new UpArpuNativeSplash(this, splashView, null, DemoApplicaion.mPlacementId_native_all, new UpArpuNativeSplashListener() {
            @Override
            public void onAdLoaded() {
                Log.i("SplashActivity", "Develop callback loaded");
            }

            @Override
            public void onNoAdError(String msg) {
                Log.i("NativeSplashActivity", "Develop callback onNoAdError :" + msg);
                Toast.makeText(NativeSplashActivity.this, "load ad error: " + msg, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onAdShow() {
                Log.i("NativeSplashActivity", "Develop callback onAdShow");
            }

            @Override
            public void onAdClick() {
                Log.i("NativeSplashActivity", "Develop callback onAdClick");
            }

            @Override
            public void onAdSkip() {
                Log.i("NativeSplashActivity", "Develop callback onAdSkip");
                finish();
            }

            @Override
            public void onAdTimeOver() {
                Log.i("NativeSplashActivity", "Develop callback onAdTimeOver");
                finish();
            }

            @Override
            public void onAdTick(long millisUtilFinished) {
                Log.i("NativeSplashActivity", "Develop callback onAdTick:" + millisUtilFinished);
            }
        });

    }
}
