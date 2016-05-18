package com.ttm.tlrb.ui.application;

import com.facebook.common.util.ByteConstants;

import okhttp3.MediaType;

/**
 * Created by Helen on 2016/5/5.
 *
 */
public class Constant {
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
}
