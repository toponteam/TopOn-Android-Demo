/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.text.TextUtils;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBidRequestInfo;
import com.qq.e.comm.managers.GDTAdSdk;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GDTBidRequestInfo extends ATBidRequestInfo {

    String buyerId;
    JSONObject jsonObject = new JSONObject();

    GDTBidRequestInfo(Map<String, Object> serviceExtra, Map<String, Object> localExtra) {
        try {
            String appid = serviceExtra.get("app_id").toString();
            String unitid = serviceExtra.get("unit_id").toString();

            Map<String, Object> buyerMap = new HashMap<>();
            GDTATInitManager.getInstance().fillRequestMap(buyerMap, serviceExtra);
            buyerId = GDTAdSdk.getGDTAdManger().getBuyerId(buyerMap);

            jsonObject.put(ATAdConst.NETWORK_REQUEST_PARAMS_KEY.APP_ID, appid);
            jsonObject.put(ATAdConst.NETWORK_REQUEST_PARAMS_KEY.UNIT_ID, unitid);
            jsonObject.put(ATAdConst.NETWORK_REQUEST_PARAMS_KEY.BUYERUID, buyerId);
            jsonObject.put(ATAdConst.NETWORK_REQUEST_PARAMS_KEY.SDK_INFO, GDTAdSdk.getGDTAdManger().getSDKInfo(unitid));

        } catch (Throwable e) {
        }
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(buyerId);
    }

    @Override
    public JSONObject toRequestJSONObject() {
        return jsonObject;
    }

}
