package com.ttm.tlrb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.ttm.tlrb.R;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.utils.HLog;

/**
 * Created by Helen on 2016/6/17.
 *
 */
public class SplashActivity extends BaseActivity{
    private static String TAG = "SplashActivity";
    public boolean canJump = false;
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
        }else {*/
            showAd();
        //}
        /*SpotManager.getInstance(RBApplication.getInstance()).loadSpotAds();
        SpotManager.getInstance(RBApplication.getInstance()).setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);
        SpotManager.getInstance(RBApplication.getInstance()).setAnimationType(SpotManager.ANIM_NONE);
        SpotManager.getInstance(RBApplication.getInstance()).showSpotAds(this);*/
        /*YouxiaoAd shuiguoAd = new YouxiaoAd(SplashActivity.this,adListener,"2483","3401");
        adLayout.addView(shuiguoAd.getFullScreenAd());*/
        //清空百度广告缓存
        /*FileUtil.deleteDir(new File(Environment.getExternalStorageDirectory(),"bddownload"));
        SharedPreferences sp = getSharedPreferences("__sdk_remote_dl_2", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        //设置广告
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
        },"2600711",true);*/
    }

    private void showAd(){
        RelativeLayout adLayout = (RelativeLayout) findViewById(R.id.layout_ad);
        SplashAD splashAD = new SplashAD(this, adLayout, "1105419691", "1090617356347870", new SplashADListener() {
            @Override
            public void onADDismissed() {
                jump();
            }

            @Override
            public void onNoAD(int i) {
                HLog.d(TAG, "onNoAD----" + i);
                jump();
            }

            @Override
            public void onADPresent() {

            }

            @Override
            public void onADClicked() {

            }
        },3000);
        /*SplashView splashView = SpotManager.getInstance(RBApplication.getInstance()).getSplashView(this);//new SplashView(this,null);
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
        }*/
    }

    /**监听广告过程*//*
    AdListener adListener=new AdListener(){
        @Override
        public void onFail(String msg) {
            Log.d(TAG,"广告加载失败"+msg);
        }

        @Override
        public void onExposure(String msg) {
            Log.d(TAG,"广告曝光展示");
        }

        @Override
        public void onClickAd(String msg) {
            Log.d(TAG,"广告点击");

        }

        @Override
        public void onBack(String msg) {
        }

        @Override
        public void onAdReceive(String msg) {
            Log.d(TAG,"广告加载成功");
        }
    };*/

    private void jump(){
        if(canJump) {
            Intent intent;
            if (TextUtils.isEmpty(RBApplication.getInstance().getSession())) {
                intent = new Intent(this, LoginActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }else {
            canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            jump();
        }
        canJump = true;
    }

    @Override
    protected void onDestroy() {
        //SpotManager.getInstance(RBApplication.getInstance()).onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
