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
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;

import java.util.HashMap;
import java.util.Map;

public class RewardVideoAdActivity extends Activity {

    private static String TAG = "RewardVideoAdActivity";
    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_rewardvideo_all
            , DemoApplicaion.mPlacementId_rewardvideo_mintegral
            , DemoApplicaion.mPlacementId_rewardvideo_GDT
            , DemoApplicaion.mPlacementId_rewardvideo_toutiao
            , DemoApplicaion.mPlacementId_rewardvideo_uniplay
            , DemoApplicaion.mPlacementId_rewardvideo_oneway
            , DemoApplicaion.mPlacementId_rewardvideo_ksyun
            , DemoApplicaion.mPlacementId_rewardvideo_baidu
            , DemoApplicaion.mPlacementId_rewardvideo_ks
            , DemoApplicaion.mPlacementId_rewardvideo_sigmob
            , DemoApplicaion.mPlacementId_rewardvideo_myoffer
    };

    String unitGroupName[] = new String[]{
            "All network",
            "Mintegral",
            "GDT",
            "Toutiao",
            "Uniplay",
            "Oneway",
            "Ksyun",
            "Baidu",
            "Kuaishou",
            "Sigmob",
            "Myoffer"
    };

    RadioGroup mRadioGroup;


    int mCurrentSelectIndex;


    ATRewardVideoAd mRewardVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mRadioGroup = (RadioGroup) findViewById(R.id.placement_select_group);

        for (int i = 0; i < unitIds.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(20, 20, 20, 20);
            radioButton.setText(unitGroupName[i]);
            radioButton.setId(i);
            mRadioGroup.addView(radioButton);
        }

        mRadioGroup.check(0);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mCurrentSelectIndex = i;
                init();
            }
        });

//        mCurrentSelectIndex = 9;
        init();

        findViewById(R.id.is_ad_ready_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isReady = mRewardVideoAd.isAdReady();
                Toast.makeText(RewardVideoAdActivity.this, "video ad ready status:" + isReady, Toast.LENGTH_SHORT).show();
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
//                mRewardVideoAd.show(RewardVideoAdActivity.this);
                mRewardVideoAd.show(RewardVideoAdActivity.this, "f5e5492eca9668");
            }
        });

    }


    private void init() {
        mRewardVideoAd = new ATRewardVideoAd(this, unitIds[mCurrentSelectIndex]);
        String userid = "test_userid_001";
        String userdata = "test_userdata_001";
//        mRewardVideoAd.setUserData(userid, userdata);
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.USER_ID, userid);
        localMap.put(ATAdConst.KEY.USER_CUSTOM_DATA, userdata);
        mRewardVideoAd.setLocalExtra(localMap);
        mRewardVideoAd.setAdListener(new ATRewardVideoListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded");
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
                Log.i(TAG, "onRewardedVideoAdFailed error:" + errorCode.printStackTrace());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdFailed:" + errorCode.printStackTrace(), Toast.LENGTH_SHORT).show();
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
                Log.i(TAG, "onRewardedVideoAdPlayFailed error:" + errorCode.printStackTrace());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayFailed:" + errorCode.printStackTrace(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdClosed:\n" + entity.toString() );
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + entity.toString());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReward(ATAdInfo entity) {
                Log.e(TAG, "onReward:\n" + entity.toString() );
                Toast.makeText(RewardVideoAdActivity.this, "onReward", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

