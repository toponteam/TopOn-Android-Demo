package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

public class PangleNativeExpressAd extends CustomNativeAd {
    private static final String TAG = PangleNativeExpressAd.class.getSimpleName();
    TTNativeExpressAd mTTNativeExpressAd;
    Context mContext;
    String mUnitId;

    TTNativeExpressAd.ExpressAdInteractionListener adapterSelfListener;

    public PangleNativeExpressAd(Context context, String unitId, TTNativeExpressAd ttNativeExpressAd, boolean isDrawFeed) {
        mContext = context.getApplicationContext();
        mUnitId = unitId;
        mTTNativeExpressAd = ttNativeExpressAd;

        setAdData(isDrawFeed);
        bindListener();
    }

    protected void renderExpressAd(TTNativeExpressAd.ExpressAdInteractionListener adapterSelfListener) {
        this.adapterSelfListener = adapterSelfListener;
        if (mTTNativeExpressAd != null) {
            mTTNativeExpressAd.render();
        }
    }

    public void setAdData(boolean isDrawFeed) {


        if (isDrawFeed) {
            // Draw Video
            mTTNativeExpressAd.setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
                @Override
                public void onVideoLoad() {

                }

                @Override
                public void onVideoError(int i, int i1) {
                    Log.i(TAG, String.format("onVideoError, errorCode: %d, errorMsg: %s",
                            i, i1 + ""));
                }

                @Override
                public void onVideoAdStartPlay() {
                    notifyAdVideoStart();
                }

                @Override
                public void onVideoAdPaused() {

                }

                @Override
                public void onVideoAdContinuePlay() {

                }

                @Override
                public void onProgressUpdate(long l, long l1) {

                }

                @Override
                public void onVideoAdComplete() {
                    notifyAdVideoEnd();
                }

                @Override
                public void onClickRetry() {

                }
            });
        }
    }

    @Override
    public void prepare(final View view, FrameLayout.LayoutParams layoutParams) {
//        bindListener();
        if (view != null && view.getContext() instanceof Activity) {
            bindDislike((Activity) view.getContext());
        }
//        if (mTTNativeExpressAd != null) {
//            mTTNativeExpressAd.render();
//        }
    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {

        if (view != null && view.getContext() instanceof Activity) {
            bindDislike((Activity) view.getContext());
        }
//        if (mTTNativeExpressAd != null) {
//            mTTNativeExpressAd.render();
//        }
    }

    /**
     * Set ads dislike, developers can customize styles
     */
    private void bindDislike(Activity activity) {
        if (mTTNativeExpressAd == null) {
            return;
        }
        //Use the default dislike popup style in the default personalization template
        mTTNativeExpressAd.setDislikeCallback(activity, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                //用户选择不喜欢原因后，移除广告展示
                notifyAdDislikeClick();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onRefuse() {

            }
        });
    }

    private void bindListener() {
        if (mTTNativeExpressAd == null) {
            return;
        }
        // Native Express
        mTTNativeExpressAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int i) {
                notifyAdClicked();
            }

            @Override
            public void onAdShow(View view, int i) {
                Log.i(TAG, "onAdShow()");
            }

            @Override
            public void onRenderFail(View view, String s, int i) {
                Log.e(TAG, String.format("onRenderFail, errorCode: %d, errorMsg: %s",
                        i, s));
                if (adapterSelfListener != null) {
                    adapterSelfListener.onRenderFail(view, s, i);
                }
            }

            @Override
            public void onRenderSuccess(View view, float v, float v1) {
                Log.i(TAG, "onRenderSuccess()");
                mMediaView = view;
                if (adapterSelfListener != null) {
                    adapterSelfListener.onRenderSuccess(view, v, v1);
                }

            }
        });

    }

    View mMediaView;


    @Override
    public Bitmap getAdLogo() {
        return null;
    }

    @Override
    public void clear(final View view) {

    }

    @Override
    public View getAdMediaView(Object... object) {
        try {
            if (mMediaView == null && mTTNativeExpressAd != null) {
                mMediaView = mTTNativeExpressAd.getExpressAdView();
            }
            return mMediaView;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }

    @Override
    public void destroy() {
        Log.i(TAG, "destroy()");
        mMediaView = null;
        if (mTTNativeExpressAd != null) {
            mTTNativeExpressAd.setExpressInteractionListener(null);
            mTTNativeExpressAd.destroy();
            mTTNativeExpressAd = null;
        }
        mContext = null;
        adapterSelfListener = null;

    }
}
