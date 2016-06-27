package com.ttm.tlrb.api.interceptor;


import android.text.TextUtils;

import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.utils.EnvironmentUtil;
import com.ttm.tlrb.utils.HLog;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Helen on 2016/4/28.
 *
 */
public class NetworkInterceptor implements Interceptor {
    public static final String TAG = "NetworkInterceptor.java";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader("X-Bmob-Application-Id","c1aa552ec6d7639a92f11a362ff22b34");
        builder.addHeader("X-Bmob-REST-API-Key","a93c8e5501c9f2ed942ae1cff46ccd77");
        String session = RBApplication.getInstance().getSession();
        if(!TextUtils.isEmpty(session)) {
            builder.addHeader("X-Bmob-Session-Token", session);
        }
        boolean isNetworkConnected = EnvironmentUtil.isNetworkConnected();
        if(!isNetworkConnected){
            builder.cacheControl(CacheControl.FORCE_CACHE);
        }
        Request newRequest = builder.build();
        Response response = chain.proceed(newRequest);
        if(isNetworkConnected){
            HLog.d(TAG,"response from network");
            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
            String cacheControl = newRequest.cacheControl().toString();
            return response.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        }else {
            HLog.d(TAG,"response from cache");
            return response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                    .removeHeader("Pragma")
                    .build();
        }
    }

}
