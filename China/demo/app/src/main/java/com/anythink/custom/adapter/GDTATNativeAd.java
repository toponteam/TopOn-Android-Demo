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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.api.NativeAdInteractionType;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListenerWithClickInfo;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedADAppMiitInfo;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.compliance.DownloadConfirmCallBack;
import com.qq.e.comm.compliance.DownloadConfirmListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class GDTATNativeAd extends CustomNativeAd {
    private static final String TAG = GDTATNativeAd.class.getSimpleName();
    WeakReference<Context> mContext;
    Context mApplicationContext;

    NativeUnifiedADData mUnifiedAdData; //Self-rendering 2.0

    int mVideoMuted;
    int mVideoAutoPlay;
    int mVideoDuration;

    int mMuteApiSet = 0;//0:not set,1:set mute,2:not mute
    View mClickView;
    MediaView mMediaView;
    boolean hasBeenPlay = false;
    //Self-rendering 2.0 must be used
    NativeAdContainer mContainer;

    protected GDTATNativeAd(Context context, NativeUnifiedADData gdtad, int videoMuted, int videoAutoPlay, int videoDuration) {

        mApplicationContext = context.getApplicationContext();
        mContext = new WeakReference<>(context);

        mVideoMuted = videoMuted;
        mVideoAutoPlay = videoAutoPlay;
        mVideoDuration = videoDuration;

        mUnifiedAdData = gdtad;
        setAdData(mUnifiedAdData);

    }

    @Override
    public void registerDownloadConfirmListener() {
        mUnifiedAdData.setDownloadConfirmListener(new DownloadConfirmListener() {
            @Override
            public void onDownloadConfirm(Activity activity, int i, String s, DownloadConfirmCallBack downloadConfirmCallBack) {
                Log.i("GDTATNativeAd", "onDownloadConfirm....");
                View clickView = mClickView;
                mClickView = null;
                GDTDownloadFirmInfo gdtDownloadFirmInfo = new GDTDownloadFirmInfo();
                gdtDownloadFirmInfo.appInfoUrl = s;
                gdtDownloadFirmInfo.scenes = i;
                gdtDownloadFirmInfo.confirmCallBack = downloadConfirmCallBack;
                notifyDownloadConfirm(activity, clickView, gdtDownloadFirmInfo);
            }
        });
    }

    public String getCallToAction(NativeUnifiedADData ad) {
        if (!TextUtils.isEmpty(ad.getCTAText())) {
            return ad.getCTAText();
        }
        boolean isapp = false;
        int status = 0;

        isapp = ad.isAppAd();
        status = ad.getAppStatus();


        if (!isapp) {
            return "浏览";
        }
        switch (status) {
            case 0:
            case 4:
            case 16:
                return "下载";
            case 1:
                return "启动";
            case 2:
                return "更新";
            case 8:
                return "安装";
            default:
                return "浏览";
        }
    }

    private void setAdData(final NativeUnifiedADData unifiedADData) {
        setTitle(unifiedADData.getTitle());
        setDescriptionText(unifiedADData.getDesc());

        setIconImageUrl(unifiedADData.getIconUrl());
        setStarRating((double) unifiedADData.getAppScore());
        setAppPrice(unifiedADData.getAppPrice());

        setCallToActionText(getCallToAction(unifiedADData));

        setMainImageUrl(unifiedADData.getImgUrl());
        setMainImageWidth(unifiedADData.getPictureWidth());
        setMainImageHeight(unifiedADData.getPictureHeight());

        setImageUrlList(unifiedADData.getImgList());

        setVideoDuration(unifiedADData.getVideoDuration() / 1000D);

        setNativeInteractionType(unifiedADData.isAppAd() ? NativeAdInteractionType.APP_DOWNLOAD_TYPE : NativeAdInteractionType.UNKNOW);

        NativeUnifiedADAppMiitInfo nativeUnifiedADAppMiitInfo = unifiedADData.getAppMiitInfo();
        if (unifiedADData.isAppAd() && nativeUnifiedADAppMiitInfo != null) {
            String downloadCount = "";
            try {
                downloadCount = String.valueOf(unifiedADData.getDownloadCount());
            } catch (Exception e) {

            }
            setAdAppInfo(new GDTATDownloadAppInfo(nativeUnifiedADAppMiitInfo, downloadCount));
        }

        if (unifiedADData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            mAdSourceType = NativeAdConst.VIDEO_TYPE;
        } else {
            mAdSourceType = NativeAdConst.IMAGE_TYPE;
        }

        unifiedADData.setNativeAdEventListener(new NativeADEventListenerWithClickInfo() {
            @Override
            public void onADExposed() {
                GDTATInitManager.getInstance().put(getShowId(), new WeakReference<>(unifiedADData));
                notifyAdImpression();
            }


            @Override
            public void onADClicked(View view) {
                mClickView = view; //Record click view
                Log.i("GDTATNativeAd", "onADClicked...." + view);
                notifyAdClicked();
            }


            @Override
            public void onADError(AdError adError) {

            }

            @Override
            public void onADStatusChanged() {

            }
        });

        setNetworkInfoMap(unifiedADData.getExtraInfo());

    }

    @Override
    public View getAdMediaView(Object... object) {

        if (mUnifiedAdData != null) {
            if (mUnifiedAdData.getAdPatternType() != AdPatternType.NATIVE_VIDEO) {
                return super.getAdMediaView(object);
            }
            if (mMediaView == null) {
                mMediaView = new MediaView(mApplicationContext);
                mMediaView.setBackgroundColor(0xff000000);
                ViewGroup.LayoutParams _params = mMediaView.getLayoutParams();
                if (_params == null) {
                    _params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                mMediaView.setLayoutParams(_params);

            }

            return mMediaView;
        }

        return super.getAdMediaView(object);
    }

    @Override
    public boolean isNativeExpress() {
        return false;
    }

    @Override
    public void prepare(View view, ATNativePrepareInfo nativePrepareInfo) {
        if (mUnifiedAdData != null && mContainer != null) {

            List<View> clickViewList = nativePrepareInfo.getClickViewList();

            if (clickViewList == null || clickViewList.size() == 0) {
                clickViewList = new ArrayList<>();
                fillChildView(view, clickViewList);
            }

            FrameLayout.LayoutParams layoutParams = nativePrepareInfo.getChoiceViewLayoutParams();

            List<View> downloadDirectlyClickViews = new ArrayList<>();
            if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
                List<View> creativeClickViewList = ((ATNativePrepareExInfo) nativePrepareInfo).getCreativeClickViewList();
                if (creativeClickViewList != null) {
                    downloadDirectlyClickViews.addAll(creativeClickViewList);
                }
            }

            mUnifiedAdData.bindAdToView(view.getContext(), mContainer, layoutParams, clickViewList, downloadDirectlyClickViews);
            try {
                if (mMediaView == null) {
                    return;
                }
                mUnifiedAdData.bindMediaView(mMediaView, new VideoOption.Builder()
                        .setAutoPlayMuted(mVideoMuted == 1)
                        .setDetailPageMuted(mVideoMuted == 1)
                        .setAutoPlayPolicy(mVideoAutoPlay)
                        .build(), new NativeADMediaListener() {
                    @Override
                    public void onVideoInit() {
                    }

                    @Override
                    public void onVideoLoading() {
                    }

                    @Override
                    public void onVideoReady() {
                    }

                    @Override
                    public void onVideoLoaded(int i) {
                    }

                    @Override
                    public void onVideoStart() {
                        notifyAdVideoStart();
                    }

                    @Override
                    public void onVideoPause() {
                    }

                    @Override
                    public void onVideoResume() {
                    }

                    @Override
                    public void onVideoCompleted() {
                        notifyAdVideoEnd();
                    }

                    @Override
                    public void onVideoError(AdError adError) {
                        notifyAdVideoVideoPlayFail("" + adError.getErrorCode(), adError.getErrorMsg());
                    }

                    @Override
                    public void onVideoStop() {

                    }

                    @Override
                    public void onVideoClicked() {

                    }
                });

                if (mMuteApiSet > 0) {
                    mUnifiedAdData.setVideoMute(mMuteApiSet == 1);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public ViewGroup getCustomAdContainer() {
        if (mUnifiedAdData != null) {
            mContainer = new NativeAdContainer(mApplicationContext);
        }
        return mContainer;
    }

    private void fillChildView(View parentView, List<View> childViews) {
        if (parentView instanceof ViewGroup && parentView != mMediaView) {
            ViewGroup viewGroup = (ViewGroup) parentView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                fillChildView(child, childViews);
            }
        } else {
            childViews.add(parentView);
        }
    }

    @Override
    public void clear(View view) {
        unregisterView(view);
    }

    private void unregisterView(View view) {
        if (view == null) {
            return;
        }
        if (view instanceof ViewGroup && view != mMediaView) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                unregisterView(child);
            }
        } else {
            view.setOnClickListener(null);
            view.setClickable(false);
        }
    }

    @Override
    public void onResume() {
        if (mUnifiedAdData != null) {
            mUnifiedAdData.resume();
        }
    }

    @Override
    public void resumeVideo() {
        if (mUnifiedAdData != null) {
            mUnifiedAdData.resumeVideo();
        }
    }

    @Override
    public void pauseVideo() {
        if (mUnifiedAdData != null) {
            mUnifiedAdData.pauseVideo();
        }
    }

    @Override
    public void setVideoMute(boolean isMute) {
        mMuteApiSet = isMute ? 1 : 2;
        if (mUnifiedAdData != null) {
            mUnifiedAdData.setVideoMute(isMute);
        }
    }

    @Override
    public double getVideoProgress() {
        if (mUnifiedAdData != null) {
            return mUnifiedAdData.getVideoCurrentPosition() / 1000D;
        }

        return super.getVideoProgress();
    }

    @Override
    public void destroy() {
        super.destroy();

        if (mUnifiedAdData != null) {
            mUnifiedAdData.setNativeAdEventListener(null);
            mUnifiedAdData.destroy();
            mUnifiedAdData = null;
        }
        mMediaView = null;

        mApplicationContext = null;
        if (mContext != null) {
            mContext.clear();
            mContext = null;
        }

        if (mContainer != null) {
            mContainer.removeAllViews();
            mContainer = null;
        }
    }
}
