/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitialAutoAd;
import com.anythink.interstitial.api.ATInterstitialAutoEventListener;
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener;
import com.anythink.nativead.api.ATNative;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InterstitalAutoActivity extends Activity {
    static final String TAG = "InterstitalAutoActivity";
    private static ATInterstitialAutoLoadListener autoLoadListener = new ATInterstitialAutoLoadListener() {
        @Override
        public void onInterstitialAutoLoaded(String placementId) {
            Log.i(TAG, "PlacementId:" + placementId + ": onInterstitialAutoLoaded");
        }

        @Override
        public void onInterstitialAutoLoadFail(String placementId, AdError adError) {
            Log.i(TAG, "PlacementId:" + placementId + ": onInterstitialAutoLoadFail:\n" + adError.getFullErrorInfo());
        }

    };

    Map<String, String> interstitialPlacementIdMap;
    String[] interstitialPlacementName;

    int mCurrentSelectIndex;

    ATInterstitialAutoEventListener autoEventListener = new ATInterstitialAutoEventListener() {
        @Override
        public void onInterstitialAdClicked(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdClicked:\n" + adInfo.toString());
        }

        @Override
        public void onInterstitialAdShow(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdShow:\n" + adInfo.toString());
        }

        @Override
        public void onInterstitialAdClose(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdClose:\n" + adInfo.toString());
        }

        @Override
        public void onInterstitialAdVideoStart(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdVideoStart:\n" + adInfo.toString());
        }

        @Override
        public void onInterstitialAdVideoEnd(ATAdInfo adInfo) {
            Log.i(TAG, "onInterstitialAdVideoEnd:\n" + adInfo.toString());
        }

        @Override
        public void onInterstitialAdVideoError(AdError adError) {
            Log.i(TAG, "onInterstitialAdVideoError:\n" + adError.getFullErrorInfo());
        }

        public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
            Log.i(TAG, "onDeeplinkCallback:\n" + adInfo.toString() + "| isSuccess:" + isSuccess);
        }

        public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_load);

        ATInterstitialAutoAd.init(this, null, autoLoadListener);

        interstitialPlacementIdMap = PlacementIdUtil.getInterstitialPlacements(this);

        Set<String> interstitialoKeyset = interstitialPlacementIdMap.keySet();
        interstitialPlacementName = new String[interstitialoKeyset.size()];
        interstitialPlacementName = interstitialoKeyset.toArray(interstitialPlacementName);


        findViewById(R.id.autoAdInit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> interstitialList = new ArrayList<>();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(InterstitalAutoActivity.this);
                dialogBuilder.setTitle("Choose Interstitial Auto Placement");
                dialogBuilder.setMultiChoiceItems(interstitialPlacementName, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            interstitialList.add(interstitialPlacementName[which]);
                        } else {
                            interstitialList.remove(interstitialPlacementName[which]);
                        }
                        Log.i("InterstitalAutoActivity", "select:" + interstitialPlacementName[which] + "---:status:" + isChecked);
                    }
                });

                dialogBuilder.setPositiveButton("Add Auto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String item : interstitialList) {
                            Log.i("InterstitalAutoActivity", "select add auto item:" + item);
                        }

                        ATInterstitialAutoAd.addPlacementId(placementNameToPlacementIds(interstitialList));

                    }
                });

                dialogBuilder.setNegativeButton("Remove Auto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String item : interstitialList) {
                            Log.i("InterstitalAutoActivity", "select remove auto item:" + item);
                        }

                        ATInterstitialAutoAd.removePlacementId(placementNameToPlacementIds(interstitialList));
                    }
                });
                dialogBuilder.create().show();
            }
        });


        Spinner spinner = (Spinner) findViewById(R.id.auto_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                InterstitalAutoActivity.this, android.R.layout.simple_spinner_dropdown_item,
                interstitialPlacementName);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(InterstitalAutoActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentSelectIndex = position;
                ATNative.entryAdScenario(interstitialPlacementIdMap.get(interstitialPlacementName[mCurrentSelectIndex]), "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.autoAdCheckReady).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATAdStatusInfo atAdStatusInfo = ATInterstitialAutoAd.checkAdStatus(interstitialPlacementIdMap.get(interstitialPlacementName[mCurrentSelectIndex]));
                Toast.makeText(InterstitalAutoActivity.this, "interstitial ad ready status:" + atAdStatusInfo.isReady(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.autoAdShow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATInterstitialAutoAd.show(InterstitalAutoActivity.this, interstitialPlacementIdMap.get(interstitialPlacementName[mCurrentSelectIndex]), autoEventListener);
            }
        });

    }


    private String[] placementNameToPlacementIds(List<String> placementNames) {
        String[] placementIds = new String[placementNames.size()];
        for (int i = 0; i < placementNames.size(); i++) {
            placementIds[i] = interstitialPlacementIdMap.get(placementNames.get(i));
        }
        return placementIds;
    }
}
