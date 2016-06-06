package com.ttm.tlrb;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;

import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobACL;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.FileBodyEn;
import com.ttm.tlrb.ui.entity.RedBomb;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import rx.Subscriber;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    final CountDownLatch signal = new CountDownLatch(1);

    public ApplicationTest() {
        super(Application.class);
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
        account.setUsername("test003");
        account.setPassword("123456");
        account.setNickname("123");
        APIManager.getInstance().register(account, new Subscriber<Account>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
                signal.countDown();
            }

            @Override
            public void onNext(Account account) {
                System.out.println(account.toString());
                signal.countDown();
            }
        });
        signal.await();
    }

    public void testAddRedBomb(){
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
        redBomb.setName("张少锋");
        redBomb.setType(1);
        redBomb.setTime("2016-06-03");

        APIManager.getInstance().addRedBomb(redBomb, new Subscriber<BmobObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onNext(BmobObject object) {
                System.out.println(object.getObjectId());
            }
        });
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
}