package com.test.ad.demo.base;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdSourceStatusListener;
import com.anythink.core.api.AdError;
import com.test.ad.demo.NativeAdActivity;
import com.test.ad.demo.TitleBar;
import com.test.ad.demo.bean.AnnotationAdType;
import com.test.ad.demo.bean.CommonViewBean;
import com.test.ad.demo.util.PlacementIdUtil;
import com.test.ad.demo.util.ViewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseActivity extends Activity {
    protected String mCurrentPlacementName;
    protected String mCurrentPlacementId;
    private Map<String, String> mPlacementIdMap;
    private CommonViewBean mCommonViewBean;

    protected TextView mTVShowLog;
    private static WeakReference<TextView> mTVShowLogWR;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentViewId());

        initView();
        initListener();
        initData();
    }

    protected abstract int getContentViewId();

    protected abstract @AnnotationAdType
    int getAdType();

    protected abstract void onSelectPlacementId(String placementId);

    protected void initView() {
        initViewWithCommonView(getCommonViewBean());
    }

    protected void initListener() {
    }

    @CallSuper
    protected void initData() {
        initPlacementIdMap(getAdType());

        if (mCommonViewBean != null) {
            initPlacementListAdapter(mCommonViewBean.getSpinnerSelectPlacement());
        }
    }

    protected CommonViewBean getCommonViewBean() {
        return null;
    }

    protected static void printLogOnUI(String msg) {
        if (mTVShowLogWR == null || mTVShowLogWR.get() == null || TextUtils.isEmpty(msg)) return;

        ViewUtil.printLog(mTVShowLogWR.get(), msg);
    }

    private void setTitleBar(TitleBar titleBar, int titleResId) {
        if (titleBar != null && titleResId != 0) {
            titleBar.setTitle(titleResId);
            titleBar.setListener(v -> {
                finish();
            });
        }
    }

    private void initViewWithCommonView(CommonViewBean commonViewBean) {
        mCommonViewBean = commonViewBean;
        if (commonViewBean != null) {
            TitleBar titleBar = commonViewBean.getTitleBar();
            if (titleBar != null) {
                setTitleBar(titleBar, commonViewBean.getTitleResId());
            }
            mTVShowLog = commonViewBean.getTvLogView();
            if (mTVShowLog != null) {
                mTVShowLogWR = new WeakReference<>(mTVShowLog);
                mTVShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
        }
    }

    protected String getNativeAdType() {
        return NativeAdActivity.NATIVE_SELF_RENDER_TYPE;
    }

    private void initPlacementIdMap(@AnnotationAdType int adType) {
        switch (adType) {
            case ATAdConst.ATMixedFormatAdType.SPLASH:
                mPlacementIdMap = PlacementIdUtil.getSplashPlacements(this);
                break;
            case ATAdConst.ATMixedFormatAdType.NATIVE:
                mPlacementIdMap = getNativeAdType().equals(NativeAdActivity.NATIVE_SELF_RENDER_TYPE) ? PlacementIdUtil.getNativeSelfrenderPlacements(this) : PlacementIdUtil.getNativeExpressPlacements(this);
                break;
            case ATAdConst.ATMixedFormatAdType.BANNER:
                mPlacementIdMap = PlacementIdUtil.getBannerPlacements(this);
                break;
            case ATAdConst.ATMixedFormatAdType.INTERSTITIAL:
                mPlacementIdMap = PlacementIdUtil.getInterstitialPlacements(this);
                break;
            case ATAdConst.ATMixedFormatAdType.REWARDED_VIDEO:
                mPlacementIdMap = PlacementIdUtil.getRewardedVideoPlacements(this);
                break;
        }
    }

    protected void initPlacementListAdapter(Spinner spinner) {
        if (spinner == null || mPlacementIdMap == null || mPlacementIdMap.size() == 0) return;

        List<String> placementNameList = new ArrayList<>(mPlacementIdMap.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, placementNameList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new PlacementSelectListenerImpl());
    }

    private class PlacementSelectListenerImpl implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            mCurrentPlacementName = parent.getSelectedItem().toString();
            if (mPlacementIdMap != null && mPlacementIdMap.size() > 0) {
                mCurrentPlacementId = mPlacementIdMap.get(mCurrentPlacementName);
            }

            if (!TextUtils.isEmpty(mCurrentPlacementId)) {
                onSelectPlacementId(mCurrentPlacementId);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public static class ATAdSourceStatusListenerImpl implements ATAdSourceStatusListener {
        private final String TAG = getClass().getSimpleName();

        @Override
        public void onAdSourceBiddingAttempt(ATAdInfo adInfo) {
            Log.i(TAG, "onAdSourceBiddingAttempt: " + adInfo.toString());
        }

        @Override
        public void onAdSourceBiddingFilled(ATAdInfo adInfo) {
            Log.i(TAG, "onAdSourceBiddingFilled: " + adInfo.toString());
        }

        @Override
        public void onAdSourceBiddingFail(ATAdInfo adInfo, AdError adError) {
            Log.i(TAG, "onAdSourceBiddingFail Info: " + adInfo.toString());
            if (adError != null) {
                Log.i(TAG, "onAdSourceBiddingFail error: " + adError.getFullErrorInfo());
            }
        }

        @Override
        public void onAdSourceAttempt(ATAdInfo adInfo) {
            Log.i(TAG, "onAdSourceAttempt: " + adInfo.toString());
        }

        @Override
        public void onAdSourceLoadFilled(ATAdInfo adInfo) {
            Log.i(TAG, "onAdSourceLoadFilled: " + adInfo.toString());
        }

        @Override
        public void onAdSourceLoadFail(ATAdInfo adInfo, AdError adError) {
            Log.i(TAG, "onAdSourceLoadFail Info: " + adInfo.toString());
            Log.i(TAG, "onAdSourceLoadFail error: " + adError.getFullErrorInfo());
        }
    }
}
