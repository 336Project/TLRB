package com.ttm.tlrb.ui.application;

import com.facebook.common.util.ByteConstants;

import okhttp3.MediaType;

/**
 * Created by Helen on 2016/5/5.
 *
 */
public class Constant {
    /**修复包*/
    public static final String CACHE_PATCH = "a_patch";
    /**网络缓存文件夹*/
    public static final String CACHE_HTTP = "network_cache";
    /**图片缓存文件夹*/
    public static final String CACHE_IMAGE = "image_cache";
    /**图片保存文件夹-系统文件夹Pictures下*/
    public static final String IMAGE_SAVE_PATH = "tlrb";
    /**崩溃日志文件夹*/
    public static final String CACHE_LOG = "crash";
    /**崩溃文件名称*/
    public static final String LOG_FILE_NAME = "AppCrash.log";
    /**apk文件*/
    public static final String DOWNLOAD_APK_NAME = "tlrb.apk";
    /**日志文件最大大小*/
    public static final int LOG_MAX_SIZE = ByteConstants.MB;//1M

    public static final long MAX_CACHE_SIZE = 100 * ByteConstants.MB;
    public static final long LOW_CACHE_SIZE = 50 * ByteConstants.MB;
    public static final long VERY_LOW_CACHE_SIZE = 10 * ByteConstants.MB;
    /**RequestBody 文本格式*/
    public static final MediaType TEXT = MediaType.parse("text/plain");
    /**RequestBody Json格式*/
    public static final MediaType JSON = MediaType.parse("application/json");
    /**页数*/
    public static final int PAGE_SIZE = 10;

    /**友盟事件ID*/
    public class Event{
        /**账号登录*/
        public static final String EVENT_ID_LOGIN_NORMAL = "10000_1";
        /**微博登录*/
        public static final String EVENT_ID_LOGIN_WB = "10000_2";
        /**qq登录*/
        public static final String EVENT_ID_LOGIN_QQ = "10000_3";
        /**微信登录*/
        public static final String EVENT_ID_LOGIN_WX = "10000_4";
        /**退出*/
        public static final String EVENT_ID_LOGOUT = "10001";
        /**注册*/
        public static final String EVENT_ID_REGISTER = "10002";
        /**检测更新*/
        public static final String EVENT_ID_UPDATE_CHECK = "20000";
        /**分享软件*/
        public static final String EVENT_ID_SOFT_SHARE = "20001";
        /**意见反馈*/
        public static final String EVENT_ID_FEEDBACK = "30000";
        /**组别添加*/
        public static final String EVENT_ID_GROUP_ADD= "40000";
        /**组别修改*/
        public static final String EVENT_ID_GROUP_UPDATE= "40001";
        /**组别删除*/
        public static final String EVENT_ID_GROUP_DELETE= "40002";
        /**红包添加*/
        public static final String EVENT_ID_BOMB_ADD= "50000";
        /**红包修改*/
        public static final String EVENT_ID_BOMB_UPDATE= "50001";
        /**红包删除*/
        public static final String EVENT_ID_BOMB_DELETE= "50002";
        /**红包查看*/
        public static final String EVENT_ID_BOMB_LOOK= "50003";
        /**开屏广告点击*/
        public static final String EVENT_ID_SPLASH_AD_CLICK= "60000";
    }
}
