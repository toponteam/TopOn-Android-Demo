package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uparpu.api.AdError;
import com.uparpu.splashad.api.UpArpuSplashAd;
import com.uparpu.splashad.api.UpArpuSplashAdListener;

public class SplashAdShowActivity extends Activity implements UpArpuSplashAdListener {
    TextView skipView;
    UpArpuSplashAd splashAd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_ad_show);

        String unitId = getIntent().getStringExtra("unitId");
        FrameLayout container = findViewById(R.id.splash_ad_container);
        skipView = findViewById(R.id.splash_ad_skip);
        skipView.setVisibility(View.VISIBLE);

        splashAd = new UpArpuSplashAd(this, container, skipView, unitId, this, null);


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
    public void onAdShow() {
        Log.i("SplashAdShowActivity", "onAdShow---------");
    }

    @Override
    public void onAdClick() {
        Log.i("SplashAdShowActivity", "onAdClick---------");
    }

    @Override
    public void onAdDismiss() {
        Log.i("SplashAdShowActivity", "onAdDismiss---------");
        finish();
        Toast.makeText(this, "start your MainActivity.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdTick(long millisUtilFinished) {
        skipView.setVisibility(View.VISIBLE);
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
