package com.ttm.tlrb.api.interceptor;


import com.ttm.tlrb.utils.HLog;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

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
        //the request body
        RequestBody requestBody = request.body();
        if(requestBody!= null) {
            StringBuilder sb = new StringBuilder("Request Body [");
            okio.Buffer buffer = new okio.Buffer();
            requestBody.writeTo(buffer);
            Charset charset = StandardCharsets.UTF_8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            if(isPlaintext(buffer)){
                sb.append(buffer.readString(charset));
                sb.append(" (Content-Type = ").append(contentType.toString()).append(",").append(requestBody.contentLength()).append("-byte body)");
            }else {
                sb.append(" (Content-Type = ").append(contentType.toString()).append(",binary ").append(requestBody.contentLength()).append("-byte body omitted)");
            }
            sb.append("]");
            HLog.d(TAG, String.format(Locale.getDefault(), "%s %s", method, sb.toString()));
        }
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

    static boolean isPlaintext(Buffer buffer){
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
