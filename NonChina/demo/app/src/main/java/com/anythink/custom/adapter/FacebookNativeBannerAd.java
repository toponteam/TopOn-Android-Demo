package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;


public class FacebookNativeBannerAd extends CustomNativeAd implements NativeAdListener {

    NativeBannerAd mFacebookNativeAd;
    Context mContext;

    NativeBannerAdView.Type mType = NativeBannerAdView.Type.HEIGHT_50;

    public FacebookNativeBannerAd(Context context
            , NativeBannerAd nativeBannerAd
            , String height) {
        mContext = context.getApplicationContext();
        mFacebookNativeAd = nativeBannerAd;
        mFacebookNativeAd.setAdListener(this);
        switch (height) {
            case "50":
                mType = NativeBannerAdView.Type.HEIGHT_50;
                break;
            case "100":
                mType = NativeBannerAdView.Type.HEIGHT_100;
                break;
            case "120":
                mType = NativeBannerAdView.Type.HEIGHT_120;
                break;
        }
    }

    public void loadAd(String bidPayload) {
        if (TextUtils.isEmpty(bidPayload)) {
            mFacebookNativeAd.loadAd();
        } else {
            mFacebookNativeAd.loadAdFromBid(bidPayload);
        }

    }


    @Override
    public void clear(final View view) {
        if (mFacebookNativeAd != null) {
            mFacebookNativeAd.unregisterView();
        }

    }

    @Override
    public void prepare(View view, FrameLayout.LayoutParams layoutParams) {
        super.prepare(view, layoutParams);
    }

    @Override
    public View getAdMediaView(Object... object) {
        try {
            if (mFacebookNativeAd != null) {
                return NativeBannerAdView.render(mContext, mFacebookNativeAd, mType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }

    @Override
    public void destroy() {
        if (mFacebookNativeAd != null) {
            mFacebookNativeAd.unregisterView();
            mFacebookNativeAd.setAdListener(null);
            mFacebookNativeAd.destroy();
            mFacebookNativeAd = null;
        }

        mContext = null;
    }

    /**
     * facebook listener--------------------------------------------------------------------------------
     **/

    @Override
    public void onError(Ad ad, AdError adError) {
    }

    @Override
    public void onAdLoaded(Ad ad) {
    }


    @Override
    public void onAdClicked(Ad ad) {
        notifyAdClicked();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }


    @Override
    public void onMediaDownloaded(Ad ad) {

    }

}
