/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.bean.CommonViewBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashAdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SplashAdActivity";

    private ATSplashAd mSplashAd;


//    private Spinner mSpinnerPlacementId;

    @Override
    public int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected int getAdType() {
        return ATAdConst.ATMixedFormatAdType.SPLASH;
    }

    @Override
    public void initView() {
        super.initView();
        findViewById(R.id.rl_type).setSelected(true);
    }

    @Override
    protected CommonViewBean getCommonViewBean() {
        CommonViewBean commonViewBean = new CommonViewBean();
        commonViewBean.setTitleBar(findViewById(R.id.title_bar));
        commonViewBean.setTvLogView(findViewById(R.id.tv_show_log));
        commonViewBean.setSpinnerSelectPlacement(findViewById(R.id.spinner_1));
        commonViewBean.setTitleResId(R.string.anythink_title_splash);
        return commonViewBean;
    }

    @Override
    protected void initListener() {
        findViewById(R.id.is_ad_ready_btn).setOnClickListener(this);
        findViewById(R.id.load_ad_btn).setOnClickListener(this);
        findViewById(R.id.show_ad_btn).setOnClickListener(this);
    }

    @Override
    protected void onSelectPlacementId(String placementId) {
        initSplashAd(placementId);
    }

    private void initSplashAd(String placementId) {
        String defaultConfig = "";
        //Mintegral
//        defaultConfig = "{\"unit_id\":1333033,\"nw_firm_id\":6,\"adapter_class\":\"com.anythink.network.mintegral.MintegralATSplashAdapter\",\"content\":\"{\\\"placement_id\\\":\\\"210169\\\",\\\"unitid\\\":\\\"276803\\\",\\\"countdown\\\":\\\"5\\\",\\\"allows_skip\\\":\\\"1\\\",\\\"orientation\\\":\\\"1\\\",\\\"appkey\\\":\\\"ef13ef712aeb0f6eb3d698c4c08add96\\\",\\\"suport_video\\\":\\\"1\\\",\\\"appid\\\":\\\"100947\\\"}\"}";

        //Tencent Ads
//        defaultConfig = "{\"unit_id\":1333176,\"nw_firm_id\":8,\"adapter_class\":\"com.anythink.network.gdt.GDTATSplashAdapter\",\"content\":\"{\\\"unit_id\\\":\\\"8863364436303842593\\\",\\\"zoomoutad_sw\\\":\\\"1\\\",\\\"app_id\\\":\\\"1101152570\\\"}\"}";

        //CSJ
//        defaultConfig = "{\"unit_id\":1333195,\"nw_firm_id\":15,\"adapter_class\":\"com.anythink.network.toutiao.TTATSplashAdapter\",\"content\":\"{\\\"personalized_template\\\":\\\"0\\\",\\\"zoomoutad_sw\\\":\\\"2\\\",\\\"button_type\\\":\\\"1\\\",\\\"dl_type\\\":\\\"2\\\",\\\"slot_id\\\":\\\"801121648\\\",\\\"app_id\\\":\\\"5001121\\\"}\"}";

        //Sigmob
//        defaultConfig = "{\"unit_id\":1333222,\"nw_firm_id\":29,\"adapter_class\":\"com.anythink.network.sigmob.SigmobATSplashAdapter\",\"content\":\"{\\\"placement_id\\\":\\\"ea1f8f21300\\\",\\\"app_id\\\":\\\"6878\\\",\\\"app_key\\\":\\\"8ebc1fd1c27e650c\\\"}\"}";

        //Baidu
//        defaultConfig = "{\"unit_id\":1329553,\"nw_firm_id\":22,\"adapter_class\":\"com.anythink.network.baidu.BaiduATSplashAdapter\",\"content\":\"{\\\"button_type\\\":\\\"0\\\",\\\"ad_place_id\\\":\\\"7854679\\\",\\\"app_id\\\":\\\"a7dd29d3\\\"}\"}";

        //Kuaishou
//        defaultConfig = "{\"unit_id\":1333246,\"nw_firm_id\":28,\"adapter_class\":\"com.anythink.network.ks.KSATSplashAdapter\",\"content\":\"{\\\"zoomoutad_sw\\\":\\\"1\\\",\\\"position_id\\\":\\\"4000000042\\\",\\\"app_id\\\":\\\"90009\\\",\\\"app_name\\\":\\\"90009\\\"}\"}";

        //Klevin
//        defaultConfig = "{\"unit_id\":1333253,\"nw_firm_id\":51,\"adapter_class\":\"com.anythink.network.klevin.KlevinATSplashAdapter\",\"content\":\"{\\\"pos_id\\\":\\\"30029\\\",\\\"app_id\\\":\\\"30008\\\"}\"}";

        mSplashAd = new ATSplashAd(this, placementId, new ATSplashExListenerImpl(), 5000, defaultConfig);
        Map<String, Object> localMap = new HashMap<>();
//        localMap.put(ATAdConst.KEY.AD_WIDTH, layoutParams.width);
//        localMap.put(ATAdConst.KEY.AD_HEIGHT, layoutParams.height);

        // Only for GDT (true: open download dialog, false: download directly)
        localMap.put(ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, true);

        mSplashAd.setLocalExtra(localMap);
        mSplashAd.setAdSourceStatusListener(new ATAdSourceStatusListenerImpl());
    }

    private void loadAd() {
        printLogOnUI(getString(R.string.anythink_ad_status_loading));
        if (mSplashAd != null) {
            mSplashAd.loadAd();
        }
    }

    private void showAd() {
        if (mSplashAd == null) {
            return;
        }
        /*
         * To collect scene arrival rate statistics, you can view related information "https://docs.toponad.com/#/en-us/android/NetworkAccess/scenario/scenario"
         * Call the "Enter AD scene" method when an AD trigger condition is met, such as:
         * The scenario is a pop-up AD after the cleanup, which is called at the end of the cleanup.
         * 1、Call "entryAdScenario" to report the arrival of the scene.
         * 2、Call "isAdReady".
         * 3、Call "show" to show AD view.
         */
        final String placementId = mCurrentPlacementId;
        ATSplashAd.entryAdScenario(placementId, "f628c7999265cd");
        if (mSplashAd.isAdReady()) {
            Intent intent = new Intent(SplashAdActivity.this, SplashAdShowActivity.class);
            intent.putExtra("placementId", placementId);
//                intent.putExtra("custom_skip_view", isCustomSkipViewCheckBox.isChecked());
            startActivity(intent);
        }
    }

    private void isAdReady() {
        ATAdStatusInfo atAdStatusInfo = mSplashAd.checkAdStatus();
        if (atAdStatusInfo.isReady()) {
            Log.i(TAG, "SplashAd is ready to show.");
            printLogOnUI( "SplashAd is ready to show.");
        } else {
            Log.i(TAG, "SplashAd isn't ready to show.");
            printLogOnUI( "SplashAd isn't ready to show.");
        }

        List<ATAdInfo> atAdInfoList = mSplashAd.checkValidAdCaches();
        Log.i(TAG, "Valid Cahce size:" + (atAdInfoList != null ? atAdInfoList.size() : 0));
        if (atAdInfoList != null) {
            for (ATAdInfo adInfo : atAdInfoList) {
                Log.i(TAG, "\nCahce detail:" + adInfo.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mSplashAd != null) {
            mSplashAd.setAdListener(null);
            mSplashAd.setAdDownloadListener(null);
            mSplashAd.setAdSourceStatusListener(null);
        }
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.is_ad_ready_btn:
                isAdReady();
                break;
            case R.id.load_ad_btn:
                loadAd();
                break;
            case R.id.show_ad_btn:
                showAd();
                break;
        }
    }

    private static class ATSplashExListenerImpl implements ATSplashExListener {
        @Override
        public void onAdLoaded(boolean isTimeout) {
            Log.i(TAG, "onAdLoaded---------isTimeout:" + isTimeout);
            printLogOnUI("onAdLoaded---------isTimeout:" + isTimeout);
        }

        @Override
        public void onAdLoadTimeout() {
            Log.i(TAG, "onAdLoadTimeout---------");
            printLogOnUI("onAdLoadTimeout---------");
        }

        @Override
        public void onNoAdError(AdError adError) {
            Log.i(TAG, "onNoAdError---------:" + adError.getFullErrorInfo());
            printLogOnUI( "onNoAdError---------:" + adError.getFullErrorInfo());
        }

        @Override
        public void onAdShow(ATAdInfo entity) {
            Log.i(TAG, "onAdShow---------:" + entity.toString());
            printLogOnUI( "onAdShow---------");
        }

        @Override
        public void onAdClick(ATAdInfo entity) {
            Log.i(TAG, "onAdClick---------:" + entity.toString());
            printLogOnUI( "onAdClick---------");
        }

        @Override
        public void onAdDismiss(ATAdInfo entity, ATSplashAdExtraInfo splashAdExtraInfo) {
            Log.i(TAG, "onAdDismiss---------:" + entity.toString());
            printLogOnUI( "onAdDismiss---------");
        }

        @Override
        public void onDeeplinkCallback(ATAdInfo entity, boolean isSuccess) {
            Log.i(TAG, "onDeeplinkCallback---------：" + entity.toString() + " isSuccess = " + isSuccess);
            printLogOnUI( "onDeeplinkCallback---------");
        }

        @Override
        public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
            Log.i(TAG, "onDownloadConfirm--------- entity = " + adInfo.toString());
            printLogOnUI( "onDownloadConfirm---------");
        }
    }
}
