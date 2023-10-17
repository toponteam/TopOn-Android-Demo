package com.test.ad.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.anythink.core.api.ATAdAppInfo;
import com.anythink.core.api.ATShakeViewListener;
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
        //log
        printNativeAdMaterial(adMaterial);

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
        FrameLayout slideViewContainer = (FrameLayout) selfRenderView.findViewById(R.id.native_ad_slide_view_container);
        FrameLayout adLogoContainer = selfRenderView.findViewById(R.id.native_ad_logo_container);   //v6.1.52+

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
        View appDownloadButton = adMaterial.getAppDownloadButton();
        if (appDownloadButton != null) {
            if (appDownloadButton instanceof AppDownloadButton) {
                ((AppDownloadButton) appDownloadButton).setTextSize(dip2px(context, 12));
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
            if (context.getResources().getDisplayMetrics().widthPixels > context.getResources()
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
            //clickViewList.add(mediaView);
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

        String adFrom = adMaterial.getAdFrom();

        // ad from
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom);
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }
        nativePrepareInfo.setAdFromView(adFromView);//bind ad from

        //渲染摇一摇组件，若广告不支持摇一摇能力则返回null，目前只有百度广告平台支持
        renderShakeView(context, adMaterial, shakeViewContainer);
        //渲染滑一滑组件，若广告不支持则返回null，滑动区域受容器大小控制，目前只有百度广告平台支持
        renderSlideView(context, adMaterial, slideViewContainer);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dip2px(context, 40), dip2px(context, 10));//ad choice
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        nativePrepareInfo.setChoiceViewLayoutParams(layoutParams);//bind layout params for ad choice
        nativePrepareInfo.setCloseView(closeView);//bind close button

        nativePrepareInfo.setClickViewList(clickViewList);//bind click view list

        //if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
        //    List<View> creativeClickViewList = new ArrayList<>();//click views
        //    creativeClickViewList.add(ctaView);
        //    ((ATNativePrepareExInfo) nativePrepareInfo).setCreativeClickViewList(creativeClickViewList);//bind custom view list
        //}

        View sixInfoView = selfRenderView.findViewById(R.id.six_info);
        ATAdAppInfo adAppInfo = adMaterial.getAdAppInfo();
        if (adAppInfo != null) {
            sixInfoView.setVisibility(View.VISIBLE);
            TextView functionTextView = sixInfoView.findViewById(R.id.function_test);
            TextView developerTextView = sixInfoView.findViewById(R.id.developer_test);
            TextView versionTextView = sixInfoView.findViewById(R.id.version_test);
            TextView privacyTextView = sixInfoView.findViewById(R.id.privacy_test);
            TextView permissionTextView = sixInfoView.findViewById(R.id.permission_test);

            developerTextView.setText(TextUtils.isEmpty(adAppInfo.getPublisher()) ? "" : adAppInfo.getPublisher());
            versionTextView.setText(TextUtils.isEmpty(adAppInfo.getAppVersion()) ? "" : adAppInfo.getAppVersion());


            if (!TextUtils.isEmpty(adAppInfo.getFunctionUrl())) {
                functionTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(functionTextView, adAppInfo.getFunctionUrl());
            } else {
                functionTextView.setOnClickListener(null);
                functionTextView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(adAppInfo.getAppPrivacyUrl())) {
                privacyTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(privacyTextView, adAppInfo.getAppPrivacyUrl());
            } else {
                privacyTextView.setVisibility(View.GONE);
                privacyTextView.setOnClickListener(null);
            }

            if (!TextUtils.isEmpty(adAppInfo.getAppPermissonUrl())) {
                permissionTextView.setVisibility(View.VISIBLE);
                setOpenUrlClickListener(permissionTextView, adAppInfo.getAppPermissonUrl());
            } else {
                permissionTextView.setVisibility(View.GONE);
                permissionTextView.setOnClickListener(null);
            }

            if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
                List<View> appInfoClickViewList = new ArrayList<>();
                appInfoClickViewList.add(functionTextView);
                appInfoClickViewList.add(developerTextView);
                appInfoClickViewList.add(versionTextView);
                appInfoClickViewList.add(privacyTextView);
                appInfoClickViewList.add(permissionTextView);
                ((ATNativePrepareExInfo) nativePrepareInfo).setAppInfoClickViewList(appInfoClickViewList);
            }
        } else {
            sixInfoView.setVisibility(View.GONE);
        }
    }

    private static void renderShakeView(Context context, ATNativeMaterial adMaterial, FrameLayout shakeViewContainer) {
        int shakeViewWidth = dip2px(context, 100);  //组件的宽，不小于80dp
        int shakeViewHeight = dip2px(context, 100); //组件的高，不小于80dp
        View shakeView = adMaterial.getShakeView(shakeViewWidth, shakeViewHeight, new ATShakeViewListener() {
            @Override
            public void onDismiss() {
                shakeViewContainer.setVisibility(View.GONE);
            }
        });
        if (shakeView != null && shakeViewContainer != null) {
            shakeViewContainer.setVisibility(View.VISIBLE);
            shakeViewContainer.removeAllViews();
            FrameLayout.LayoutParams shakeViewLayoutParams = new FrameLayout.LayoutParams(shakeViewWidth, shakeViewHeight);
            shakeViewLayoutParams.gravity = Gravity.CENTER;
            shakeViewContainer.addView(shakeView, shakeViewLayoutParams);
        }
    }

    private static void renderSlideView(Context context, ATNativeMaterial adMaterial, FrameLayout slideViewContainer) {
        int slideViewWidth = dip2px(context, 120);  //滑动引导区域的宽
        int slideViewHeight = dip2px(context, 50); // 滑动引导区域的高
        int repeat = 5; //动画的重复次数，结束后自动隐藏组件
        View slideView = adMaterial.getSlideView(slideViewWidth, slideViewHeight, repeat, new ATShakeViewListener() {
            @Override
            public void onDismiss() {
                slideViewContainer.setVisibility(View.GONE);
            }
        });
        if (slideView != null && slideViewContainer != null) {
            slideViewContainer.setVisibility(View.VISIBLE);
            slideViewContainer.removeAllViews();
            FrameLayout.LayoutParams slideViewLayoutParams = new FrameLayout.LayoutParams(slideViewWidth, slideViewHeight + dip2px(context, 50));
            slideViewLayoutParams.gravity = Gravity.CENTER;
            slideViewContainer.addView(slideView, slideViewLayoutParams);
        }
    }

    private static void setOpenUrlClickListener(View view, final String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Context context = view.getContext();
                    if (context != null) {
                        context.startActivity(intent);
                    }
                } catch (Throwable e2) {
                    e2.printStackTrace();
                }
            }
        });

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
                "adMaterial: " + adMaterial + "\n" +
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
