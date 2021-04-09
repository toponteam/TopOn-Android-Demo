/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoExListener;
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
                ATAdStatusInfo atAdStatusInfo = mRewardVideoAd.checkAdStatus();
                Toast.makeText(RewardVideoAdActivity.this, "video ad ready status:" + atAdStatusInfo.isReady(), Toast.LENGTH_SHORT).show();
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
        mRewardVideoAd.setLocalExtra(localMap);
        mRewardVideoAd.setAdListener(new ATRewardVideoExListener() {

            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
            }

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
                Log.i(TAG, "onRewardedVideoAdPlayFailed error:" + errorCode.getFullErrorInfo());
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
    }

}

