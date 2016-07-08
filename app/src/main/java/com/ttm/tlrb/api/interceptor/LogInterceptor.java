package com.ttm.tlrb.api.interceptor;


import com.ttm.tlrb.utils.HLog;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Helen on 2016/4/28.
 *
 */
public class LogInterceptor implements Interceptor{
    public static final String TAG = "LogInterceptor.java";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //the request url
        String url = request.url().toString();
        //the request method
        String method = request.method();

        long t1 = System.nanoTime();
        HLog.d(TAG,String.format(Locale.getDefault(),"Sending %s request [url = %s]",method,url));
        /*//the request body
        RequestBody requestBody = request.body();
        if(requestBody!= null) {
            StringBuilder sb = new StringBuilder("Request Body [");
            if(requestBody instanceof FormBody){
                FormBody fb = (FormBody)requestBody;
                for (int i=0;i<fb.size();i++){
                    sb.append(fb.name(i)).append("=").append(fb.value(i)).append(",");
                }
            }else if(requestBody instanceof MultipartBody){
                MultipartBody mb = (MultipartBody) requestBody;
                sb.append(mb.boundary());
            }

            sb.append("]");
            HLog.d(TAG, String.format(Locale.getDefault(), "%s %s", method, sb.toString()));
        }*/
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        //the response time
        HLog.d(TAG,String.format(Locale.getDefault(),"Received response for [url = %s] in %.1fms",url, (t2-t1)/1e6d));

        //the response state
        HLog.d(TAG,String.format(Locale.CHINA,"Received response is %s ,message[%s],code[%d]",response.isSuccessful()?"success":"fail",response.message(),response.code()));

        //the response data
        String bodyString;
        //1.
        ResponseBody originalBody = response.body();
        ResponseBody body = response.peekBody(originalBody.contentLength());
        bodyString = body.string();
        //2.
        /*BufferedSource source = body.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = Charset.defaultCharset();
        MediaType contentType = body.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        bodyString = buffer.clone().readString(charset);*/
        HLog.d(TAG,String.format("Received response json string [%s]",bodyString));

        return response;
    }
}
