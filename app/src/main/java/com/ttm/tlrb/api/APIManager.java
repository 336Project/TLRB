package com.ttm.tlrb.api;


import com.ttm.tlrb.api.interceptor.LogInterceptor;
import com.ttm.tlrb.api.interceptor.NetworkInterceptor;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.utils.EnvironmentUtil;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Helen on 2016/4/27.
 *
 */
public class APIManager {
    private Retrofit retrofit;
    private APIService apiService;
    private APIManager(){
        File cacheFile = new File(EnvironmentUtil.getCacheFile(), Constant.CACHE_HTTP);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new NetworkInterceptor())
                .addNetworkInterceptor(new LogInterceptor())
                .cache(new Cache(cacheFile, Constant.MAX_CACHE_SIZE))
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        apiService = retrofit.create(APIService.class);
    }

    public static APIManager getInstance(){
        return APIManagerInstance.INSTANCE;
    }

    private static class APIManagerInstance{
        final static APIManager INSTANCE = new APIManager();
    }

    private <T> T getService(Class<T> clazz){
        return retrofit.create(clazz);
    }

    public APIService getAPIService(){
        return apiService;
    }
}
