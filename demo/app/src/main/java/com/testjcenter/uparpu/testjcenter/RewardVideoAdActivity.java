package com.testjcenter.uparpu.testjcenter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.uparpu.api.AdError;
import com.uparpu.network.adcolony.AdColonyUpArpuConst;
import com.uparpu.network.adcolony.AdColonyUparpuRewardedVideoSetting;
import com.uparpu.network.admob.AdmobUpArpuConst;
import com.uparpu.network.admob.AdmobUpArpuRewardedVideoSetting;
import com.uparpu.network.applovin.ApplovinUpArpuConst;
import com.uparpu.network.applovin.ApplovinUpArpuRewardedVideoSetting;
import com.uparpu.network.chartboost.ChartboostUpArpuConst;
import com.uparpu.network.chartboost.ChartboostUpArpuRewardedVideoSetting;
import com.uparpu.network.flurry.FlurryUpArpuConst;
import com.uparpu.network.flurry.FlurryUpArpuRewardedVideoSetting;
import com.uparpu.network.inmobi.InmobiUpArpuConst;
import com.uparpu.network.inmobi.InmobiUpArpuRewardedVideoSetting;
import com.uparpu.network.ironsource.IronsourceUpArpuRewardedVideoSetting;
import com.uparpu.network.ironsource.IronsourceUparpuConst;
import com.uparpu.network.mintegral.MintegralUpArpuConst;
import com.uparpu.network.mintegral.MintegralUpArpuRewardedVideoSetting;
import com.uparpu.network.mopub.MopubUpArpuConst;
import com.uparpu.network.mopub.MopubUpArpuRewardedVideoSetting;
import com.uparpu.network.tapjoy.TapjoyUpArpuConst;
import com.uparpu.network.tapjoy.TapjoyUpArpuRewardedVideoSetting;
import com.uparpu.network.toutiao.TTUpArpuConst;
import com.uparpu.network.toutiao.TTUpArpuRewardedVideoSetting;
import com.uparpu.network.unityads.UnityAdsUpArpuConst;
import com.uparpu.network.unityads.UnityAdsUpArpuRewardedVideoSetting;
import com.uparpu.network.vungle.VungleRewardedVideoSetting;
import com.uparpu.network.vungle.VungleUpArpuConst;
import com.uparpu.rewardvideo.api.UpArpuRewardVideoAd;
import com.uparpu.rewardvideo.api.UpArpuRewardVideoListener;

public class RewardVideoAdActivity extends Activity {

