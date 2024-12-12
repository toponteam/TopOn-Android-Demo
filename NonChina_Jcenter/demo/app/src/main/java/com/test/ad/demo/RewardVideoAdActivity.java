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
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.ATShowConfig;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoAutoAd;
import com.anythink.rewardvideo.api.ATRewardVideoAutoEventListener;
import com.anythink.rewardvideo.api.ATRewardVideoAutoLoadListener;
import com.anythink.rewardvideo.api.ATRewardVideoExListener;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.bean.CommonViewBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardVideoAdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = RewardVideoAdActivity.class.getSimpleName();

    private ATRewardVideoAd mRewardVideoAd;
    private final Map<String, Boolean> mAutoLoadPlacementIdMap = new HashMap<>();
    private boolean mIsAutoLoad;

    private TextView mTvLoadAdBtn;
    private TextView mTvIsAdReadyBtn;
    private TextView mTvShowAdBtn;
    private CheckBox mCbAutoLoad;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_video;
    }

    @Override
    protected int getAdType() {
        return ATAdConst.ATMixedFormatAdType.REWARDED_VIDEO;
    }

    @Override
    protected void onSelectPlacementId(String placementId) {
        boolean isAutoLoad = Boolean.TRUE.equals(mAutoLoadPlacementIdMap.get(placementId));
        if (mCbAutoLoad != null) {
            mCbAutoLoad.setChecked(isAutoLoad);
        }

        initRewardVideoAd(placementId);
    }

    @Override
    protected CommonViewBean getCommonViewBean() {
        final CommonViewBean commonViewBean = new CommonViewBean();
        commonViewBean.setTitleBar(findViewById(R.id.title_bar));
        commonViewBean.setTvLogView(findViewById(R.id.tv_show_log));
        commonViewBean.setSpinnerSelectPlacement(findViewById(R.id.spinner_1));
        commonViewBean.setTitleResId(R.string.anythink_title_rewarded_video);
        return commonViewBean;
    }

    @Override
    protected void initView() {
        super.initView();
        mTvLoadAdBtn = findViewById(R.id.load_ad_btn);
        mTvIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        mTvShowAdBtn = findViewById(R.id.show_ad_btn);
        initAutoLoad();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTvLoadAdBtn.setOnClickListener(this);
        mTvIsAdReadyBtn.setOnClickListener(this);
        mTvShowAdBtn.setOnClickListener(this);
    }

    private void initRewardVideoAd(String placementId) {
        mRewardVideoAd = new ATRewardVideoAd(this, placementId);

        mRewardVideoAd.setAdRevenueListener(new AdRevenueListenerImpl());
        mRewardVideoAd.setAdListener(new ATRewardVideoExListener() {

            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
                printLogOnUI("onDeeplinkCallback");
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                Log.i(TAG, "onDownloadConfirm: " + adInfo.toString());
                printLogOnUI("onDownloadConfirm");
            }

            //-------------------------- Only for CSJ --------------------------
            @Override
            public void onRewardedVideoAdAgainPlayStart(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayStart:\n" + entity.toString());
                printLogOnUI("onRewardedVideoAdAgainPlayStart");
            }

            @Override
            public void onRewardedVideoAdAgainPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayEnd:\n" + entity.toString());
                printLogOnUI("onRewardedVideoAdAgainPlayEnd");
            }

            @Override
            public void onRewardedVideoAdAgainPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayFailed error: " + errorCode.getFullErrorInfo());
                printLogOnUI("onRewardedVideoAdAgainPlayFailed:" + errorCode.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdAgainPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayClicked: " + entity.toString());
                printLogOnUI("onRewardedVideoAdAgainPlayClicked");
            }

            @Override
            public void onAgainReward(ATAdInfo entity) {
                Log.i(TAG, "onAgainReward:\n" + entity.toString());
                printLogOnUI("onAgainReward");

                printRewardInfo("onAgainReward rewardInfo", entity);
            }

            @Override
            public void onAgainRewardFailed(ATAdInfo entity) {
                Log.i(TAG, "onAgainRewardFailed:\n" + entity.toString());
                printLogOnUI("onAgainRewardFailed");

                printRewardInfo("onAgainRewardFailed rewardInfo", entity);
            }

            @Override
            public void onRewardFailed(ATAdInfo entity) {
                Log.i(TAG, "onRewardFailed:\n" + entity.toString());
                printLogOnUI("onRewardFailed");

                printRewardInfo("onRewardFailed rewardInfo", entity);
            }
            //-------------------------- Only for CSJ --------------------------

            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded");
                printLogOnUI("onRewardedVideoAdLoaded");
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
                Log.i(TAG, "onRewardedVideoAdFailed error:" + errorCode.getFullErrorInfo());
                printLogOnUI("onRewardedVideoAdFailed:" + errorCode.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:\n" + entity.toString());
                printLogOnUI("onRewardedVideoAdPlayStart");
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + entity.toString());
                printLogOnUI("onRewardedVideoAdPlayEnd");
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayFailed:\n" + entity.toString());
                printLogOnUI("onRewardedVideoAdPlayFailed:" + errorCode.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdClosed:\n" + entity.toString());
                printLogOnUI("onRewardedVideoAdClosed");
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + entity.toString());
                printLogOnUI("onRewardedVideoAdPlayClicked");
            }

            @Override
            public void onReward(ATAdInfo entity) {
                Log.e(TAG, "onReward:\n" + entity.toString());
                printLogOnUI("onReward");

                printRewardInfo("onReward rewardInfo", entity);
            }
        });

        mRewardVideoAd.setAdSourceStatusListener(new ATAdSourceStatusListenerImpl());
    }

    private void initAutoLoad() {
        ATRewardVideoAutoAd.init(this, null, autoLoadListener);
        mCbAutoLoad = findViewById(R.id.ck_auto_load);
        mCbAutoLoad.setVisibility(View.VISIBLE);
        mCbAutoLoad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final String placementId = mCurrentPlacementId;
                if (isChecked) {
                    mIsAutoLoad = true;
                    mAutoLoadPlacementIdMap.put(placementId, true);
                    ATRewardVideoAutoAd.addPlacementId(placementId);
                    mTvLoadAdBtn.setVisibility(View.GONE);
                } else {
                    mIsAutoLoad = false;
                    mAutoLoadPlacementIdMap.put(placementId, false);
                    ATRewardVideoAutoAd.removePlacementId(placementId);
                    mTvLoadAdBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadAd() {
        if (mRewardVideoAd == null) {
            printLogOnUI("ATRewardVideoAd is not init.");
            return;
        }
        printLogOnUI(getString(R.string.anythink_ad_status_loading));

        String userid = "test_userid_001";
        String userdata = "test_userdata_001";
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.USER_ID, userid);
        localMap.put(ATAdConst.KEY.USER_CUSTOM_DATA, userdata);

        mRewardVideoAd.setLocalExtra(localMap);
        mRewardVideoAd.load();
    }

    private void isAdReady() {
        if (mRewardVideoAd == null) {
            return;
        }
        if (mIsAutoLoad) {
            printLogOnUI("video auto load ad ready status:" + ATRewardVideoAutoAd.isAdReady(mCurrentPlacementId));
        } else {
//        boolean isReady = mRewardVideoAd.isAdReady();
            ATAdStatusInfo atAdStatusInfo = mRewardVideoAd.checkAdStatus();
            printLogOnUI("video ad ready status:" + atAdStatusInfo.isReady());

            List<ATAdInfo> atAdInfoList = mRewardVideoAd.checkValidAdCaches();
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
//            ATRewardVideoAutoAd.show(this, mCurrentPlacementId, autoEventListener);
            ATRewardVideoAutoAd.show(this, mCurrentPlacementId, getATShowConfig(), autoEventListener, new AdRevenueListenerImpl());
        } else {
//            mRewardVideoAd.show(RewardVideoAdActivity.this);
            mRewardVideoAd.show(RewardVideoAdActivity.this, getATShowConfig());
        }
    }

    public static final ATRewardVideoAutoLoadListener autoLoadListener = new ATRewardVideoAutoLoadListener() {
        @Override
        public void onRewardVideoAutoLoaded(String placementId) {
            initPlacementIdLocalExtra(placementId);
            Log.i(TAG, "PlacementId:" + placementId + ": onRewardVideoAutoLoaded");
            printLogOnUI("PlacementId:" + placementId + ": onRewardVideoAutoLoaded");
        }

        @Override
        public void onRewardVideoAutoLoadFail(String placementId, AdError adError) {
            Log.i(TAG, "PlacementId:" + placementId + ": onRewardVideoAutoLoadFail:\n" + adError.getFullErrorInfo());
            printLogOnUI("PlacementId:" + placementId + ": onRewardVideoAutoLoadFail:\n" + adError.getFullErrorInfo());
        }
    };

    public static void initPlacementIdLocalExtra(String placementId) {
        String userid = "test_userid_001";
        String userdata = "test_userdata_001_" + placementId + "_" + System.currentTimeMillis();
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.USER_ID, userid);
        localMap.put(ATAdConst.KEY.USER_CUSTOM_DATA, userdata);
        Log.i(TAG, "Set PlacementId:" + placementId + ": UserId:" + userid + "| userdata:" + userdata);
        ATRewardVideoAutoAd.setLocalExtra(placementId, localMap);
    }

    private static final ATRewardVideoAutoEventListener autoEventListener = new ATRewardVideoAutoEventListener() {

        @Override
        public void onRewardedVideoAdPlayStart(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdPlayStart:\n" + adInfo.toString());
            printLogOnUI("onRewardedVideoAdPlayStart:");
        }

        @Override
        public void onRewardedVideoAdPlayEnd(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + adInfo.toString());
            printLogOnUI("onRewardedVideoAdPlayEnd");
        }

        @Override
        public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdPlayFailed:\n" + adInfo.toString());
            printLogOnUI("onRewardedVideoAdPlayFailed");
        }

        @Override
        public void onRewardedVideoAdClosed(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdClosed:\n" + adInfo.toString());
            printLogOnUI("onRewardedVideoAdClosed");
        }

        @Override
        public void onRewardedVideoAdPlayClicked(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + adInfo.toString());
            printLogOnUI("onRewardedVideoAdPlayClicked");
        }

        @Override
        public void onReward(ATAdInfo adInfo) {
            Log.e(TAG, "onReward:\n" + adInfo.toString());
            printLogOnUI("onReward");

            printRewardInfo("onReward rewardInfo", adInfo);
        }

        @Override
        public void onRewardFailed(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardFailed:\n" + adInfo.toString());
            printLogOnUI("onRewardFailed");

            printRewardInfo("onRewardFailed rewardInfo", adInfo);
        }

        public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
            Log.i(TAG, "onDeeplinkCallback:\n" + adInfo.toString() + "| isSuccess:" + isSuccess);
            printLogOnUI("onDeeplinkCallback");
        }

        public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
            Log.i(TAG, "onDownloadConfirm:\n" + adInfo.toString());
            printLogOnUI("onDownloadConfirm");
        }

        //again listener
        public void onRewardedVideoAdAgainPlayStart(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdAgainPlayStart:\n" + adInfo.toString());
            printLogOnUI("onRewardedVideoAdAgainPlayStart");
        }

        public void onRewardedVideoAdAgainPlayEnd(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdAgainPlayEnd:\n" + adInfo.toString());
            printLogOnUI("onRewardedVideoAdAgainPlayEnd");
        }

        public void onRewardedVideoAdAgainPlayFailed(AdError adError, ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdAgainPlayFailed:\n" + adInfo.toString() + "｜error：" + adError.getFullErrorInfo());
            printLogOnUI("onRewardedVideoAdAgainPlayFailed");
        }

        public void onRewardedVideoAdAgainPlayClicked(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdAgainPlayClicked:\n" + adInfo.toString());
            printLogOnUI("onRewardedVideoAdAgainPlayClicked");
        }

        public void onAgainReward(ATAdInfo adInfo) {
            Log.i(TAG, "onAgainReward:\n" + adInfo.toString());
            printLogOnUI("onAgainReward");

            printRewardInfo("onAgainReward rewardInfo", adInfo);
        }

        @Override
        public void onAgainRewardFailed(ATAdInfo adInfo) {
            Log.i(TAG, "onAgainRewardFailed:\n" + adInfo.toString());
            printLogOnUI("onAgainRewardFailed");

            printRewardInfo("onAgainRewardFailed rewardInfo", adInfo);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Map.Entry<String, Boolean> entry : mAutoLoadPlacementIdMap.entrySet()) {
            ATRewardVideoAutoAd.removePlacementId(entry.getKey());
        }
        if (mRewardVideoAd != null) {
            mRewardVideoAd.setAdSourceStatusListener(null);
            mRewardVideoAd.setAdDownloadListener(null);
            mRewardVideoAd.setAdListener(null);
            mRewardVideoAd.setAdMultipleLoadedListener(null);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_ad_btn:
                loadAd();
                break;
            case R.id.is_ad_ready_btn:
                isAdReady();
                break;
            case R.id.show_ad_btn:
                ATRewardVideoAd.entryAdScenario(mCurrentPlacementId, AdConst.SCENARIO_ID.REWARD_VIDEO_AD_SCENARIO);
                if (mRewardVideoAd != null && mRewardVideoAd.isAdReady()) {
                    showAd();
                } else {
                    printLogOnUI(getString(R.string.anythink_ad_status_not_load));
                }
                break;
        }
    }

    private ATShowConfig getATShowConfig() {
        ATShowConfig.Builder builder = new ATShowConfig.Builder();
        builder.scenarioId(AdConst.SCENARIO_ID.REWARD_VIDEO_AD_SCENARIO);
        builder.showCustomExt(AdConst.SHOW_CUSTOM_EXT.REWARD_VIDEO_AD_SHOW_CUSTOM_EXT);

        return builder.build();
    }

    private static void printRewardInfo(String msg, ATAdInfo entity) {
        Map<String, Object> extInfoMap = entity.getExtInfoMap();
        if (extInfoMap != null) {
            Object rewardInfoObj = extInfoMap.get(ATAdConst.REWARD_EXTRA.REWARD_INFO);
            if (rewardInfoObj instanceof Map) {
                Map<String, Object> rewardMap = (Map<String, Object>) rewardInfoObj;

                for (Map.Entry<String, Object> stringObjectEntry : rewardMap.entrySet()) {
                    String key = stringObjectEntry.getKey();
                    Object value = stringObjectEntry.getValue();

                    Log.e(TAG, msg + ": key=" + key + ", value=" + value);
                }
            }
        }
    }
}

