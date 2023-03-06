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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdSourceStatusListener;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialAutoAd;
import com.anythink.interstitial.api.ATInterstitialAutoEventListener;
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener;
import com.anythink.interstitial.api.ATInterstitialExListener;
import com.test.ad.demo.util.PlacementIdUtil;
import com.test.ad.demo.utils.ViewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterstitialAdActivity extends Activity {

    private static final String TAG = InterstitialAdActivity.class.getSimpleName();

    ATInterstitial mInterstitialAd;
    private String mCurrentNetworkName;
    private Map<String, String> mPlacementIdMap;
    private Map<String, Boolean> mAutoLoadPlacementIdMap = new HashMap<>();
    private boolean isAutoLoad;

    private Spinner mSpinner;
    private CheckBox ckAutoLoad;
    private TextView tvLoadAdBtn;
    private TextView tvIsAdReadyBtn;
    private TextView tvShowAdBtn;
    private TextView tvShowLog;

    private RelativeLayout rlInterstitial;
    private RelativeLayout rlfullscreen;

    private static WeakReference<TextView> tvShowLogReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_interstitial);

        ATInterstitialAutoAd.init(this, null, autoLoadListener);

        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(R.string.anythink_title_interstitial);
        titleBar.setListener(new TitleBarClickListener() {
            @Override
            public void onBackClick(View v) {
                finish();
            }
        });

        tvShowLog = findViewById(R.id.tv_show_log);
        tvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvShowLogReference = new WeakReference<>(tvShowLog);
        tvLoadAdBtn = findViewById(R.id.load_ad_btn);
        tvIsAdReadyBtn = findViewById(R.id.is_ad_ready_btn);
        tvShowAdBtn = findViewById(R.id.show_ad_btn);
        rlInterstitial = findViewById(R.id.rl_interstitial);
        rlfullscreen = findViewById(R.id.rl_fullscreen);

        mPlacementIdMap = PlacementIdUtil.getInterstitialPlacements(this);
        List<String> placementNameList = new ArrayList<>(mPlacementIdMap.keySet());
        mCurrentNetworkName = placementNameList.get(0);

        mSpinner = (Spinner) findViewById(R.id.spinner_1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                placementNameList);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

