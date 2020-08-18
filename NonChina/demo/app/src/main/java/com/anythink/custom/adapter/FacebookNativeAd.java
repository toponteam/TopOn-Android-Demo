package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;

import java.util.List;


public class FacebookNativeAd extends CustomNativeAd implements NativeAdListener {

    NativeAd mFacebookNativeAd;
    Context mContext;

    public FacebookNativeAd(Context context, NativeAd nativeAd) {
        mContext = context.getApplicationContext();
        mFacebookNativeAd = nativeAd;
        mFacebookNativeAd.setAdListener(this);
    }

    public void loadAd(String bidPayload) {
        if (TextUtils.isEmpty(bidPayload)) {
            mFacebookNativeAd.loadAd();
        } else {
            mFacebookNativeAd.loadAdFromBid(bidPayload);
        }

    }

    // Lifecycle Handlers
    @Override
    public void prepare(final View view, FrameLayout.LayoutParams layoutParams) {
        if (view == null) {
            return;
        }
        try {
            if (mContainer != null) {
                mFacebookNativeAd.registerViewForInteraction(mContainer, mMediaView, mAdIconView);
            } else {
                mFacebookNativeAd.registerViewForInteraction(view, mMediaView, mAdIconView);
            }
            prepareFacebookAdChoiceView(view, layoutParams);

        } catch (Exception e) {

        }
    }

    private void prepareFacebookAdChoiceView(View view, FrameLayout.LayoutParams layoutParams) {
        AdOptionsView adOptionsView = new AdOptionsView(view.getContext(), mFacebookNativeAd, mContainer);
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        }

        if (layoutParams.height > 0) {
            float scale = mContext.getResources().getDisplayMetrics().density;
            int iconSize = (int) (layoutParams.height / scale + 0.5f);
            adOptionsView.setIconSizeDp(iconSize);
        }

        mContainer.addView(adOptionsView, layoutParams);
    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {
        if (view == null) {
            return;
        }
        try {
            if (mContainer != null) {
                mFacebookNativeAd.registerViewForInteraction(mContainer, mMediaView, mAdIconView, clickViewList);
            } else {
                mFacebookNativeAd.registerViewForInteraction(view, mMediaView, mAdIconView, clickViewList);
            }
            prepareFacebookAdChoiceView(view, layoutParams);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    NativeAdLayout mContainer;

    @Override
    public ViewGroup getCustomAdContainer() {
        mContainer = new NativeAdLayout(mContext);
        return mContainer;
    }

    MediaView mMediaView;

    @Override
    public View getAdMediaView(Object... object) {
        try {
            if (mMediaView != null) {
                mMediaView.setListener(null);
                mMediaView.destroy();
                mMediaView = null;
            }
            mMediaView = new MediaView(mContext);
            mMediaView.setListener(new MediaViewListener() {
                @Override
                public void onPlay(MediaView mediaView) {
                }

                @Override
                public void onVolumeChange(MediaView mediaView, float v) {
                }

                @Override
                public void onPause(MediaView mediaView) {
                }

                @Override
                public void onComplete(MediaView mediaView) {
                    notifyAdVideoEnd();
                }

                @Override
                public void onEnterFullscreen(MediaView mediaView) {
                }

                @Override
                public void onExitFullscreen(MediaView mediaView) {
                }

                @Override
                public void onFullscreenBackground(MediaView mediaView) {
                }

                @Override
                public void onFullscreenForeground(MediaView mediaView) {
                }
            });
            return mMediaView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    AdIconView mAdIconView;

    @Override
    public View getAdIconView() {
        try {
            if (mAdIconView != null) {
                mAdIconView.destroy();
                mAdIconView = null;
            }
            mAdIconView = new AdIconView(mContext);
            return mAdIconView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void clear(final View view) {
        if (mMediaView != null) {
            mMediaView.setListener(null);
            mMediaView.destroy();
            mMediaView = null;
        }
        if (mFacebookNativeAd != null) {
            mFacebookNativeAd.unregisterView();
        }

    }

    @Override
    public void destroy() {
        if (mFacebookNativeAd != null) {
            mFacebookNativeAd.setAdListener(null);
            mFacebookNativeAd.unregisterView();
            mFacebookNativeAd.destroy();
            mFacebookNativeAd = null;
        }
        if (mMediaView != null) {
            mMediaView.setListener(null);
            mMediaView.destroy();
            mMediaView = null;
        }
        mContext = null;
        if (mAdIconView != null) {
            mAdIconView.destroy();
            mAdIconView = null;
        }
        if (mContainer != null) {
            mContainer.removeAllViews();
            mContainer = null;
        }
    }

    /**
     * facebook listener--------------------------------------------------------------------------------
     **/

    @Override
    public void onError(Ad ad, AdError adError) {

    }

    @Override
    public void onAdLoaded(Ad ad) {
    }

    @Override
    public String getTitle() {
        if (mFacebookNativeAd != null) {
            return mFacebookNativeAd.getAdHeadline();
        }
        return "";
    }


    @Override
    public String getDescriptionText() {
        if (mFacebookNativeAd != null) {
            return mFacebookNativeAd.getAdBodyText();
        }
        return "";
    }

    @Override
    public String getCallToActionText() {
        if (mFacebookNativeAd != null) {
            return mFacebookNativeAd.getAdCallToAction();
        }
        return "";
    }

    @Override
    public String getAdFrom() {
        if (mFacebookNativeAd != null) {
            return mFacebookNativeAd.getSponsoredTranslation();
        }
        return "";
    }

    @Override
    public void onAdClicked(Ad ad) {
        notifyAdClicked();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }

    boolean mIsAutoPlay;

    public void setIsAutoPlay(boolean isAutoPlay) {
        mIsAutoPlay = isAutoPlay;
    }

    @Override
    public void onMediaDownloaded(Ad ad) {

    }
}
