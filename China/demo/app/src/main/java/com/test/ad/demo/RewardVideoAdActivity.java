/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.anythink.china.api.ATAppDownloadListener;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdSourceStatusListener;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.network.gdt.GDTDownloadFirmInfo;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoAutoAd;
import com.anythink.rewardvideo.api.ATRewardVideoAutoEventListener;
import com.anythink.rewardvideo.api.ATRewardVideoAutoLoadListener;
import com.anythink.rewardvideo.api.ATRewardVideoExListener;
import com.test.ad.demo.gdt.DownloadApkConfirmDialogWebView;
import com.test.ad.demo.util.PlacementIdUtil;
import com.test.ad.demo.utils.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardVideoAdActivity extends Activity {

    private static final String TAG = RewardVideoAdActivity.class.getSimpleName();

    ATRewardVideoAd mRewardVideoAd;
    private String mCurrentNetworkName;
    private Map<String, String> mPlacementIdMap;
    private Map<String, Boolean> mAutoLoadPlacementIdMap = new HashMap<>();
    private boolean isAutoLoad;

    private TextView tvLoadAdBtn;
    private TextView tvIsAdReadyBtn;
    private TextView tvShowAdBtn;
    private TextView tvShowLog;
    private CheckBox ckAutoLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);

        ATRewardVideoAutoAd.init(this, null, autoLoadListener);

        findViewById(R.id.rl_type).setSelected(true);

        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.anythink_title_rewarded_video);
        titleBar.setListener(new TitleBarClickListener() {
            @Override
            public void onBackClick(View v) {
                finish();
            }
        });

        tvShowLog = findViewById(R.id.tv_show_log);
        tvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvLoadAdBtn = findViewById(R.id.load_ad_btn);
        tvIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        tvShowAdBtn = findViewById(R.id.show_ad_btn);

        mPlacementIdMap = PlacementIdUtil.getRewardedVideoPlacements(this);
        List<String> placementNameList = new ArrayList<>(mPlacementIdMap.keySet());
        mCurrentNetworkName = placementNameList.get(0);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                placementNameList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mCurrentNetworkName = parent.getItemAtPosition(position).toString();
