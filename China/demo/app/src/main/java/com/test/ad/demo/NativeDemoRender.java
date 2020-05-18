package com.test.ad.demo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.anythink.nativead.api.ATNativeAdRenderer;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2018/1/18.
 */

public class NativeDemoRender implements ATNativeAdRenderer<CustomNativeAd> {

    Context mContext;
    List<View> mClickView = new ArrayList<>();

    public NativeDemoRender(Context context) {
        mContext = context;
    }

    View mDevelopView;

    int mNetworkType;

    @Override
    public View createView(Context context, int networkType) {
        if (mDevelopView == null) {
            mDevelopView = LayoutInflater.from(context).inflate(R.layout.native_ad_item, null);
        }
        mNetworkType = networkType;
        if(mDevelopView.getParent() != null){
            ((ViewGroup)mDevelopView.getParent()).removeView(mDevelopView);
        }
        return mDevelopView;
    }

    @Override
    public void renderAdView(View view, CustomNativeAd ad) {
        TextView titleView = (TextView) view.findViewById(R.id.native_ad_title);
        TextView descView = (TextView) view.findViewById(R.id.native_ad_desc);
        TextView ctaView = (TextView) view.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = (TextView) view.findViewById(R.id.native_ad_from);
        FrameLayout contentArea = (FrameLayout) view.findViewById(R.id.native_ad_content_image_area);
        FrameLayout iconArea = (FrameLayout) view.findViewById(R.id.native_ad_image);
        final SimpleDraweeView logoView = (SimpleDraweeView) view.findViewById(R.id.native_ad_logo);

        titleView.setText("");
        descView.setText("");
        ctaView.setText("");
        adFromView.setText("");
        titleView.setText("");
        contentArea.removeAllViews();
        iconArea.removeAllViews();
        logoView.setImageDrawable(null);


        if (ad.isNativeExpress()) {//是 个性化模板
            titleView.setVisibility(View.GONE);
            descView.setVisibility(View.GONE);
            ctaView.setVisibility(View.GONE);
            logoView.setVisibility(View.GONE);
            iconArea.setVisibility(View.GONE);
        } else {
            titleView.setVisibility(View.VISIBLE);
            descView.setVisibility(View.VISIBLE);
            ctaView.setVisibility(View.VISIBLE);
            logoView.setVisibility(View.VISIBLE);
            iconArea.setVisibility(View.VISIBLE);
        }


        View mediaView = ad.getAdMediaView(contentArea, contentArea.getWidth());
        View adiconView = ad.getAdIconView();


        final SimpleDraweeView iconView = new SimpleDraweeView(mContext);
        if (adiconView == null) {
            iconArea.addView(iconView);
            iconView.setImageURI(ad.getIconImageUrl());
        } else {
            iconArea.addView(adiconView);
        }


        if (!TextUtils.isEmpty(ad.getAdChoiceIconUrl())) {
            logoView.setImageURI(ad.getAdChoiceIconUrl());
        } else {
//            logoView.setImageResource(R.drawable.ad_logo);
        }


        if (mediaView != null) {

            if(mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }

            contentArea.addView(mediaView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        } else {

            final SimpleDraweeView imageView = new SimpleDraweeView(mContext);

            imageView.setImageURI(ad.getMainImageUrl());
            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            contentArea.addView(imageView, params);
        }

        titleView.setText(ad.getTitle());
        descView.setText(ad.getDescriptionText());
        ctaView.setText(ad.getCallToActionText());
        if (!TextUtils.isEmpty(ad.getAdFrom())) {
            adFromView.setText(ad.getAdFrom() != null ? ad.getAdFrom() : "");
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }

        mClickView.clear();

        mClickView.add(ctaView);

    }

    public List<View> getClickView() {
        return mClickView;
    }
}