//                Toast.makeText(InterstitialAdActivity.this,
//                        parent.getItemAtPosition(position).toString(),
//                        Toast.LENGTH_SHORT).show();

                mCurrentNetworkName = parent.getSelectedItem().toString();
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
                /*
                 To collect scene arrival rate statistics, you can view related information "https://docs.toponad.com/#/en-us/android/NetworkAccess/scenario/scenario"
                 Call the "Enter AD scene" method when an AD trigger condition is met, such as:
                 ** The scenario is a pop-up AD after the cleanup, which is called at the end of the cleanup.
                 * 1、Call "entryAdScenario" to report the arrival of the scene.
                 * 2、Call "isAdReady".
                 * 3、Call "show" to show AD view.
                 * (Note the difference between auto and manual)
                 */
                ATInterstitial.entryAdScenario(placementId, "f5e54937b0483d");
                if(mInterstitialAd.isAdReady()){
                    showAd();
                }
            }
        });

    }


    private void init(String placementId) {
        mInterstitialAd = new ATInterstitial(this, placementId);

        mInterstitialAd.setAdListener(new ATInterstitialExListener() {

            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {

            }

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                ViewUtil.printLog(tvShowLog, "onInterstitialAdLoaded");
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Log.i(TAG, "onInterstitialAdLoadFail:\n" + adError.getFullErrorInfo());
                ViewUtil.printLog(tvShowLog, "onInterstitialAdLoadFail:" + adError.getFullErrorInfo());
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClicked:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog, "onInterstitialAdClicked");
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdShow:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog, "onInterstitialAdShow");
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClose:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog, "onInterstitialAdClose");
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoStart:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog, "onInterstitialAdVideoStart");
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoEnd:\n" + entity.toString());
                ViewUtil.printLog(tvShowLog, "onInterstitialAdVideoEnd");
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Log.i(TAG, "onInterstitialAdVideoError:\n" + adError.getFullErrorInfo());
                ViewUtil.printLog(tvShowLog, "onInterstitialAdVideoError");
            }

        });


        mInterstitialAd.setAdSourceStatusListener(new ATAdSourceStatusListener() {
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

//    public void changeBg(View view,boolean selected) {
//        view.setBackgroundResource(selected ? R.drawable.bg_white_selected : R.drawable.bg_white);
//    }

    private void initAutoLoad() {
        ckAutoLoad = findViewById(R.id.ck_auto_load);
        ckAutoLoad.setVisibility(View.VISIBLE);
        ckAutoLoad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAutoLoad = true;
                    mAutoLoadPlacementIdMap.put(mPlacementIdMap.get(mCurrentNetworkName), true);
                    ATInterstitialAutoAd.addPlacementId(mPlacementIdMap.get(mCurrentNetworkName));
                    tvLoadAdBtn.setVisibility(View.GONE);
                } else {
                    isAutoLoad = false;
                    mAutoLoadPlacementIdMap.put(mPlacementIdMap.get(mCurrentNetworkName), false);
                    ATInterstitialAutoAd.removePlacementId(mPlacementIdMap.get(mCurrentNetworkName));
                    tvLoadAdBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadAd() {
        Map<String, Object> localMap = new HashMap<>();

//        localMap.put(ATAdConst.KEY.AD_WIDTH, getResources().getDisplayMetrics().widthPixels);
//        localMap.put(ATAdConst.KEY.AD_HEIGHT, getResources().getDisplayMetrics().heightPixels);

        mInterstitialAd.setLocalExtra(localMap);
        mInterstitialAd.load();
    }

    private void isAdReady() {
        if (isAutoLoad) {
            ViewUtil.printLog(tvShowLog, "interstitial auto load ad ready status:" + ATInterstitialAutoAd.isAdReady(mPlacementIdMap.get(mCurrentNetworkName)));
        } else {
//         boolean isReady = mInterstitialAd.isAdReady();
            ATAdStatusInfo atAdStatusInfo = mInterstitialAd.checkAdStatus();
            ViewUtil.printLog(tvShowLog, "interstitial ad ready status:" + atAdStatusInfo.isReady());
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
        if (isAutoLoad) {
            ATInterstitialAutoAd.show(this, mPlacementIdMap.get(mCurrentNetworkName), autoEventListener);
        } else {
            mInterstitialAd.show(InterstitialAdActivity.this);
//        mInterstitialAd.show(InterstitialAdActivity.this, "f5e54937b0483d");
        }
    }

    private static ATInterstitialAutoLoadListener autoLoadListener = new ATInterstitialAutoLoadListener() {
        @Override
        public void onInterstitialAutoLoaded(String placementId) {
            Log.i(TAG, "PlacementId:" + placementId + ": onInterstitialAutoLoaded");
            TextView tvLog = tvShowLogReference != null ? tvShowLogReference.get() : null;
            if (tvLog != null) {
                ViewUtil.printLog(tvLog, "PlacementId:" + placementId + ": onInterstitialAutoLoaded");
            }
        }

        @Override
        public void onInterstitialAutoLoadFail(String placementId, AdError adError) {
            Log.i(TAG, "PlacementId:" + placementId + ": onInterstitialAutoLoadFail:\n" + adError.getFullErrorInfo());
            TextView tvLog = tvShowLogReference != null ? tvShowLogReference.get() : null;
            if (tvLog != null) {
                ViewUtil.printLog(tvLog, "PlacementId:" + placementId + ": onInterstitialAutoLoadFail:\n" + adError.getFullErrorInfo());
            }
        }

    };

    ATInterstitialAutoEventListener autoEventListener = new ATInterstitialAutoEventListener() {
        @Override
        public void onInterstitialAdClicked(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdClicked:" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onInterstitialAdClicked:");
        }

        @Override
        public void onInterstitialAdShow(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdShow:" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onInterstitialAdShow");
        }

        @Override
        public void onInterstitialAdClose(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdClose:" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onInterstitialAdClose");
        }

        @Override
        public void onInterstitialAdVideoStart(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdVideoStart:" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onInterstitialAdVideoStart");
        }

        @Override
        public void onInterstitialAdVideoEnd(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdVideoEnd:" + adInfo.toString());
            ViewUtil.printLog(tvShowLog, "onInterstitialAdVideoEnd");
        }

        @Override
        public void onInterstitialAdVideoError(AdError adError) {
            Log.i(TAG, "onInterstitialAdVideoError:" + adError.getFullErrorInfo());
            ViewUtil.printLog(tvShowLog, "onInterstitialAdVideoError" + adError.getFullErrorInfo());
        }

        public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
            Log.i(TAG, "onDeeplinkCallback:\n" + adInfo.toString() + "| isSuccess:" + isSuccess);
        }

        public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
            Log.i(TAG, "onDownloadConfirm:\n" + adInfo.toString());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvShowLog = null;
        for (Map.Entry<String, Boolean> entry : mAutoLoadPlacementIdMap.entrySet()) {
            ATInterstitialAutoAd.removePlacementId(entry.getKey());
        }

        if (mInterstitialAd != null) {
            mInterstitialAd.setAdSourceStatusListener(null);
            mInterstitialAd.setAdDownloadListener(null);
            mInterstitialAd.setAdListener(null);
        }
    }

}

