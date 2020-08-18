package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATMediationRequestInfo;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdListener;

public class SplashAdShowActivity extends Activity implements ATSplashAdListener {
    ATSplashAd splashAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_ad_show);

        String unitId = getIntent().getStringExtra("unitId");
        FrameLayout container = findViewById(R.id.splash_ad_container);
        ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
        /**You should set size to the layout param.**/
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85);

        ATMediationRequestInfo atMediationRequestInfo = null;
//        atMediationRequestInfo = new MintegralATRequestInfo("100947", "ef13ef712aeb0f6eb3d698c4c08add96", "210169", "276803");
//        atMediationRequestInfo.setAdSourceId("68188");
        splashAd = new ATSplashAd(this, container, unitId, atMediationRequestInfo, this);


    }

    @Override
    public void onAdLoaded() {
        Log.i("SplashAdShowActivity", "onAdLoaded---------");
    }

    @Override
    public void onNoAdError(AdError adError) {
        Log.i("SplashAdShowActivity", "onNoAdError---------:" + adError.printStackTrace());
        jumpToMainActivity();
    }

    @Override
    public void onAdShow(ATAdInfo entity) {
        Log.i("SplashAdShowActivity", "onAdShow:\n" + entity.toString());
    }


    @Override
    public void onAdClick(ATAdInfo entity) {
        Log.i("SplashAdShowActivity", "onAdClick:\n" + entity.toString());
    }

    @Override
    public void onAdDismiss(ATAdInfo entity) {
        Log.i("SplashAdShowActivity", "onAdDismiss:\n" + entity.toString());
        jumpToMainActivity();
    }

    @Override
    public void onAdTick(long millisUtilFinished) {
        Log.i("SplashAdShowActivity", "onAdTick---------：" + millisUtilFinished);
    }

    boolean hasHandleJump = false;

    public void jumpToMainActivity() {
        if (!hasHandleJump) {
            hasHandleJump = true;
            finish();
            Toast.makeText(this, "start your MainActivity.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashAd != null) {
            splashAd.onDestory();
        }

    }
}
