package com.ttm.tlrb.api.interceptor;


import com.ttm.tlrb.utils.HLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

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

        //state
        HLog.d(TAG,String.format(Locale.CHINA,"Received response is %s ,message[%s],code[%d]",response.isSuccessful()?"success":"fail",response.message(),response.code()));

        //data
        ResponseBody body = response.body();

        BufferedSource source = body.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = Charset.defaultCharset();
        MediaType contentType = body.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        String bodyString = buffer.clone().readString(charset);
        HLog.d(TAG,String.format("Received response json string [%s]",bodyString));

        return response;
    }
}
