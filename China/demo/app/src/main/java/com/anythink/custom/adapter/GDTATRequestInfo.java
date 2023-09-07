/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import com.anythink.core.api.ATMediationRequestInfo;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class GDTATRequestInfo extends ATMediationRequestInfo {

    HashMap<String, Object> paramMap;

    public GDTATRequestInfo(String appId, String unitId) {
        this.networkFirmId = GDTATConst.NETWORK_FIRM_ID;
        paramMap = new HashMap<>();
        paramMap.put("app_id", appId);
        paramMap.put("unit_id", unitId);
    }


    @Override
    public void setFormat(String format) {
        switch (format) {
            case "4":
                className = GDTATSplashAdapter.class.getName();
                break;
        }
    }

    @Override
    public Map<String, Object> getRequestParamMap() {
        return paramMap;
    }
}
