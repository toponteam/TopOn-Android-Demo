package com.testjcenter.uparpu.testjcenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.uparpu.nativead.api.UpArpuNativeAdRenderer;
import com.uparpu.nativead.api.UpArpuNativeAdView;
import com.uparpu.nativead.unitgroup.api.CustomNativeAd;
import com.uparpu.network.gdt.GDTLocationKeyMaps;
/**
 * Created by Z on 2018/1/18.
 */

public class UpArpuRender implements UpArpuNativeAdRenderer<CustomNativeAd> {

    Context mContext;

    public UpArpuRender(Context context) {
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
        return mDevelopView;
    }



    @Override
    public void renderAdView(View view, CustomNativeAd ad) {
        TextView titleView = (TextView) view.findViewById(R.id.native_ad_title);
        TextView descView = (TextView) view.findViewById(R.id.native_ad_desc);
        TextView ctaView = (TextView) view.findViewById(R.id.native_ad_install_btn);

        FrameLayout contentArea = (FrameLayout) view.findViewById(R.id.native_ad_content_image_area);
        View mediaView = ad.getAdMediaView(contentArea, contentArea.getWidth());

        final SimpleDraweeView iconView = (SimpleDraweeView) view.findViewById(R.id.native_ad_image);
        iconView.setImageDrawable(null);
        iconView.setImageURI(ad.getIconImageUrl());

        final SimpleDraweeView logoView = (SimpleDraweeView) view.findViewById(R.id.native_ad_logo);
        if (!TextUtils.isEmpty(ad.getAdChoiceIconUrl())) {
            logoView.setVisibility(View.VISIBLE);
            logoView.setImageURI(ad.getAdChoiceIconUrl());
        } else {
//            logoView.setImageDrawable(null);
            logoView.setVisibility(View.GONE);
        }

        contentArea.removeAllViews();
        if (mediaView != null) {
            Log.i("test","111");
            if(mNetworkType == GDTLocationKeyMaps.getGDTType() && mediaView instanceof NativeExpressADView){
                titleView.setVisibility(View.GONE);
                descView.setVisibility(View.GONE);
                ctaView.setVisibility(View.GONE);
                logoView.setVisibility(View.GONE);
                iconView.setVisibility(View.GONE);
            }

            int height = contentArea.getWidth() == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : contentArea.getWidth() * 3 / 4;
//            CommonLogUtil.d("1111",height+":pppp:"+ViewGroup.LayoutParams.WRAP_CONTENT+"--contentArea.getWidth():"+contentArea.getWidth());
            contentArea.addView(mediaView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        } else {
            Log.i("test","222");
            int height = contentArea.getWidth() == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : contentArea.getWidth() * 3 / 4;
            final SimpleDraweeView imageView = new SimpleDraweeView(mContext);


                imageView.setImageURI(ad.getMainImageUrl());
                contentArea.addView(imageView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        }

        titleView.setText(ad.getTitle());
        descView.setText(ad.getDescriptionText());
        ctaView.setText(ad.getCallToActionText());
    }
}
