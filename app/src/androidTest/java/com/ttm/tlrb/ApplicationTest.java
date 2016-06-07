package com.ttm.tlrb;

import android.os.Environment;
import android.test.ApplicationTestCase;

import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobACL;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.FileBodyEn;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.utils.GsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<RBApplication> {
    final CountDownLatch signal = new CountDownLatch(1);

    public ApplicationTest() {
        super(RBApplication.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //testGetRedBombList();
        //testRegister();
    }

    public void testGetRedBombList(){
        APIManager.getInstance().getRedBombList("test002", 0,1, 2, new Subscriber<List<RedBomb>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onNext(List<RedBomb> redBombs) {
                if(redBombs != null && !redBombs.isEmpty()){
                    System.out.println(redBombs.get(0));
                }
            }
        });
    }

    public void testRegister() throws InterruptedException {
        Account account = new Account();
        account.setUsername("test0011");
        account.setPassword("123456");
        account.setNickname("123");
        APIManager.getInstance().register(account, new Subscriber<Account>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException){
                    HttpException httpException = (HttpException) e;
                    retrofit2.Response<?> response = httpException.response();
                    ResponseBody errorBody = response.errorBody();
                    try {
                        String error = errorBody.string();
                        String code = GsonUtil.getJsonByKey(error,"code");
                        if("202".equals(code)){
                            //用户已存在
                            System.out.println("用户已存在");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    errorBody.close();
                }
                signal.countDown();
            }

            @Override
            public void onNext(Account account) {
                System.out.println("account = "+account.toString());
                signal.countDown();
            }
        });
        signal.await();
    }

    public void testLogin() throws InterruptedException {
        APIManager.getInstance()
                .getAPIService()
                .login("test006","123456")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Account>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        signal.countDown();
                    }

                    @Override
                    public void onNext(Account account) {
                        System.out.println(account);
                        signal.countDown();
                    }
                });
        signal.await();
    }

    public void testUpdateUser() throws InterruptedException {
        RBApplication.getInstance().setSession("cafd25f3404f237480e34e500bf69d43");
        Account account = new Account();
        account.setObjectId("fcc558814e");
        account.setNickname("123456");
        /*String objectId = "fcc558814e";
        BmobACL acl = new BmobACL();
        acl.setReadAccess(objectId,true);
        acl.setWriteAccess(objectId,true);
        Map<String,Object> map = new HashMap<>();
        map.put("ACL",acl.getAclMap());
        RequestBody body = RequestBody.create(Constant.JSON, GsonUtil.fromObject2Json(map));*/
        APIManager.getInstance()
                .updateUser(account,new Subscriber<BmobObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException){
                            HttpException httpException = (HttpException) e;
                            retrofit2.Response<?> response = httpException.response();
                            String msg = response.message();
                            ResponseBody errorBody = response.errorBody();
                            try {
                                String error = errorBody.string();
                                errorBody.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        System.out.println(e.getMessage());
                        signal.countDown();
                    }

                    @Override
                    public void onNext(BmobObject object) {
                        System.out.println(object);
                        signal.countDown();
                    }
                });
        signal.await();
    }

    public void testAddRedBomb() throws InterruptedException {
        RedBomb redBomb =  new RedBomb();
        BmobACL acl = new BmobACL();
        acl.setReadAccess("40b6c198a1",true);
        acl.setWriteAccess("40b6c198a1",true);
        redBomb.setACL(acl);
        redBomb.setUserName("test001");
        redBomb.setGift("没有随礼");
        redBomb.setMoney(600d);
        redBomb.setCategoryName("默认");
        redBomb.setTarget(1);
        redBomb.setName("张少锋2");
        redBomb.setType(1);
        redBomb.setTime("2016-06-03");

        APIManager.getInstance().addRedBomb(redBomb, new Subscriber<BmobObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
                signal.countDown();
            }

            @Override
            public void onNext(BmobObject object) {
                System.out.println(object.getObjectId());
                signal.countDown();
            }
        });
        signal.await();
    }

    public void testUploadFile() throws InterruptedException {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/dayjoke","a0f57cc0d367596106cbcd26a73d2b9e.jpg");
        APIManager.getInstance().uploadFile(file, new Subscriber<FileBodyEn>() {
            @Override
            public void onCompleted() {
                signal.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
                signal.countDown();
            }

            @Override
            public void onNext(FileBodyEn fileBodyEn) {
                System.out.println(fileBodyEn);
                signal.countDown();
            }
        });
        signal.await();
    }

    public void testAddCategory(){

    }
}