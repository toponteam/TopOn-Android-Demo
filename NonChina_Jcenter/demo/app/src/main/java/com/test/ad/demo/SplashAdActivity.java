package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNativeAdCustomRender;
import com.anythink.core.api.ATNativeAdInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.bean.CommonViewBean;
import com.test.ad.demo.util.MediationNativeAdUtil;

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
        mSplashAd = new ATSplashAd(this, placementId, new ATSplashExListenerImpl(), 5000);
        Map<String, Object> localMap = new HashMap<>();

        mSplashAd.setLocalExtra(localMap);
        mSplashAd.setAdSourceStatusListener(new ATAdSourceStatusListenerImpl());
        mSplashAd.setAdRevenueListener(new AdRevenueListenerImpl());
    }

    private void loadAd() {
        printLogOnUI(getString(R.string.anythink_ad_status_loading));
        if (mSplashAd != null) {
            //开屏广告使用原生自渲染广告时，设置自定义渲染方式：只需要在发起请求时额外设置setNativeAdCustomRender即可，请求、展示广告流程同开屏广告接入流程相同。
            mSplashAd.setNativeAdCustomRender(new NativeAdCustomRender(this));
            mSplashAd.loadAd();
        }
    }

    private void showAd() {
        if (mSplashAd == null) {
            return;
        }
        final String placementId = mCurrentPlacementId;
        ATSplashAd.entryAdScenario(placementId, AdConst.SCENARIO_ID.SPLASH_AD_SCENARIO);
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
            mSplashAd.setAdMultipleLoadedListener(null);
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
