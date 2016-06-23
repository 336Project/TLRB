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
import com.umeng.analytics.MobclickAgent;


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

    private void initUmeng() {
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        //MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.enableEncrypt(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    private void initAD() {
        //AdManager.getInstance(this).init("abef61a1925a5d96","95ce78a402f6cf12",true);
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
