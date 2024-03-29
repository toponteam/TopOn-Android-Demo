package com.test.ad.demo;

import com.anythink.nativead.api.ATNativeView;
import com.anythink.nativead.api.NativeAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NativeListHelper {

    private int mShowAdInterval;

    /**
     * All native ad cache
     */
    private List<NativeAd> mNativeAdCacheList;
    /**
     * The native ad cache already displayed
     */
    private List<NativeAd> mHasShowNativeAdList;
    /**
     * All indexes that cannot display ads
     */
    private Set<Integer> mDoNotShowAdPositionSet;
    /**
     * Index mapping to native ad
     */
    private Map<Integer, NativeAd> mPosition2NativeAdMap;

    public NativeListHelper(int showAdInterval) {
        mShowAdInterval = showAdInterval;
        mNativeAdCacheList = new ArrayList<>();
        mHasShowNativeAdList = new ArrayList<>();
        mPosition2NativeAdMap = new HashMap<>();
    }

    public void addLoadedCache(NativeAd nativeAd) {
        this.mNativeAdCacheList.add(nativeAd);
    }

    public NativeAd getNativeAd(int position) {
        return mPosition2NativeAdMap.get(position);
    }

    public void putNativeAd(int position, NativeAd nativeAd) {
        mPosition2NativeAdMap.put(position, nativeAd);
    }


    public NativeAd popNativeAdCache() {
        return mNativeAdCacheList.remove(0);
    }


    public boolean hasBindAdCacheBy(int position) {
        return mPosition2NativeAdMap.containsKey(position) && mPosition2NativeAdMap.get(position) != null;
    }

    public boolean hasCache() {
        return mNativeAdCacheList != null && mNativeAdCacheList.size() > 0;
    }

    public void removeAdView(int removePosition) {
        if (mPosition2NativeAdMap.containsKey(removePosition)) {

            NativeAd nativeAd = mPosition2NativeAdMap.get(removePosition);
            if (nativeAd != null) {
                nativeAd.destory();
            }
            mPosition2NativeAdMap.remove(removePosition);

            if (mDoNotShowAdPositionSet == null) {
                mDoNotShowAdPositionSet = new HashSet<>();
            }
            mDoNotShowAdPositionSet.add(removePosition);
        }
    }

    public boolean canShowAd(int position) {
        return mDoNotShowAdPositionSet == null || !mDoNotShowAdPositionSet.contains(position);
    }

    /**
     * check this postion if need to show ad
     */
    public boolean isAd(int i) {
        boolean result = i % mShowAdInterval == 0;
        return result;
    }

    public int getImpressionAd(int first, int last) {
        for (int i = first; i < last; i++) {
            if (isAd(i)) {
                return i;
            }
        }
        return -1;
    }


    public void onResume(int first, int last) {
        int position = getImpressionAd(first, last);
        if (position != -1) {
            if (mPosition2NativeAdMap.containsKey(position)) {
                NativeAd nativeAd = mPosition2NativeAdMap.get(position);

                if (nativeAd != null) {
                    nativeAd.onResume();
                }
            }
        }
    }

    public void onPause(int first, int last) {
        int position = getImpressionAd(first, last);
        if (position != -1) {
            if (mPosition2NativeAdMap.containsKey(position)) {
                NativeAd nativeAd = mPosition2NativeAdMap.get(position);

                if (nativeAd != null) {
                    nativeAd.onPause();
                }
            }
        }
    }

    public void clearView(int position, ATNativeView view) {

        if (mPosition2NativeAdMap.containsKey(position)) {
            NativeAd nativeAd = mPosition2NativeAdMap.get(position);

            if (nativeAd != null) {
                nativeAd.clear(view);
            }
        }
    }

    public void onDestroy() {
        if (mNativeAdCacheList != null) {
            mNativeAdCacheList.clear();
            mNativeAdCacheList = null;
        }

        if (mPosition2NativeAdMap != null) {
            mPosition2NativeAdMap.clear();
            mPosition2NativeAdMap = null;
        }

        if (mHasShowNativeAdList != null) {
            int size = mHasShowNativeAdList.size();
            for (int i = 0; i < size; i++) {
                NativeAd nativeAd = mHasShowNativeAdList.get(i);
                if (nativeAd != null) {
                    nativeAd.destory();
                }
            }
            mHasShowNativeAdList.clear();
            mHasShowNativeAdList = null;
        }

        if (mDoNotShowAdPositionSet != null) {
            mDoNotShowAdPositionSet.clear();
            mDoNotShowAdPositionSet = null;
        }
    }

}
