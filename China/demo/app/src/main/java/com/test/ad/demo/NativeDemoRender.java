/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.anythink.core.api.ATAdAppInfo;
import com.anythink.nativead.api.ATNativeAdRenderer;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.api.NativeAdInteractionType;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;

import java.util.ArrayList;
import java.util.List;


public class NativeDemoRender implements ATNativeAdRenderer<CustomNativeAd> {

    Context mContext;
    List<View> mClickView = new ArrayList<>();
    List<View> mClickDownloadDirectViews = new ArrayList<>();
    View mCloseView;

    boolean isSelfHandleDownloadConfirm;

    public NativeDemoRender(Context context) {
        mContext = context;
    }

    public void setWhetherSettingDownloadConfirmListener(boolean isSelfHandle) {
        isSelfHandleDownloadConfirm = isSelfHandle;
    }

    View mDevelopView;

    int mNetworkFirmId;
    String adType;

    int mAdWidth;

    public void setAdWidth(int adWidth) {
        mAdWidth = adWidth;
    }

    @Override
    public View createView(Context context, int networkFirmId) {
//        if (mDevelopView == null) {
        mDevelopView = LayoutInflater.from(context).inflate(R.layout.native_ad_item, null);
//        }
        mNetworkFirmId = networkFirmId;
        if (mDevelopView.getParent() != null) {
            ((ViewGroup) mDevelopView.getParent()).removeView(mDevelopView);
        }
        return mDevelopView;
    }

