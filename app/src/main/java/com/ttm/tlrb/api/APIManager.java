package com.ttm.tlrb.api;


import com.ttm.tlrb.api.interceptor.LogInterceptor;
import com.ttm.tlrb.api.interceptor.NetworkInterceptor;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BaseEn;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.ui.entity.RequestBatch;
import com.ttm.tlrb.ui.entity.ResponseEn;
import com.ttm.tlrb.utils.EnvironmentUtil;
import com.ttm.tlrb.utils.GsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    /**
     * 登录
     * @param username 用户名
     * @param pwd 密码
     * @param subscriber 回调
     */
    public void login(String username, String pwd, Subscriber<Account> subscriber){
        getAPIService().login(username,pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 注册
     * @param account 用户对象
     * @param subscriber 回调
     */
    public void register(Account account,Subscriber<Account> subscriber){
        RequestBody body = RequestBody.create(Constant.JSON,account.toString());
        getAPIService().register(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 添加条红包数据到服务器
     * @param redBomb 添加对象
     * @param subscriber 回调监听
     */
    public void addRedBomb(RedBomb redBomb, Subscriber<BmobObject> subscriber){
        RequestBody body = RequestBody.create(Constant.JSON,redBomb.toString());
        getAPIService().postRedBomb(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取红包列表
     * @param username 当前用户名
     * @param page 页码
     * @param size 页大小
     * @param subscriber 回调
     */
    public void getRedBombList(String username,int page,int size,Subscriber<List<RedBomb>> subscriber){
        Map<String,String> where = new HashMap<>();
        where.put("userName",username);
        int skip = size * (page-1);
        getAPIService().getRedBomb(GsonUtil.fromMap2Json(where),skip, size)
                .map(new Func1<ResponseEn<RedBomb>, List<RedBomb>>() {
                    @Override
                    public List<RedBomb> call(ResponseEn<RedBomb> redBombResponseEn) {
                        if(redBombResponseEn != null){
                            return redBombResponseEn.results;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 批量添加红包数据
     * @param redBombs 添加对象
     * @param subscriber 回调监听
     *
     */
    @Deprecated
    public void postRedBombBatch(List<RedBomb> redBombs,Subscriber<BaseEn> subscriber){

        Map<String,List<RequestBatch>> map = new HashMap<>();
        List<RequestBatch> requestBatches = new ArrayList<>(1);

        RequestBatch request = new RequestBatch();
        request.setMethod("POST");
        request.setPath("/1/classes/RedBomb");
        request.setBody(GsonUtil.fromList2Json(redBombs));
        requestBatches.add(request);
        map.put("requests",requestBatches);

        RequestBody requestBody = RequestBody.create(Constant.JSON, GsonUtil.fromMap2Json(map));
        APIManager.getInstance().getAPIService()
                .postBatch(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