//                Toast.makeText(RewardVideoAdActivity.this,
//                        mCurrentNetworkName,
//                        Toast.LENGTH_SHORT).show();

                String placementId = mPlacementIdMap.get(mCurrentNetworkName);
                init(placementId);

                if (mAutoLoadPlacementIdMap.get(placementId) != null && mAutoLoadPlacementIdMap.get(placementId)) {
                    ckAutoLoad.setChecked(true);
                } else {
                    ckAutoLoad.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        initAutoLoad();

        String placementId = mPlacementIdMap.get(mCurrentNetworkName);
        init(placementId);


        tvIsAdReadyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdReady();
            }
        });

        tvLoadAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd();
            }
        });

        tvShowAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd();
            }
        });

    }

    private void init(String placementId) {
        mRewardVideoAd = new ATRewardVideoAd(this, placementId);
        ATRewardVideoAd.entryAdScenario(placementId, "f5e5492eca9668");

        mRewardVideoAd.setAdListener(new ATRewardVideoExListener() {

            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                /**
                 * Only for GDT
                 */
                if (networkConfirmInfo instanceof GDTDownloadFirmInfo) {
                    //Open Dialog view
                    //Open Dialog view
                    try {
                        new DownloadApkConfirmDialogWebView(context, ((GDTDownloadFirmInfo) networkConfirmInfo).appInfoUrl, ((GDTDownloadFirmInfo) networkConfirmInfo).confirmCallBack).show();
                        Log.i(TAG, "nonDownloadConfirm open confirm dialog");
                    } catch (Throwable e) {
                        if (((GDTDownloadFirmInfo) networkConfirmInfo).confirmCallBack != null) {
                            ((GDTDownloadFirmInfo) networkConfirmInfo).confirmCallBack.onConfirm();
                        }
                    }
                }
            }

            //-------------------------- Only for CSJ --------------------------
            @Override
            public void onRewardedVideoAdAgainPlayStart(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayStart:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdAgainPlayStart");
            }

            @Override
            public void onRewardedVideoAdAgainPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayEnd:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdAgainPlayEnd");
            }

            @Override
            public void onRewardedVideoAdAgainPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayFailed error: " + errorCode.getFullErrorInfo());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdAgainPlayFailed:" + errorCode.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdAgainPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayClicked: " + entity.toString());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdAgainPlayClicked");
            }

            @Override
            public void onAgainReward(ATAdInfo entity) {
                Log.i(TAG, "onAgainReward:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onAgainReward");
            }
            //-------------------------- Only for CSJ --------------------------

            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded");
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdLoaded");
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
                Log.i(TAG, "onRewardedVideoAdFailed error:" + errorCode.getFullErrorInfo());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdFailed:" + errorCode.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdPlayStart");
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdPlayEnd");
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdPlayFailed:" + errorCode.getFullErrorInfo());
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdClosed:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdClosed");
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onRewardedVideoAdPlayClicked");
            }

            @Override
            public void onReward(ATAdInfo entity) {
                Log.e(TAG, "onReward:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog,"onReward");
            }
        });

        mRewardVideoAd.setAdDownloadListener(new ATAppDownloadListener() {

            @Override
            public void onDownloadStart(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadStart: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadUpdate(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadUpdate: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadPause(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                ViewUtil.printLog(tvShowLog,"ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadPause: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadFinish(ATAdInfo adInfo, long totalBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadFinish: totalBytes: " + totalBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadFail(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadFail: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onInstalled(ATAdInfo adInfo, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onInstalled:"
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }
        });

        mRewardVideoAd.setAdSourceStatusListener(new ATAdSourceStatusListener() {
            @Override
            public void onAdSourceBiddingAttempt(ATAdInfo adInfo) {
                Log.i(TAG, "onAdSourceBiddingAttempt: " + adInfo.toString());
            }

            @Override
            public void onAdSourceBiddingFilled(ATAdInfo adInfo) {
                Log.i(TAG, "onAdSourceBiddingFilled: " + adInfo.toString());
            }

            @Override
            public void onAdSourceBiddingFail(ATAdInfo adInfo, AdError adError) {
                Log.i(TAG, "onAdSourceBiddingFail Info: " + adInfo.toString());
                Log.i(TAG, "onAdSourceBiddingFail error: " + adError.getFullErrorInfo());
            }

            @Override
            public void onAdSourceAttempt(ATAdInfo adInfo) {
                Log.i(TAG, "onAdSourceAttempt: " + adInfo.toString());
            }

            @Override
            public void onAdSourceLoadFilled(ATAdInfo adInfo) {
                Log.i(TAG, "onAdSourceLoadFilled: " + adInfo.toString());
            }

            @Override
            public void onAdSourceLoadFail(ATAdInfo adInfo, AdError adError) {
                Log.i(TAG, "onAdSourceLoadFail Info: " + adInfo.toString());
                Log.i(TAG, "onAdSourceLoadFail error: " + adError.getFullErrorInfo());
            }
        });

    }

    private void initAutoLoad() {
        ckAutoLoad = findViewById(R.id.ck_auto_load);
        ckAutoLoad.setVisibility(View.VISIBLE);
        ckAutoLoad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAutoLoad = true;
                    mAutoLoadPlacementIdMap.put(mPlacementIdMap.get(mCurrentNetworkName),true);
                    ATRewardVideoAutoAd.addPlacementId(mPlacementIdMap.get(mCurrentNetworkName));
                    tvLoadAdBtn.setVisibility(View.GONE);
                } else {
                    isAutoLoad = false;
                    mAutoLoadPlacementIdMap.put(mPlacementIdMap.get(mCurrentNetworkName),false);
                    ATRewardVideoAutoAd.removePlacementId(mPlacementIdMap.get(mCurrentNetworkName));
                    tvLoadAdBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadAd() {
        String userid = "test_userid_001";
        String userdata = "test_userdata_001";
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.USER_ID, userid);
        localMap.put(ATAdConst.KEY.USER_CUSTOM_DATA, userdata);

        // Only for GDT (true: open download dialog, false: download directly)
        localMap.put(ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, true);

        mRewardVideoAd.setLocalExtra(localMap);
        mRewardVideoAd.load();
    }

    private void isAdReady() {
        if (isAutoLoad) {
            ViewUtil.printLog(tvShowLog, "video auto load ad ready status:" + ATRewardVideoAutoAd.isAdReady(mPlacementIdMap.get(mCurrentNetworkName)));
        } else {
//        boolean isReady = mRewardVideoAd.isAdReady();
            ATAdStatusInfo atAdStatusInfo = mRewardVideoAd.checkAdStatus();
            ViewUtil.printLog(tvShowLog, "video ad ready status:" + atAdStatusInfo.isReady());

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
        if (isAutoLoad) {
            ATRewardVideoAutoAd.show(this, mPlacementIdMap.get(mCurrentNetworkName), autoEventListener);
        } else {
            mRewardVideoAd.show(RewardVideoAdActivity.this);
//        mRewardVideoAd.show(RewardVideoAdActivity.this, "f5e5492eca9668");
        }
    }

    private ATRewardVideoAutoLoadListener autoLoadListener = new ATRewardVideoAutoLoadListener() {
        @Override
        public void onRewardVideoAutoLoaded(String placementId) {
            initPlacementIdLocalExtra(placementId);
            Log.i(TAG,"PlacementId:" + placementId + ": onRewardVideoAutoLoaded");
            ViewUtil.printLog(tvShowLog,"PlacementId:" + placementId + ": onRewardVideoAutoLoaded");
        }

        @Override
        public void onRewardVideoAutoLoadFail(String placementId, AdError adError) {
            Log.i(TAG,"PlacementId:" + placementId + ": onRewardVideoAutoLoadFail:\n" + adError.getFullErrorInfo());
            ViewUtil.printLog(tvShowLog,"PlacementId:" + placementId + ": onRewardVideoAutoLoadFail:\n" + adError.getFullErrorInfo());
        }
    };

    private void initPlacementIdLocalExtra(String placementId) {
        String userid = "test_userid_001";
        String userdata = "test_userdata_001_" + placementId + "_" + System.currentTimeMillis();
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.USER_ID, userid);
        localMap.put(ATAdConst.KEY.USER_CUSTOM_DATA, userdata);
        Log.i(TAG,"Set PlacementId:" + placementId + ": UserId:" + userid + "| userdata:" + userdata);
        ATRewardVideoAutoAd.setLocalExtra(placementId, localMap);
    }

    ATRewardVideoAutoEventListener autoEventListener = new ATRewardVideoAutoEventListener() {

        @Override
        public void onRewardedVideoAdPlayStart(ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdPlayStart:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdPlayStart:");
        }

        @Override
        public void onRewardedVideoAdPlayEnd(ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdPlayEnd:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdPlayEnd");
        }

        @Override
        public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdPlayFailed:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdPlayFailed");
        }

        @Override
        public void onRewardedVideoAdClosed(ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdClosed:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdClosed");
        }

        @Override
        public void onRewardedVideoAdPlayClicked(ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdPlayClicked:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdPlayClicked");
        }

        @Override
        public void onReward(ATAdInfo adInfo) {
            Log.e(TAG, "onReward:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog,"onReward");
        }

        public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
            Log.i(TAG,"onDeeplinkCallback:\n" + adInfo.toString() + "| isSuccess:" + isSuccess);
        }

        public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
            Log.i(TAG,"onDownloadConfirm:\n" + adInfo.toString());
        }

        //again listener
        public void onRewardedVideoAdAgainPlayStart(ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdAgainPlayStart:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdAgainPlayStart");
        }

        public void onRewardedVideoAdAgainPlayEnd(ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdAgainPlayEnd:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdAgainPlayEnd");
        }

        public void onRewardedVideoAdAgainPlayFailed(AdError adError, ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdAgainPlayFailed:\n" + adInfo.toString() + "｜error：" + adError.getFullErrorInfo());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdAgainPlayFailed");
        }

        public void onRewardedVideoAdAgainPlayClicked(ATAdInfo adInfo) {
            Log.i(TAG,"onRewardedVideoAdAgainPlayClicked:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onRewardedVideoAdAgainPlayClicked");
        }

        public void onAgainReward(ATAdInfo adInfo) {
            Log.i(TAG,"onAgainReward:\n" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onAgainReward");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoLoadListener != null) {
            autoLoadListener = null;
        }
        for (Map.Entry<String, Boolean> entry : mAutoLoadPlacementIdMap.entrySet()) {
            ATRewardVideoAutoAd.removePlacementId(entry.getKey());
        }
    }

}

