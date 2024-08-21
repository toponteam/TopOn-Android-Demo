package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getStringFromMap;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;

import java.util.HashMap;
import java.util.Map;


public class AdmobRewardVideoAdapter extends CustomRewardVideoAdapter {
    private static final String TAG = AdmobRewardVideoAdapter.class.getSimpleName();

    RewardedAd mRewardedAd;
    AdRequest mAdRequest = null;

    private String mUnitId = "";

    boolean isPlayComplete = false;
    boolean isAdReady = false;

    private RewardedAdLoadCallback mRewardedAdLoadCallback;
    private FullScreenContentCallback mFullScreenContentCallback;
    private OnUserEarnedRewardListener mOnUserEarnedRewardListener;

    Map<String, Object> mExtraMap;

    @Override
    public void destory() {
        mRewardedAd = null;

        mRewardedAdLoadCallback = null;
        mFullScreenContentCallback = null;
        mOnUserEarnedRewardListener = null;

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

    public void startLoadAd(Context context, final Map<String, Object> serverExtras, Map<String, Object> localExtra) {
        AdRequest.Builder adRequestBuilder = AdMobInitManager.getInstance().getRequestBuilder();
        mAdRequest = adRequestBuilder.build();
        startLoadRewardedVideoAd(context);
    }

    public void startLoadRewardedVideoAd(final Context context) {
        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mRewardedAdLoadCallback = new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            mRewardedAd = null;

                            if (mLoadListener != null) {
                                mLoadListener.onAdLoadError(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                            }
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            isAdReady = true;

                            if (mLoadListener != null) {
                                mLoadListener.onAdCacheLoaded();
                            }
                        }
                    };
                    RewardedAd.load(context, mUnitId, mAdRequest, mRewardedAdLoadCallback);
                } catch (Throwable e) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("", e.getMessage());
                    }
                }
            }
        });
    }


    @Override
    public boolean isAdReady() {
        return isAdReady && mRewardedAd != null;
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        return AdMobInitManager.getInstance().setUserDataConsent(context, isConsent, isEUTraffic);
    }

    @Override
    public void show(Activity activity) {
        isAdReady = false;
        mFullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                isPlayComplete = false;
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayStart();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayFailed(String.valueOf(adError.getCode()), adError.getMessage());
                }
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdClosed();
                }
            }

            @Override
            public void onAdClicked() {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayClicked();
                }
            }
        };

        mOnUserEarnedRewardListener = new OnUserEarnedRewardListener() {

            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                // Handle the reward.

                if (!isPlayComplete) {
                    isPlayComplete = true;
                    if (mImpressionListener != null) {
                        mImpressionListener.onRewardedVideoAdPlayEnd();
                    }
                }

                try {
                    if (mExtraMap == null) {
                        mExtraMap = new HashMap<>();
                    }

                    if (rewardItem != null) {
                        Map<String, Object> rewardMap = new HashMap<>();
                        rewardMap.put(AdmobConst.REWARD_EXTRA.REWARD_EXTRA_KEY_REWARD_AMOUNT, rewardItem.getAmount());
                        rewardMap.put(AdmobConst.REWARD_EXTRA.REWARD_EXTRA_KEY_REWARD_TYPE, rewardItem.getType());

                        mExtraMap.put(ATAdConst.REWARD_EXTRA.REWARD_INFO, rewardMap);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (mImpressionListener != null) {
                    mImpressionListener.onReward();
                }
            }
        };

        showRewardedVideoAd(activity);
    }


    private void showRewardedVideoAd(Activity activity) {
        ServerSideVerificationOptions serverSideVerificationOptions = new ServerSideVerificationOptions.Builder()
                .setUserId(mUserId)
                .setCustomData(mUserData)
                .build();
        mRewardedAd.setServerSideVerificationOptions(serverSideVerificationOptions);

        mRewardedAd.setFullScreenContentCallback(mFullScreenContentCallback);
        mRewardedAd.show(activity, mOnUserEarnedRewardListener);
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