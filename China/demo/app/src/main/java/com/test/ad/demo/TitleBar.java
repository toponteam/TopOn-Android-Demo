package com.test.ad.demo;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBar extends RelativeLayout {

    TextView mTextView;
    TitleBarClickListener mListener;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.include_title_bar, this, true);

        mTextView = ((TextView) findViewById(R.id.tv_title));
        mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onBackClick(v);
                }
            }
        });
    }

    public void setTitle(int titleResId) {
        mTextView.setText(titleResId);
    }

    public void setListener(TitleBarClickListener listener) {
        mListener = listener;
    }


}