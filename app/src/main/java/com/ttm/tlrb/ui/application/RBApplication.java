package com.ttm.tlrb.ui.application;

import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.ttm.tlrb.BuildConfig;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.utils.ApkUtils;
import com.ttm.tlrb.utils.EnvironmentUtil;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.io.File;

import th.ds.wa.AdManager;


/**
 * Created by Helen on 2016/4/28.
 *
 */
public class RBApplication extends Application{
    private static RBApplication instance;
    private String session = "";

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
        initUmeng();
        registerUpgrade();
    }

    private void initSocial() {
        //新浪微博
        PlatformConfig.setSinaWeibo("1248844603", "05a4661638e0973fab354c4561ee693f","http://sns.whalecloud.com/sina2/callback");
        //QQ
        PlatformConfig.setQQZone("1105419691","0Ap15PWrubt2QEzu");
        //微信
        PlatformConfig.setWeixin("wxcdc197b98763f77c","d438ff02585a119e1870482b85dc8460");
    }

    private void initUmeng() {
        UMConfigure.setEncryptEnabled(true);
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        UMConfigure.preInit(this,null,null);
        UMConfigure.init(this,null,null,UMConfigure.DEVICE_TYPE_PHONE,"");
        initSocial();
    }

    private void initAD() {
        AdManager.getInstance(this).init("abef61a1925a5d96", "95ce78a402f6cf12", false, BuildConfig.DEBUG);
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

    private long enqueue;
    private void registerUpgrade(){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if(enqueue == id){
                    ApkUtils.installApk(getInstance(),
                            Uri.fromFile(new File(EnvironmentUtil.getDownloadFile(), Constant.DOWNLOAD_APK_NAME)));
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void startDownloadApk(String url){
        enqueue = ApkUtils.downloadApk(this,url);
    }

    public static RBApplication getInstance() {
        return instance;
    }
}
