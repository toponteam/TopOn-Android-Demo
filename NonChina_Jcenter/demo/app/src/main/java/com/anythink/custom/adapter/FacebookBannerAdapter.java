package com.anythink.custom.adapter;

import android.content.Context;
import android.view.View;

import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.Map;

/**
 * Created by Z on 2018/6/27.
 */

public class FacebookBannerAdapter extends CustomBannerAdapter {

    private String unitid = "";


    AdView mBannerView;

    String size = "";

    @Override
    public void loadCustomNetworkAd(final Context activity, Map<String, Object> serverExtras, Map<String, Object> localExtras) {

        if (serverExtras.containsKey("unit_id")) {
            unitid = (String) serverExtras.get("unit_id");

        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "facebook unitid is empty.");
            }
            return;
        }

        FacebookInitManager.getInstance().initSDK(activity.getApplicationContext(), serverExtras);

        if (serverExtras.containsKey("size")) {
            size = serverExtras.get("size").toString();
        }


        final AdListener adListener = new AdListener() {
            @Override
            public void onAdLoaded(Ad ad) {
                mBannerView = (AdView) ad;
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();

                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(adError.getErrorCode() + "", adError.getErrorMessage());

                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClicked();
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdShow();
                }
            }
        };

        AdView adView = null;
        switch (size) {
            case "320x50":
                adView = new AdView(activity, unitid, AdSize.BANNER_HEIGHT_50);
                break;
            case "320x90":
                adView = new AdView(activity, unitid, AdSize.BANNER_HEIGHT_90);
                break;
            case "300x250":
                adView = new AdView(activity, unitid, AdSize.RECTANGLE_HEIGHT_250);
                break;
            default:
                adView = new AdView(activity, unitid, AdSize.BANNER_HEIGHT_50);
                break;
        }
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    @Override
    public View getBannerView() {
        return mBannerView;
    }

    @Override
    public void destory() {
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
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
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return false;
    }

    @Override
    public String getNetworkPlacementId() {
        return unitid;
    }

}