    private static String TAG = "RewardVideoAdActivity";
    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_rewardvideo_all
            , DemoApplicaion.mPlacementId_rewardvideo_facebook
            , DemoApplicaion.mPlacementId_rewardvideo_admob
            , DemoApplicaion.mPlacementId_rewardvideo_inmobi
            , DemoApplicaion.mPlacementId_rewardvideo_flurry
            , DemoApplicaion.mPlacementId_rewardvideo_applovin
            , DemoApplicaion.mPlacementId_rewardvideo_mintegral
            , DemoApplicaion.mPlacementId_rewardvideo_mopub
            , DemoApplicaion.mPlacementId_rewardvideo_GDT
            , DemoApplicaion.mPlacementId_rewardvideo_CHARTBOOST
            , DemoApplicaion.mPlacementId_rewardvideo_TAPJOY
            , DemoApplicaion.mPlacementId_rewardvideo_IRONSOURCE
            , DemoApplicaion.mPlacementId_rewardvideo_UNITYAD
            , DemoApplicaion.mPlacementId_rewardvideo_vungle
            , DemoApplicaion.mPlacementId_rewardvideo_adcolony
            , DemoApplicaion.mPlacementId_rewardvideo_toutiao
            , DemoApplicaion.mPlacementId_rewardvideo_uniplay
            , DemoApplicaion.mPlacementId_rewardvideo_oneway
            , DemoApplicaion.mPlacementId_rewardvideo_ksyun
            , DemoApplicaion.mPlacementId_rewardvideo_mobpower
            , DemoApplicaion.mPlacementId_rewardvideo_appnext
            , DemoApplicaion.mPlacementId_rewardvideo_baidu
            , DemoApplicaion.mPlacementId_rewardvideo_nend
            , DemoApplicaion.mPlacementId_rewardvideo_maio
    };

    String unitGroupName[] = new String[]{
            "All network",
            "facebook",
            "admob",
            "inmobi",
            "flurry",
            "applovin",
            "mintegral",
            "mopub",
            "gdt",
            "chartboost",
            "tapjoy",
            "ironsource",
            "unity3d",
            "vungle",
            "adcolony",
            "toutiao",
            "uniplay",
            "oneway",
            "Ksyun",
            "mobpower",
            "appnext",
            "baidu",
            "nend",
            "maio"
    };

    RadioGroup mRadioGroup;


    int mCurrentSelectIndex;


    UpArpuRewardVideoAd mRewardVideoAd;

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
                mRewardVideoAd.show();
            }
        });

        findViewById(R.id.clean_ad_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRewardVideoAd.clean();
            }
        });

    }


    private void init() {
        if (mRewardVideoAd != null) {
            mRewardVideoAd.onDestory();
            mRewardVideoAd = null;
        }
        mRewardVideoAd = new UpArpuRewardVideoAd(this, unitIds[mCurrentSelectIndex]);
        String userid = "test_userid_001";
        mRewardVideoAd.setUserData(userid, "");
        addSetting();
        mRewardVideoAd.setAdListener(new UpArpuRewardVideoListener() {
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
            public void onRewardedVideoAdPlayStart() {
                Log.i(TAG, "onRewardedVideoAdPlayStart");
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayStart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayEnd() {
                Log.i(TAG, "onRewardedVideoAdPlayEnd");
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayEnd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode) {
                Log.i(TAG, "onRewardedVideoAdPlayFailed error:" + errorCode.printStackTrace());
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayFailed:" + errorCode.printStackTrace(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed(boolean isRewarded) {
                Log.i(TAG, "onRewardedVideoAdClosed reward:" + isRewarded);
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdClosed: isrewadr:" + isRewarded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayClicked() {
                Log.i(TAG, "onRewardedVideoAdPlayClicked");
                Toast.makeText(RewardVideoAdActivity.this, "onRewardedVideoAdPlayClicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSetting() {
        AdmobUpArpuRewardedVideoSetting _admobUpArpuMediationSetting = new AdmobUpArpuRewardedVideoSetting();
        mRewardVideoAd.addSetting(AdmobUpArpuConst.NETWORK_FIRM_ID, _admobUpArpuMediationSetting);

        MintegralUpArpuRewardedVideoSetting _mintegralUpArpuMediationSetting = new MintegralUpArpuRewardedVideoSetting();

        mRewardVideoAd.addSetting(MintegralUpArpuConst.NETWORK_FIRM_ID, _mintegralUpArpuMediationSetting);


        ApplovinUpArpuRewardedVideoSetting _applovinUpArpuMediationSetting = new ApplovinUpArpuRewardedVideoSetting();

        mRewardVideoAd.addSetting(ApplovinUpArpuConst.NETWORK_FIRM_ID, _applovinUpArpuMediationSetting);


        FlurryUpArpuRewardedVideoSetting _flurryUpArpuMediationSetting = new FlurryUpArpuRewardedVideoSetting();

        mRewardVideoAd.addSetting(FlurryUpArpuConst.NETWORK_FIRM_ID, _flurryUpArpuMediationSetting);


        InmobiUpArpuRewardedVideoSetting _inmobiUpArpuMediationSetting = new InmobiUpArpuRewardedVideoSetting();

        mRewardVideoAd.addSetting(InmobiUpArpuConst.NETWORK_FIRM_ID, _inmobiUpArpuMediationSetting);


        MopubUpArpuRewardedVideoSetting _mopubUpArpuMediationSetting = new MopubUpArpuRewardedVideoSetting();
        mRewardVideoAd.addSetting(MopubUpArpuConst.NETWORK_FIRM_ID, _mopubUpArpuMediationSetting);


        ChartboostUpArpuRewardedVideoSetting _chartboostUpArpuMediationSetting = new ChartboostUpArpuRewardedVideoSetting();
        mRewardVideoAd.addSetting(ChartboostUpArpuConst.NETWORK_FIRM_ID, _chartboostUpArpuMediationSetting);

        TapjoyUpArpuRewardedVideoSetting _tapjoyUpArpuMediationSetting = new TapjoyUpArpuRewardedVideoSetting();
        mRewardVideoAd.addSetting(TapjoyUpArpuConst.NETWORK_FIRM_ID, _tapjoyUpArpuMediationSetting);

        IronsourceUpArpuRewardedVideoSetting _ironsourceUpArpuMediationSetting = new IronsourceUpArpuRewardedVideoSetting();
        mRewardVideoAd.addSetting(IronsourceUparpuConst.NETWORK_FIRM_ID, _ironsourceUpArpuMediationSetting);

        UnityAdsUpArpuRewardedVideoSetting _unityAdUpArpuMediationSetting = new UnityAdsUpArpuRewardedVideoSetting();
        mRewardVideoAd.addSetting(UnityAdsUpArpuConst.NETWORK_FIRM_ID, _unityAdUpArpuMediationSetting);

        VungleRewardedVideoSetting vungleRewardVideoSetting = new VungleRewardedVideoSetting();
        vungleRewardVideoSetting.setOrientation(2);
        vungleRewardVideoSetting.setSoundEnable(true);
        mRewardVideoAd.addSetting(VungleUpArpuConst.NETWORK_FIRM_ID, vungleRewardVideoSetting);


        AdColonyUparpuRewardedVideoSetting adColonyUparpuRewardVideoSetting = new AdColonyUparpuRewardedVideoSetting();
        adColonyUparpuRewardVideoSetting.setEnableConfirmationDialog(false);
        adColonyUparpuRewardVideoSetting.setEnableResultsDialog(false);
        mRewardVideoAd.addSetting(AdColonyUpArpuConst.NETWORK_FIRM_ID, adColonyUparpuRewardVideoSetting);

        TTUpArpuRewardedVideoSetting ttUpArpuRewardedVideoSetting = new TTUpArpuRewardedVideoSetting();
        ttUpArpuRewardedVideoSetting.setRequirePermission(true);
        mRewardVideoAd.addSetting(TTUpArpuConst.NETWORK_FIRM_ID, ttUpArpuRewardedVideoSetting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRewardVideoAd.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRewardVideoAd.onPause();
    }
}

