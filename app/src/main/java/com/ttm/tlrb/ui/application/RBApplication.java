package com.ttm.tlrb.ui.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.alipay.euler.andfix.patch.PatchManager;
import com.baidu.mobads.AppActivity;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.qhad.ads.sdk.adcore.QhAdActivity;
import com.qhad.ads.sdk.adcore.Qhad;
import com.qq.e.ads.ADActivity;
import com.ttm.tlrb.BuildConfig;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.ui.activity.AdActivity;
import com.ttm.tlrb.utils.EnvironmentUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.youxiaoad.ssp.poster.cp.CpAdActivity;
import com.youxiaoad.ssp.poster.ui.AdDetailsActivity;

import th.ds.wa.AdManager;


/**
 * Created by Helen on 2016/4/28.
 *
 */
public class RBApplication extends Application implements Application.ActivityLifecycleCallbacks{
    private static RBApplication instance;
    private String session = "";
    private PatchManager mPatchManager;
    private Handler mHandler = new Handler();

    public String getSession() {
        if(TextUtils.isEmpty(session)){
            session = UserManager.getInstance().getSessionToken();
        }
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = EnvironmentUtil.getProcessName(this,android.os.Process.myPid());
        if(getPackageName().equals(processName)){
            init();
        }
    }

    private void init(){
        instance = this;
        EnvironmentUtil.init();
        HCrashHandler.init(this);
        initFresco();
        initAD();
        initPatchManager();
        initUmeng();
        initSocial();
        registerActivityLifecycleCallbacks(this);
    }

    private void initSocial() {
        //新浪微博
        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
        PlatformConfig.setSinaWeibo("1248844603", "05a4661638e0973fab354c4561ee693f");
        //QQ
        PlatformConfig.setQQZone("1105419691","0Ap15PWrubt2QEzu");
        //微信
        PlatformConfig.setWeixin("wxcdc197b98763f77c","d438ff02585a119e1870482b85dc8460");
    }

    private void initUmeng() {
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        //MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.enableEncrypt(true);
        MobclickAgent.setCheckDevice(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    private void initAD() {
        AdManager.getInstance(this).init("abef61a1925a5d96", "95ce78a402f6cf12", false, BuildConfig.DEBUG);
        Qhad.setLogSwitch(this,true);
    }

    private void initPatchManager() {
        mPatchManager = new PatchManager(this);
        mPatchManager.init(BuildConfig.VERSION_NAME);
        mPatchManager.loadPatch();
    }

    public PatchManager getPatchManager() {
        return mPatchManager;
    }

    private void initFresco(){
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(EnvironmentUtil.getCacheFile())
                .setBaseDirectoryName(Constant.CACHE_IMAGE)
                .setMaxCacheSize(Constant.MAX_CACHE_SIZE)
                .setMaxCacheSizeOnLowDiskSpace(Constant.LOW_CACHE_SIZE)
                .setMaxCacheSizeOnVeryLowDiskSpace(Constant.VERY_LOW_CACHE_SIZE)
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig).build();
        Fresco.initialize(this, config);
        //清理fresco缓存
        //Fresco.getImagePipeline().clearCaches();
    }

    public static RBApplication getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }
    private boolean isHandle = false;
    @Override
    public void onActivityStarted(final Activity activity) {
        if(activity instanceof ADActivity || activity instanceof QhAdActivity || activity instanceof CpAdActivity || activity instanceof AdDetailsActivity){
            isHandle = true;
            closeActivity(activity);
        }else if(activity instanceof com.baidu.mobads.AppActivity){
			isHandle = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppActivity appActivity = (AppActivity)activity;
                    long downTime = SystemClock.uptimeMillis();
                    MotionEvent downEvent = MotionEvent.obtain(downTime,downTime,MotionEvent.ACTION_DOWN,231.67822f,235.70493f,0);
                    appActivity.curWebview.dispatchTouchEvent(downEvent);
                    long upTime = SystemClock.uptimeMillis();
                    MotionEvent upEvent = MotionEvent.obtain(upTime,upTime,MotionEvent.ACTION_UP,231.67822f,235.70493f,0);
                    appActivity.curWebview.dispatchTouchEvent(upEvent);
                    closeActivity(activity);
                }
            },10*1000);

        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(activity instanceof AdActivity){
            isHandle = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof AdActivity){
            restartAdActivity();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if(activity instanceof AdActivity){
            unregisterActivityLifecycleCallbacks(this);
            mHandler.removeCallbacks(mRestartActivityRunnable);
            if(mCloseActivityRunnable != null) {
                mHandler.removeCallbacks(mCloseActivityRunnable);
            }
        }
    }

    private static final int TIME_COUNT = 5000;
    //关闭activity
    private void closeActivity(final Activity activity){
        if(mCloseActivityRunnable != null) {
            mHandler.removeCallbacks(mCloseActivityRunnable);
        }
        mCloseActivityRunnable = new CloseActivityRunnable(activity);
        mHandler.postDelayed(mCloseActivityRunnable,TIME_COUNT);
    }

    private CloseActivityRunnable mCloseActivityRunnable;
    private class CloseActivityRunnable implements Runnable{
        Activity activity;
        CloseActivityRunnable(Activity activity){
            this.activity = activity;
        }
        @Override
        public void run() {
            activity.finish();
        }
    }


    //重启AdActivity
    private void restartAdActivity(){
        mHandler.removeCallbacks(mRestartActivityRunnable);
        mHandler.postDelayed(mRestartActivityRunnable,TIME_COUNT);
    }
    private RestartActivityRunnable mRestartActivityRunnable = new RestartActivityRunnable();
    private class RestartActivityRunnable implements Runnable{
        @Override
        public void run() {
            if(!isHandle){
                Intent i = new Intent(instance, AdActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                instance.startActivity(i);
            }
        }
    }

}
