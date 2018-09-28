package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.uparpu.api.AdError;
import com.uparpu.interstitial.api.UpArpuInterstitial;
import com.uparpu.interstitial.api.UpArpuInterstitialListener;

public class InterstitialAdActivity extends Activity {

    private static String TAG = "InterstitialAdActivity";
    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_interstitial_all
          //  , DemoApplicaion.mPlacementId_interstitial_facebook
            , DemoApplicaion.mPlacementId_interstitial_admob
            //, DemoApplicaion.mPlacementId_interstitial_inmobi
          //  , DemoApplicaion.mPlacementId_interstitial_flurry
           // , DemoApplicaion.mPlacementId_interstitial_applovin
           // , DemoApplicaion.mPlacementId_interstitial_mobivsta
          //  , DemoApplicaion.mPlacementId_interstitial_mopub
            , DemoApplicaion.mPlacementId_interstitial_GDT
          //  , DemoApplicaion.mPlacementId_interstitial_CHARTBOOST
          //  , DemoApplicaion.mPlacementId_interstitial_TAPJOY
           // , DemoApplicaion.mPlacementId_interstitial_IRONSOURCE
          //  , DemoApplicaion.mPlacementId_interstitial_UNITYAD
           // , DemoApplicaion.mPlacementId_interstitial_vungle
           // , DemoApplicaion.mPlacementId_interstitial_adcolony
            , DemoApplicaion.mPlacementId_interstitial_video_toutiao
            , DemoApplicaion.mPlacementId_interstitial_toutiao
            , DemoApplicaion.mPlacementId_interstitial_uniplay
            , DemoApplicaion.mPlacementId_interstitial_oneway
    };

    String unitGroupName[] = new String[]{
            "All network",
         //   "facebook",
            "admob",
          //  "inmobi",
          //  "flurry",
            //"applovin",
          //  "mintegral",
         //   "mopub",
            "gdt",
           // "chartboost",
           // "tapjoy",
           // "ironsource",
          //  "unity3d",
         //   "vungle",
           // "adcolony",
            "toutiao_video",
            "toutiao",
            "uniplay",
            "oneway"
    };

    RadioGroup mRadioGroup;


    int mCurrentSelectIndex;


    UpArpuInterstitial mInterstitialAd;

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
        mInterstitialAd = new UpArpuInterstitial(this, unitIds[mCurrentSelectIndex]);
        addSetting();
        mInterstitialAd.setAdListener(new UpArpuInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Log.i(TAG, "onInterstitialAdLoadFail");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdLoadFail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdClicked() {
                Log.i(TAG, "onInterstitialAdClicked");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdShow() {
                Log.i(TAG, "onInterstitialAdShow");
                Toast.makeText(InterstitialAdActivity.this, "onInterstitialAdShow", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdClose() {
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

