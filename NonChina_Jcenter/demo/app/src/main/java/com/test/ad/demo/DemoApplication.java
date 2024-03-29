package com.test.ad.demo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import androidx.multidex.MultiDexApplication;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATInitConfig;
import com.anythink.core.api.ATNetworkConfig;
import com.anythink.core.api.ATSDK;
import com.facebook.stetho.Stetho;
import com.test.ad.demo.util.PlacementIdUtil;

import java.util.ArrayList;
import java.util.List;


public class DemoApplication extends MultiDexApplication {

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

        if (!isMainProcess(this)) {
            return;
        }

        Stetho.initializeWithDefaults(getApplicationContext());
        ATSDK.setNetworkLogDebug(true);
        ATSDK.integrationChecking(getApplicationContext());
//        ATSDK.deniedUploadDeviceInfo(
//                DeviceDataInfo.DEVICE_SCREEN_SIZE
//                , DeviceDataInfo.ANDROID_ID
//                , DeviceDataInfo.APP_PACKAGE_NAME
//                , DeviceDataInfo.APP_VERSION_CODE
//                , DeviceDataInfo.APP_VERSION_NAME
//                , DeviceDataInfo.BRAND
//                , DeviceDataInfo.GAID
//                , DeviceDataInfo.LANGUAGE
//                , DeviceDataInfo.MCC
//                , DeviceDataInfo.MNC
//                , DeviceDataInfo.MODEL
//                , DeviceDataInfo.ORIENTATION
//                , DeviceDataInfo.OS_VERSION_CODE
//                , DeviceDataInfo.OS_VERSION_NAME
//                , DeviceDataInfo.TIMEZONE
//                , DeviceDataInfo.USER_AGENT
//                , DeviceDataInfo.NETWORK_TYPE
//                , ChinaDeviceDataInfo.IMEI
//                , ChinaDeviceDataInfo.MAC
//                , ChinaDeviceDataInfo.OAID
//                , DeviceDataInfo.INSTALLER
//
//        );

        ATSDK.setPersonalizedAdStatus(ATAdConst.PRIVACY.PERSIONALIZED_ALLOW_STATUS);
//        ATSDK.setUseHTTP(true);
        ATSDK.init(this, PlacementIdUtil.getAppId(this), PlacementIdUtil.getAppKey(this));

//        ATNetworkConfig atNetworkConfig = getAtNetworkConfig();
//        ATSDK.init(this, appid, appKey, atNetworkConfig);

    }

    private ATNetworkConfig getAtNetworkConfig() {
        List<ATInitConfig> atInitConfigs = new ArrayList<>();

//        ATInitConfig pangleATInitConfig = new PangleATInitConfig("8025677");
//        ATInitConfig mintegralATInitConfig = new MintegralATInitConfig("100947", "ef13ef712aeb0f6eb3d698c4c08add96");
//        ATInitConfig facebookATInitConfig = new FacebookATInitConfig();
//        ATInitConfig vungleAtInitConfig = new VungleATInitConfig("5ad59a853d927044ac75263a");
//        ATInitConfig adColonyATInitConfig = new AdColonyATInitConfig("app251236acbb494d48a8", "vz6ddfc996216e4c2b99", null);
//        ATInitConfig myTargetATInitConfig = new MyTargetATInitConfig();
//
//        atInitConfigs.add(pangleATInitConfig);
//        atInitConfigs.add(mintegralATInitConfig);
//        atInitConfigs.add(facebookATInitConfig);
//        atInitConfigs.add(vungleAtInitConfig);
//        atInitConfigs.add(adColonyATInitConfig);
//        atInitConfigs.add(myTargetATInitConfig);

        ATNetworkConfig.Builder builder = new ATNetworkConfig.Builder();
        builder.withInitConfigList(atInitConfigs);
        return builder.build();
    }

    public boolean isMainProcess(Context context) {
        try {
            if (null != context) {
                return context.getPackageName().equals(getProcessName(context));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getProcessName(Context cxt) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

}
