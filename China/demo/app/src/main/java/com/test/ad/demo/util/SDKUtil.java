package com.test.ad.demo.util;

import android.content.Context;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATSDK;

public class SDKUtil {
   public static boolean isInitialized = false;
   public static void initSDK(Context context) {
      if(isInitialized){
         return;
      }
      isInitialized = true;
      ATSDK.setNetworkLogDebug(true);
      ATSDK.integrationChecking(context.getApplicationContext());
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
      ATSDK.init(context, PlacementIdUtil.getAppId(context), PlacementIdUtil.getAppKey(context));

//        ATNetworkConfig atNetworkConfig = getAtNetworkConfig();
//        ATSDK.init(this, appid, appKey, atNetworkConfig);
   }
}
