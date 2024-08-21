package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getBooleanFromMap;
import static com.anythink.core.api.ATInitMediation.getStringFromMap;

import android.content.Context;
import android.text.TextUtils;

import com.anythink.core.api.MediationInitCallback;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;

import java.util.Map;

public class AdmobNativeAdapter extends CustomNativeAdapter {

    private String mUnitId;

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtras, final Map<String, Object> localExtras) {
        String unitId = getStringFromMap(serverExtras, "unit_id");
        String mediaRatio = getStringFromMap(serverExtras, "media_ratio");
        if (TextUtils.isEmpty(unitId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "unitId is empty.");
            }
            return;
        }

        mUnitId = unitId;

        boolean isAutoPlay = getBooleanFromMap(serverExtras, CustomNativeAd.IS_AUTO_PLAY_KEY, false);


        final String finalUnitId = unitId;
        final String finalMediaRatio = mediaRatio;
        final boolean finalIsAutoPlay = isAutoPlay;
        Context application = context.getApplicationContext();
        AdMobInitManager.getInstance().initSDK(application, serverExtras, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                startLoadAd(application, serverExtras, localExtras, finalUnitId, finalMediaRatio, finalIsAutoPlay);
            }

            @Override
            public void onFail(String errorMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", errorMsg);
                }
            }
        });
    }

    private void startLoadAd(final Context context, final Map<String, Object> serverExtras, final Map<String, Object> localExtras, final String unitId, final String mediaRatio, final boolean isAutoPlay) {
        runOnNetworkRequestThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AdmobNativeAd.LoadCallbackListener selfListener = new AdmobNativeAd.LoadCallbackListener() {
                        @Override
                        public void onSuccess(CustomNativeAd nativeAd) {
                            if (mLoadListener != null) {
                                mLoadListener.onAdCacheLoaded(nativeAd);
                            }
                        }

                        @Override
                        public void onFail(String errorCode, String errorMsg) {
                            if (mLoadListener != null) {
                                mLoadListener.onAdLoadError(errorCode, errorMsg);
                            }
                        }
                    };

                    AdmobNativeAd admobiATNativeAd = new AdmobNativeAd(context, mediaRatio, unitId, selfListener, serverExtras, localExtras);
                    admobiATNativeAd.setIsAutoPlay(isAutoPlay);
                    admobiATNativeAd.loadAd(context, serverExtras, localExtras);
                } catch (Throwable e) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("", e.getMessage());
                    }
                }
            }
        });

    }

    @Override
    public String getNetworkSDKVersion() {
        return AdMobInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public void destory() {

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

}

