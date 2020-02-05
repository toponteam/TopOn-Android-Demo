package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;

public class InterstitialAdActivity extends Activity {

    private static String TAG = "InterstitialAdActivity";
    String unitIds[] = new String[]{
        DemoApplicaion.mPlacementId_interstitial_all
                , DemoApplicaion.mPlacementId_interstitial_mintegral
                , DemoApplicaion.mPlacementId_interstitial_video_mintegral
                , DemoApplicaion.mPlacementId_interstitial_GDT
                , DemoApplicaion.mPlacementId_interstitial_video_toutiao
                , DemoApplicaion.mPlacementId_interstitial_toutiao
                , DemoApplicaion.mPlacementId_interstitial_uniplay
                , DemoApplicaion.mPlacementId_interstitial_oneway
                , DemoApplicaion.mPlacementId_interstitial_baidu
                , DemoApplicaion.mPlacementId_interstitial_ks
                , DemoApplicaion.mPlacementId_interstitial_sigmob
                , DemoApplicaion.mPlacementId_interstitial_myoffer
    };

    String unitGroupName[] = new String[]{
            "All network",
            "mintegral",
            "mintegral video",
            "gdt",
            "toutiao video",
            "toutiao",
            "uniplay",
            "oneway",
            "baidu",
            "kuaishou",
            "sigmob",
            "myoffer"
    };

    RadioGroup mRadioGroup;


    int mCurrentSelectIndex;


    ATInterstitial mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mRadioGroup = (RadioGroup) findViewById(R.id.placement_select_group);

        for (int i = 0; i < unitIds.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(20, 20, 20, 20);                 // 设置文字距离按钮四周的距离
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
                boolean isReady = mInterstitialAd.isAdReady();
                Toast.makeText(InterstitialAdActivity.this, "video ad ready status:" + isReady, Toast.LENGTH_SHORT).show();
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
                mInterstitialAd.show();
            }
        });

        findViewById(R.id.clean_ad_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterstitialAd.clean();
            }
        });

    }


    private void init() {
        if (mInterstitialAd != null) {
            mInterstitialAd.onDestory();
            mInterstitialAd = null;
        }
        mInterstitialAd = new ATInterstitial(this, unitIds[mCurrentSelectIndex]);
        addSetting();
        mInterstitialAd.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Log.i(TAG, "onInterstitialAdLoadFail");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdLoadFail:" + adError.printStackTrace(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClicked");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdShow");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdShow", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClose");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdClose", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdVideoStart() {
                Log.i(TAG, "onInterstitialAdVideoStart");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdVideoStart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdVideoEnd() {
                Log.i(TAG, "onInterstitialAdVideoEnd");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdVideoEnd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Log.i(TAG, "onInterstitialAdVideoError");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdVideoError", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void addSetting() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mInterstitialAd.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mInterstitialAd.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInterstitialAd != null) {
            mInterstitialAd.onDestory();
        }
    }
}

