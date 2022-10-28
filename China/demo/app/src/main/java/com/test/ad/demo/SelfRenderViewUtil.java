package com.test.ad.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.anythink.core.api.ATShakeViewListener;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;

import java.util.ArrayList;
import java.util.List;

public class SelfRenderViewUtil {

    public static void bindSelfRenderView(Context context, ATNativeMaterial adMaterial, View selfRenderView, ATNativePrepareInfo nativePrepareInfo) {
        int padding = dip2px(context, 5);
        selfRenderView.setPadding(padding, padding, padding, padding);
        TextView titleView = (TextView) selfRenderView.findViewById(R.id.native_ad_title);
        TextView descView = (TextView) selfRenderView.findViewById(R.id.native_ad_desc);
        TextView ctaView = (TextView) selfRenderView.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = (TextView) selfRenderView.findViewById(R.id.native_ad_from);
        FrameLayout iconArea = (FrameLayout) selfRenderView.findViewById(R.id.native_ad_image);
        FrameLayout contentArea = (FrameLayout) selfRenderView.findViewById(R.id.native_ad_content_image_area);
        final ATNativeImageView logoView = (ATNativeImageView) selfRenderView.findViewById(R.id.native_ad_logo);
        View closeView = selfRenderView.findViewById(R.id.native_ad_close);
        FrameLayout shakeViewContainer = (FrameLayout) selfRenderView.findViewById(R.id.native_ad_shake_view_container);

        // bind view
        if (nativePrepareInfo == null) {
            nativePrepareInfo = new ATNativePrepareInfo();
        }
        List<View> clickViewList = new ArrayList<>();//click views


        String title = adMaterial.getTitle();
        // title
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            nativePrepareInfo.setTitleView(titleView);//bind title
            clickViewList.add(titleView);
            titleView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
        }


        String descriptionText = adMaterial.getDescriptionText();
        if (!TextUtils.isEmpty(descriptionText)) {
            // desc
            descView.setText(descriptionText);
            nativePrepareInfo.setDescView(descView);//bind desc
            clickViewList.add(descView);
            descView.setVisibility(View.VISIBLE);
        } else {
            descView.setVisibility(View.GONE);
        }

        // icon
        View adIconView = adMaterial.getAdIconView();
        String iconImageUrl = adMaterial.getIconImageUrl();
        iconArea.removeAllViews();
        final ATNativeImageView iconView = new ATNativeImageView(context);
        if (adIconView != null) {
            iconArea.addView(adIconView);
            nativePrepareInfo.setIconView(adIconView);//bind icon
            clickViewList.add(adIconView);
            iconArea.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(iconImageUrl)) {
            iconArea.addView(iconView);
            iconView.setImage(iconImageUrl);
            nativePrepareInfo.setIconView(iconView);//bind icon
            clickViewList.add(iconView);
            iconArea.setVisibility(View.VISIBLE);
        } else {
            iconArea.setVisibility(View.INVISIBLE);
        }

        // cta button
        String callToActionText = adMaterial.getCallToActionText();
        if (!TextUtils.isEmpty(callToActionText)) {
            ctaView.setText(callToActionText);
            nativePrepareInfo.setCtaView(ctaView);//bind cta button
            clickViewList.add(ctaView);
            ctaView.setVisibility(View.VISIBLE);
        } else {
            ctaView.setVisibility(View.GONE);
        }

        // media view
        View mediaView = adMaterial.getAdMediaView(contentArea);
        int mainImageHeight = adMaterial.getMainImageHeight();
        int mainImageWidth = adMaterial.getMainImageWidth();

        int realMainImageWidth = context.getResources().getDisplayMetrics().widthPixels - dip2px(context, 10);
        int realMainHeight = 0;

        FrameLayout.LayoutParams mainImageParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                , FrameLayout.LayoutParams.WRAP_CONTENT);
        if (mainImageWidth > 0 && mainImageHeight > 0 && mainImageWidth > mainImageHeight) {
            realMainHeight = realMainImageWidth * mainImageHeight / mainImageWidth;
            mainImageParam.width = realMainImageWidth;
            mainImageParam.height = realMainHeight;
        } else {
            mainImageParam.width = FrameLayout.LayoutParams.MATCH_PARENT;
            mainImageParam.height = realMainImageWidth * 600 / 1024;
        }

        contentArea.removeAllViews();
        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }
            mainImageParam.gravity = Gravity.CENTER;
            mediaView.setLayoutParams(mainImageParam);
            contentArea.addView(mediaView, mainImageParam);
            clickViewList.add(mediaView);
            contentArea.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(adMaterial.getMainImageUrl())) {
            ATNativeImageView imageView = new ATNativeImageView(context);
            imageView.setImage(adMaterial.getMainImageUrl());
            imageView.setLayoutParams(mainImageParam);
            contentArea.addView(imageView, mainImageParam);

            nativePrepareInfo.setMainImageView(imageView);//bind main image
            clickViewList.add(imageView);
            contentArea.setVisibility(View.VISIBLE);
        } else {
            contentArea.removeAllViews();
            contentArea.setVisibility(View.GONE);
        }


        //Ad Logo
        String adChoiceIconUrl = adMaterial.getAdChoiceIconUrl();
        Bitmap adLogoBitmap = adMaterial.getAdLogo();
        if (!TextUtils.isEmpty(adChoiceIconUrl)) {
            logoView.setImage(adChoiceIconUrl);
            nativePrepareInfo.setAdLogoView(logoView);//bind ad choice
            logoView.setVisibility(View.VISIBLE);
        } else if (adLogoBitmap != null) {
            logoView.setImageBitmap(adLogoBitmap);
            logoView.setVisibility(View.VISIBLE);
        } else {
            logoView.setImageBitmap(null);
            logoView.setVisibility(View.GONE);
        }

        String adFrom = adMaterial.getAdFrom();

        // ad from
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom);
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }
        nativePrepareInfo.setAdFromView(adFromView);//bind ad from

        // get the shakeView if the ad platform support, width or height at least 80dp.
        int shakeViewWidth = dip2px(context, 100), shakeViewHeight = dip2px(context, 100);
        View shakeView = adMaterial.getShakeView(shakeViewWidth, shakeViewHeight, new ATShakeViewListener() {
            @Override
            public void onDismiss() {

            }
        });
        if (shakeView != null && shakeViewContainer != null) {
            shakeViewContainer.setVisibility(View.VISIBLE);
            shakeViewContainer.removeAllViews();
            FrameLayout.LayoutParams shakeViewLayoutParams = new FrameLayout.LayoutParams(shakeViewWidth, shakeViewHeight);
            shakeViewLayoutParams.gravity = Gravity.CENTER;
            shakeViewContainer.addView(shakeView, shakeViewLayoutParams);
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dip2px(context, 40), dip2px(context, 10));//ad choice
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        nativePrepareInfo.setChoiceViewLayoutParams(layoutParams);//bind layout params for ad choice
        nativePrepareInfo.setCloseView(closeView);//bind close button

        nativePrepareInfo.setClickViewList(clickViewList);//bind click view list

        if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
            List<View> creativeClickViewList = new ArrayList<>();//click views
            creativeClickViewList.add(ctaView);
            ((ATNativePrepareExInfo) nativePrepareInfo).setCreativeClickViewList(creativeClickViewList);//bind custom view list
        }
    }

//    private static View initializePlayer(Context context, String url) {
//        VideoView videoView = new VideoView(context);
//        videoView.setVideoURI(Uri.parse(url));
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//            }
//        });
//        videoView.start();
//
//        return videoView;
//    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


}
