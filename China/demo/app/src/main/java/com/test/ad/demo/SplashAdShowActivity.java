/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATMediationRequestInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.network.gdt.GDTDownloadFirmInfo;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashExListenerWithConfirmInfo;
import com.test.ad.demo.gdt.DownloadApkConfirmDialogWebView;

import java.util.HashMap;
import java.util.Map;

public class SplashAdShowActivity extends Activity implements ATSplashExListenerWithConfirmInfo {

    private static final String TAG = SplashAdShowActivity.class.getSimpleName();

    ATSplashAd splashAd;
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_ad_show);

        String placementId = getIntent().getStringExtra("placementId");
        container = findViewById(R.id.splash_ad_container);
        ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
        Configuration cf = getResources().getConfiguration();

        int ori = cf.orientation;

        /**You should set size to the layout param.**/
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            layoutParams.height = getResources().getDisplayMetrics().heightPixels;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            layoutParams.width = getResources().getDisplayMetrics().widthPixels;
            layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            layoutParams.width = getResources().getDisplayMetrics().widthPixels;
            layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.85);
        }

        ATMediationRequestInfo atMediationRequestInfo = null;

//        atMediationRequestInfo = new MintegralATRequestInfo("100947", "ef13ef712aeb0f6eb3d698c4c08add96", "210169", "276803");
//        atMediationRequestInfo.setAdSourceId("71606");
//
//        atMediationRequestInfo = new GDTATRequestInfo("1101152570", "8863364436303842593");
//        atMediationRequestInfo.setAdSourceId("71602");
//
//        atMediationRequestInfo = new TTATRequestInfo("5020321", "820321537", false);
//        atMediationRequestInfo.setAdSourceId("71600");

//        atMediationRequestInfo = new SigmobiATRequestInfo("1282", "27531c7c64157934", "e04d1ac9231");
//        atMediationRequestInfo.setAdSourceId("71608");
//
//        atMediationRequestInfo = new BaiduATRequestInfo("e866cfb0", "2058622");
//        atMediationRequestInfo.setAdSourceId("71609");

//        atMediationRequestInfo  = new KSATRequestInfo("501400010", "5014000234");
//        atMediationRequestInfo.setAdSourceId("88377");
        splashAd = new ATSplashAd(this, placementId, atMediationRequestInfo, this, 5000);

        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.AD_WIDTH, layoutParams.width);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, layoutParams.height);

        // Only for GDT (true: open download dialog, false: download directly)
        localMap.put(ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, true);

        splashAd.setLocalExtra(localMap);

        if (splashAd.isAdReady()) {
            Log.i(TAG, "SplashAd is ready to show.");
            splashAd.show(this, container);
        } else {
            Log.i(TAG, "SplashAd isn't ready to show, start to request.");
            splashAd.loadAd();
        }


        ATSplashAd.checkSplashDefaultConfigList(this, placementId, null);
    }

    @Override
    public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
        Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
    }

    @Override
    public void onAdLoaded() {
        Log.i(TAG, "onAdLoaded---------");
        splashAd.show(this, container);
    }

    @Override
    public void onNoAdError(AdError adError) {
        Log.i(TAG, "onNoAdError---------:" + adError.getFullErrorInfo());
        jumpToMainActivity();
    }

    @Override
    public void onAdShow(ATAdInfo entity) {
        Log.i(TAG, "onAdShow:\n" + entity.toString());
    }

    @Override
    public void onAdClick(ATAdInfo entity) {
        Log.i(TAG, "onAdClick:\n" + entity.toString());
    }

    @Override
    public void onAdDismiss(ATAdInfo entity) {
        Log.i(TAG, "onAdDismiss:\n" + entity.toString());
        jumpToMainActivity();
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

    @Override
    public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
        /**
         * Only for GDT
         */
        if (networkConfirmInfo instanceof GDTDownloadFirmInfo) {
            //Open Dialog view
            new DownloadApkConfirmDialogWebView(context, ((GDTDownloadFirmInfo) networkConfirmInfo).appInfoUrl, ((GDTDownloadFirmInfo) networkConfirmInfo).confirmCallBack).show();
            Log.i(TAG, "nonDownloadConfirm open confirm dialog");
        }
    }
}
