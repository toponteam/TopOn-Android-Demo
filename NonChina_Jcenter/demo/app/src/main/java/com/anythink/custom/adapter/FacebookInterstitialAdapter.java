package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.util.Map;


public class FacebookInterstitialAdapter extends CustomInterstitialAdapter {

    InterstitialAd mInterstitialAd;
    String mUnitid;

    String mPayload;

    /***
     * load ad
     */
    private void startLoad(final Context context) {

        final InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

            @Override
            public void onError(Ad ad, AdError adError) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(adError.getErrorCode() + "", "" + adError.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdShow();
                }
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }
        };


        mInterstitialAd = new InterstitialAd(context.getApplicationContext(), mUnitid);
        // Load a new interstitial.
        final InterstitialAd.InterstitialAdLoadConfigBuilder adConfig = mInterstitialAd.buildLoadAdConfig()
                .withAdListener(interstitialAdListener);

        if (!TextUtils.isEmpty(mPayload)) {
            adConfig.withBid(mPayload);
        }

        mInterstitialAd.loadAd(adConfig.build());
    }


    @Override
    public void destory() {
        try {
            if (mInterstitialAd != null) {
                mInterstitialAd.setAdListener(null);
                mInterstitialAd.destroy();
                mInterstitialAd = null;
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

        if (serverExtras.containsKey("payload")) {
            mPayload = serverExtras.get("payload").toString();
        }

        FacebookInitManager.getInstance().initSDK(context.getApplicationContext(), serverExtras);
        startLoad(context);
    }

    @Override
    public boolean isAdReady() {
        if (mInterstitialAd == null || !mInterstitialAd.isAdLoaded()) {
            return false;
        }

        if (mInterstitialAd.isAdInvalidated()) {
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
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
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