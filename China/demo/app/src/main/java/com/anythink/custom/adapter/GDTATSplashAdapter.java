/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import static com.anythink.core.api.ATInitMediation.getBooleanFromMap;
import static com.anythink.core.api.ATInitMediation.getStringFromMap;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBidRequestInfoListener;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.ErrorCode;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.splashad.api.ATSplashEyeAdListener;
import com.anythink.splashad.api.IATSplashEyeAd;
import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADZoomOutListener;
import com.qq.e.comm.compliance.DownloadConfirmCallBack;
import com.qq.e.comm.compliance.DownloadConfirmListener;

import java.util.Map;

public class GDTATSplashAdapter extends CustomSplashAdapter implements SplashADZoomOutListener {

    final String TAG = GDTATSplashAdapter.class.getSimpleName();
    boolean isSplashEyeAd;
    GDTATSplashEyeAd mSplashEyeAd;
    ViewGroup mSplashView;
    String mPayload;
    boolean isC2SBidding = false;
    private String mAppId;
    private String mUnitId;
    private boolean isReady;
    private SplashAD splashAD;
    private boolean isUseDownloadDialogFrame;
    // Splash V+
    private boolean zoomoutAdSwitch = false;

    private void startLoadAd(final Context context, Map<String, Object> serverExtra) {
        if (TextUtils.isEmpty(mPayload) || isC2SBidding) {
            splashAD = new SplashAD(context, mUnitId, GDTATSplashAdapter.this, mFetchAdTimeout);
            splashAD.setLoadAdParams(GDTATInitManager.getInstance().getLoadAdParams(serverExtra));
        } else {
            splashAD = new SplashAD(context, mUnitId, GDTATSplashAdapter.this, mFetchAdTimeout,
                    mPayload);
        }

        splashAD.fetchAdOnly();
    }

    @Override
    public String getNetworkName() {
        return GDTATInitManager.getInstance().getNetworkName();
    }

