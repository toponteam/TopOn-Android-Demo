package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdRenderer;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventListener;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.NativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.network.toutiao.TTATConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeListActivity extends Activity {

    public static final String TAG = NativeListActivity.class.getSimpleName();

    private RecyclerView rvNative;
    private NativeListAdapter mAdapter;
    private int mPage = -1;
    private final int mDataCountInPerPage = 20;
    private final int mCheckLoadItemInterval = 2;
    private boolean isLoadSuccessful;
    private int firstCompletelyVisibleItemPosition = -1;
    private int lastCompletelyVisibleItemPosition = -1;

    String unitIds[] = new String[]{
//            DemoApplicaion.mPlacementId_native_admob
//            DemoApplicaion.mPlacementId_native_all
            DemoApplicaion.mPlacementId_native_mintegral
//            , DemoApplicaion.mPlacementId_native_GDT
//            , DemoApplicaion.mPlacementId_native_toutiao
//            , DemoApplicaion.mPlacementId_native_toutiao_drawer
//            , DemoApplicaion.mPlacementId_native_baidu

    };

    private int adViewWidth;
    private int adViewHeight;
    private ATNative mATNative;
    private boolean isLoadingAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_list);

        adViewWidth = getResources().getDisplayMetrics().widthPixels;
        adViewHeight = dip2px(340);
        initRv();

        checkAndLoadAd();
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void initRv() {
        final List<String> data = createMockData();

        rvNative = findViewById(R.id.rv_native);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(NativeListActivity.this, LinearLayoutManager.VERTICAL, false);
        rvNative.setLayoutManager(layoutManager);
        mAdapter = new NativeListAdapter(adViewWidth, adViewHeight, data, new NativeListAdapter.OnNativeListCallback() {
            @Override
            public ATNativeAdView onBindAdView(NativeAd nativeAd, ATNativeAdView atNativeAdView,  ATNativeAdRenderer<? extends CustomNativeAd> atNativeAdRenderer) {
                return fetchAd(nativeAd, atNativeAdView, atNativeAdRenderer);
            }

            @Override
            public void onClickLoadMore() {
                List<String> mockData = createMockData();
                mAdapter.addData(mockData);
            }
        });
        rvNative.setAdapter(mAdapter);

        rvNative.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (isLoadingAd) {
                    return;
                }

                if (lastCompletelyVisibleItemPosition % mAdapter.getIntervalAd() == mCheckLoadItemInterval
                        || layoutManager.findFirstVisibleItemPosition() % mAdapter.getIntervalAd() == mCheckLoadItemInterval) {
//                    checkAndLoadAd();
                }

            }
        });
    }

    private List<String> createMockData() {
        mPage++;

        List<String> data = new ArrayList<>();
        for (int i = 0; i < mDataCountInPerPage; i++) {
            data.add("Data: " + ((mPage * mDataCountInPerPage) + i));
        }
        return data;

    }

    @Override
    protected void onDestroy() {
        if (mAdapter != null) {
            mAdapter.onDestroy();
            mAdapter = null;
        }

        if (rvNative != null) {
            rvNative.setAdapter(null);
            rvNative = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mAdapter != null) {
            mAdapter.onPause(firstCompletelyVisibleItemPosition, lastCompletelyVisibleItemPosition);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mAdapter != null) {
            mAdapter.onResume(firstCompletelyVisibleItemPosition, lastCompletelyVisibleItemPosition);
        }
        super.onResume();
    }

    // ----------------------------------------------------------------------------------------

    public void checkAndLoadAd() {

        if (isLoadingAd) {
            return;
        }

        if (mATNative == null) {
            Log.e(TAG, "checkAndLoadAd: no ad obj, need to load ad");
            loadAd();
            return;
        }

        NativeAd nativeAd = mATNative.getNativeAd();
        if (nativeAd == null) {
            Log.e(TAG, "checkAndLoadAd: no cache, need to load ad");
            loadAd();

        } else {
            addLoadedCache(nativeAd);
            Log.e(TAG, "checkAndLoadAd:  has cache");
        }
    }


    private void loadAd() {
        if (mATNative == null) {
            mATNative = new ATNative(this, unitIds[0], new ATNativeNetworkListener() {
                @Override
                public void onNativeAdLoaded() {
                    isLoadingAd = false;
                    Log.i(TAG, "native ad onNativeAdLoaded------------- ");

                    NativeAd nativeAd = mATNative.getNativeAd();
                    addLoadedCache(nativeAd);

                    if (!isLoadSuccessful) {
                        isLoadSuccessful = true;
                        findViewById(R.id.rv_native).setVisibility(View.VISIBLE);
                        findViewById(R.id.pb).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                    isLoadingAd = false;
                    Log.e(TAG, "native ad onNativeAdLoadFail------------- " + adError.printStackTrace());
                }
            });
        }

        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);
        mATNative.setLocalExtra(localMap);

        //load ad
        mATNative.makeAdRequest();
        isLoadingAd = true;

        Log.i(TAG, "native ad start to load ad------------- ");

    }

    private void addLoadedCache(NativeAd nativeAd) {
        if (mATNative != null) {
            if (mAdapter != null && nativeAd != null) {
                mAdapter.addCache(nativeAd);
            }
        }
    }

    private ATNativeAdView fetchAd(NativeAd nativeAd, ATNativeAdView atNativeAdView,  ATNativeAdRenderer<? extends CustomNativeAd> atNativeAdRenderer) {

        if (nativeAd != null) {
            Log.i(TAG, "fetchAd: startRenderAd");
            renderAd(nativeAd, atNativeAdView, atNativeAdRenderer);

            return atNativeAdView;
        }
        return null;
    }

    private void renderAd(final NativeAd nativeAd, final ATNativeAdView atNativeAdView,  ATNativeAdRenderer<? extends CustomNativeAd> atNativeAdRenderer) {
        nativeAd.setNativeEventListener(new ATNativeEventListener() {
            @Override
            public void onAdImpressed(ATNativeAdView view, ATAdInfo entity) {
                Log.i(TAG, "native ad onAdImpressed--------\n" + entity.toString());

                checkAndLoadAd();
            }

            @Override
            public void onAdClicked(ATNativeAdView view, ATAdInfo entity) {
                Log.i(TAG, "native ad onAdClicked--------\n" + entity.toString());
            }

            @Override
            public void onAdVideoStart(ATNativeAdView view) {
                Log.i(TAG, "native ad onAdVideoStart--------");
            }

            @Override
            public void onAdVideoEnd(ATNativeAdView view) {
                Log.i(TAG, "native ad onAdVideoEnd--------");
            }

            @Override
            public void onAdVideoProgress(ATNativeAdView view, int progress) {
                Log.i(TAG, "native ad onAdVideoProgress--------:" + progress);
            }
        });

        nativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
            @Override
            public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                for (int i = firstCompletelyVisibleItemPosition; i < lastCompletelyVisibleItemPosition; i++) {
                    RecyclerView.ViewHolder viewHolder = rvNative.findViewHolderForAdapterPosition(i);
                    if (viewHolder != null && viewHolder.itemView instanceof ATNativeAdView) {
                        if (atNativeAdView == viewHolder.itemView) {
                            Log.i(TAG, "onAdCloseButtonClick: remove " + i);
                            mAdapter.removeAdView(i);
                            return;
                        }
                    }
                }
            }
        });

        try {
            Log.i(TAG, "native ad start to render ad------------- ");
            nativeAd.renderAdView(atNativeAdView, atNativeAdRenderer);
            nativeAd.prepare(atNativeAdView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
