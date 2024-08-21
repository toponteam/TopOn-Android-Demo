package com.anythink.custom.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anythink.core.api.ATAdConst;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdmobNativeAd extends CustomNativeAd implements NativeAd.OnNativeAdLoadedListener {

    private final String TAG = AdmobNativeAd.class.getSimpleName();

    Context mContext;
    LoadCallbackListener mCustomNativeListener;
    String mUnitId;


    MediaView mMediaView;

    NativeAd mNativeAd;

    int mediaRatio = NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_UNKNOWN;
    int adChoicePlacement = -1;

    private AdmobNativeAd(Context context, String unitId, LoadCallbackListener customNativeListener, Map<String, Object> serverExtras, Map<String, Object> localExtras) {
        mContext = context.getApplicationContext();
        mCustomNativeListener = customNativeListener;
        mUnitId = unitId;
    }

    public AdmobNativeAd(Context context, String ratio, String unitId, LoadCallbackListener customNativeListener, Map<String, Object> serverExtras, Map<String, Object> localExtras) {

        this(context, unitId, customNativeListener, serverExtras, localExtras);

        if (!TextUtils.isEmpty(ratio)) {
            switch (ratio) {
                case "1":
                    this.mediaRatio = NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_ANY;
                    break;
                case "2":
                    this.mediaRatio = NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE;
                    break;
                case "3":
                    this.mediaRatio = NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT;
                    break;
                case "4":
                    this.mediaRatio = NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_SQUARE;
                    break;
                default:
                    this.mediaRatio = NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_UNKNOWN;
                    break;
            }
        }

        if (localExtras != null) {

            try {
                if (localExtras.containsKey(ATAdConst.KEY.AD_CHOICES_PLACEMENT)) {
                    int tempAdChoicePlacement = Integer.parseInt(localExtras.get(ATAdConst.KEY.AD_CHOICES_PLACEMENT).toString());
                    switch (tempAdChoicePlacement) {
                        case ATAdConst.AD_CHOICES_PLACEMENT_TOP_LEFT:
                            adChoicePlacement = NativeAdOptions.ADCHOICES_TOP_LEFT;
                            break;
                        case ATAdConst.AD_CHOICES_PLACEMENT_TOP_RIGHT:
                            adChoicePlacement = NativeAdOptions.ADCHOICES_TOP_RIGHT;
                            break;
                        case ATAdConst.AD_CHOICES_PLACEMENT_BOTTOM_RIGHT:
                            adChoicePlacement = NativeAdOptions.ADCHOICES_BOTTOM_RIGHT;
                            break;
                        case ATAdConst.AD_CHOICES_PLACEMENT_BOTTOM_LEFT:
                            adChoicePlacement = NativeAdOptions.ADCHOICES_BOTTOM_LEFT;
                            break;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    public void loadAd(final Context context, final Map<String, Object> serverExtras, final Map<String, Object> localExtras) {

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions.Builder builder = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .setMediaAspectRatio(mediaRatio);

        if (adChoicePlacement != -1) {
            builder.setAdChoicesPlacement(adChoicePlacement);
        }

        NativeAdOptions adOptions = builder.build();


        AdLoader adLoader = new AdLoader.Builder(context, mUnitId)
                .forNativeAd(AdmobNativeAd.this)
                .withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        if (mCustomNativeListener != null) {
                            mCustomNativeListener.onFail(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                        }
                        mCustomNativeListener = null;
                    }

                    @Override
                    public void onAdClicked() {
                        notifyAdClicked();
                    }

                    @Override
                    public void onAdImpression() {
                        notifyAdImpression();
                    }

                })
                .withNativeAdOptions(adOptions).build();


        AdRequest.Builder adRequestBuilder = AdMobInitManager.getInstance().getRequestBuilder();
        adLoader.loadAd(adRequestBuilder.build());
    }

    @Override
    public void onNativeAdLoaded(NativeAd nativeAd) {
        mNativeAd = nativeAd;

        setTitle(mNativeAd.getHeadline());
        setDescriptionText(mNativeAd.getBody());
        if (mNativeAd != null && mNativeAd.getIcon() != null && mNativeAd.getIcon().getUri() != null) {
            setIconImageUrl(mNativeAd.getIcon().getUri().toString());
        }

        List<NativeAd.Image> imageList = mNativeAd.getImages();
        if (imageList != null && imageList.size() > 0) {
            NativeAd.Image image = imageList.get(0);
            if (image != null && image.getUri() != null) {
                setMainImageUrl(image.getUri().toString());

                Drawable drawable = image.getDrawable();
                if (drawable != null) {
                    setMainImageWidth(drawable.getIntrinsicWidth());
                    setMainImageHeight(drawable.getIntrinsicHeight());
                }
            }
        }

        setCallToActionText(mNativeAd.getCallToAction());
        setStarRating(mNativeAd.getStarRating() == null ? 5.0 : mNativeAd.getStarRating());
        setAdFrom(mNativeAd.getStore());
        try {
            setAppPrice(Double.valueOf(mNativeAd.getPrice()));
        } catch (Exception e) {

        }
        setAdvertiserName(mNativeAd.getAdvertiser());

        MediaContent mediaContent = mNativeAd.getMediaContent();
        if (mediaContent != null && mediaContent.hasVideoContent()) {
            setVideoDuration(mediaContent.getDuration());
            mAdSourceType = NativeAdConst.VIDEO_TYPE;
        } else {
            mAdSourceType = NativeAdConst.IMAGE_TYPE;
        }

        if (mCustomNativeListener != null) {
            mCustomNativeListener.onSuccess(AdmobNativeAd.this);
        }
        mCustomNativeListener = null;

    }


    NativeAdView mNativeAdView;

    @Override
    public ViewGroup getCustomAdContainer() {
        mNativeAdView = new NativeAdView(mContext);
        return mNativeAdView;
    }

    @Override
    public View getAdMediaView(Object... object) {
        if (mMediaView == null) {
            mMediaView = new MediaView(mContext);
            mMediaView.setImageScaleType(ImageView.ScaleType.FIT_CENTER);

            if (mNativeAd != null) {
                MediaContent mediaContent = mNativeAd.getMediaContent();
                if (mediaContent != null) {
                    mMediaView.setMediaContent(mediaContent);
                    VideoController videoController = mediaContent.getVideoController();
                    if (videoController != null) {
                        videoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                            @Override
                            public void onVideoStart() {
                                super.onVideoStart();
                                notifyAdVideoStart();
                            }

                            @Override
                            public void onVideoPlay() {
                                super.onVideoPlay();
                            }

                            @Override
                            public void onVideoPause() {
                                super.onVideoPause();
                            }

                            @Override
                            public void onVideoEnd() {
                                super.onVideoEnd();
                                notifyAdVideoEnd();
                            }

                            @Override
                            public void onVideoMute(boolean b) {
                                super.onVideoMute(b);
                            }
                        });
                    }
                }
            }
        }

        return mMediaView;
    }

    boolean hasSetTitleView = false;
    boolean hasSetDescView = false;
    boolean hasSetCtaView = false;
    boolean hasSetMainImageView = false;
    boolean hasSetIconView = false;

    @Override
    public void prepare(View view, ATNativePrepareInfo nativePrepareInfo) {
        try {
            View titleView = nativePrepareInfo.getTitleView();
            View descView = nativePrepareInfo.getDescView();
            View ctaView = nativePrepareInfo.getCtaView();
            View mainImageView = nativePrepareInfo.getMainImageView();
            View iconView = nativePrepareInfo.getIconView();

            if (mNativeAdView != null && mMediaView != null) {
                mNativeAdView.setMediaView(mMediaView);
            }

            List<View> clickViewList = nativePrepareInfo.getClickViewList();
            if (clickViewList != null && clickViewList.size() > 0) {
                for (View clickView : clickViewList) {

                    if (titleView != null) {
                        if (clickView == titleView) {
                            mNativeAdView.setHeadlineView(clickView);
                            continue;
                        }
                    } else if (clickView instanceof TextView) {
                        if (TextUtils.equals(mNativeAd.getHeadline(), ((TextView) clickView).getText())) {
                            mNativeAdView.setHeadlineView(clickView);
                            continue;
                        }
                    }

                    if (descView != null) {
                        if (clickView == descView) {
                            mNativeAdView.setBodyView(clickView);
                            continue;
                        }
                    } else if (clickView instanceof TextView) {
                        if (TextUtils.equals(mNativeAd.getBody(), ((TextView) clickView).getText())) {
                            mNativeAdView.setBodyView(clickView);
                            continue;
                        }
                    }

                    if (ctaView != null) {
                        if (clickView == ctaView) {
                            mNativeAdView.setCallToActionView(clickView);
                            continue;
                        }
                    } else if (clickView instanceof TextView) {
                        if (TextUtils.equals(mNativeAd.getCallToAction(), ((TextView) clickView).getText())) {
                            mNativeAdView.setCallToActionView(clickView);
                            continue;
                        }
                    }

                    if (iconView != null) {
                        if (clickView == iconView) {
                            mNativeAdView.setIconView(clickView);
                            continue;
                        }
                    }

                    if (mainImageView != null) {
                        if (clickView == mainImageView) {
                            if (clickView instanceof ImageView) {
                                mNativeAdView.setImageView(clickView);
                            }
                        }
                    }
                }

                if (mNativeAdView != null && mNativeAd != null) {
                    mNativeAdView.setNativeAd(mNativeAd);
                }
                return;
            }

            hasSetTitleView = false;
            hasSetDescView = false;
            hasSetCtaView = false;
            hasSetMainImageView = false;
            hasSetIconView = false;

            if (titleView != null) {
                hasSetTitleView = true;
                mNativeAdView.setHeadlineView(titleView);
            }
            if (descView != null) {
                hasSetDescView = true;
                mNativeAdView.setBodyView(descView);
            }
            if (ctaView != null) {
                hasSetCtaView = true;
                mNativeAdView.setCallToActionView(ctaView);
            }
            if (iconView != null) {
                hasSetIconView = true;
                mNativeAdView.setIconView(iconView);
            }
            if (mainImageView != null) {
                if (mainImageView instanceof ImageView) {
                    hasSetMainImageView = true;
                    mNativeAdView.setImageView(mainImageView);
                }
            }

            if (hasSetTitleView && hasSetDescView && hasSetCtaView && hasSetIconView && hasSetMainImageView) {
                if (mNativeAdView != null && mNativeAd != null) {
                    mNativeAdView.setNativeAd(mNativeAd);
                }
                return;
            }


            List<View> imageViews = new ArrayList<>();

            getView(imageViews, mNativeAdView);

            for (int i = 0; i < imageViews.size(); i++) {

                View imageView = imageViews.get(i);

                if (!hasSetIconView) {
                    hasSetIconView = true;
                    mNativeAdView.setIconView(imageView);
                    continue;
                }
                if (hasSetMainImageView) {
                    break;
                }

                hasSetMainImageView = true;
                mNativeAdView.setImageView(imageView);
            }
            if (mNativeAdView != null && mNativeAd != null) {
                mNativeAdView.setNativeAd(mNativeAd);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void getView(List<View> imageViews, View view) {
        if (view instanceof ViewGroup && view != mMediaView) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                getView(imageViews, child);
            }
        } else {
            if (view instanceof ImageView) {
                if (!hasSetIconView || !hasSetMainImageView) {
                    imageViews.add(view);
                }
            } else if (view instanceof Button || view instanceof TextView) {
                String text = ((TextView) view).getText().toString();
                if (mNativeAd != null && mNativeAdView != null) {
                    if (!hasSetTitleView && text.equals(mNativeAd.getHeadline())) {
                        hasSetTitleView = true;
                        mNativeAdView.setHeadlineView(view);
                    }
                    if (!hasSetDescView && text.equals(mNativeAd.getBody())) {
                        hasSetDescView = true;
                        mNativeAdView.setBodyView(view);
                    }
                    if (!hasSetCtaView && text.equals(mNativeAd.getCallToAction())) {
                        hasSetCtaView = true;
                        mNativeAdView.setCallToActionView(view);
                    }
                }
            }
        }
    }

    @Override
    public void clear(View view) {
    }

    @Override
    public void destroy() {
        if (mNativeAdView != null) {
            mNativeAdView.destroy();
            mNativeAdView = null;
        }
        mMediaView = null;
        mCustomNativeListener = null;
        mContext = null;
        if (mNativeAd != null) {
            mNativeAd.destroy();
            mNativeAd = null;
        }
    }

    @Override
    public void setVideoMute(boolean isMute) {
        super.setVideoMute(isMute);
        if (mNativeAd != null && mNativeAd.getMediaContent() != null &&
                mNativeAd.getMediaContent().getVideoController() != null) {
            mNativeAd.getMediaContent().getVideoController().mute(isMute);
        }
    }

    boolean mIsAutoPlay;

    public void setIsAutoPlay(boolean isAutoPlay) {
        mIsAutoPlay = isAutoPlay;
    }

    protected interface LoadCallbackListener {
        void onSuccess(CustomNativeAd customNativeAd);

        void onFail(String errorCode, String errorMsg);
    }
}
