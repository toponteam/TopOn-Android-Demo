package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.nativead.splash.api.ATNativeSplash;
import com.anythink.nativead.splash.api.ATNativeSplashListener;
import com.anythink.network.toutiao.TTATConst;
import com.mcore.core.common.utils.CommonUtil;

import java.util.HashMap;
import java.util.Map;

public class NativeSplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_splash);
        FrameLayout splashView = findViewById(R.id.native_splash_view);
        Map<String, Object> localMap = new HashMap<>();
//        //广点通需要加的
//        localMap.put(GDTATConst.ADTYPE, "1");
//        localMap.put(GDTATConst.AD_WIDTH, ADSize.FULL_WIDTH);
//        localMap.put(GDTATConst.AD_HEIGHT, ADSize.FULL_WIDTH);
        //穿山甲个性化模板
        localMap.put(TTATConst.NATIVE_AD_IMAGE_WIDTH, getResources().getDisplayMetrics().widthPixels - CommonUtil.dip2px(this, 20));
        localMap.put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, CommonUtil.dip2px(this, 200));

//        config.mode = ATNativeSplashConfig.ICON_IMAGE_MODE;
        ATNativeSplash splash = new ATNativeSplash(this, splashView, null, DemoApplicaion.mPlacementId_native_all, localMap, new ATNativeSplashListener() {
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
            public void onAdShow(ATAdInfo entity) {
                Log.i("NativeSplashActivity", "Develop callback onAdShow");
            }

            @Override
            public void onAdClick(ATAdInfo entity) {
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