    @Override
    public void renderAdView(View view, CustomNativeAd ad) {
        List<View> customDownloadViews = new ArrayList<>();
        mClickView.clear();
        TextView titleView = (TextView) view.findViewById(R.id.native_ad_title);
        TextView descView = (TextView) view.findViewById(R.id.native_ad_desc);
        TextView ctaView = (TextView) view.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = (TextView) view.findViewById(R.id.native_ad_from);
        FrameLayout contentArea = (FrameLayout) view.findViewById(R.id.native_ad_content_image_area);
        FrameLayout iconArea = (FrameLayout) view.findViewById(R.id.native_ad_image);
        final ATNativeImageView logoView = (ATNativeImageView) view.findViewById(R.id.native_ad_logo);
        View versionArea = view.findViewById(R.id.native_ad_version_area);
        TextView versionTextView = view.findViewById(R.id.native_ad_version);
        versionTextView.setText(Html.fromHtml("<u>" + "版本" + "</u>"));
        versionArea.setVisibility(View.GONE);


        if (mNetworkFirmId == 8 && isSelfHandleDownloadConfirm) {
            customDownloadViews.add(versionTextView);
            versionTextView.setVisibility(View.VISIBLE);
        } else {
            versionTextView.setVisibility(View.GONE);
        }

        //bind view to download directly(Only for Baidu, GDT)
        //(For GDT):If NativeAd call setDownloadConfirmListener, these views' click event will callback to NativeAd.DownloadConfirmListener.onDownloadConfirm and you must handle these event)
        customDownloadViews.add(ctaView);
        // bind close button
        CustomNativeAd.ExtraInfo extraInfo = new CustomNativeAd.ExtraInfo.Builder()
                .setCloseView(mCloseView)
                .setCustomViewList(customDownloadViews) //bind view to download directly(Only for Baidu, GDT, GDT must handle click confirm in DownloadConfirmListener)
                .build();

        ad.setExtraInfo(extraInfo);

        ATAdAppInfo appInfo = ad.getAdAppInfo();
        if (appInfo != null) {
            Log.i("NativeDemoRender", "AppInfo:" + appInfo.toString());
        }


        mClickDownloadDirectViews = new ArrayList<>();
        //Only for GDT
        if (mNetworkFirmId == 8) {
            mClickDownloadDirectViews.add(ctaView);
        } else {
            mClickView.add(ctaView);
        }


        titleView.setText("");
        descView.setText("");
        ctaView.setText("");
        adFromView.setText("");
        titleView.setText("");
        contentArea.removeAllViews();
        iconArea.removeAllViews();
        logoView.setImageDrawable(null);

        View mediaView = ad.getAdMediaView(contentArea, contentArea.getWidth());

        Log.i("NativeDemoRender", "Ad Interaction type:" + (ad.getNativeAdInteractionType() == NativeAdInteractionType.APP_TYPE ? "Application" : "UNKNOW"));

        adType = ad.getAdType();
        switch (adType) {
            case CustomNativeAd.NativeAdConst.VIDEO_TYPE:
                Log.i("NativeDemoRender", "Ad source type: Video" + ", video duration: " + ad.getVideoDuration());
                break;
            case CustomNativeAd.NativeAdConst.IMAGE_TYPE:
                Log.i("NativeDemoRender", "Ad source type: Image");
                break;
            default:
                Log.i("NativeDemoRender", "Ad source type: Unknown");
                break;
        }

        switch (ad.getNativeType()) {
            case CustomNativeAd.NativeType.FEED:
                Log.i("NativeDemoRender", "Native type: Feed");
                break;
            case CustomNativeAd.NativeType.PATCH:
                Log.i("NativeDemoRender", "Native type: Patch");
                break;
        }


        if (ad.isNativeExpress()) {// 模板渲染（个性化模板、自动渲染）
            titleView.setVisibility(View.GONE);
            descView.setVisibility(View.GONE);
            ctaView.setVisibility(View.GONE);
            logoView.setVisibility(View.GONE);
            iconArea.setVisibility(View.GONE);
            if (mCloseView != null) {
                mCloseView.setVisibility(View.GONE);
            }
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (mNetworkFirmId == 6) { //Mintegral express ad's height must be setted
                height = mAdWidth * 3 / 4;
            }

            contentArea.addView(mediaView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
            return;
        }

        // 自渲染（自定义渲染）

        titleView.setVisibility(View.VISIBLE);
        descView.setVisibility(View.VISIBLE);
        ctaView.setVisibility(View.VISIBLE);
        logoView.setVisibility(View.VISIBLE);
        iconArea.setVisibility(View.VISIBLE);
        versionArea.setVisibility(View.VISIBLE);
        if (mCloseView != null) {
            mCloseView.setVisibility(View.VISIBLE);
        }
        View adiconView = ad.getAdIconView();


        final ATNativeImageView iconView = new ATNativeImageView(mContext);
        if (adiconView == null) {
            iconArea.addView(iconView);
            iconView.setImage(ad.getIconImageUrl());
            mClickView.add(iconView);
        } else {
            iconArea.addView(adiconView);
        }


        if (!TextUtils.isEmpty(ad.getAdChoiceIconUrl())) {
            logoView.setImage(ad.getAdChoiceIconUrl());
        } else {
//            logoView.setImageResource(R.drawable.ad_logo);
        }


        int height = mAdWidth * 600 / 1024;
        height = height <= 0 ? FrameLayout.LayoutParams.WRAP_CONTENT : height;
        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
            params.gravity = Gravity.CENTER;
            mediaView.setLayoutParams(params);
            contentArea.addView(mediaView, params);

        } else {
            if (!TextUtils.isEmpty(ad.getVideoUrl())) {
                View playerView = initializePlayer(mContext, ad.getVideoUrl());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
                params.gravity = Gravity.CENTER;
                playerView.setLayoutParams(params);
                contentArea.addView(playerView, params);
            } else {
                ATNativeImageView imageView = new ATNativeImageView(mContext);
                imageView.setImage(ad.getMainImageUrl());
                ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
                imageView.setLayoutParams(params);
                contentArea.addView(imageView, params);
                mClickView.add(imageView);
            }

        }


        titleView.setText(ad.getTitle());
        descView.setText(ad.getDescriptionText());

        if (!TextUtils.isEmpty(ad.getCallToActionText())) {
            ctaView.setVisibility(View.VISIBLE);
            ctaView.setText(ad.getCallToActionText());
        } else {
            ctaView.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(ad.getAdFrom())) {
            adFromView.setText(ad.getAdFrom() != null ? ad.getAdFrom() : "");
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }

        mClickView.add(titleView);
        mClickView.add(descView);


    }

    public List<View> getClickView() {
        return mClickView;
    }

    public void setCloseView(ImageView closeView) {
        this.mCloseView = closeView;

    }

    private View initializePlayer(Context context, String url) {
        VideoView videoView = new VideoView(context);
        videoView.setVideoURI(Uri.parse(url));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
            }
        });
        videoView.start();

        return videoView;
    }

    public List<View> getDownloadDirectViews() {
        return mClickDownloadDirectViews;
    }
}
