/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlacementIdUtil {

    public static final String TAG = PlacementIdUtil.class.getSimpleName();

    private static final String placementIdJson = "placementId.json";
    private static Map<String, String> rewardedVideoPlacements;
    private static Map<String, String> interstitialPlacements;
    private static Map<String, String> fullScreenPlacements;
    private static Map<String, String> nativePlacements;
    private static Map<String, String> drawPlacements;
    private static Map<String, String> patchPlacements;
    private static Map<String, String> bannerPlacements;
    private static Map<String, String> splashPlacements;

    private static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder result = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                result.append(line);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, String> getPlacementIdMap(Context context, String placmentIdJsonFileName, String format) {
        String jsonString = getFromAssets(context, placmentIdJsonFileName);

        Map<String, String> result = new LinkedHashMap<>();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            JSONObject placementJsonObject = jsonObject.optJSONObject(format);
            Iterator<String> placements = placementJsonObject.keys();

            String placementName;
            String placementId;
            while (placements.hasNext()) {
                placementName = placements.next();
                placementId = (String) placementJsonObject.get(placementName);

                result.put(placementName, placementId);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "getPlacementIdMap: parse json error -> " + format);
        }
        return result;
    }

    public static Map<String, String> getRewardedVideoPlacements(Context context) {
        if (rewardedVideoPlacements == null) {
            rewardedVideoPlacements = getPlacementIdMap(context, placementIdJson, "rewarded_video");
        }
        return rewardedVideoPlacements;
    }

    public static Map<String, String> getInterstitialPlacements(Context context) {
        if (interstitialPlacements == null) {
            interstitialPlacements = getPlacementIdMap(context, placementIdJson, "interstitial");
        }
        return interstitialPlacements;
    }

    public static Map<String, String> getFullScreenPlacements(Context context) {
        if (fullScreenPlacements == null) {
            fullScreenPlacements = getPlacementIdMap(context, placementIdJson, "fullscreen");
        }
        return fullScreenPlacements;
    }

    public static Map<String, String> getBannerPlacements(Context context) {
        if (bannerPlacements == null) {
            bannerPlacements = getPlacementIdMap(context, placementIdJson, "banner");
        }
        return bannerPlacements;
    }

    public static Map<String, String> getNativePlacements(Context context) {
        if (nativePlacements == null) {
            nativePlacements = getPlacementIdMap(context, placementIdJson, "native");
        }
        return nativePlacements;
    }

    public static Map<String, String> getDrawPlacements(Context context) {
        if (drawPlacements == null) {
            drawPlacements = getPlacementIdMap(context, placementIdJson, "draw");
        }
        return drawPlacements;
    }

    public static Map<String, String> getPatchPlacements(Context context) {
        if (patchPlacements == null) {
            patchPlacements = getPlacementIdMap(context, placementIdJson, "patch");
        }
        return patchPlacements;
    }

    public static Map<String, String> getSplashPlacements(Context context) {
        if (splashPlacements == null) {
            splashPlacements = getPlacementIdMap(context, placementIdJson, "splash");
        }
        return splashPlacements;
    }

}