/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.anythink.banner.api.ATBannerExListener;
import com.anythink.banner.api.ATBannerView;
import com.anythink.china.api.ATAppDownloadListener;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.network.gdt.GDTDownloadFirmInfo;
import com.test.ad.demo.gdt.DownloadApkConfirmDialogWebView;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerAdActivity extends Activity {

    private static final String TAG = BannerAdActivity.class.getSimpleName();

    ATBannerView mBannerView;

    Map<String, Object> localMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_banner);

        Map<String, String> placementIdMap = PlacementIdUtil.getBannerPlacements(this);
        List<String> placementNameList = new ArrayList<>(placementIdMap.keySet());

        Spinner spinner = (Spinner) findViewById(R.id.banner_spinner);
        final FrameLayout frameLayout = findViewById(R.id.adview_container);
        mBannerView = new ATBannerView(this);

        // Only for GDT (true: open download dialog, false: download directly)
        localMap.put(ATAdConst.KEY.AD_CLICK_CONFIRM_STATUS, true);
        mBannerView.setPlacementId(placementIdMap.get(placementNameList.get(0)));
        mBannerView.setLocalExtra(localMap);

        frameLayout.addView(mBannerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip2px(300)));
        mBannerView.setBannerAdListener(new ATBannerExListener() {

            @Override
            public void onDeeplinkCallback(boolean isRefresh, ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                /**
                 * Only for GDT
                 */
                if (networkConfirmInfo instanceof GDTDownloadFirmInfo) {
                    //Open Dialog view
                    try {
                        new DownloadApkConfirmDialogWebView(context, ((GDTDownloadFirmInfo) networkConfirmInfo).appInfoUrl, ((GDTDownloadFirmInfo) networkConfirmInfo).confirmCallBack).show();
                        Log.i(TAG, "nonDownloadConfirm open confirm dialog");
                    } catch (Throwable e) {
                        if (((GDTDownloadFirmInfo) networkConfirmInfo).confirmCallBack != null) {
                            ((GDTDownloadFirmInfo) networkConfirmInfo).confirmCallBack.onConfirm();
                        }
                    }
                }
            }

            @Override
            public void onBannerLoaded() {
                Log.i(TAG, "onBannerLoaded");
                Toast.makeText(BannerAdActivity.this,
                        "onBannerLoaded",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerFailed(AdError adError) {
                Log.i(TAG, "onBannerFailed: " + adError.getFullErrorInfo());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerFailed: " + adError.getFullErrorInfo(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerClicked(ATAdInfo entity) {
                Log.i(TAG, "onBannerClicked:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerClicked",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerShow(ATAdInfo entity) {
                Log.i(TAG, "onBannerShow:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerShow",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerClose(ATAdInfo entity) {
                Log.i(TAG, "onBannerClose:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerClose",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerAutoRefreshed(ATAdInfo entity) {
                Log.i(TAG, "onBannerAutoRefreshed:" + entity.toString());
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
                Log.i(TAG, "onBannerAutoRefreshFail: " + adError.getFullErrorInfo());

            }
        });

        mBannerView.setAdDownloadListener(new ATAppDownloadListener() {

            @Override
            public void onDownloadStart(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadStart: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadUpdate(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadUpdate: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadPause(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadPause: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadFinish(ATAdInfo adInfo, long totalBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadFinish: totalBytes: " + totalBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onDownloadFail(ATAdInfo adInfo, long totalBytes, long currBytes, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onDownloadFail: totalBytes: " + totalBytes
                        + "\ncurrBytes:" + currBytes
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }

            @Override
            public void onInstalled(ATAdInfo adInfo, String fileName, String appName) {
                Log.i(TAG, "ATAdInfo:" + adInfo.toString() + "\n" + "onInstalled:"
                        + "\nfileName:" + fileName
                        + "\nappName:" + appName);
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                BannerAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                placementNameList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(BannerAdActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                String placementName = parent.getSelectedItem().toString();
                mBannerView.setPlacementId(placementIdMap.get(placementName));
                mBannerView.setLocalExtra(localMap);
                mBannerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBannerView.loadAd();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBannerView != null) {
            mBannerView.destroy();
        }
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
