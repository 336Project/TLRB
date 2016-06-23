package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.baidu.mobads.AdView;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Intent intent;
        if(TextUtils.isEmpty(RBApplication.getInstance().getSession())){
            intent = new Intent(this,LoginActivity.class);
        }else {
            intent = new Intent(this,MainActivity.class);
        }
        SplashView splashView = new SplashView(this,null);
        splashView.setIntent(intent);
        splashView.setShowReciprocal(true);
        splashView.hideCloseBtn(false);
        splashView.setIsJumpTargetWhenFail(true);

        setContentView(splashView.getSplashView());
        SpotManager.getInstance(this).showSplashSpotAds(this, splashView,null);*/
        setContentView(R.layout.activity_splash);
        //清空百度广告缓存
        FileUtil.deleteDir(new File(Environment.getExternalStorageDirectory(),"bddownload"));
        SharedPreferences sp = getSharedPreferences("__sdk_remote_dl_2", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        //设置广告
        AdView.setAppSid(this,"f57536be");
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
        //SpotManager.getInstance(this).onDestroy();
        super.onDestroy();
    }
}
