package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getStringFromMap;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Map;


public class AdmobInterstitialAdapter extends CustomInterstitialAdapter {
    private static final String TAG = AdmobInterstitialAdapter.class.getSimpleName();

    InterstitialAd mInterstitialAd;
    AdRequest mAdRequest = null;
    private String mUnitId = "";

    boolean isAdReady = false;

    private FullScreenContentCallback mFullScreenContentCallback;
    private InterstitialAdLoadCallback mInterstitialAdLoadCallback;

    Map<String, Object> mExtraMap;

    /***
     * load ad
     */
    private void startLoadAd(final Context context, Map<String, Object> serverExtras, Map<String, Object> localExtras) {
        AdRequest.Builder adRequestBuilder = AdMobInitManager.getInstance().getRequestBuilder();
        mAdRequest = adRequestBuilder.build();
        mInterstitialAdLoadCallback = new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                isAdReady = true;
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;

                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                }
            }
        };


        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    InterstitialAd.load(context, mUnitId, mAdRequest, mInterstitialAdLoadCallback);
                } catch (Throwable e) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("", e.getMessage());
                    }
                }
            }
        });

    }

    @Override
    public void destory() {
        mInterstitialAd = null;

        mInterstitialAdLoadCallback = null;
        mFullScreenContentCallback = null;

        mAdRequest = null;
    }


    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtras, Map<String, Object> localExtra) {
        mUnitId = getStringFromMap(serverExtras, "unit_id");
        if (TextUtils.isEmpty(mUnitId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "unitId is empty.");
            }
            return;
        }

        //init
        Context application = context.getApplicationContext();
        AdMobInitManager.getInstance().initSDK(application, serverExtras, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                startLoadAd(application, serverExtras, localExtra);
            }

            @Override
            public void onFail(String errorMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", errorMsg);
                }
            }
        });
    }

    @Override
    public boolean isAdReady() {
        return mInterstitialAd != null && isAdReady;
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return AdMobInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
    }

    /***
     * Show Ad
     */
    @Override
    public void show(Activity activity) {
        isAdReady = false;

        mFullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when fullscreen content failed to show.
                mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdShow();
                }
            }

            @Override
            public void onAdClicked() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }
        };
        mInterstitialAd.setFullScreenContentCallback(mFullScreenContentCallback);

        mInterstitialAd.show(activity);
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
    public String getNetworkPlacementId() {
        return mUnitId;
    }

    @Override
    public Map<String, Object> getNetworkInfoMap() {
        return mExtraMap;
    }
}