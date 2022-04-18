/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class NativeVideoButtonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private String TAG = NativeVideoButtonAdapter.class.getSimpleName();

    private List<String> mData;
    private OnNativeVideoButtonCallback mOnNativeVideoButtonCallback;

    public NativeVideoButtonAdapter(List<String> data, OnNativeVideoButtonCallback callback) {
        mData = data;
        mOnNativeVideoButtonCallback = callback;
    }

    public void addData(List<String> data) {
        if (data != null) {
            int oldSize = mData.size();
            int InsertSize = data.size();
            this.mData.addAll(data);

            this.notifyItemRangeChanged(oldSize, InsertSize);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return onCreateDataViewHolder(viewGroup);
    }

    private NativeVideoButtonAdapter.ButtonViewHolder onCreateDataViewHolder(@NonNull ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.native_video_button_item, viewGroup, false);
        return new NativeVideoButtonAdapter.ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        onBindDataViewHolder((NativeVideoButtonAdapter.ButtonViewHolder) viewHolder, position);
    }

    private void onBindDataViewHolder(NativeVideoButtonAdapter.ButtonViewHolder viewHolder, int position) {
        if (mData != null && mData.size() != 0) {
            viewHolder.btAction.setText(mData.get(position));
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnNativeVideoButtonCallback != null) {
                        mOnNativeVideoButtonCallback.onClick(mData.get(position));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button btAction;

        ButtonViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            btAction = mView.findViewById(R.id.bt_action);
        }
    }

    public interface OnNativeVideoButtonCallback {
        void onClick(String action);
    }

}