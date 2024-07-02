package com.test.ad.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.anythink.core.api.IATAdvertiserInfoOperate;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.api.ATNativeMaterial;
import com.anythink.nativead.api.ATNativePrepareExInfo;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.huawei.hms.ads.AppDownloadButton;
import com.test.ad.demo.view.MutiImageView;

import java.util.ArrayList;
import java.util.List;

public class SelfRenderViewUtil {
    private static final String TAG = SelfRenderViewUtil.class.getSimpleName();

    public static void bindSelfRenderView(Context context, ATNativeMaterial adMaterial, View selfRenderView, ATNativePrepareInfo nativePrepareInfo) {
        printNativeAdMaterial(adMaterial);
        int padding = dip2px(context, 5);
        selfRenderView.setPadding(padding, padding, padding, padding);
        TextView titleView = selfRenderView.findViewById(R.id.native_ad_title);
        TextView descView = selfRenderView.findViewById(R.id.native_ad_desc);
        TextView ctaView = selfRenderView.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = selfRenderView.findViewById(R.id.native_ad_from);
        FrameLayout iconArea = selfRenderView.findViewById(R.id.native_ad_image);
        FrameLayout contentArea = selfRenderView.findViewById(R.id.native_ad_content_image_area);
        final ATNativeImageView logoView = selfRenderView.findViewById(R.id.native_ad_logo);
        View closeView = selfRenderView.findViewById(R.id.native_ad_close);
        TextView domainView = selfRenderView.findViewById(R.id.native_ad_domain);   //(v6.1.20+) Yandex domain
        TextView warningView = selfRenderView.findViewById(R.id.native_ad_warning); //(v6.1.20+) Yandex warning
        FrameLayout adLogoContainer = selfRenderView.findViewById(R.id.native_ad_logo_container);   //v6.1.52+
        TextView advertiserIcon = selfRenderView.findViewById(R.id.native_advertiser_icon);     //v6.1.70+

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

        // AppDownloadButton(Only Huawei Ads support)
        View lastView = ((ViewGroup) selfRenderView).getChildAt(((ViewGroup) selfRenderView).getChildCount() - 1);
        // Remove AppDownloadButton since last time added
        if (lastView instanceof AppDownloadButton) {
            ((ViewGroup) selfRenderView).removeView(lastView);
        }
        View appDownloadButton = adMaterial.getCallToActionButton();
        if (appDownloadButton != null) {
            if (appDownloadButton instanceof AppDownloadButton) {
                ((AppDownloadButton) appDownloadButton).setTextSize(dip2px(context, 12));
                appDownloadButton.setPadding(dip2px(context, 46),dip2px(context, 24),dip2px(context, 46),dip2px(context, 24));
            }
            ViewGroup.LayoutParams ctaParams = ctaView.getLayoutParams();
            ((ViewGroup) selfRenderView).addView(appDownloadButton, ctaParams);
            appDownloadButton.setVisibility(View.VISIBLE);
            ctaView.setVisibility(View.INVISIBLE);
        }

        // media view
        View mediaView = adMaterial.getAdMediaView(contentArea);
        int mainImageHeight = adMaterial.getMainImageHeight();
        int mainImageWidth = adMaterial.getMainImageWidth();

        FrameLayout.LayoutParams mainImageParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                , FrameLayout.LayoutParams.WRAP_CONTENT);
        if (mediaView == null) {
            ViewTreeObserver viewTreeObserver = selfRenderView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            // 移除监听器
                            selfRenderView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                            int realMainImageWidth = selfRenderView.getWidth() - dip2px(context,
                                    10);
                            int realMainHeight = 0;

                            if (mainImageWidth > 0 && mainImageHeight > 0 && mainImageWidth > mainImageHeight) {
                                realMainHeight = realMainImageWidth * mainImageHeight / mainImageWidth;
                                mainImageParam.width = realMainImageWidth;
                                mainImageParam.height = realMainHeight;
                            } else {
                                mainImageParam.width = FrameLayout.LayoutParams.MATCH_PARENT;
                                mainImageParam.height = realMainImageWidth * 600 / 1024;
                            }
                        }
                    });
        } else {
            int realMainImageWidth = context.getResources()
                    .getDisplayMetrics().widthPixels - dip2px(context, 10);
            if (context.getResources()
                    .getDisplayMetrics().widthPixels > context.getResources()
                    .getDisplayMetrics().heightPixels) {//Horizontal screen
                realMainImageWidth = context.getResources()
                        .getDisplayMetrics().widthPixels - dip2px(context, 10) - dip2px(context,
                        330) - dip2px(context, 130);
            }
            if (mainImageWidth > 0 && mainImageHeight > 0 && mainImageWidth > mainImageHeight) {
                mainImageParam.width = FrameLayout.LayoutParams.MATCH_PARENT;
                mainImageParam.height = realMainImageWidth * mainImageHeight / mainImageWidth;
            } else {
                mainImageParam.width = FrameLayout.LayoutParams.MATCH_PARENT;
                mainImageParam.height = realMainImageWidth * 600 / 1024;
            }
        }

        List<String> imageList = adMaterial.getImageUrlList();

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
        } else if (imageList != null && imageList.size() > 1) {
            MutiImageView mutiImageView = new MutiImageView(context);
            mutiImageView.setImageList(imageList, mainImageWidth, mainImageHeight);
            nativePrepareInfo.setMainImageView(mutiImageView);//bind main image
            contentArea.addView(mutiImageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            clickViewList.add(mutiImageView);
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
        View adLogoView = adMaterial.getAdLogoView();
        if (adLogoView != null) {
            adLogoContainer.setVisibility(View.VISIBLE);
            adLogoContainer.removeAllViews();
            adLogoContainer.addView(adLogoView);
        } else {
            adLogoContainer.setVisibility(View.GONE);

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
        }

        //ad from (v6.1.52+)
        String adFrom = adMaterial.getAdFrom();
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom);
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }
        nativePrepareInfo.setAdFromView(adFromView);//bind ad from

        //advertiser info (v6.1.70+)
        final IATAdvertiserInfoOperate advertiserInfoOperate = adMaterial.getAdvertiserInfoOperate();
        if (advertiserInfoOperate == null) {
            //When the advertiserInfoOperate is null, hide the advertiser information icon.
            advertiserIcon.setVisibility(View.GONE);
        } else {
            //When the advertiserInfoOperate is not null, show the advertiser information icon and
            //call the API to pull up the advertiser information pop-up box.
            advertiserIcon.setVisibility(View.VISIBLE);
            advertiserIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    advertiserInfoOperate.showAdvertiserInfoDialog(advertiserIcon, true);
                }
            });
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dip2px(context, 40), dip2px(context, 10));//ad choice
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        nativePrepareInfo.setChoiceViewLayoutParams(layoutParams);//bind layout params for ad choice
        nativePrepareInfo.setCloseView(closeView);//bind close button

        String domain = adMaterial.getDomain(); //(v6.1.20+) Yandex domain
        if (!TextUtils.isEmpty(domain)) {
            domainView.setVisibility(View.VISIBLE);
            domainView.setText(domain);
            clickViewList.add(domainView);
            nativePrepareInfo.setDomainView(domainView);
        } else {
            domainView.setVisibility(View.GONE);
        }

        String warning = adMaterial.getWarning(); //(v6.1.20+) Yandex warning
        if (!TextUtils.isEmpty(warning)) {
            warningView.setVisibility(View.VISIBLE);
            warningView.setText(warning);
            clickViewList.add(warningView);
            nativePrepareInfo.setWarningView(warningView);
        } else {
            warningView.setVisibility(View.GONE);
        }

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

    private static void printNativeAdMaterial(ATNativeMaterial adMaterial) {
        if (adMaterial == null) return;

        String adType = adMaterial.getAdType();
        switch (adType) {
            case CustomNativeAd.NativeAdConst.VIDEO_TYPE:
                Log.i(TAG, "Ad source type: Video" + ", video duration: " + adMaterial.getVideoDuration());
                break;
            case CustomNativeAd.NativeAdConst.IMAGE_TYPE:
                Log.i(TAG, "Ad source type: Image");
                break;
            default:
                Log.i(TAG, "Ad source type: Unknown");
                break;
        }
        switch (adMaterial.getNativeType()) {
            case CustomNativeAd.NativeType.FEED:
                Log.i(TAG, "Native type: Feed");
                break;
            case CustomNativeAd.NativeType.PATCH:
                Log.i(TAG, "Native type: Patch");
                break;
        }

        Log.i(TAG, "show native material:" + "\n" +
                "adMaterial:" + adMaterial + "\n" +
                "getTitle:" + adMaterial.getTitle() + "\n" +
                "getDescriptionText:" + adMaterial.getDescriptionText() + "\n" +
                "getNativeType:" + adMaterial.getNativeType() + "\n" +
                "getAdMediaView:" + adMaterial.getAdMediaView() + "\n" +
                "getAdIconView:" + adMaterial.getAdIconView() + "\n" +
                "getIconImageUrl:" + adMaterial.getIconImageUrl() + "\n" +
                "getMainImageUrl:" + adMaterial.getMainImageUrl() + "\n" +
                "getMainImageWidth:" + adMaterial.getMainImageWidth() + "\n" +
                "getMainImageHeight:" + adMaterial.getMainImageHeight() + "\n" +
                "getVideoWidth:" + adMaterial.getVideoWidth() + "\n" +
                "getVideoHeight:" + adMaterial.getVideoHeight() + "\n" +
                "getAppPrice:" + adMaterial.getAppPrice() + "\n" +
                "getAppCommentNum:" + adMaterial.getAppCommentNum() + "\n" +
                "getCallToActionText:" + adMaterial.getCallToActionText() + "\n" +
                "getStarRating:" + adMaterial.getStarRating() + "\n" +
                "getVideoUrl:" + adMaterial.getVideoUrl() + "\n" +
                "getAdChoiceIconUrl:" + adMaterial.getAdChoiceIconUrl() + "\n" +
                "getAdFrom:" + adMaterial.getAdFrom() + "\n" +
                "getImageUrlList:" + adMaterial.getImageUrlList() + "\n" +
                "getNetworkInfoMap:" + adMaterial.getNetworkInfoMap() + "\n" +
                "getAdAppInfo:" + adMaterial.getAdAppInfo() + "\n" +
                "getNativeAdInteractionType:" + (adMaterial.getNativeAdInteractionType()) + "\n" +
                "getVideoDuration:" + adMaterial.getVideoDuration() + "\n" +
                "getAdvertiserName:" + adMaterial.getAdvertiserName() + "\n" +
                "getNativeType:" + adMaterial.getNativeType() + "\n" +
                "getAdType:" + adMaterial.getAdType() + "\n" +
                "getNativeCustomVideo:" + adMaterial.getNativeCustomVideo() + "\n" +
                "getAdLogo:" + adMaterial.getAdLogo() + "\n" +
                "getNativeExpressWidth:" + adMaterial.getNativeExpressWidth() + "\n" +
                "getNativeExpressHeight" + adMaterial.getNativeExpressHeight() + "\n"
        );
    }
}
