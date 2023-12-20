package com.test.ad.demo.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.anythink.nativead.api.ATNativeImageView;
import com.test.ad.demo.SelfRenderViewUtil;

import java.util.List;

public class MutiImageView extends LinearLayout {
    int padding;

    public MutiImageView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        padding = SelfRenderViewUtil.dip2px(context, 5);
//        setPadding(padding, padding, padding, padding);
    }

    public void setImageList(List<String> imageList, int imageWidth, int imageHeight) {
        removeAllViews();
        int size = imageList.size();
        for (String url : imageList) {
            int width = getResources().getDisplayMetrics().widthPixels;

            ATNativeImageView atNativeImageView = new ATNativeImageView(getContext());
            atNativeImageView.setImage(url);
            atNativeImageView.setPadding(padding, padding, padding, padding);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width * 600 / size / 1024);
            if (imageWidth > 0 && imageHeight > 0) {
                layoutParams.height = width * imageHeight / size / imageWidth;
            }
            layoutParams.weight = 1;

            addView(atNativeImageView, layoutParams);
        }
    }
}
