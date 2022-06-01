package com.test.ad.demo;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;

import java.util.ArrayList;
import java.util.List;

public class SelfRenderViewUtil {

    public static void bindSelfRenderView(Context context, ATNativeMaterial adMaterial, View selfRenderView, ATNativePrepareInfo nativePrepareInfo, int height) {
        TextView titleView = (TextView) selfRenderView.findViewById(R.id.native_ad_title);
        TextView descView = (TextView) selfRenderView.findViewById(R.id.native_ad_desc);
        TextView ctaView = (TextView) selfRenderView.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = (TextView) selfRenderView.findViewById(R.id.native_ad_from);
        FrameLayout iconArea = (FrameLayout) selfRenderView.findViewById(R.id.native_ad_image);
        FrameLayout contentArea = (FrameLayout) selfRenderView.findViewById(R.id.native_ad_content_image_area);
        final ATNativeImageView logoView = (ATNativeImageView) selfRenderView.findViewById(R.id.native_ad_logo);
        View closeView = selfRenderView.findViewById(R.id.native_ad_close);

        titleView.setText("");
        descView.setText("");
        ctaView.setText("");
        adFromView.setText("");
        titleView.setText("");
        contentArea.removeAllViews();
        iconArea.removeAllViews();
        logoView.setImageDrawable(null);

        titleView.setVisibility(View.VISIBLE);
        descView.setVisibility(View.VISIBLE);
        ctaView.setVisibility(View.VISIBLE);
        logoView.setVisibility(View.VISIBLE);
        iconArea.setVisibility(View.VISIBLE);
        closeView.setVisibility(View.VISIBLE);


        // bind view
        if (nativePrepareInfo == null) {
            nativePrepareInfo = new ATNativePrepareInfo();
        }
        List<View> clickViewList = new ArrayList<>();//click views


        String title = adMaterial.getTitle();
        String descriptionText = adMaterial.getDescriptionText();
        View adIconView = adMaterial.getAdIconView();
        String iconImageUrl = adMaterial.getIconImageUrl();
        String callToActionText = adMaterial.getCallToActionText();
        View mediaView = adMaterial.getAdMediaView(contentArea);
        String adChoiceIconUrl = adMaterial.getAdChoiceIconUrl();
        String adFrom = adMaterial.getAdFrom();
        View adLogoView = adMaterial.getAdLogoView();

        // title
        titleView.setText(title);
        nativePrepareInfo.setTitleView(titleView);//bind title
        clickViewList.add(titleView);


        // desc
        descView.setText(descriptionText);
        nativePrepareInfo.setDescView(descView);//bind desc
        clickViewList.add(descView);


        // cta button
        if (!TextUtils.isEmpty(callToActionText)) {
            ctaView.setText(callToActionText);
        } else {
            ctaView.setVisibility(View.GONE);
        }
        nativePrepareInfo.setCtaView(ctaView);//bind cta button
        clickViewList.add(ctaView);


        // icon
        final ATNativeImageView iconView = new ATNativeImageView(context);
        if (adIconView == null) {
            iconArea.addView(iconView);
            iconView.setImage(iconImageUrl);
            nativePrepareInfo.setIconView(iconView);//bind icon
            clickViewList.add(iconView);
        } else {
            iconArea.addView(adIconView);
            nativePrepareInfo.setIconView(adIconView);//bind icon
            clickViewList.add(adIconView);
        }


        // media view
        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
            params.gravity = Gravity.CENTER;
            mediaView.setLayoutParams(params);
            contentArea.addView(mediaView, params);
            clickViewList.add(mediaView);
        } else {
            if (!TextUtils.isEmpty(adMaterial.getVideoUrl())) {
                View playerView = initializePlayer(context, adMaterial.getVideoUrl());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
                params.gravity = Gravity.CENTER;
                playerView.setLayoutParams(params);
                contentArea.addView(playerView, params);
                clickViewList.add(playerView);
            } else {
                ATNativeImageView imageView = new ATNativeImageView(context);
                imageView.setImage(adMaterial.getMainImageUrl());
                ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
                imageView.setLayoutParams(params);
                contentArea.addView(imageView, params);

                nativePrepareInfo.setMainImageView(imageView);//bind main image
                clickViewList.add(imageView);
            }
        }


        // ad from
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom);
        } else {
            adFromView.setVisibility(View.GONE);
        }
        nativePrepareInfo.setAdFromView(adFromView);//bind ad from


        // ad choice
        if (!TextUtils.isEmpty(adChoiceIconUrl)) {
            logoView.setImage(adChoiceIconUrl);
            nativePrepareInfo.setAdLogoView(logoView);//bind ad choice
        } else if (adLogoView != null) {
            FrameLayout logoContainer = selfRenderView.findViewById(R.id.native_ad_logo_container);
            if (logoContainer != null) {
                logoContainer.setVisibility(View.VISIBLE);
                logoContainer.addView(adLogoView);
                nativePrepareInfo.setAdLogoView(adLogoView);//bind ad choice
            }
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dip2px(context, 40), dip2px(context, 10));//ad choice
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        nativePrepareInfo.setChoiceViewLayoutParams(layoutParams);//bind layout params for ad choice

        nativePrepareInfo.setCloseView(closeView);//bind close button


        nativePrepareInfo.setClickViewList(clickViewList);//bind click view list

        if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
            List<View> creativeClickViewList = new ArrayList<>();//click views
            creativeClickViewList.add(descView);
            ((ATNativePrepareExInfo) nativePrepareInfo).setCreativeClickViewList(creativeClickViewList);//bind custom view list
        }
    }

    private static View initializePlayer(Context context, String url) {
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

    private static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


}
