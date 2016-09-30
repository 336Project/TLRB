package com.ttm.tlrb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.qhad.ads.sdk.adcore.Qhad;
import com.qhad.ads.sdk.interfaces.IQhBannerAd;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.ttm.tlrb.R;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.utils.HLog;
import com.youxiaoad.ssp.core.YouxiaoAd;
import com.youxiaoad.ssp.listener.AdListener;

import org.json.JSONObject;

import java.util.Random;

import th.ds.wa.normal.banner.BannerManager;

/**
 * Created by Helen on 2016/9/27.
 *
 */

public class AdActivity extends TitlebarActivity implements View.OnClickListener{
    private static final String TAG = "AdActivity";
    private LinearLayout mLayoutAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        mLayoutAd = (LinearLayout) findViewById(R.id.layout_ad);
        mLayoutAd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HLog.d(TAG,"x = "+event.getX()+",y = "+event.getY());
                //min(0,0) max(704,440)
                return false;
            }
        });
        findViewById(R.id.btn_refresh).setOnClickListener(this);
        findViewById(R.id.btn_refresh_run).setOnClickListener(this);
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
        IQhBannerAd bannerAd = Qhad.showBanner(mLayoutAd,this,"P5uQQp26FP",false);
        if(bannerAd != null) {
            bannerAd.showAds(this);
        }
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
        //优效
        YouxiaoAd youxiaoAd = new YouxiaoAd(this, "2483", "3815", new AdListener() {
            @Override
            public void onFail(String s) {
                HLog.d(TAG,"onFail "+s);
            }

            @Override
            public void onBack(String s) {
                HLog.d(TAG,"onBack "+s);
            }

            @Override
            public void onAdReceive(String s) {
                HLog.d(TAG,"onAdReceive "+s);
            }

            @Override
            public void onExposure(String s) {
                HLog.d(TAG,"onExposure "+s);
            }

            @Override
            public void onClickAd(String s) {
                HLog.d(TAG,"onClickAd "+s);
            }
        });
        View adViewYX = youxiaoAd.showBannerAD(false,false);
        if(adViewYX != null) {
            mLayoutAd.addView(adViewYX);
        }
    }

    private boolean isAutoRefresh = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refresh:
                isAutoRefresh = false;
                refreshAd();
                break;
            case R.id.btn_refresh_run:
                isAutoRefresh = true;
                startRefreshRunnable();
                break;
        }
    }

    private Handler mHandler = new Handler();
    private void refreshAd(){
        mLayoutAd.removeAllViews();
        initAd();
        startEventRunnable();
    }

    private static final int TIME_COUNT_EVENT = 3000;
    private void startEventRunnable(){
        stopEventRunnable();
        mHandler.postDelayed(mEventRunnable,TIME_COUNT_EVENT);
    }
    private void stopEventRunnable(){
        mHandler.removeCallbacks(mEventRunnable);
    }
    private EventRunnable mEventRunnable = new EventRunnable();
    private class EventRunnable implements Runnable{
        @Override
        public void run() {
            if(!isDestroyed()) {
                dispatchEvent();
                mHandler.postDelayed(this, TIME_COUNT_EVENT);
            }
        }
    }

    private void dispatchEvent(){
        int x,y,i;
        Random random = new Random();
        i = random.nextInt(100)+1;
        if(i<=50){
            return;
        }
        x = random.nextInt(mLayoutAd.getWidth());
        y = random.nextInt(mLayoutAd.getHeight());

        long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = MotionEvent.obtain(downTime,downTime, MotionEvent.ACTION_DOWN,x,y,0);
        mLayoutAd.dispatchTouchEvent(downEvent);
        long upTime = SystemClock.uptimeMillis();
        MotionEvent upEvent = MotionEvent.obtain(upTime,upTime, MotionEvent.ACTION_UP,x,y,0);
        mLayoutAd.dispatchTouchEvent(upEvent);
    }

    private RefreshRunnable mRefreshRunnable = new RefreshRunnable();
    private class RefreshRunnable implements Runnable{
        @Override
        public void run() {
            if(!isDestroyed()) {
                refreshAd();
                mHandler.postDelayed(this,TIME_COUNT_REFRESH);
            }
        }
    }

    private static final int TIME_COUNT_REFRESH = 5*1000;
    private void startRefreshRunnable(){
        stopRefreshRunnable();
        mHandler.postDelayed(mRefreshRunnable,TIME_COUNT_REFRESH);
    }

    private void stopRefreshRunnable(){
        mHandler.removeCallbacks(mRefreshRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopEventRunnable();
        stopRefreshRunnable();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isAutoRefresh){
            startRefreshRunnable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRefreshRunnable();
        stopEventRunnable();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        HLog.d(TAG,"onNewIntent");
    }
}
