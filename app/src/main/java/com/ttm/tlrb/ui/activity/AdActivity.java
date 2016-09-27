package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.ttm.tlrb.R;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.utils.HLog;

import org.json.JSONObject;

import th.ds.wa.normal.banner.BannerManager;

/**
 * Created by Helen on 2016/9/27.
 *
 */

public class AdActivity extends TitlebarActivity{
    private static final String TAG = "AdActivity";
    private LinearLayout mLayoutAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        mLayoutAd = (LinearLayout) findViewById(R.id.layout_ad);
        initAd();
    }


    private void initAd() {
        //腾讯广告
        final BannerView adViewT = new BannerView(this, ADSize.BANNER, "1105419691", "6040413346244812");
        adViewT.setRefresh(5);
        adViewT.setADListener(new AbstractBannerADListener() {
            @Override
            public void onNoAD(int i) {
                HLog.d(TAG,"onNoAD----"+i);
                adViewT.loadAD();
            }

            @Override
            public void onADReceiv() {
                HLog.d(TAG,"onADReceive");
            }
        });
        adViewT.loadAD();
        mLayoutAd.addView(adViewT);
        //百度广告
        String adPlaceId = "2733053";
        AdView adViewB = new AdView(this,adPlaceId);
        adViewB.setListener(new AdViewListener() {
            @Override
            public void onAdReady(AdView adView) {

            }

            @Override
            public void onAdShow(JSONObject jsonObject) {
                HLog.d(TAG,"onAdShow");
            }

            @Override
            public void onAdClick(JSONObject jsonObject) {

            }

            @Override
            public void onAdFailed(String s) {
                HLog.d(TAG,"onFailedToReceivedAd error = "+s);
            }

            @Override
            public void onAdSwitch() {
                HLog.d(TAG,"onSwitchedAd");
            }

            @Override
            public void onAdClose(JSONObject jsonObject) {

            }
        });
        mLayoutAd.addView(adViewB);
        //360

        //有米
        View adViewY = BannerManager.getInstance(RBApplication.getInstance()).getBanner(this);
        if(adViewY != null) {
            BannerManager.getInstance(RBApplication.getInstance()).setAdListener(new th.ds.wa.normal.banner.AdViewListener() {
                @Override
                public void onReceivedAd() {
                    HLog.d(TAG,"onReceivedAd");
                }

                @Override
                public void onSwitchedAd() {
                    HLog.d(TAG,"onSwitchedAd");
                }

                @Override
                public void onFailedToReceivedAd() {
                    HLog.d(TAG,"onFailedToReceivedAd");
                }
            });
            mLayoutAd.addView(adViewY);
        }
    }
}