    @Override
    public boolean isAdReady() {
        return isReady;
    }

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra,
                                    final Map<String, Object> localExtra) {
        initRequestParams(serverExtra, localExtra);

        if (TextUtils.isEmpty(mAppId) || TextUtils.isEmpty(mUnitId)) {
            notifyATLoadFail("", "GTD appid or unitId is empty.");
            return;
        }

        final Context applicationContext = context.getApplicationContext();
        GDTATInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                if (getMixedFormatAdType() == ATAdConst.ATMixedFormatAdType.NATIVE) {
                    if (!serverExtra.containsKey("video_muted")) {
                        serverExtra.put("video_muted", "1");
                    }
                    thirdPartyLoad(new GDTATAdapter(), context, serverExtra, localExtra);
                } else {
                    startLoadAd(applicationContext, serverExtra);
                }
            }

            @Override
            public void onFail(String errorMsg) {
                notifyATLoadFail("", errorMsg);
            }
        });
    }

    private void initRequestParams(Map<String, Object> serverExtra,
                                   Map<String, Object> localExtra) {
        mAppId = getStringFromMap(serverExtra, "app_id");
        mUnitId = getStringFromMap(serverExtra, "unit_id");
        mPayload = getStringFromMap(serverExtra, "payload");

        isReady = false;

        isUseDownloadDialogFrame = getBooleanFromMap(localExtra,
                ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, false);

        if (serverExtra.containsKey("zoomoutad_sw")) {
            zoomoutAdSwitch = TextUtils.equals("2", getStringFromMap(serverExtra, "zoomoutad_sw"));
        }
    }

    @Override
    public void show(Activity activity, ViewGroup container) {
        if (container == null) {
            if (mImpressionListener != null) {
                mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
                mImpressionListener.onSplashAdShowFail(
                        ErrorCode.getErrorCode(ErrorCode.adShowError, "", "Container is null"));
                mImpressionListener.onSplashAdDismiss();
            }
            return;
        }

        if (isReady && splashAD != null) {
            if (zoomoutAdSwitch) {

                mSplashView = new FrameLayout(container.getContext());
                container.addView(mSplashView,
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                mSplashView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (splashAD != null && mSplashView != null) {
                                splashAD.showAd(mSplashView);
                            }
                        } catch (Throwable t) {
                            if (mImpressionListener != null) {
                                mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
                                mImpressionListener.onSplashAdShowFail(
                                        ErrorCode.getErrorCode(ErrorCode.adShowError, "",
                                                "GDT Splash show with exception"));
                                mImpressionListener.onSplashAdDismiss();
                            }
                        }
                    }
                });
            } else {
                container.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (splashAD != null && container != null) {
                                splashAD.showAd(container);
                            }
                        } catch (Throwable t) {
                            if (mImpressionListener != null) {
                                mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
                                mImpressionListener.onSplashAdShowFail(
                                        ErrorCode.getErrorCode(ErrorCode.adShowError, "",
                                                "GDT Splash show with exception"));
                                mImpressionListener.onSplashAdDismiss();
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void destory() {
        splashAD = null;
    }

    @Override
    public String getNetworkPlacementId() {
        return mUnitId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return GDTATInitManager.getInstance().getNetworkVersion();
    }

    @Override
    public void onADDismissed() {

        if (zoomoutAdSwitch && isSplashEyeAd) {
            if (mSplashEyeAd != null) {
                ATSplashEyeAdListener atSplashEyeAdListener = mSplashEyeAd.getSplashEyeAdListener();
                if (atSplashEyeAdListener != null) {
                    atSplashEyeAdListener.onAdDismiss(true, "");
                }
            }
        } else {
            if (mImpressionListener != null) {
                mImpressionListener.onSplashAdDismiss();
            }
        }
    }

    @Override
    public void onNoAD(com.qq.e.comm.util.AdError adError) {
        if (adError != null) {
            notifyATLoadFail(adError.getErrorCode() + "", adError.getErrorMsg());
            //if gdt splash show fail,will call this
            if (mImpressionListener != null) {
                Log.e(TAG,
                        "GDT Splash show fail:[errorCode:" + adError.getErrorCode() + ",errorMsg:" + adError.getErrorMsg() + "]");
                mDismissType = ATAdConst.DISMISS_TYPE.SHOWFAILED;
                mImpressionListener.onSplashAdShowFail(
                        ErrorCode.getErrorCode(ErrorCode.adShowError, "" + adError.getErrorCode(),
                                adError.getErrorMsg()));
                mImpressionListener.onSplashAdDismiss();
            }
        } else {
            notifyATLoadFail("", "GDT Splash show fail");
            //if gdt splash show fail,will call this
            if (mImpressionListener != null) {
                mImpressionListener.onSplashAdShowFail(
                        ErrorCode.getErrorCode(ErrorCode.adShowError, "", "GDT Splash show fail"));
                mImpressionListener.onSplashAdDismiss();
            }
        }
    }

    @Override
    public void onADPresent() {
    }

    @Override
    public void onADClicked() {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdClicked();
        }
    }

    @Override
    public void onADTick(long l) {

    }

    @Override
    public void onADExposure() {
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdShow();
        }
    }

    @Override
    public void onADLoaded(long l) {
        isReady = true;
        if (splashAD != null && isUseDownloadDialogFrame) {
            splashAD.setDownloadConfirmListener(new DownloadConfirmListener() {
                @Override
                public void onDownloadConfirm(Activity activity, int i, String s,
                                              DownloadConfirmCallBack downloadConfirmCallBack) {
                    if (mImpressionListener != null) {
                        GDTDownloadFirmInfo gdtDownloadFirmInfo = new GDTDownloadFirmInfo();
                        gdtDownloadFirmInfo.appInfoUrl = s;
                        gdtDownloadFirmInfo.scenes = i;
                        gdtDownloadFirmInfo.confirmCallBack = downloadConfirmCallBack;
                        mImpressionListener.onDownloadConfirm(activity, gdtDownloadFirmInfo);
                    }
                }
            });
        }

        if (isC2SBidding) {
            if (mBiddingListener != null) {
                if (splashAD != null) {
                    int ecpm = splashAD.getECPM();
                    double price = ecpm;
                    GDTATBiddingNotice biddingNotice = new GDTATBiddingNotice(splashAD);
                    mBiddingListener.onC2SBiddingResultWithCache(
                            ATBiddingResult.success(price, System.currentTimeMillis() + "",
                                    biddingNotice, ATAdConst.CURRENCY.RMB_CENT), null);
                } else {
                    notifyATLoadFail("", "GDT: SplashAD had been destroy.");
                }
            }
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }
    }


    @Override
    public IATSplashEyeAd getSplashEyeAd() {
        return mSplashEyeAd;
    }

    @Override
    public void onZoomOut() {
        isSplashEyeAd = true;

        if (zoomoutAdSwitch) {
            mSplashEyeAd = new GDTATSplashEyeAd(this, splashAD);
            mSplashEyeAd.setSplashView(mSplashView);

            if (mImpressionListener != null) {
                mImpressionListener.onSplashAdDismiss();
            }
        }

    }

    @Override
    public void onZoomOutPlayFinish() {

    }

    @Override
    public boolean isSupportZoomOut() {
        return zoomoutAdSwitch;
    }

    @Override
    public ATInitMediation getMediationInitManager() {
        return GDTATInitManager.getInstance();
    }

    @Override
    public void getBidRequestInfo(Context applicationContext, final Map<String, Object> serverExtra,
                                  Map<String, Object> localExtra,
                                  final ATBidRequestInfoListener bidRequestInfoListener) {
        mUnitId = getStringFromMap(serverExtra, "unit_id");
        GDTATInitManager.getInstance()
                .getBidRequestInfo(applicationContext, serverExtra, localExtra,
                        bidRequestInfoListener);
    }

    @Override
    public boolean startBiddingRequest(Context context, Map<String, Object> serverExtra,
                                       Map<String, Object> localExtra,
                                       ATBiddingListener biddingListener) {
        isC2SBidding = true;
        if (getMixedFormatAdType() == ATAdConst.ATMixedFormatAdType.NATIVE) {
            return false;
        }
        loadCustomNetworkAd(context, serverExtra, localExtra);
        return true;
    }

}
