/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import com.anythink.core.api.ATNetworkConfirmInfo;
import com.qq.e.comm.compliance.DownloadConfirmCallBack;

public class GDTDownloadFirmInfo extends ATNetworkConfirmInfo {
    public int scenes;
    public String appInfoUrl;
    public DownloadConfirmCallBack confirmCallBack;
}
