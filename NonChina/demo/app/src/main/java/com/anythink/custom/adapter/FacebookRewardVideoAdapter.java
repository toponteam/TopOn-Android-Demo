package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.anythink.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardData;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;

import java.util.Map;


public class FacebookRewardVideoAdapter extends CustomRewardVideoAdapter {

    RewardedVideoAd rewardedVideoAd;
    String mUnitid;

    String mPayload;

    /***
     * load ad
     */
    private void startLoad(final Context context) {

        final RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(error.getErrorCode() + "", "" + error.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayStart();
                }
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                // Call method to give reward
                // giveReward();
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayEnd();
                }

                if (mImpressionListener != null) {
                    mImpressionListener.onReward();
                }
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdClosed();
                }

            }
        };

        rewardedVideoAd = new RewardedVideoAd(context.getApplicationContext(), mUnitid);
        RewardedVideoAd.RewardedVideoAdLoadConfigBuilder adConfig = rewardedVideoAd
                .buildLoadAdConfig()
                .withAdListener(rewardedVideoAdListener)
                .withFailOnCacheFailureEnabled(true)
                .withRVChainEnabled(true);

        adConfig.withRewardData(new RewardData(mUserId, mUserData));

        if (!TextUtils.isEmpty(mPayload)) {
            adConfig.withBid(mPayload);
        }
        rewardedVideoAd.loadAd(adConfig.build());
    }

    @Override
    public void destory() {
        try {
            if (rewardedVideoAd != null) {
                rewardedVideoAd.setAdListener(null);
                rewardedVideoAd.destroy();
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtras, Map<String, Object> localExtras) {

        if (serverExtras.containsKey("unit_id")) {
            mUnitid = (String) serverExtras.get("unit_id");
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "facebook sdkkey is empty.");
            }
            return;
        }
        FacebookInitManager.getInstance().initSDK(context.getApplicationContext(), serverExtras);

        if (serverExtras.containsKey("payload")) {
            mPayload = serverExtras.get("payload").toString();
        }

        startLoad(context);
    }

    @Override
    public boolean isAdReady() {
        if (rewardedVideoAd == null || !rewardedVideoAd.isAdLoaded()) {
            return false;
        }
        if (rewardedVideoAd.isAdInvalidated()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return false;
    }

    @Override
    public void show(Activity activity) {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.show();
        }

    }

    @Override
    public String getNetworkSDKVersion() {
        return FacebookInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public String getNetworkName() {
        return FacebookInitManager.getInstance().getNetworkName();
    }

    @Override
    public String getNetworkPlacementId() {
        return mUnitid;
    }
}