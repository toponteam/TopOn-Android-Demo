/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;

interface GDTATNativeLoadListener {
    void notifyLoaded(CustomNativeAd... customNativeAds);

    void notifyError(String errorCode, String errorMsg);
}
