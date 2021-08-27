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
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anythink.china.api.ATAppDownloadListener;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialExListener;
import com.anythink.network.gdt.GDTDownloadFirmInfo;
import com.test.ad.demo.gdt.DownloadApkConfirmDialogWebView;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterstitialAdActivity extends Activity {

    private static final String TAG = InterstitialAdActivity.class.getSimpleName();

    ATInterstitial mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Map<String, String> placementIdMap = PlacementIdUtil.getInterstitialPlacements(this);
        List<String> placementNameList = new ArrayList<>(placementIdMap.keySet());

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.placement_select_group);

        for (int i = 0; i < placementNameList.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(20, 20, 20, 20);
            radioButton.setText(placementNameList.get(i));
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }

        radioGroup.check(0);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String placementName = placementNameList.get(i);
                init(placementIdMap.get(placementName));
            }
        });

        String placementName = placementNameList.get(0);
        init(placementIdMap.get(placementName));

        findViewById(R.id.is_ad_ready_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isReady = mInterstitialAd.isAdReady();
                ATAdStatusInfo atAdStatusInfo = mInterstitialAd.checkAdStatus();
                Toast.makeText(InterstitialAdActivity.this, "interstitial ad ready status:" + atAdStatusInfo.isReady(), Toast.LENGTH_SHORT).show();
                List<ATAdInfo> atAdInfoList = mInterstitialAd.checkValidAdCaches();
                Log.i(TAG, "Valid Cahce size:" + (atAdInfoList != null ? atAdInfoList.size() : 0));
                if (atAdInfoList != null) {
                    for (ATAdInfo adInfo : atAdInfoList) {
                        Log.i(TAG, "\nCahce detail:" + adInfo.toString());
                    }
                }
            }
        });

        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterstitialAd.load();
            }
        });

        findViewById(R.id.show_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterstitialAd.show(InterstitialAdActivity.this);
//                mInterstitialAd.show(InterstitialAdActivity.this, "f5e54937b0483d");
            }
        });

    }


    private void init(String placementId) {
        mInterstitialAd = new ATInterstitial(this, placementId);

        Map<String, Object> localMap = new HashMap<>();

        // Only for GDT (true: open download dialog, false: download directly)
        localMap.put(ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, true);

        mInterstitialAd.setLocalExtra(localMap);

        mInterstitialAd.setAdListener(new ATInterstitialExListener() {

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

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Log.i(TAG, "onInterstitialAdLoadFail:\n" + adError.getFullErrorInfo());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdLoadFail:" + adError.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClicked:\n" + entity.toString());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdShow:\n" + entity.toString());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdShow", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClose:\n" + entity.toString());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdClose", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoStart:\n" + entity.toString());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdVideoStart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoEnd:\n" + entity.toString());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdVideoEnd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Log.i(TAG, "onInterstitialAdVideoError:\n" + adError.getFullErrorInfo());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdVideoError", Toast.LENGTH_SHORT).show();
            }

        });

        mInterstitialAd.setAdDownloadListener(new ATAppDownloadListener() {

            @Override
            public void onDownloadStart(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadStart: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadUpdate(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadUpdate: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadPause(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadPause: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadFinish(ATAdInfo adInfo, long totalBytes, String fileName, String appName) {
                Log.e(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadFinish: totalBytes: " + totalBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadFail(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.e(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadFail: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onInstalled(ATAdInfo adInfo, String fileName, String appName) {
                Log.e(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onInstalled:"
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }
        });

    }

}

