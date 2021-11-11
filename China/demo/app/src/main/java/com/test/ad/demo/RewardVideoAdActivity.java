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
import com.anythink.network.gdt.GDTDownloadFirmInfo;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoExListener;
import com.test.ad.demo.gdt.DownloadApkConfirmDialogWebView;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardVideoAdActivity extends Activity {

    private static final String TAG = RewardVideoAdActivity.class.getSimpleName();

    ATRewardVideoAd mRewardVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Map<String, String> placementIdMap = PlacementIdUtil.getRewardedVideoPlacements(this);
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
//                boolean isReady = mRewardVideoAd.isAdReady();
                ATAdStatusInfo atAdStatusInfo = mRewardVideoAd.checkAdStatus();
                Toast.makeText(RewardVideoAdActivity.this, "video ad ready status:" + atAdStatusInfo.isReady(), Toast.LENGTH_SHORT).show();
                List<ATAdInfo> atAdInfoList = mRewardVideoAd.checkValidAdCaches();
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
                mRewardVideoAd.load();
            }
        });

        findViewById(R.id.show_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRewardVideoAd.show(RewardVideoAdActivity.this);
//                mRewardVideoAd.show(RewardVideoAdActivity.this, "f5e5492eca9668");
            }
        });

    }


    private void init(String placementId) {
        mRewardVideoAd = new ATRewardVideoAd(this, placementId);
        String userid = "test_userid_001";
        String userdata = "test_userdata_001";
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.USER_ID, userid);
        localMap.put(ATAdConst.KEY.USER_CUSTOM_DATA, userdata);

        // Only for GDT (true: open download dialog, false: download directly)
        localMap.put(ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, true);

        mRewardVideoAd.setLocalExtra(localMap);
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
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdAgainPlayStart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdAgainPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayEnd:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdAgainPlayEnd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdAgainPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayFailed error: " + errorCode.getFullErrorInfo());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdAgainPlayFailed:" + errorCode.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdAgainPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayClicked:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdAgainPlayClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAgainReward(ATAdInfo entity) {
                Log.i(TAG, "onAgainReward:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onAgainReward", Toast.LENGTH_SHORT).show();
            }
            //-------------------------- Only for CSJ --------------------------

            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded");
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
                Log.i(TAG, "onRewardedVideoAdFailed error:" + errorCode.getFullErrorInfo());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdFailed:" + errorCode.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayStart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayEnd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayFailed error: " + errorCode.getFullErrorInfo());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayFailed:" + errorCode.getFullErrorInfo(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdClosed:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReward(ATAdInfo entity) {
                Log.e(TAG, "onReward:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onReward", Toast.LENGTH_SHORT).show();
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
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadPause: totalBytes: " + totalBytes
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

    }

}

