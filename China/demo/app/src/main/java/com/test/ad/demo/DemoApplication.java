/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.webkit.WebView;

import com.anythink.core.api.ATSDK;
import com.facebook.stetho.Stetho;

import java.util.HashMap;
import java.util.Map;

public class DemoApplication extends MultiDexApplication {
    public static final String appid = "a5aa1f9deda26d";
    public static final String appKey = "4f7b9ac17decb9babec83aac078742c7";

    @Override
    public void onCreate() {
        super.onCreate();
//        JacocoHelper.Builder builder = new JacocoHelper.Builder();
//        builder.setApplication(this).setDebuggable(true);
//        JacocoHelper.initialize(builder.build());

        //Android 9 or above must be set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        Stetho.initializeWithDefaults(getApplicationContext());
        ATSDK.setNetworkLogDebug(true);
        ATSDK.integrationChecking(getApplicationContext());

        Map<String, Object> custommap = new HashMap<String, Object>();
        custommap.put("key1","initCustomMap1");
        custommap.put("key2","initCustomMap2");
        ATSDK.initCustomMap(custommap);

        Map<String, Object> subcustommap = new HashMap<String, Object>();
        subcustommap.put("key1","initPlacementCustomMap1");
        subcustommap.put("key2","initPlacementCustomMap2");
        ATSDK.initPlacementCustomMap("b5aa1fa4165ea3",subcustommap);//native  facebook

        ATSDK.setChannel("testChannle");
        ATSDK.setSubChannel("testSubChannle");

        ATSDK.init(this, appid, appKey);

    }

}
