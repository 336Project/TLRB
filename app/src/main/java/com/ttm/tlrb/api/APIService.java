package com.ttm.tlrb.api;

import com.ttm.tlrb.BuildConfig;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BaseEn;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.Category;
import com.ttm.tlrb.ui.entity.FileBodyEn;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.ui.entity.ResponseEn;
import com.ttm.tlrb.ui.entity.VersionInfo;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Helen on 2016/4/27.
 *
 */
public interface APIService {
    String BASE_URL = "https://api.bmob.cn/";
    String BASE_DOWNLOAD_FILE_URL = BuildConfig.BASE_DOWNLOAD_FILE_URL;
    //int BATCH_LIMIT_COUNT = 50;
    /**登录*/
    @GET("1/login")
    Observable<Account> login(@Query("username") String username, @Query("password") String password);
    /**授权登录*/
    @POST("1/users")
    Observable<Account> loginWithAuth(@Body RequestBody body);
    /**注册*/
    @POST("1/users")
    Observable<Account> register(@Body RequestBody body);
    /**查询用户*/
    @GET("1/users/{objectId}")
    Observable<Account> getUser(@Path("objectId") String id);
    /**更新用户*/
    @PUT("1/users/{objectId}")
    Observable<BmobObject> putUser(@Path("objectId") String id,@Body RequestBody body);
    /**修改用户密码*/
    @PUT("1/updateUserPassword/{objectId}")
    Observable<BmobObject> putUserPassword(@Path("objectId") String id,@Body RequestBody body);
    /**文件上传**/
    @POST("2/files/{fileName}")
    Observable<FileBodyEn> postFileUpload(@Path("fileName") String fileName, @Body RequestBody file);
    /**下载文件*/
    @GET(BASE_DOWNLOAD_FILE_URL+"{fileId}")
    Observable<Response<ResponseBody>> getFile(@Path("fileId") String fileId);
    /**用户反馈**/
    @POST("1/classes/Feedback")
    Observable<BaseEn> postFeedback(@Body RequestBody feedBack);
    /**检测更新**/
    @GET("1/classes/VersionInfo")
    Observable<ResponseEn<VersionInfo>> getVersionInfo(@Query("where") String where,@Query("limit") int limit, @Query("order") String order);
    /**获取红包列表数据,分页**/
    /*@Headers("Cache-Control: public, max-age=600")//10分钟刷新一次*/
    @GET("1/classes/RedBomb")
    Observable<ResponseEn<RedBomb>> getRedBomb(@Query("where") String where,@Query("skip") int skip,@Query("limit") int limit,@Query("order") String order);
    /**批量添加数据(一次只能操作50条)**/
    /*@POST("https://api.bmob.cn/1/batch")
    Observable<BaseEn> postBatch(@Body RequestBody requests);*/
    /**添加单条数据**/
    @POST("1/classes/RedBomb")
    Observable<BmobObject> postRedBomb(@Body RequestBody data);
    /**修改单条数据**/
    @PUT("1/classes/RedBomb/{objectId}")
    Observable<BmobObject> putRedBomb(@Path("objectId") String id,@Body RequestBody body);
    /**删除红包*/
    @DELETE("1/classes/RedBomb/{objectId}")
    Observable<BmobObject> deleteRedBomb(@Path("objectId") String id);
    /**统计红包收入、支出*/
    @GET("1/classes/RedBomb")
    Observable<ResponseEn<Map<String,String>>> countRedBombMoney(@Query("where") String where,@Query("sum") String sumColumn,@Query("groupby") String groupByColumn);
    /**获取组别*/
    @GET("1/classes/Category")
    Observable<ResponseEn<Category>> getCategory(@Query("where") String where,@Query("limit") int limit,@Query("order") String order);
    /**统计组别*/
    @GET("1/classes/Category")
    Observable<ResponseEn<Category>> countCategory(@Query("where") String where,@Query("limit") int limit,@Query("count") String count);
    /**添加分组*/
    @POST("1/classes/Category")
    Observable<BmobObject> postCategory(@Body RequestBody body);
    /**更新分组*/
    @PUT("1/classes/Category/{objectId}")
    Observable<BmobObject> putCategory(@Path("objectId") String id,@Body RequestBody body);
    /**删除分组*/
    @DELETE("1/classes/Category/{objectId}")
    Observable<BmobObject> deleteCategory(@Path("objectId") String id);
    /**获取验证码*/
    @POST("1/requestSmsCode")
    Observable<BmobObject> getSmsCode(@Body RequestBody body);
    /**验证手机验证码*/
    @POST("1/verifySmsCode/{code}")
    Observable<BmobObject> verifySmsCode(@Path("code") String code,@Body RequestBody body);
    /**验证邮箱*/
    @POST("1/requestEmailVerify")
    Observable<BmobObject> verifyEmail(@Body RequestBody body);
    /**短信密码重置*/
    @PUT("/1/resetPasswordBySmsCode/{code}")
    Observable<BmobObject> resetPasswordBySmsCode(@Path("code") String code,@Body RequestBody body);
    /**邮箱密码重置*/
    @POST("/1/requestPasswordReset")
    Observable<BmobObject> resetPasswordByEmail(@Body RequestBody body);
}
