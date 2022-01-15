/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.rewardvideo.api.ATRewardVideoAutoAd;
import com.anythink.rewardvideo.api.ATRewardVideoAutoEventListener;
import com.anythink.rewardvideo.api.ATRewardVideoAutoLoadListener;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RewardVideoAutoActivity extends Activity {
    static final String TAG = "RewardVideoAutoActivity";
    private static ATRewardVideoAutoLoadListener autoLoadListener = new ATRewardVideoAutoLoadListener() {
        @Override
        public void onRewardVideoAutoLoaded(String placementId) {
            Log.i(TAG, "PlacementId:" + placementId + ": onRewardVideoAutoLoaded");
            initPlacementIdLocalExtra(placementId);

        }

        @Override
        public void onRewardVideoAutoLoadFail(String placementId, AdError adError) {
            Log.i(TAG, "PlacementId:" + placementId + ": onRewardVideoAutoLoadFail:\n" + adError.getFullErrorInfo());
        }
    };

    Map<String, String> rewardvideoPlacementIdMap;
    String[] rewardvideoPlacementName;

    int mCurrentSelectIndex;

    ATRewardVideoAutoEventListener autoEventListener = new ATRewardVideoAutoEventListener() {

        @Override
        public void onRewardedVideoAdPlayStart(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdPlayStart:\n" + adInfo.toString());
        }

        @Override
        public void onRewardedVideoAdPlayEnd(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + adInfo.toString());
        }

        @Override
        public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdPlayFailed:\n" + adInfo.toString());
        }

        @Override
        public void onRewardedVideoAdClosed(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdClosed:\n" + adInfo.toString());
        }

        @Override
        public void onRewardedVideoAdPlayClicked(ATAdInfo adInfo) {
            Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + adInfo.toString());
        }

        @Override
        public void onReward(ATAdInfo adInfo) {
            Log.e(TAG, "onReward:\n" + adInfo.toString());
        }

        public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
            Log.i(TAG, "onDeeplinkCallback:\n" + adInfo.toString() + "| isSuccess:" + isSuccess);
        }

        public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {

        }

        //again listener
        public void onRewardedVideoAdAgainPlayStart(ATAdInfo adInfo) {
            Log.e(TAG, "onRewardedVideoAdAgainPlayStart:\n" + adInfo.toString());
        }

        public void onRewardedVideoAdAgainPlayEnd(ATAdInfo adInfo) {
            Log.e(TAG, "onRewardedVideoAdAgainPlayEnd:\n" + adInfo.toString());
        }

        public void onRewardedVideoAdAgainPlayFailed(AdError adError, ATAdInfo adInfo) {
            Log.e(TAG, "onRewardedVideoAdAgainPlayFailed:\n" + adInfo.toString() + "｜error：" + adError.getFullErrorInfo());
        }

        public void onRewardedVideoAdAgainPlayClicked(ATAdInfo adInfo) {
            Log.e(TAG, "onRewardedVideoAdAgainPlayClicked:\n" + adInfo.toString());
        }

        public void onAgainReward(ATAdInfo adInfo) {
            Log.e(TAG, "onAgainReward:\n" + adInfo.toString());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_load);

        ATRewardVideoAutoAd.init(this, null, autoLoadListener);

        rewardvideoPlacementIdMap = PlacementIdUtil.getRewardedVideoPlacements(this);

        Set<String> rewardVideoKeyset = rewardvideoPlacementIdMap.keySet();
        rewardvideoPlacementName = new String[rewardVideoKeyset.size()];
        rewardvideoPlacementName = rewardVideoKeyset.toArray(rewardvideoPlacementName);


        findViewById(R.id.autoAdInit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> rewardVideoList = new ArrayList<>();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RewardVideoAutoActivity.this);
                dialogBuilder.setTitle("Choose rewardVideo Auto Placement");
                dialogBuilder.setMultiChoiceItems(rewardvideoPlacementName, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            rewardVideoList.add(rewardvideoPlacementName[which]);
                        } else {
                            rewardVideoList.remove(rewardvideoPlacementName[which]);
                        }
                        Log.i("RewardVideoAutoActivity", "select:" + rewardvideoPlacementName[which] + "---:status:" + isChecked);
                    }
                });

                dialogBuilder.setPositiveButton("Add Auto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String item : rewardVideoList) {
                            Log.i("RewardVideoAutoActivity", "select add auto item:" + item);
                            initPlacementIdLocalExtra(rewardvideoPlacementIdMap.get(item));
                        }

                        ATRewardVideoAutoAd.addPlacementId(placementNameToPlacementIds(rewardVideoList));

                    }
                });

                dialogBuilder.setNegativeButton("Remove Auto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String item : rewardVideoList) {
                            Log.i("RewardVideoAutoActivity", "select remove auto item:" + item);
                        }

                        ATRewardVideoAutoAd.removePlacementId(placementNameToPlacementIds(rewardVideoList));
                    }
                });
                dialogBuilder.create().show();
            }
        });


        Spinner spinner = (Spinner) findViewById(R.id.auto_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                RewardVideoAutoActivity.this, android.R.layout.simple_spinner_dropdown_item,
                rewardvideoPlacementName);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(RewardVideoAutoActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentSelectIndex = position;
                ATNative.entryAdScenario(rewardvideoPlacementIdMap.get(rewardvideoPlacementName[mCurrentSelectIndex]), "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.autoAdCheckReady).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATAdStatusInfo atAdStatusInfo = ATRewardVideoAutoAd.checkAdStatus(rewardvideoPlacementIdMap.get(rewardvideoPlacementName[mCurrentSelectIndex]));
                Toast.makeText(RewardVideoAutoActivity.this, "rewardVideo ad ready status:" + atAdStatusInfo.isReady(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.autoAdShow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATRewardVideoAutoAd.show(RewardVideoAutoActivity.this, rewardvideoPlacementIdMap.get(rewardvideoPlacementName[mCurrentSelectIndex]), autoEventListener);
            }
        });

    }


    private String[] placementNameToPlacementIds(List<String> placementNames) {
        String[] placementIds = new String[placementNames.size()];
        for (int i = 0; i < placementNames.size(); i++) {
            placementIds[i] = rewardvideoPlacementIdMap.get(placementNames.get(i));
        }
        return placementIds;
    }

    private static void initPlacementIdLocalExtra(String placementId) {
        String userid = "test_userid_001";
        String userdata = "test_userdata_001_" + placementId + "_" + System.currentTimeMillis();
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.USER_ID, userid);
        localMap.put(ATAdConst.KEY.USER_CUSTOM_DATA, userdata);
        Log.i(TAG, "Set PlacementId:" + placementId + ": UserId:" + userid + "| userdata:" + userdata);
        ATRewardVideoAutoAd.setLocalExtra(placementId, localMap);
    }
}
