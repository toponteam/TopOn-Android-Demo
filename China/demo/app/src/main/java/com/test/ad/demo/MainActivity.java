/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.anythink.core.api.ATSDK;
import com.anythink.core.api.DeviceInfoCallback;

import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends Activity {

    private int OVERLAY_PERMISSION_REQ_CODE = 2333;
    WindowManager manager;
    TextView tvWindowInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        alertWindow();

        ((TextView)findViewById(R.id.tv_sdk_demo)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        findViewById(R.id.nativeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeAdActivity.class));
            }
        });

        findViewById(R.id.rewardedVideoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardVideoAdActivity.class));
            }
        });

        findViewById(R.id.interstitialBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InterstitialAdActivity.class));
            }
        });

        findViewById(R.id.bannerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BannerAdActivity.class));
            }
        });

        findViewById(R.id.splashBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SplashAdActivity.class));
            }
        });

//        findViewById(R.id.nativeBannerAdBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, NativeBannerActivity.class));
//            }
//        });
//
//        findViewById(R.id.nativeSplashAdBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, NativeSplashActivity.class));
//            }
//        });
//
//        findViewById(R.id.nativeListBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, NativeListActivity.class));
//            }
//        });
//
//        findViewById(R.id.multiLoadBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, MultipleFormatLoadActivity.class));
//            }
//        });
//
//        findViewById(R.id.rewardedVideoAutoBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, RewardVideoAutoActivity.class));
//            }
//        });
//
//        findViewById(R.id.interstitialAutoBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, InterstitalAutoActivity.class));
//            }
//        });

        ((TextView) findViewById(R.id.tv_version)).setText(getResources().getString(R.string.anythink_sdk_version, ATSDK.getSDKVersionName()));

        ATSDK.testModeDeviceInfo(this, new DeviceInfoCallback() {
            @Override
            public void deviceInfo(String deviceInfo) {
                if (!TextUtils.isEmpty(deviceInfo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(deviceInfo);
                        String androidID = jsonObject.optString("AndroidID");

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                TextView deviceIdTextView = (TextView) findViewById(R.id.tv_device_id);
                                deviceIdTextView.setText(getResources().getString(R.string.anythink_click_to_copy_device_id, androidID));
                                deviceIdTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        copyContentToClipboard(MainActivity.this, androidID);

                                        Toast.makeText(MainActivity.this, "AndroidID：" + androidID, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void copyContentToClipboard(Context context, String content) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", content);
        cm.setPrimaryClip(mClipData);
    }
    

}
