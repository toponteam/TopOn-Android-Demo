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
    
    public void alertWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 7.0 以上需要引导用去设置开启窗口浮动权限
            requestDrawOverLays();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 6.0 动态申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, OVERLAY_PERMISSION_REQ_CODE);
        }
    }

    private void createWindow() {
        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 8.0 以上type需要设置成这个
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }

            params.gravity = Gravity.BOTTOM | Gravity.END;

            View windowView = LayoutInflater.from(this).inflate(R.layout.layout_window_info, null);
            tvWindowInfo = windowView.findViewById(R.id.tv_window_info);
            windowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertStackInfo();
                }
            });

            manager.addView(windowView, params);
            new Thread(() -> {
                try {
                    while (true) {
                        tvWindowInfo.post(() -> tvWindowInfo.setText("Thread activeNum:" + Thread.activeCount() + "\nThread groupNum:" + Thread.currentThread().getThreadGroup().activeGroupCount()));
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // android 23 以上先引导用户开启这个权限 该权限动态申请不了
    @TargetApi(Build.VERSION_CODES.M)
    public void requestDrawOverLays() {
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            Toast.makeText(this, "无法展示全局提示窗口，请授予权限", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.this.getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            Toast.makeText(this, "权限已经授予", Toast.LENGTH_SHORT).show();
            createWindow();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "设置权限拒绝", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "设置权限成功", Toast.LENGTH_SHORT).show();
                createWindow();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            if (manager != null && windowView.getWm() == null) {
//                wm.addView(windowView, mLayoutParams);
//            }

            Toast.makeText(this, "权限申请成功1111", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "权限申请失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void alertStackInfo() {
        Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
        Log.e("albertThreadDebug", "all start==============================================");
        for (Map.Entry<Thread, StackTraceElement[]> entry : threadMap.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stackElements = entry.getValue();
            Log.e("albertThreadDebug", "name:" + thread.getName() + " id:" + thread.getId() + " thread:" + thread.getPriority() + " begin==========");
            for (int i = 0; i < stackElements.length; i++) {
                StringBuilder stringBuilder = new StringBuilder("    ");
                stringBuilder.append(stackElements[i].getClassName() + ".")
                        .append(stackElements[i].getMethodName() + "(")
                        .append(stackElements[i].getFileName() + ":")
                        .append(stackElements[i].getLineNumber() + ")");
                Log.e("albertThreadDebug", stringBuilder.toString());
            }
            Log.e("albertThreadDebug", "name:" + thread.getName() + " id:" + thread.getId() + " thread:" + thread.getPriority() + " end==========");
        }
        Log.e("albertThreadDebug", "all end==============================================");
    }
}