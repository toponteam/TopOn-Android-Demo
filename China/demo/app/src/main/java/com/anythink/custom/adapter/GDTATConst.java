/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import com.qq.e.comm.managers.status.SDKStatus;


public class GDTATConst {
    public static final String AD_HEIGHT = "gdtad_height";

    public static final int NETWORK_FIRM_ID = 8;

    protected static final String CHANNEL = "299";

    public static String getNetworkVersion() {
        try {
            return SDKStatus.getIntegrationSDKVersion();
        } catch (Throwable e) {

        }
        return "";
    }

    public static class DEBUGGER_CONFIG {
        public static final int GDT_NETWORK = 8;

        public static final int GDT_NATIVE_TEMPLATE = 1;
        public static final int GDT_NATIVE_SELF_RENDER = 2;
        public static final int GDT_NATIVE_DRAW_TEMPLATE = 3;
        public static final int GDT_NATIVE_DRAW_SELF_RENDER = 4;

        public static final int GDT_INTERSTITIAL = 1;
        public static final int GDT_INTERSTITIAL_FULL_SCREEN = 2;
    }
}
