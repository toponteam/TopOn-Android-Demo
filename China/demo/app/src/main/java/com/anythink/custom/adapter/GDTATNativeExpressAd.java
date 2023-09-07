/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.compliance.DownloadConfirmCallBack;
import com.qq.e.comm.compliance.DownloadConfirmListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.constants.LoadAdParams;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;

import java.lang.ref.WeakReference;
import java.util.List;

public class GDTATNativeExpressAd extends CustomNativeAd {
    NativeExpressAD mNativeExpressAD;
    NativeExpressADView mNativeExpressADView;

    GDTATNativeLoadListener mLoadListener;

    String mPayload;

    protected GDTATNativeExpressAd(Context context, String unitId, int localWidth, int localHeight,
                                   int videoMuted, int videoAutoPlay, int videoDuration, String payload) {

        mPayload = payload;
        NativeExpressAD.NativeExpressADListener nativeExpressADListener = new NativeExpressAD.NativeExpressADListener() {

            @Override
            public void onNoAD(AdError pAdError) {
                if (mLoadListener != null) {
                    mLoadListener.notifyError(pAdError.getErrorCode() + "", pAdError.getErrorMsg());
                }
                mLoadListener = null;
            }

            @Override
            public void onADLoaded(List<NativeExpressADView> pList) {
                if (pList.size() > 0) {
                    NativeExpressADView nativeExpressADView = pList.get(0);
                    setNetworkInfoMap(nativeExpressADView.getExtraInfo());
                    nativeExpressADView.render();
                } else {
                    if (mLoadListener != null) {
                        mLoadListener.notifyError("", "GDT Ad request success but no Ad return.");
                    }
                    mLoadListener = null;
                }


            }

            @Override
            public void onRenderFail(NativeExpressADView pNativeExpressADView) {
                if (mLoadListener != null) {
                    mLoadListener.notifyError("", "GDT onRenderFail");
                }
                mLoadListener = null;
            }

            @Override
            public void onRenderSuccess(NativeExpressADView pNativeExpressADView) {
                mNativeExpressADView = pNativeExpressADView;

                AdData boundData = mNativeExpressADView.getBoundData();
                if (boundData != null) {
                    if (boundData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                        mAdSourceType = NativeAdConst.VIDEO_TYPE;
                    } else {
                        mAdSourceType = NativeAdConst.IMAGE_TYPE;
                    }
                }
                setVideoDuration(boundData != null ? boundData.getVideoDuration() / 1000 : 0);

                mNativeExpressADView.setMediaListener(new NativeExpressMediaListener() {
                    @Override
                    public void onVideoInit(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onVideoLoading(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onVideoCached(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {

                    }

                    @Override
                    public void onVideoStart(NativeExpressADView nativeExpressADView) {
                        notifyAdVideoStart();
                    }

                    @Override
                    public void onVideoPause(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onVideoComplete(NativeExpressADView nativeExpressADView) {
                        notifyAdVideoEnd();
                    }

                    @Override
                    public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
                        notifyAdVideoVideoPlayFail("" + adError.getErrorCode(), adError.getErrorMsg());
                    }

                    @Override
                    public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onVideoPageClose(NativeExpressADView nativeExpressADView) {

                    }
                });

                if (mLoadListener != null) {
                    mLoadListener.notifyLoaded(GDTATNativeExpressAd.this);
                }
                mLoadListener = null;
            }

            @Override
            public void onADExposure(NativeExpressADView pNativeExpressADView) {
                GDTATInitManager.getInstance().put(getShowId(), new WeakReference<>(mNativeExpressADView));
                notifyAdImpression();
            }

            @Override
            public void onADClicked(NativeExpressADView pNativeExpressADView) {
                notifyAdClicked();
            }

            @Override
            public void onADClosed(NativeExpressADView pNativeExpressADView) {
                notifyAdDislikeClick();
            }

            @Override
            public void onADLeftApplication(NativeExpressADView pNativeExpressADView) {
            }
        };
        int width = ADSize.FULL_WIDTH;
        int height = ADSize.AUTO_HEIGHT;
        if (localWidth > 0) {
            width = GDTATInitManager.getInstance().px2dip(context, localWidth);
        }
        if (localHeight > 0) {
            height = GDTATInitManager.getInstance().px2dip(context, localHeight);
        }

        if (TextUtils.isEmpty(payload)) {
            mNativeExpressAD = new NativeExpressAD(context, new ADSize(width, height), unitId, nativeExpressADListener); // Context must be Activity

        } else {
            mNativeExpressAD = new NativeExpressAD(context, new ADSize(width, height), unitId, nativeExpressADListener, payload); // Context must be Activity

        }

        VideoOption option = new VideoOption.Builder()
                .setAutoPlayMuted(videoMuted == 1)
                .setDetailPageMuted(videoMuted == 1)
                .setAutoPlayPolicy(videoAutoPlay)
                .build();

        mNativeExpressAD.setVideoOption(option);
        if (videoDuration != -1) {
            mNativeExpressAD.setMaxVideoDuration(videoDuration);
        }
    }

    protected void loadAD(GDTATNativeLoadListener loadListener, LoadAdParams loadAdParams) {
        mLoadListener = loadListener;
        if (TextUtils.isEmpty(mPayload)) {
            mNativeExpressAD.loadAD(1, loadAdParams);
        } else {
            mNativeExpressAD.loadAD(1);
        }

    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }


    @Override
    public View getAdMediaView(Object... object) {
        return mNativeExpressADView;
    }

    @Override
    public void registerDownloadConfirmListener() {
        if (mNativeExpressADView != null) {
            mNativeExpressADView.setDownloadConfirmListener(new DownloadConfirmListener() {
                @Override
                public void onDownloadConfirm(Activity activity, int i, String s, DownloadConfirmCallBack downloadConfirmCallBack) {
                    GDTDownloadFirmInfo gdtDownloadFirmInfo = new GDTDownloadFirmInfo();
                    gdtDownloadFirmInfo.appInfoUrl = s;
                    gdtDownloadFirmInfo.scenes = i;
                    gdtDownloadFirmInfo.confirmCallBack = downloadConfirmCallBack;
                    notifyDownloadConfirm(activity, null, gdtDownloadFirmInfo);
                }
            });
        }
    }


    @Override
    public void destroy() {
        if (mNativeExpressADView != null) {
            mNativeExpressADView.setMediaListener(null);
            mNativeExpressADView.destroy();
        }
        mNativeExpressADView = null;
        mLoadListener = null;
        mNativeExpressAD = null;
    }

}
