package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.anythink.banner.api.ATBannerExListener;
import com.anythink.banner.api.ATBannerView;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATNativeAdCustomRender;
import com.anythink.core.api.ATNativeAdInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.ATShowConfig;
import com.anythink.core.api.AdError;
import com.test.ad.demo.base.BaseActivity;
import com.test.ad.demo.bean.CommonViewBean;
import com.test.ad.demo.util.MediationNativeAdUtil;
import com.test.ad.demo.util.ViewUtil;

import java.util.HashMap;
import java.util.Map;

public class BannerAdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = BannerAdActivity.class.getSimpleName();

    private ATBannerView mBannerView;
    private TextView tvLoadAdBtn;
    private ScrollView scrollView;
    private FrameLayout mBannerViewContainer;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_banner;
    }

    @Override
    protected int getAdType() {
        return ATAdConst.ATMixedFormatAdType.BANNER;
    }

    @Override
    protected void onSelectPlacementId(String placementId) {
        mBannerView.setPlacementId(placementId);
        ATBannerView.entryAdScenario(placementId, AdConst.SCENARIO_ID.BANNER_AD_SCENARIO);
        //        mBannerView.setScenario(AdConst.SCENARIO_ID.BANNER_AD_SCENARIO);
        mBannerView.setShowConfig(getATShowConfig());
    }

    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.rl_type).setSelected(true);
        tvLoadAdBtn = findViewById(R.id.banner_load_ad_btn);
        mBannerViewContainer = findViewById(R.id.adview_container);

        //Loading and displaying ads should keep the container and BannerView visible all the time
        mBannerViewContainer.setVisibility(View.VISIBLE);

        scrollView = findViewById(R.id.scroll_view);

        initBannerView();
        addBannerViewToContainer();
    }

    @Override
    protected CommonViewBean getCommonViewBean() {
        CommonViewBean commonViewBean = new CommonViewBean();
        commonViewBean.setTitleBar(findViewById(R.id.title_bar));
        commonViewBean.setTvLogView(findViewById(R.id.tv_show_log));
        commonViewBean.setSpinnerSelectPlacement(findViewById(R.id.spinner_1));
        commonViewBean.setTitleResId(R.string.anythink_title_banner);
        return commonViewBean;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        super.initListener();
        if (mTVShowLog != null) {
            mTVShowLog.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    return false;
                }
            });
        }
        tvLoadAdBtn.setOnClickListener(this);
    }

    private void initBannerView() {
        mBannerView = new ATBannerView(this);
        //Loading and displaying ads should keep the container and BannerView visible all the time
        mBannerView.setVisibility(View.VISIBLE);
        mBannerView.setAdRevenueListener(new AdRevenueListenerImpl());
        mBannerView.setBannerAdListener(new ATBannerExListener() {

            @Override
            public void onDeeplinkCallback(boolean isRefresh, ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                Log.i(TAG, "onDownloadConfirm:" + adInfo.toString() + " networkConfirmInfo:" + networkConfirmInfo);
            }

            @Override
            public void onBannerLoaded() {
                Log.i(TAG, "onBannerLoaded");
                printLogOnUI("onBannerLoaded");
                if (scrollView != null) {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }

            @Override
            public void onBannerFailed(AdError adError) {
                Log.i(TAG, "onBannerFailed: " + adError.getFullErrorInfo());
                printLogOnUI("onBannerFailed" + adError.getFullErrorInfo());
            }

            @Override
            public void onBannerClicked(ATAdInfo entity) {
                Log.i(TAG, "onBannerClicked:" + entity.toString());
                printLogOnUI("onBannerClicked");
            }

            @Override
            public void onBannerShow(ATAdInfo entity) {
                Log.i(TAG, "onBannerShow:" + entity.toString());
                printLogOnUI("onBannerShow");
            }

            @Override
            public void onBannerClose(ATAdInfo entity) {
                Log.i(TAG, "onBannerClose:" + entity.toString());
                printLogOnUI("onBannerClose");
            }

            @Override
            public void onBannerAutoRefreshed(ATAdInfo entity) {
                Log.i(TAG, "onBannerAutoRefreshed:" + entity.toString());
                printLogOnUI("onBannerAutoRefreshed");
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
                Log.i(TAG, "onBannerAutoRefreshFail: " + adError.getFullErrorInfo());
                printLogOnUI("onBannerAutoRefreshFail");
            }
        });

        mBannerView.setAdSourceStatusListener(new ATAdSourceStatusListenerImpl());
    }

    private void loadAd() {
        printLogOnUI(getString(R.string.anythink_ad_status_loading));
        //Loading and displaying ads should keep the container and BannerView visible all the time
        mBannerView.setVisibility(View.VISIBLE);
        mBannerViewContainer.setVisibility(View.VISIBLE);

        int padding = dip2px(12);
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.AD_WIDTH, getResources().getDisplayMetrics().widthPixels - 2 * padding);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, dip2px(60));
        mBannerView.setLocalExtra(localMap);

        //横幅广告使用原生自渲染广告时，设置自定义渲染方式：只需要在发起请求时额外设置setNativeAdCustomRender即可，请求、展示广告流程同横幅广告接入流程相同。
        mBannerView.setNativeAdCustomRender(new NativeAdCustomRender(this));

        mBannerView.loadAd();
    }

    private void addBannerViewToContainer() {
        if (mBannerViewContainer != null && mBannerView != null) {
            mBannerViewContainer.addView(mBannerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mBannerViewContainer.getLayoutParams().height));
        }
    }

    @Override
    protected void onDestroy() {
        if (mBannerViewContainer != null) {
            mBannerViewContainer.removeAllViews();
        }
        if (mBannerView != null) {
            mBannerView.destroy();
        }
        super.onDestroy();
    }

    private int dip2px(int dipValue) {
        return ViewUtil.dip2px(this, dipValue);
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        if (v.getId() == R.id.banner_load_ad_btn) {
            loadAd();
        }
    }

    private ATShowConfig getATShowConfig() {
        ATShowConfig.Builder builder = new ATShowConfig.Builder();
        builder.scenarioId(AdConst.SCENARIO_ID.BANNER_AD_SCENARIO);
        builder.showCustomExt(AdConst.SHOW_CUSTOM_EXT.BANNER_AD_SHOW_CUSTOM_EXT);

        return builder.build();
    }
}
