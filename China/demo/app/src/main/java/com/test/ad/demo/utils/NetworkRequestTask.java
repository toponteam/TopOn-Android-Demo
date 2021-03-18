/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Network Request Util
 */
public class NetworkRequestTask extends AsyncTask<String, Void, String> {
    public static final String TAG = "NetworkRequestTask";

    @Override
    protected String doInBackground(String... params) {
        String stringUrl = params[0];

        if (TextUtils.isEmpty(stringUrl)) {
            return null;
        }

        String result;
        String line;
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.connect();
            InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);

            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            streamReader.close();
            result = sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }
}
