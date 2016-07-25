package com.ttm.tlrb.api.e;

import android.content.Context;

import com.ttm.tlrb.utils.GsonUtil;
import com.ttm.tlrb.utils.ToastUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Helen on 2016/6/17.
 *
 */
public class HttpExceptionHandle {
    private HttpException mHttpException;
    private ErrorResponse mErrorResponse;
    private Context mContext;
    public HttpExceptionHandle (HttpException e, Context context){
        this.mHttpException = e;
        this.mContext = context;
        process();
    }

    private void process(){
        try {
            retrofit2.Response<?> response = mHttpException.response();
            ResponseBody errorBody = response.errorBody();
            String e = errorBody.string();
            mErrorResponse = GsonUtil.fromJson(e,ErrorResponse.class);
            errorBody.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void handle(){
        switch (getErrorCode()){
            case 101:
                ToastUtil.showToast(mContext,"账号或密码错误");
                break;
            case 202:
                ToastUtil.showToast(mContext,"用户名已存在");
                break;
            case 203:
                ToastUtil.showToast(mContext,"邮箱已存在");
                break;
            case 207:
                ToastUtil.showToast(mContext,"验证码错误");
                break;
            case 209:
                ToastUtil.showToast(mContext,"手机号码已存在");
                break;
            case 210:
                ToastUtil.showToast(mContext,"旧密码不正确");
                break;
            case 10010:
                ToastUtil.showToast(mContext,"该手机号发送短信达到限制");
                break;
            case 10011:
                ToastUtil.showToast(mContext,"该账户无可用的发送短信条数");
                break;
            case 10012:
                ToastUtil.showToast(mContext,"身份信息必须审核通过才能使用该功能");
                break;
            case 10013:
                ToastUtil.showToast(mContext,"非法短信内容");
                break;
        }
    }

    public int getErrorCode(){
        if(mErrorResponse != null){
            return mErrorResponse.code;
        }
        return 0;
    }

    public String getErrorMessage(){
        if(mErrorResponse != null){
            return mErrorResponse.error;
        }
        return "";
    }

    private static class ErrorResponse{
        public int code;
        public String error;
    }
}
