package com.ttm.tlrb.api.interceptor;


import com.ttm.tlrb.utils.HLog;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Helen on 2016/4/28.
 *
 */
public class LogInterceptor implements Interceptor{
    public static final String TAG = "LogInterceptor.java";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        String method = request.method();
        long t1 = System.nanoTime();
        HLog.d(TAG,String.format(Locale.getDefault(),"Sending %s request [url = %s]",method,url));

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        HLog.d(TAG,String.format(Locale.getDefault(),"Received response for [url = %s] in %.1fms",url, (t2-t1)/1e6d));
        return response;
    }
}
