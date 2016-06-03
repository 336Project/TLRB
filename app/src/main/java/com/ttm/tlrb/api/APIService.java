package com.ttm.tlrb.api;

import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BaseEn;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.FileBodyEn;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.ui.entity.ResponseEn;
import com.ttm.tlrb.ui.entity.VersionInfo;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Helen on 2016/4/27.
 *
 */
public interface APIService {
    String BASE_URL = "https://api.bmob.cn/";
    int BATCH_LIMIT_COUNT = 50;
    /**登录*/
    @GET("1/login")
    Observable<Account> login(@Query("username") String username, @Query("password") String password);
    /**注册*/
    @POST("1/users")
    Observable<Account> register(@Body RequestBody user);

    /**文件上传**/
    @POST("2/files/{fileName}")
    Observable<FileBodyEn> postFileUpload(@Path("fileName") String fileName, @Body RequestBody file);
    /**用户反馈**/
    @POST("1/classes/Feedback")
    Observable<BaseEn> postFeedback(@Body RequestBody feedBack);
    /**检测更新**/
    @GET("1/classes/VersionInfo")
    Observable<ResponseEn<VersionInfo>> getVersionInfo(@Query("limit") int limit, @Query("order") String bql);

    /**获取红包列表数据,分页**/
    @Headers("Cache-Control: public, max-age=600")//10分钟
    @GET("1/classes/RedBomb")
    Observable<ResponseEn<RedBomb>> getRedBomb(@Query("where") String where,@Query("skip") int skip,@Query("limit") int limit);

    /**批量添加数据(一次只能操作50条)**/
    @POST("https://api.bmob.cn/1/batch")
    Observable<BaseEn> postBatch(@Body RequestBody requests);

    /**添加单条数据**/
    @POST("1/classes/RedBomb")
    Observable<BmobObject> postRedBomb(@Body RequestBody data);
}
