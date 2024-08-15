package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getStringFromMap;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ErrorCode;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Map;

public class AdmobSplashAdapter extends CustomSplashAdapter {

    public static final String TAG = AdmobSplashAdapter.class.getSimpleName();

    private String mUnitId = "";

    AppOpenAd.AppOpenAdLoadCallback loadCallback;
    FullScreenContentCallback fullScreenContentCallback;

    AppOpenAd mAppOpenAd;

    private boolean hasCallbackImpression = false;
    Map<String, Object> mExtraMap;

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra,
                                    Map<String, Object> localExtra) {
        mUnitId = getStringFromMap(serverExtra, "unit_id");
        if (TextUtils.isEmpty(mUnitId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "unitId is empty.");
            }
            return;
        }

        Context application = context.getApplicationContext();
        AdMobInitManager.getInstance().initSDK(application, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                startLoadSplashAd(application, serverExtra, localExtra);
            }

            @Override
            public void onFail(String errorMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", errorMsg);
                }
            }
        });
    }

    private void startLoadSplashAd(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {

        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {

            /**
             * Called when an app open ad has loaded.
             *
             * @param appOpenAd the loaded app open ad.
             */
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                mAppOpenAd = appOpenAd;
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                }
            }
        };

        AdRequest.Builder adRequestBuilder = AdMobInitManager.getInstance().getRequestBuilder();
        final AdRequest request = adRequestBuilder.build();
        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                AppOpenAd.load(context, mUnitId, request, loadCallback);
            }
        });

    }

    @Override
    public boolean isAdReady() {
        return mAppOpenAd != null;
    }

    @Override
    public void show(Activity activity, ViewGroup container) {
        fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.e(TAG, "Admob splash show fail: " + adError.getCode() + ", " + adError.getMessage());
                mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdShowFail(ErrorCode.getErrorCode(ErrorCode.adShowError, "" + adError.getCode(), adError.getMessage()));
                    mImpressionListener.onSplashAdDismiss();
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdShow();
                }
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdDismiss();
                }
            }

            @Override
            public void onAdClicked() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdClicked();
                }
            }
        };

        mAppOpenAd.setFullScreenContentCallback(fullScreenContentCallback);

        mAppOpenAd.show(activity);
    }


    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return AdMobInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
    }

    @Override
    public void destory() {
        loadCallback = null;
        fullScreenContentCallback = null;
    }

    @Override
    public String getNetworkPlacementId() {
        return mUnitId;
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
    public Map<String, Object> getNetworkInfoMap() {
        return mExtraMap;
    }
}
