package com.ttm.tlrb.ui.application;

import android.app.Application;
import android.text.TextUtils;

import com.alipay.euler.andfix.patch.PatchManager;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.ttm.tlrb.BuildConfig;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.utils.EnvironmentUtil;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;

import th.ds.wa.AdManager;


/**
 * Created by Helen on 2016/4/28.
 *
 */
public class RBApplication extends Application{
    private static RBApplication instance;
    private String session = "";
    private PatchManager mPatchManager;

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
}
