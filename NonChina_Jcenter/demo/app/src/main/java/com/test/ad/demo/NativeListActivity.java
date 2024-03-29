package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.test.ad.demo.bean.RecycleViewDataBean;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeListActivity extends Activity {

    public static final String TAG = NativeListActivity.class.getSimpleName();

    private RecyclerView dataRecycleView;
    private NativeListAdapter mAdapter;
    private int mPage = -1;
    private final int mDataCountInPerPage = 12;

    private String placementId;

    private ATNative mATNative;

    ATNativeNetworkListener nativeNetworkListener = new ATNativeNetworkListener() {
        @Override
        public void onNativeAdLoaded() {
            Log.i(TAG, "native ad onNativeAdLoaded------------- ");
        }

        @Override
        public void onNativeAdLoadFail(AdError adError) {
            Log.e(TAG, "native ad onNativeAdLoadFail------------- " + adError.getFullErrorInfo());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_list);

//        placementId = PlacementIdUtil.getNativePlacements(this).get("Toutiao");
//        placementId = PlacementIdUtil.getNativePlacements(this).get("All");
//        placementId = PlacementIdUtil.getNativePlacements(this).get("Mintegral");
//        placementId = PlacementIdUtil.getNativePlacements(this).get("GDT");
//        placementId = PlacementIdUtil.getNativePlacements(this).get("Toutiao Draw");
//        placementId = PlacementIdUtil.getNativePlacements(this).get("Baidu");
        placementId = PlacementIdUtil.getListPlacementId(this);

        initView();
        requestNativeAd();
        startRequestData();
        findViewById(R.id.rv_native).setVisibility(View.VISIBLE);
        findViewById(R.id.pb).setVisibility(View.GONE);
    }

    private void startRequestData() {
        final List<RecycleViewDataBean> data = createMockData();
        if (mAdapter == null) {
            mAdapter = new NativeListAdapter(data, new NativeListAdapter.OnNativeListCallback() {
                @Override
                public void onClickLoadMore() {
                    startRequestData();
                }
            });
            mAdapter.setNativeAdHandler(mATNative);
            dataRecycleView.setAdapter(mAdapter);

        } else {
            mAdapter.addData(data);
        }

    }

    private void initView() {
        dataRecycleView = findViewById(R.id.rv_native);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(NativeListActivity.this, LinearLayoutManager.VERTICAL, false);
        dataRecycleView.setLayoutManager(layoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(new ColorDrawable(Color.parseColor("#444444")));
//        dataRecycleView.addItemDecoration(dividerItemDecoration);
    }

    private List<RecycleViewDataBean> createMockData() {
        mPage++;

        List<RecycleViewDataBean> data = new ArrayList<>();
        for (int i = 0; i < mDataCountInPerPage; i++) {
            RecycleViewDataBean recycleViewDataBean = new RecycleViewDataBean();
            if (i != 0 && (i + 1) % 6 == 0) {
                recycleViewDataBean.dataType = RecycleViewDataBean.AD_DATA_TYPE;
            } else {
                recycleViewDataBean.dataType = RecycleViewDataBean.NORMAL_DATA_TYPE;
                recycleViewDataBean.content = "Data: " + ((mPage * mDataCountInPerPage) + i);
            }
            data.add(recycleViewDataBean);
        }
        return data;

    }

    @Override
    protected void onDestroy() {
        if (mAdapter != null) {
            mAdapter.onDestroy();
            mAdapter = null;
        }

        if (dataRecycleView != null) {
            dataRecycleView.setAdapter(null);
            dataRecycleView = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mAdapter != null) {
            mAdapter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mAdapter != null) {
            mAdapter.onResume();
        }
        super.onResume();
    }

    // ----------------------------------------------------------------------------------------

    private void requestNativeAd() {
        if (mATNative == null) {
            mATNative = new ATNative(this, placementId, nativeNetworkListener);
        }

        //load ad
        mATNative.makeAdRequest();

        Log.i(TAG, "native ad start to load ad------------- ");
    }

}
