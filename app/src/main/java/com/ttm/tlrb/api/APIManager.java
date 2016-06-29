package com.ttm.tlrb.api;


import android.text.TextUtils;

import com.ttm.tlrb.BuildConfig;
import com.ttm.tlrb.api.e.CategoryExistException;
import com.ttm.tlrb.api.e.CategoryOverCountException;
import com.ttm.tlrb.api.interceptor.LogInterceptor;
import com.ttm.tlrb.api.interceptor.NetworkInterceptor;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.AuthData;
import com.ttm.tlrb.ui.entity.BmobACL;
import com.ttm.tlrb.ui.entity.BmobFile;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.Category;
import com.ttm.tlrb.ui.entity.FileBodyEn;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.ui.entity.ResponseEn;
import com.ttm.tlrb.ui.entity.VersionInfo;
import com.ttm.tlrb.utils.EnvironmentUtil;
import com.ttm.tlrb.utils.GsonUtil;
import com.ttm.tlrb.utils.HLog;
import com.ttm.tlrb.utils.MD5;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Helen on 2016/4/27.
 *
 */
public class APIManager {
    private Retrofit retrofit;
    private APIService apiService;
    private UserManager mUserManager;
    private APIManager(){
        File cacheFile = new File(EnvironmentUtil.getCacheFile(), Constant.CACHE_HTTP);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addNetworkInterceptor(new NetworkInterceptor());
        builder.cache(new Cache(cacheFile, Constant.MAX_CACHE_SIZE));
        if(BuildConfig.DEBUG){
            builder.addNetworkInterceptor(new LogInterceptor());
        }
        OkHttpClient client = builder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        apiService = getService(APIService.class);
        mUserManager = UserManager.getInstance();
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
                .doOnNext(new Action1<Account>() {
                    @Override
                    public void call(Account account) {
                        RBApplication.getInstance().setSession(account.getSessionToken());
                        mUserManager.updateAccount(account);
                        MobclickAgent.onProfileSignIn(account.getUsername());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 授权登录
     * @param authData 授权信息
     * @param subscriber 回调
     */
    public void loginWithAuthData(final AuthData authData, Subscriber<Account> subscriber){
        Map<String,Object> map = new HashMap<>();
        map.put("authData",authData.getAuthData());
        final RequestBody body = RequestBody.create(Constant.JSON,GsonUtil.fromMap2Json(map));
        getAPIService().loginWithAuth(body)
                .doOnNext(new Action1<Account>() {
                    @Override
                    public void call(Account account) {

                        account.setNickname(authData.getUserNickname());
                        account.setPortrait(authData.getUserPortrait());
                        RBApplication.getInstance().setSession(account.getSessionToken());
                        mUserManager.updateAccount(account);
                        //判断平台
                        AuthData.Platform platform = authData.getPlatform();
                        int type = 0;
                        if(platform == AuthData.Platform.PLATFORM_WB){
                            MobclickAgent.onProfileSignIn("WB",account.getUsername());
                            type = 1;
                        }else if(platform == AuthData.Platform.PLATFORM_QQ){
                            MobclickAgent.onProfileSignIn("QQ",account.getUsername());
                            type = 3;
                        }else if(platform == AuthData.Platform.PLATFORM_WX){
                            MobclickAgent.onProfileSignIn("WX",account.getUsername());
                            type = 2;
                        }

                        //修改用户信息
                        if(account.getACL() == null) {
                            String objectId = account.getObjectId();
                            Account a = new Account();
                            a.setNickname(account.getNickname());
                            a.setPortrait(account.getPortrait());
                            a.setType(type);
                            a.setObjectId(objectId);
                            BmobACL acl = new BmobACL();
                            acl.setReadAccess(objectId, true);
                            acl.setWriteAccess(objectId, true);
                            a.setACL(acl);
                            updateUser(a, new Subscriber<BmobObject>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    //do nothing
                                }

                                @Override
                                public void onNext(BmobObject object) {
                                    //do nothing
                                }
                            });
                        }
                    }
                })
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
        final RequestBody body = RequestBody.create(Constant.JSON,account.toString());
        /*Map<String,Object> where = new HashMap<>();
        where.put("username",account.getUsername());
        getUser(GsonUtil.fromMap2Json(where))*/
        getAPIService().register(body)
                //先校验账号是否已经存在
                /*.flatMap(new Func1<ResponseEn<Account>, Observable<Account>>() {
                    @Override
                    public Observable<Account> call(ResponseEn<Account> responseEn) {
                        if(responseEn != null
                                && responseEn.results != null
                                && responseEn.results.size() > 0){
                            throw new UserExistException();
                        }
                        return getAPIService().register(body);
                    }
                })*/
                .doOnNext(new Action1<Account>() {
                    @Override
                    public void call(Account account) {
                        //注册成功后，修改用户的ACL规则，使其他用户无法获取该用户的信息
                        RBApplication.getInstance().setSession(account.getSessionToken());
                        String objectId = account.getObjectId();
                        Account a = new Account();
                        a.setObjectId(objectId);
                        BmobACL acl = new BmobACL();
                        acl.setReadAccess(objectId, true);
                        acl.setWriteAccess(objectId, true);
                        a.setACL(acl);
                        updateUser(a, new Subscriber<BmobObject>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                //do nothing
                            }

                            @Override
                            public void onNext(BmobObject object) {
                                //do nothing
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 更新用户
     * @param account 当前用户
     * @param subscriber 回调
     */
    public void updateUser(final Account account, Subscriber<BmobObject> subscriber){
        String id = account.getObjectId();
        account.setObjectId(null);
        RequestBody body = RequestBody.create(Constant.JSON, account.toString());
        getAPIService()
                .putUser(id, body)
                /*.doOnNext(new Action1<BmobObject>() {
                    @Override
                    public void call(BmobObject object) {
                        mUserManager.updateAccount(account);
                    }
                })*/
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
        redBomb.setACL(mUserManager.getUserACL());
        redBomb.setUserName(mUserManager.getAccount().getUsername());

        RequestBody body = RequestBody.create(Constant.JSON,redBomb.toString());
        getAPIService().postRedBomb(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 修改红包数据到服务器
     * @param redBomb 添加对象
     * @param subscriber 回调监听
     */
    public void updateRedBomb(RedBomb redBomb, Subscriber<BmobObject> subscriber){
        RedBomb updateRedBomb=new RedBomb();
        updateRedBomb.setCategoryName(redBomb.getCategoryName());
        updateRedBomb.setGift(redBomb.getGift());
        updateRedBomb.setMoney(redBomb.getMoney());
        updateRedBomb.setName(redBomb.getName());
        updateRedBomb.setRemark(redBomb.getRemark());
        updateRedBomb.setTarget(redBomb.getTarget());
        updateRedBomb.setTime(redBomb.getTime());
        updateRedBomb.setType(redBomb.getType());
        RequestBody body = RequestBody.create(Constant.JSON,updateRedBomb.toString());
        getAPIService().putRedBomb(redBomb.getObjectId(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 删除红包数据到服务器
     * @param objectId 要删除的红包数据的id
     */
    public void deleteRedBomb(String objectId, Subscriber<BmobObject> subscriber){
        getAPIService().deleteRedBomb(objectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取红包列表
     * @param page 页码
     * @param size 页大小
     * @param subscriber 回调
     */
    public void getRedBombList(int type,int page,int size,Subscriber<List<RedBomb>> subscriber){
        //关联当前用户
        Map<String,Object> where = new HashMap<>();
        where.put("userName",mUserManager.getAccount().getUsername());
        if(type == 1 || type == 2) {
            where.put("type", type);
        }

        int skip = size * (page-1);
        getAPIService().getRedBomb(GsonUtil.fromMap2Json(where),skip, size,"-createdAt")
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
     * 统计红包收入支出
     */
    public void countRedBombMoney(Subscriber<List<Map<String,String>>> subscriber){
        getAPIService().countRedBombMoney("money","type")
                .map(new Func1<ResponseEn<Map<String,String>>, List<Map<String,String>>>() {
                    @Override
                    public List<Map<String, String>> call(ResponseEn<Map<String, String>> responseEn) {
                        if(responseEn != null){
                            return responseEn.results;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 文件上传
     * @param file 要上传的文件
     * @param subscriber 回调
     */
    public void uploadFile(File file, Subscriber<String> subscriber){
        if(file == null){
            throw new NullPointerException("upload file not be null");
        }
        HLog.d("uploadFile","File is exist " + file.exists());
        //获取文件类型
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(file.getAbsolutePath());
        HLog.d("uploadFile","ContentType = " + contentType);
        MediaType mediaType = MediaType.parse(contentType);
        //生成文件名称
        String name = file.getName();
        int index = name.lastIndexOf(".");
        String suffix = name.substring(index);
        String fileName = MD5.toMd5(name)+suffix;

        //构造RequestBody并发起请求
        RequestBody requestBody = RequestBody.create(mediaType,file);
        getAPIService().postFileUpload(fileName,requestBody)
                .map(new Func1<FileBodyEn, String>() {
                    @Override
                    public String call(FileBodyEn fileBodyEn) {
                        HLog.d("uploadFile",fileBodyEn.toString());
                        return fileBodyEn.getUrl();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    /**
     * 检查更新
     * @param subscriber 回调
     */
    public void checkVersionUpdate(Subscriber<VersionInfo> subscriber){
        Map<String,Object> where = new HashMap<>();
        where.put("isPatch",false);
        getAPIService().getVersionInfo(GsonUtil.fromMap2Json(where),1,"-version")
                .map(new Func1<ResponseEn<VersionInfo>, VersionInfo>() {
                    @Override
                    public VersionInfo call(ResponseEn<VersionInfo> responseEn) {
                        List<VersionInfo> versionInfoList = responseEn.results;
                        if(versionInfoList != null && !versionInfoList.isEmpty()){
                            return versionInfoList.get(0);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 登录时进行检测更新
     */
    private boolean isCheck = false;
    public void loginCheckVersion(Subscriber<VersionInfo> subscriber){
        if (isCheck) return;
        isCheck = true;
        getAPIService().getVersionInfo(null,5,"-version")
                .map(new Func1<ResponseEn<VersionInfo>, List<VersionInfo>>() {
                    @Override
                    public List<VersionInfo> call(ResponseEn<VersionInfo> versionInfoResponseEn) {
                        return versionInfoResponseEn.results;
                    }
                })
                .map(new Func1<List<VersionInfo>, VersionInfo>() {
                    @Override
                    public VersionInfo call(List<VersionInfo> versionInfos) {
                        VersionInfo currInfo = null;
                        if(versionInfos != null){
                            for (VersionInfo info:versionInfos){
                                String version = info.getVersion();
                                if(!info.getPatch() && version.compareTo(BuildConfig.VERSION_NAME) > 0){//新版本
                                    currInfo = info;
                                    break;
                                }else if(info.getPatch() && version.equals(BuildConfig.VERSION_NAME)) {//有修复包
                                    currInfo = info;
                                    break;
                                }
                            }
                        }
                        return currInfo;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 热修复
     */
    public void addPatch(){
        Map<String,Object> where = new HashMap<>();
        where.put("isPatch",true);
        where.put("version", BuildConfig.VERSION_NAME);
        getAPIService().getVersionInfo(GsonUtil.fromMap2Json(where),10,"-updatedAt")
                .flatMap(new Func1<ResponseEn<VersionInfo>, Observable<String>>() {
                    @Override
                    public Observable<String> call(ResponseEn<VersionInfo> versionInfoResponseEn) {
                        List<VersionInfo> versionInfos = versionInfoResponseEn.results;
                        List<String> urls = new ArrayList<String>();
                        if(versionInfos != null){
                            for (VersionInfo info:versionInfos){
                                HLog.d("APIManager",info.toString());
                                BmobFile file = info.getFile();
                                if(file != null) {
                                    String url = file.getUrl();
                                    String fileName = url.substring(url.lastIndexOf("/")+1,url.length());
                                    File patchFile = new File(EnvironmentUtil.getCacheFile() + File.separator + Constant.CACHE_PATCH, fileName);
                                    if (info.getPatch() && !TextUtils.isEmpty(url) && !patchFile.exists()) {
                                        urls.add(url);
                                    }
                                }
                            }
                        }
                        return Observable.from(urls.toArray(new String[urls.size()]));
                    }
                })
                .flatMap(new Func1<String, Observable<Response<ResponseBody>>>() {
                    @Override
                    public Observable<Response<ResponseBody>> call(String url) {
                        String fileId = url.replace(APIService.BASE_DOWNLOAD_FILE_URL,"");
                        return getAPIService().getFile(fileId);
                    }
                })
                .map(new Func1<Response<ResponseBody>, File>() {
                    @Override
                    public File call(Response<ResponseBody> response) {
                        okhttp3.Response resp = response.raw();
                        if(resp.isSuccessful()) {
                            String url = resp.request().url().url().getPath();
                            url = URLDecoder.decode(url);
                            HLog.d("APIManager","response decode url == "+url);
                            String fileName = url.substring(url.lastIndexOf("/")+1,url.length());
                            HLog.d("APIManager","file name == "+fileName);
                            File file = new File(EnvironmentUtil.getCacheFile() + File.separator + Constant.CACHE_PATCH, fileName);
                            FileOutputStream fos = null;
                            ResponseBody body = response.body();
                            try {
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                                fos = new FileOutputStream(file, false);
                                fos.write(body.bytes());
                                fos.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (fos != null) {
                                    try {
                                        fos.close();
                                        body.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            return file;
                        }
                        return null;
                    }
                })
                .map(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        if(file != null) {
                            try {
                                RBApplication.getInstance().getPatchManager().addPatch(file.getAbsolutePath());
                                return true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        HLog.e("APIManager","onError",e);
                    }

                    @Override
                    public void onNext(Boolean isSuccess) {
                        HLog.d("APIManager","onNext----"+isSuccess);
                    }
                });
    }

    /**
     * 添加分组
     * @param category 组别对象
     * @param subscriber 回调
     */
    public void addCategory(Category category, Subscriber<BmobObject> subscriber){
        final String userName = mUserManager.getAccount().getUsername();
        final String name = category.getName();
        if(TextUtils.isEmpty(userName)){
            throw new NullPointerException("Category's userName not be null");
        }
        if(TextUtils.isEmpty(name)){
            throw new NullPointerException("Category's name not be null");
        }

        category.setACL(mUserManager.getUserACL());
        category.setUserName(userName);

        Map<String,Object> where = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        map.put("$in",new String[]{userName,"-1"});
        where.put("userName",map);
        where.put("name",name);
        final RequestBody body = RequestBody.create(Constant.JSON,category.toString());
        getAPIService().getCategory(GsonUtil.fromMap2Json(where),Category.LIMIT_COUNT,null)
                //先查看是否已经存在相同的组名
                .flatMap(new Func1<ResponseEn<Category>, Observable<ResponseEn<Category>>>() {
                    @Override
                    public Observable<ResponseEn<Category>> call(ResponseEn<Category> categoryResponseEn) {
                        List<Category> categoryList = categoryResponseEn.results;
                        if(categoryList != null && categoryList.size() > 0){
                            throw new CategoryExistException(name);
                        }
                        Map<String,Object> where = new HashMap<>();
                        Map<String,Object> map = new HashMap<>();
                        map.put("$in",new String[]{userName,"-1"});
                        where.put("userName",map);
                        return getAPIService().countCategory(GsonUtil.fromMap2Json(where),0,"1");
                    }
                })
                //是否超出个数限制
                .flatMap(new Func1<ResponseEn<Category>, Observable<BmobObject>>() {
                    @Override
                    public Observable<BmobObject> call(ResponseEn<Category> categoryResponseEn) {
                        if(categoryResponseEn.count >= Category.LIMIT_COUNT){
                            throw new CategoryOverCountException();
                        }
                        return getAPIService().postCategory(body);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取组别列表
     * @param subscriber 回调
     */
    public void getCategoryList(Subscriber<List<Category>> subscriber){
        String userName = mUserManager.getAccount().getUsername();
        Map<String,Object> where = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        map.put("$in",new String[]{userName,"-1"});
        where.put("userName",map);
        getAPIService().getCategory(GsonUtil.fromMap2Json(where),Category.LIMIT_COUNT,"createdAt")
                .map(new Func1<ResponseEn<Category>, List<Category>>() {
                    @Override
                    public List<Category> call(ResponseEn<Category> categoryResponseEn) {
                        return categoryResponseEn.results;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 删除组别
     * @param objectId 组别id
     * @param subscriber 回调
     */
    public void deleteCategory(String objectId, Subscriber<BmobObject> subscriber){
        getAPIService().deleteCategory(objectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 修改组别
     * @param objectId 要修改的组别id
     * @param name 修改后的组别名称
     * @param subscriber 回调
     */
    public void updateCategory(final String objectId, final String name, Subscriber<BmobObject> subscriber){
        final String userName = mUserManager.getAccount().getUsername();
        Map<String,Object> where = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        map.put("$in",new String[]{userName,"-1"});
        where.put("userName",map);
        where.put("name",name);
        getAPIService().getCategory(GsonUtil.fromMap2Json(where),Category.LIMIT_COUNT,null)
                //先查看是否已经存在相同的组名
                .flatMap(new Func1<ResponseEn<Category>, Observable<BmobObject>>() {
                    @Override
                    public Observable<BmobObject> call(ResponseEn<Category> categoryResponseEn) {
                        List<Category> categoryList = categoryResponseEn.results;
                        if(categoryList != null && categoryList.size() > 0){
                            throw new CategoryExistException(name);
                        }
                        Map<String,String> bodyMap = new HashMap<String,String>();
                        bodyMap.put("name",name);
                        RequestBody body = RequestBody.create(Constant.JSON,GsonUtil.fromMap2Json(bodyMap));
                        return getAPIService().putCategory(objectId,body);
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
    /*@Deprecated
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
    }*/

}
