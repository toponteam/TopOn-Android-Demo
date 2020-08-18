package com.test.ad.demo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anythink.nativead.api.ATNativeAdRenderer;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.NativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;

import java.util.List;

public class NativeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = NativeListAdapter.class.getSimpleName();

    /**
     * data
     */
    private static final int TYPE_DATA = 0;
    /**
     * ad
     */
    private static final int TYPE_AD = 1;
    /**
     * load more data
     */
    private static final int TYPE_MORE = 2;

    private int mShowAdInterval = 10;

    private int mAdWidth;
    private int mAdHeight;
    private List<String> mData;

    private OnNativeListCallback mOnNativeListCallback;

    private NativeListHelper mNativeListHelper;

    public NativeListAdapter(int width, int height, List<String> data, OnNativeListCallback onNativeListCallback) {

        this.mAdWidth = width;
        this.mAdHeight = height;
        this.mData = data;
        this.mOnNativeListCallback = onNativeListCallback;
        this.mNativeListHelper = new NativeListHelper(mShowAdInterval);
    }

    public int getIntervalAd() {
        return this.mShowAdInterval;
    }

    public void addData(List<String> data) {
        if (data != null) {
            int oldSize = mData.size();
            int InsertSize = data.size();
            this.mData.addAll(data);

            this.notifyItemRangeChanged(oldSize, InsertSize);
        }
    }

    public void addCache(NativeAd nativeAd) {
        if (this.mNativeListHelper != null) {
            this.mNativeListHelper.addLoadedCache(nativeAd);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemViewType) {

        switch (itemViewType) {
            case TYPE_AD:
                return onCreateAdViewHolder(viewGroup);

            case TYPE_MORE:
                return onCreateMoreViewHolder(viewGroup);

            case TYPE_DATA:
            default:
                return onCreateDataViewHolder(viewGroup);
        }
    }

    private AdViewHolder onCreateAdViewHolder(@NonNull ViewGroup viewGroup) {
        Log.i(TAG, "onCreateAdViewHolder: 创建adView");
        ATNativeAdView atNativeAdView = new ATNativeAdView(viewGroup.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mAdWidth, mAdHeight);
        atNativeAdView.setLayoutParams(params);
        return new AdViewHolder(atNativeAdView);
    }

    private DataViewHolder onCreateDataViewHolder(@NonNull ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.native_list_data_item, viewGroup, false);
        return new DataViewHolder(view);
    }

    private MoreViewHolder onCreateMoreViewHolder(@NonNull ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.native_list_more_item, viewGroup, false);
        return new MoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);

        switch (itemViewType) {
            case TYPE_AD:
                if (viewHolder instanceof AdViewHolder) {
                    Log.i(TAG, "onBindViewHolder:" + viewHolder.toString());
                    onBindAdViewHolder((AdViewHolder) viewHolder, position);
                }
                break;
            case TYPE_MORE:
                if (viewHolder instanceof MoreViewHolder) {
                    onBindMoreViewHoler((MoreViewHolder) viewHolder);
                }
                break;

            case TYPE_DATA:
            default:
                if (viewHolder instanceof DataViewHolder) {
                    onBindDataViewHolder((DataViewHolder) viewHolder, position);
                }
                break;
        }
    }

    /**
     * bind ad view
     */
    private void onBindAdViewHolder(AdViewHolder viewHolder, int position) {

        if (mNativeListHelper != null) {

            NativeAd nativeAd = mNativeListHelper.getNativeAd(position);

            if (nativeAd == null) {
                nativeAd = mNativeListHelper.popNativeAdCache();
            }
            Log.i(TAG, "onBindAdViewHolder: nativeAd: " + nativeAd + ",   " + position);

            if (nativeAd != null && mOnNativeListCallback != null) {
                mOnNativeListCallback.onBindAdView(nativeAd, viewHolder.mATNativeAdView, viewHolder.nativeDemoRender);
                mNativeListHelper.putNativeAd(position, nativeAd);
            }
        }
    }

    /**
     * bind mock data
     */
    private void onBindDataViewHolder(DataViewHolder viewHolder, int position) {
        viewHolder.mTvData.setText(mData.get(position));

//        if (mNativeListHelper != null) {
//            mNativeListHelper.setCanShowAd(position);
//        }
    }

    private void onBindMoreViewHoler(MoreViewHolder viewHolder) {
        viewHolder.mTvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnNativeListCallback != null) {
                    mOnNativeListCallback.onClickLoadMore();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == mData.size()) {
            return TYPE_MORE;
        }

        if (mNativeListHelper != null && mNativeListHelper.canShowAd(position)) {
            if (mNativeListHelper.hasBindAdCacheBy(position)) {
                Log.i(TAG, "getItemViewType: adcache " + position);
                return TYPE_AD;
            }

            if (mNativeListHelper.isAd(position) && mNativeListHelper.hasCache()) {
                Log.i(TAG, "getItemViewType: cache  ad, no render " + position);
                return TYPE_AD;
            }
        }
//        Log.i(TAG, "getItemViewType: data " + position);
        return TYPE_DATA;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView mTvData;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            mTvData = mView.findViewById(R.id.tv_data);
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {

        ATNativeAdView mATNativeAdView;
        ViewGroup mAdContainer;
        NativeDemoRender nativeDemoRender;

        AdViewHolder(@NonNull View itemView) {
            super(itemView);

            mATNativeAdView = (ATNativeAdView) itemView;
            mAdContainer = mATNativeAdView.findViewById(R.id.ad_container);
            nativeDemoRender = new NativeDemoRender(itemView.getContext());
        }
    }

    public static class MoreViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView mTvMore;

        MoreViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            mTvMore = itemView.findViewById(R.id.tv_more);
        }
    }

    public void removeAdView(int position) {
        if (mNativeListHelper != null && position != -1) {
            mNativeListHelper.removeAdView(position);
//            notifyItemRemoved(position);
        }
    }

    public void onResume(int first, int last) {
        if (mNativeListHelper != null) {
            mNativeListHelper.onResume(first, last);
        }
    }

    public void onPause(int first, int last) {
        if (mNativeListHelper != null) {
            mNativeListHelper.onPause(first, last);
        }
    }

    public void onDestroy() {
        if (mNativeListHelper != null) {
            mNativeListHelper.onDestroy();
            mNativeListHelper = null;
        }

        if (mData != null) {
            mData.clear();
            mData = null;
        }

        mOnNativeListCallback = null;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        Log.i(TAG, "View recycled:" + holder.getAdapterPosition() + "---holder:" + holder.toString());
        super.onViewRecycled(holder);
        if (mNativeListHelper != null && holder instanceof AdViewHolder) {
            mNativeListHelper.clearView(holder.getAdapterPosition(), ((AdViewHolder) holder).mATNativeAdView);
        }
    }

    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        Log.i(TAG, "View onViewAttachedToWindow:" + holder.getAdapterPosition() + "---holder:" + holder.toString());
    }

    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        Log.i(TAG, "View onViewDetachedFromWindow:" + holder.getAdapterPosition() + "---holder:" + holder.toString());
    }


    public interface OnNativeListCallback {
        ATNativeAdView onBindAdView(NativeAd nativeAd, ATNativeAdView atNativeAdView, ATNativeAdRenderer<? extends CustomNativeAd> atNativeAdRenderer);

        void onClickLoadMore();
    }
}
