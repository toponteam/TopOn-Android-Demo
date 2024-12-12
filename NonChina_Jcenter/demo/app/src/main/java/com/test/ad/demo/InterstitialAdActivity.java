package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNativeAdCustomRender;
import com.anythink.core.api.ATNativeAdInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.ATShowConfig;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialAutoAd;
import com.anythink.interstitial.api.ATInterstitialAutoEventListener;
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener;
import com.anythink.interstitial.api.ATInterstitialExListener;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.bean.CommonViewBean;
import com.test.ad.demo.util.MediationNativeAdUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterstitialAdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = InterstitialAdActivity.class.getSimpleName();

    private ATInterstitial mInterstitialAd;
    private boolean mIsAutoLoad;
    private final Map<String, Boolean> mAutoLoadPlacementIdMap = new HashMap<>();

    private CheckBox mCBAutoLoad;
    private TextView mTVLoadAdBtn;
    private TextView mTVIsAdReadyBtn;
    private TextView mTVShowAdBtn;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_interstitial;
    }

    @Override
    protected int getAdType() {
        return ATAdConst.ATMixedFormatAdType.INTERSTITIAL;
    }

    @Override
    protected void onSelectPlacementId(String placementId) {
        boolean isAutoLoad = Boolean.TRUE.equals(mAutoLoadPlacementIdMap.get(placementId));
        if (mCBAutoLoad != null) {
            mCBAutoLoad.setChecked(isAutoLoad);
        }
        initInterstitialAd(placementId);
    }

    @Override
    protected CommonViewBean getCommonViewBean() {
        final CommonViewBean commonViewBean = new CommonViewBean();
        commonViewBean.setTitleBar(findViewById(R.id.title_bar));
        commonViewBean.setSpinnerSelectPlacement(findViewById(R.id.spinner_1));
        commonViewBean.setTvLogView(findViewById(R.id.tv_show_log));
        commonViewBean.setTitleResId(R.string.anythink_title_interstitial);

        return commonViewBean;
    }

    @Override
    protected void initView() {
        super.initView();
        mTVLoadAdBtn = findViewById(R.id.load_ad_btn);
        mTVIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        mTVShowAdBtn = findViewById(R.id.show_ad_btn);

        initAutoLoad();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTVLoadAdBtn.setOnClickListener(this);
        mTVIsAdReadyBtn.setOnClickListener(this);
        mTVShowAdBtn.setOnClickListener(this);
    }

    private void initInterstitialAd(String placementId) {
        mInterstitialAd = new ATInterstitial(this, placementId);
        mInterstitialAd.setAdRevenueListener(new AdRevenueListenerImpl());
        mInterstitialAd.setAdListener(new ATInterstitialExListener() {

            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
                printLogOnUI("onDeeplinkCallback");
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                Log.i(TAG, "onDownloadConfirm: adInfo=" + adInfo.toString());
                printLogOnUI("onDownloadConfirm");
            }

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                printLogOnUI("onInterstitialAdLoaded");
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Log.i(TAG, "onInterstitialAdLoadFail:\n" + adError.getFullErrorInfo());
                printLogOnUI("onInterstitialAdLoadFail:" + adError.getFullErrorInfo());
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClicked:\n" + entity.toString());
                printLogOnUI("onInterstitialAdClicked");
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdShow:\n" + entity.toString());
                printLogOnUI("onInterstitialAdShow");
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClose:\n" + entity.toString());
                printLogOnUI("onInterstitialAdClose");
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoStart:\n" + entity.toString());
                printLogOnUI("onInterstitialAdVideoStart");
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoEnd:\n" + entity.toString());
                printLogOnUI("onInterstitialAdVideoEnd");
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Log.i(TAG, "onInterstitialAdVideoError:\n" + adError.getFullErrorInfo());
                printLogOnUI("onInterstitialAdVideoError");
            }

        });

        mInterstitialAd.setAdSourceStatusListener(new ATAdSourceStatusListenerImpl());
    }

    private void initAutoLoad() {
        ATInterstitialAutoAd.init(this, null, autoLoadListener);
        mCBAutoLoad = findViewById(R.id.ck_auto_load);
        mCBAutoLoad.setVisibility(View.VISIBLE);
        mCBAutoLoad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsAutoLoad = true;

                    final String curPlacementId = mCurrentPlacementId;
                    mAutoLoadPlacementIdMap.put(curPlacementId, true);
                    ATInterstitialAutoAd.addPlacementId(curPlacementId);
                    mTVLoadAdBtn.setVisibility(View.GONE);
                } else {
                    mIsAutoLoad = false;

                    final String curPlacementId = mCurrentPlacementId;
                    mAutoLoadPlacementIdMap.put(curPlacementId, false);
                    ATInterstitialAutoAd.removePlacementId(curPlacementId);
                    mTVLoadAdBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadAd() {
        if (mInterstitialAd == null) {
            printLogOnUI("ATInterstitial is not init.");
            return;
        }
        printLogOnUI(getString(R.string.anythink_ad_status_loading));

        Map<String, Object> localMap = new HashMap<>();

//        localMap.put(ATAdConst.KEY.AD_WIDTH, getResources().getDisplayMetrics().widthPixels);
//        localMap.put(ATAdConst.KEY.AD_HEIGHT, getResources().getDisplayMetrics().heightPixels);

        //插屏广告使用原生自渲染广告时，设置自定义渲染方式：只需要在发起请求时额外设置setNativeAdCustomRender即可，请求、展示广告流程同插屏广告接入流程相同。
        mInterstitialAd.setNativeAdCustomRender(new NativeAdCustomRender(this));
        mInterstitialAd.setLocalExtra(localMap);
        mInterstitialAd.load();
    }

    private void isAdReady() {
        if (mIsAutoLoad) {
            printLogOnUI("interstitial auto load ad ready status:" + ATInterstitialAutoAd.isAdReady(mCurrentPlacementId));
        } else {
//         boolean isReady = mInterstitialAd.isAdReady();
            ATAdStatusInfo atAdStatusInfo = mInterstitialAd.checkAdStatus();
            printLogOnUI("interstitial ad ready status:" + atAdStatusInfo.isReady());
            List<ATAdInfo> atAdInfoList = mInterstitialAd.checkValidAdCaches();
            Log.i(TAG, "Valid Cahce size:" + (atAdInfoList != null ? atAdInfoList.size() : 0));
            if (atAdInfoList != null) {
                for (ATAdInfo adInfo : atAdInfoList) {
                    Log.i(TAG, "\nCahce detail:" + adInfo.toString());
                }
            }
        }
    }

    private void showAd() {
        if (mIsAutoLoad) {
//            ATInterstitialAutoAd.show(this, mCurrentPlacementId, autoEventListener);
            ATInterstitialAutoAd.show(this, mCurrentPlacementId, getATShowConfig(), autoEventListener, new AdRevenueListenerImpl());
        } else {
//            mInterstitialAd.show(InterstitialAdActivity.this);
            mInterstitialAd.show(InterstitialAdActivity.this, getATShowConfig());
        }
    }

    private static final ATInterstitialAutoLoadListener autoLoadListener = new ATInterstitialAutoLoadListener() {
        @Override
        public void onInterstitialAutoLoaded(String placementId) {
            Log.i(TAG, "PlacementId:" + placementId + ": onInterstitialAutoLoaded");
            printLogOnUI("PlacementId:" + placementId + ": onInterstitialAutoLoaded");
        }

        @Override
        public void onInterstitialAutoLoadFail(String placementId, AdError adError) {
            Log.i(TAG, "PlacementId:" + placementId + ": onInterstitialAutoLoadFail:\n" + adError.getFullErrorInfo());
            printLogOnUI("PlacementId:" + placementId + ": onInterstitialAutoLoadFail:\n" + adError.getFullErrorInfo());
        }

    };

    private static final ATInterstitialAutoEventListener autoEventListener = new ATInterstitialAutoEventListener() {
        @Override
        public void onInterstitialAdClicked(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdClicked:" + adInfo.toString());
            printLogOnUI("onInterstitialAdClicked:");
        }

        @Override
        public void onInterstitialAdShow(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdShow:" + adInfo.toString());
            printLogOnUI("onInterstitialAdShow");
        }

        @Override
        public void onInterstitialAdClose(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdClose:" + adInfo.toString());
            printLogOnUI("onInterstitialAdClose");
        }

        @Override
        public void onInterstitialAdVideoStart(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdVideoStart:" + adInfo.toString());
            printLogOnUI("onInterstitialAdVideoStart");
        }

        @Override
        public void onInterstitialAdVideoEnd(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdVideoEnd:" + adInfo.toString());
            printLogOnUI("onInterstitialAdVideoEnd");
        }

        @Override
        public void onInterstitialAdVideoError(AdError adError) {
            Log.i(TAG, "onInterstitialAdVideoError:" + adError.getFullErrorInfo());
            printLogOnUI("onInterstitialAdVideoError:" + adError.getFullErrorInfo());
        }

        public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
            Log.i(TAG, "onDeeplinkCallback:\n" + adInfo.toString() + "| isSuccess:" + isSuccess);
            printLogOnUI("onDeeplinkCallback: isSuccess=" + isSuccess);
        }

        public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
            Log.i(TAG, "onDownloadConfirm:\n" + adInfo.toString());
            printLogOnUI("onDownloadConfirm");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Map.Entry<String, Boolean> entry : mAutoLoadPlacementIdMap.entrySet()) {
            ATInterstitialAutoAd.removePlacementId(entry.getKey());
        }

        if (mInterstitialAd != null) {
            mInterstitialAd.setAdSourceStatusListener(null);
            mInterstitialAd.setAdDownloadListener(null);
            mInterstitialAd.setAdListener(null);
            mInterstitialAd.setAdMultipleLoadedListener(null);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v == null) return;
        switch (v.getId()) {
            case R.id.load_ad_btn:
                loadAd();
                break;
            case R.id.is_ad_ready_btn:
                isAdReady();
                break;
            case R.id.show_ad_btn:
                ATInterstitial.entryAdScenario(mCurrentPlacementId, AdConst.SCENARIO_ID.INTERSTITIAL_AD_SCENARIO);
                if (mInterstitialAd.isAdReady()) {
                    showAd();
                }
                break;
        }
    }

    private ATShowConfig getATShowConfig() {
        ATShowConfig.Builder builder = new ATShowConfig.Builder();
        builder.scenarioId(AdConst.SCENARIO_ID.INTERSTITIAL_AD_SCENARIO);
        builder.showCustomExt(AdConst.SHOW_CUSTOM_EXT.INTERSTITIAL_AD_SHOW_CUSTOM_EXT);

        return builder.build();
    }
}

