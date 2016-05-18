package com.ttm.tlrb.api;

import com.ttm.tlrb.ui.entity.BaseEn;
import com.ttm.tlrb.ui.entity.FileBodyEn;
import com.ttm.tlrb.ui.entity.VersionInfoResponseEn;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
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

    @POST("2/files/{fileName}")
    Observable<FileBodyEn> postFileUpload(@Path("fileName") String fileName, @Body RequestBody file);

    @POST("1/classes/Feedback")
    Observable<BaseEn> postFeedback(@Body RequestBody feedBack);

    @GET("1/classes/VersionInfo")
    Observable<VersionInfoResponseEn> getVersionInfo(@Query("limit") int limit, @Query("order") String bql);
}
