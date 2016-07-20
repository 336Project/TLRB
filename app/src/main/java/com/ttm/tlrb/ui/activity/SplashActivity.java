package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.baidu.mobads.AppActivity;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.ttm.tlrb.R;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.utils.FileUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

/**
 * Created by Helen on 2016/6/17.
 *
 */
public class SplashActivity extends BaseActivity{
    private static String TAG = "SplashActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //移除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        /*boolean isAdOpen = SPUtil.getInstance().getBoolean("isAdOpen",false) || BuildConfig.DEBUG;
        if(!isAdOpen) {
            AdManager.getInstance(RBApplication.getInstance()).asyncGetOnlineConfig("isOpen", new OnlineConfigCallBack() {
                @Override
                public void onGetOnlineConfigSuccessful(String key, String value) {
                    HLog.d(TAG," key = "+key+" value = "+value);
                    if ("isOpen".equals(key) && "true".equals(value)) {
                        SPUtil.getInstance().putBoolean("isAdOpen", true).commit();
                        showAd();
                    } else {
                        jump();
                    }
                }

                @Override
                public void onGetOnlineConfigFailed(String s) {
                    HLog.d(TAG,s);
                    jump();
                }
            });
        }else {
            showAd();
        }*/
        //清空百度广告缓存
        FileUtil.deleteDir(new File(Environment.getExternalStorageDirectory(),"bddownload"));
        SharedPreferences sp = getSharedPreferences("__sdk_remote_dl_2", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        //设置广告
        AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_BLUE_THEME);
        RelativeLayout adLayout = (RelativeLayout) findViewById(R.id.layout_ad);
        new SplashAd(this, adLayout, new SplashAdListener() {
            @Override
            public void onAdPresent() {

            }

            @Override
            public void onAdDismissed() {
                jump();
            }

            @Override
            public void onAdFailed(String s) {
                jump();
            }

            @Override
            public void onAdClick() {
                MobclickAgent.onEvent(SplashActivity.this, Constant.Event.EVENT_ID_SPLASH_AD_CLICK);
            }
        },"2600711",true);
    }

    /*private void showAd(){
        RelativeLayout adLayout = (RelativeLayout) findViewById(R.id.layout_ad);
        SplashView splashView = SpotManager.getInstance(RBApplication.getInstance()).getSplashView(this);//new SplashView(this,null);
        if(splashView != null) {
            Intent intent;
            if(TextUtils.isEmpty(RBApplication.getInstance().getSession())){
                intent = new Intent(this,LoginActivity.class);
            }else {
                intent = new Intent(this,MainActivity.class);
            }
            splashView.setIntent(intent);
            splashView.setShowReciprocal(true);
            splashView.hideCloseBtn(false);
            splashView.setIsJumpTargetWhenFail(true);
            adLayout.addView(splashView.getSplashView());
            SpotManager.getInstance(RBApplication.getInstance()).showSplashSpotAds(this, splashView, new SpotDialogListener() {
                @Override
                public void onShowSuccess() {
                    HLog.d(TAG, "onShowSuccess");
                }

                @Override
                public void onShowFailed() {
                    HLog.d(TAG, "onShowFailed");
                }

                @Override
                public void onSpotClosed() {
                    HLog.d(TAG, "onSpotClosed");
                    finish();
                }

                @Override
                public void onSpotClick(boolean b) {
                    HLog.d(TAG, "onSpotClick " + b);
                    MobclickAgent.onEvent(SplashActivity.this, Constant.Event.EVENT_ID_SPLASH_AD_CLICK);
                }
            });
        }else {
            jump();
        }
    }*/


    private void jump(){
        Intent intent;
        if(TextUtils.isEmpty(RBApplication.getInstance().getSession())){
            intent = new Intent(this,LoginActivity.class);
        }else {
            intent = new Intent(this,MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        //SpotManager.getInstance(RBApplication.getInstance()).onDestroy();
        super.onDestroy();
    }
}
