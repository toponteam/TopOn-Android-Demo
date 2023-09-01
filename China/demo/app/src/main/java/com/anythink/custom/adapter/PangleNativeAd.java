/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;

import java.util.ArrayList;
import java.util.List;

public class PangleNativeAd extends CustomNativeAd {
    TTNativeAd mTTFeedAd;
    Context mContext;
    String mUnitId;

    public PangleNativeAd(Context context, String unitId, TTNativeAd ttFeedAd) {
        mContext = context.getApplicationContext();
        mUnitId = unitId;
        mTTFeedAd = ttFeedAd;

        setAdData();
    }


    public void setAdData() {
        setTitle(mTTFeedAd.getTitle());
        setDescriptionText(mTTFeedAd.getDescription());
        setIconImageUrl(mTTFeedAd.getIcon().getImageUrl());
        List<TTImage> imageList = mTTFeedAd.getImageList();
        ArrayList<String> imageStringList = new ArrayList<>();

        if (imageList != null && imageList.size() > 0) {
            for (TTImage ttImage : imageList) {
                imageStringList.add(ttImage.getImageUrl());
            }
            TTImage mainImage = imageList.get(0);
            setMainImageUrl(mainImage.getImageUrl());
            setMainImageWidth(mainImage.getWidth());
            setMainImageHeight( mainImage.getHeight());
        }
        setImageUrlList(imageStringList);
        setCallToActionText(mTTFeedAd.getButtonText());


        if (mTTFeedAd instanceof TTFeedAd) {
            ((TTFeedAd) mTTFeedAd).setVideoAdListener(new TTFeedAd.VideoAdListener() {
                @Override
                public void onVideoLoad(TTFeedAd ttFeedAd) {
                }

                @Override
                public void onVideoError(int i, int i1) {
                    notifyAdVideoVideoPlayFail("" + i, "" + i1);
                }

                @Override
                public void onVideoAdStartPlay(TTFeedAd ttFeedAd) {
                    notifyAdVideoStart();
                }

                @Override
                public void onVideoAdPaused(TTFeedAd ttFeedAd) {
                }

                @Override
                public void onVideoAdContinuePlay(TTFeedAd ttFeedAd) {
                }

                @Override
                public void onVideoAdComplete(TTFeedAd ttFeedAd) {
                    notifyAdVideoEnd();
                }

                @Override
                public void onProgressUpdate(long s, long s1) {
                }
            });
        }


    }

    private void getChildView(List<View> childViews, View view) {
        if (view instanceof ViewGroup && view != mTTFeedAd.getAdView()) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                getChildView(childViews, child);
            }
        } else {
            if (view != mTTFeedAd.getAdView()) {
                childViews.add(view);
            }
        }
    }

    @Override
    public void prepare(final View view, ATNativePrepareInfo nativePrepareInfo) {

        List<View> clickViewList = nativePrepareInfo.getClickViewList();

        if (clickViewList == null) {
            clickViewList = new ArrayList<>();
            getChildView(clickViewList, view);
        }

        mTTFeedAd.registerViewForInteraction((ViewGroup) view, clickViewList, clickViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                notifyAdClicked();
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                notifyAdClicked();
            }

            @Override
            public void onAdShow(TTNativeAd ttNativeAd) {

            }
        });

        if (view.getContext() instanceof Activity) {
            mTTFeedAd.setActivityForDownloadApp(((Activity) view.getContext()));

            bindDislike(((Activity) view.getContext()));
        }

    }


    private void bindDislike(final Activity activity) {
        bindDislikeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTTFeedAd == null) {
                    return;
                }

                TTAdDislike dislikeDialog = mTTFeedAd.getDislikeDialog(activity);
                dislikeDialog.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onSelected(int i, String s, boolean b) {
                        notifyAdDislikeClick();
                    }

                    @Deprecated
                    public void onSelected(int i, String s) {
                        notifyAdDislikeClick();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Deprecated
                    public void onRefuse() {

                    }
                });
                if (!dislikeDialog.isShow()) {
                    dislikeDialog.showDislikeDialog();
                }
            }
        });
    }


    @Override
    public Bitmap getAdLogo() {
        if (mTTFeedAd != null) {
            return mTTFeedAd.getAdLogo();
        }
        return null;
    }

    @Override
    public void clear(final View view) {
    }

    @Override
    public View getAdMediaView(Object... object) {
        try {
            return mTTFeedAd.getAdView();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public void destroy() {
        try {
            if (mTTFeedAd != null) {
                mTTFeedAd.setActivityForDownloadApp(null);
            }
        } catch (Exception e) {

        }
        mContext = null;
        mTTFeedAd = null;
    }
}
