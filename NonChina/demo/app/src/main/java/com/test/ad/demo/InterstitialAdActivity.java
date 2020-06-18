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
            , DemoApplicaion.mPlacementId_interstitial_facebook
            , DemoApplicaion.mPlacementId_interstitial_admob
            , DemoApplicaion.mPlacementId_interstitial_inmobi
            , DemoApplicaion.mPlacementId_interstitial_flurry
            , DemoApplicaion.mPlacementId_interstitial_applovin
            , DemoApplicaion.mPlacementId_interstitial_mintegral
            , DemoApplicaion.mPlacementId_interstitial_video_mintegral
            , DemoApplicaion.mPlacementId_interstitial_mopub
            , DemoApplicaion.mPlacementId_interstitial_CHARTBOOST
            , DemoApplicaion.mPlacementId_interstitial_TAPJOY
            , DemoApplicaion.mPlacementId_interstitial_IRONSOURCE
            , DemoApplicaion.mPlacementId_interstitial_UNITYAD
            , DemoApplicaion.mPlacementId_interstitial_vungle
            , DemoApplicaion.mPlacementId_interstitial_adcolony
            , DemoApplicaion.mPlacementId_interstitial_appnext
            , DemoApplicaion.mPlacementId_interstitial_nend
            , DemoApplicaion.mPlacementId_interstitia_maio
            , DemoApplicaion.mPlacementId_interstitia_startapp
            , DemoApplicaion.mPlacementId_interstitial_myoffer
            , DemoApplicaion.mPlacementId_interstitial_ogury
            , DemoApplicaion.mPlacementId_interstitial_fyber
    };

    String unitGroupName[] = new String[]{
            "All network",
            "facebook",
            "admob",
            "inmobi",
            "flurry",
            "applovin",
            "mintegral",
            "mintegral video",
            "mopub",
            "chartboost",
            "tapjoy",
            "ironsource",
            "unity3d",
            "vungle",
            "adcolony",
            "appnext",
            "nend",
            "maio",
            "startapp",
            "myoffer",
            "ogury",
            "fyber"
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
                mInterstitialAd.show(InterstitialAdActivity.this);
            }
        });

    }


    private void init() {
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
                Log.i(TAG, "onInterstitialAdLoadFail:\n" + adError.printStackTrace());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdLoadFail:" + adError.printStackTrace(), Toast.LENGTH_SHORT).show();
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
                Log.i(TAG, "onInterstitialAdVideoError:\n" + adError.printStackTrace());
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdVideoError", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void addSetting() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

