package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getStringFromMap;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.anythink.core.api.MediationInitCallback;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;

import java.util.Map;


public class AdmobBannerAdapter extends CustomBannerAdapter {
    private static final String TAG = AdmobBannerAdapter.class.getSimpleName();

    AdRequest mAdRequest = null;
    private String mUnitId = "";

    AdView mBannerView;

    Map<String, Object> mExtraMap;

    @Override
    public void loadCustomNetworkAd(final Context activity, final Map<String, Object> serverExtras, final Map<String, Object> localExtras) {
        mUnitId = getStringFromMap(serverExtras, "unit_id");
        if (TextUtils.isEmpty(mUnitId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "unitId is empty.");
            }
            return;
        }
        Context application = activity.getApplicationContext();
        AdMobInitManager.getInstance().initSDK(application, serverExtras, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        startLoadAd(application, serverExtras, localExtras);
                    }
                });
            }

            @Override
            public void onFail(String errorMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", errorMsg);
                }
            }

        });
    }

    private void startLoadAd(Context activity, Map<String, Object> serverExtras, Map<String, Object> localExtras) {

        final AdView adView = new AdView(activity);
        AdSize adSize = AdmobConst.getAdaptiveBannerAdSize(activity, localExtras, serverExtras);
        adView.setAdSize(adSize);
        adView.setAdUnitId(mUnitId);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mBannerView = adView;
                mBannerView.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(AdValue adValue) {
                        if (mImpressionEventListener != null) {
                            mImpressionEventListener.onBannerAdShow();
                        }
                    }
                });

                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                }
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdClicked() {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClicked();
                }
            }

            @Override
            public void onAdClosed() {

            }

        });

        AdRequest.Builder adRequestBuilder = AdMobInitManager.getInstance().getRequestBuilder();
        mAdRequest = adRequestBuilder.build();
        adView.loadAd(mAdRequest);
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
        return AdMobInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public String getNetworkName() {
        return AdMobInitManager.getInstance().getNetworkName();
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return AdMobInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
    }

    @Override
    public String getNetworkPlacementId() {
        return mUnitId;
    }

    @Override
    public Map<String, Object> getNetworkInfoMap() {
        return mExtraMap;
    }
}