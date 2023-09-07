/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import com.anythink.core.api.ATInitConfig;


public class GDTATInitConfig extends ATInitConfig {

    public GDTATInitConfig(String appId) {
        paramMap.put("app_id", appId);

        initMediation = GDTATInitManager.getInstance();
    }
}
