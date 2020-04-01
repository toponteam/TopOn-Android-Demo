package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdListener;

public class SplashAdShowActivity extends Activity implements ATSplashAdListener {
    TextView skipView;
    ATSplashAd splashAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_ad_show);

        String unitId = getIntent().getStringExtra("unitId");
        FrameLayout container = findViewById(R.id.splash_ad_container);
        skipView = findViewById(R.id.splash_ad_skip); /**Skipview must be visible.**/

        splashAd = new ATSplashAd(this, container, skipView, unitId, this);


    }

    @Override
    public void onAdLoaded() {
        Log.i("SplashAdShowActivity", "onAdLoaded---------");
    }

    @Override
    public void onNoAdError(AdError adError) {
        Log.i("SplashAdShowActivity", "onNoAdError---------:" + adError.printStackTrace());
        finish();
        Toast.makeText(this, "start your MainActivity.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdShow(ATAdInfo entity) {
        skipView.setBackgroundColor(0xff868282);
        Log.i("SplashAdShowActivity", "onAdShow:\n" + entity.toString());
    }

    @Override
    public void onAdClick(ATAdInfo entity) {
        Log.i("SplashAdShowActivity", "onAdClick:\n" + entity.toString());
    }

    @Override
    public void onAdDismiss(ATAdInfo entity) {
        Log.i("SplashAdShowActivity", "onAdDismiss:\n" + entity.toString());
        finish();
        Toast.makeText(this, "start your MainActivity.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdTick(long millisUtilFinished) {
        Log.i("SplashAdShowActivity", "onAdTick---------：" + millisUtilFinished);
        skipView.setText(String.valueOf(millisUtilFinished / 1000) + "| SKIP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashAd != null) {
            splashAd.onDestory();
        }

    }
}
