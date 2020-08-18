package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.anythink.banner.api.ATBannerListener;
import com.anythink.banner.api.ATBannerView;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;

import java.util.HashMap;

public class BannerAdActivity extends Activity {


    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_banner_all
            , DemoApplicaion.mPlacementId_banner_GDT
            , DemoApplicaion.mPlacementId_banner_toutiao
            , DemoApplicaion.mPlacementId_banner_uniplay
            , DemoApplicaion.mPlacementId_banner_mintegral
            , DemoApplicaion.mPLacementId_banner_baidu
    };

    String unitGroupName[] = new String[]{
            "All",
            "GDT",
            "Toutiao",
            "Uniplay",
            "Mintegral",
            "Baidu",
    };

    ATBannerView mBannerView;

    int mCurrentSelectIndex;
    boolean hasAddBannerView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_banner);

        Spinner spinner = (Spinner) findViewById(R.id.banner_spinner);
        final FrameLayout frameLayout = findViewById(R.id.adview_container);
        mBannerView = new ATBannerView(this);
        frameLayout.addView(mBannerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip2px(300)));
        mBannerView.setBannerAdListener(new ATBannerListener() {
            @Override
            public void onBannerLoaded() {
                Log.i("BannerAdActivity", "onBannerLoaded");
                Toast.makeText(BannerAdActivity.this,
                        "onBannerLoaded",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerFailed(AdError adError) {
                Log.i("BannerAdActivity", "onBannerFailed：" + adError.printStackTrace());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerFailed: " + adError.printStackTrace(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerClicked(ATAdInfo entity) {
                Log.i("BannerAdActivity", "onBannerClicked:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerClicked",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerShow(ATAdInfo entity) {
                Log.i("BannerAdActivity", "onBannerShow:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerShow",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerClose(ATAdInfo entity) {
                Log.i("BannerAdActivity", "onBannerClose:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerClose",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerAutoRefreshed(ATAdInfo entity) {
                Log.i("BannerAdActivity", "onBannerAutoRefreshed:" + entity.toString());
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
                Log.i("BannerAdActivity", "onBannerAutoRefreshFail");

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                BannerAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                unitGroupName);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(BannerAdActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentSelectIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> maps = new HashMap<>();
                mBannerView.setUnitId(unitIds[mCurrentSelectIndex]);
                mBannerView.loadAd();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBannerView != null) {
            mBannerView.destroy();
        }
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
