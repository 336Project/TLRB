package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.GsonUtil;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.utils.VerifyUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UpdateEmailActivity extends TitlebarActivity implements View.OnClickListener{

    private EditText mEditTextEmail;
    private Account mAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        setTitle("修改邮箱");
        initView();
    }

    private void initView() {
        mAccount = UserManager.getInstance().getAccount();
        String oldEmail = "";
        if(mAccount!=null){
            oldEmail = mAccount.getEmail();
        }
        mEditTextEmail = (EditText) findViewById(R.id.editText_email);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mEditTextEmail.setText(oldEmail);
    }
    private void confirm(){
        final String newEmail = mEditTextEmail.getText().toString().trim();
        if(mAccount ==null){
            ToastUtil.showToast(UpdateEmailActivity.this,"登录异常，请重新登录");
            return;
        }
        if(!VerifyUtil.checkEmail(newEmail)){
            ToastUtil.showToast(UpdateEmailActivity.this,"请输入正确的邮箱地址");
            return;
        }
        Account newAccount = new Account();
        newAccount.setEmail(newEmail);
        RequestBody body = RequestBody.create(Constant.JSON, newAccount.toString());
        APIManager.getInstance().getAPIService().putUser(mAccount.getObjectId(),body)
                .flatMap(new Func1<BmobObject, Observable<BmobObject>>() {
                    @Override
                    public Observable<BmobObject> call(BmobObject object) {
                        Map<String,String> map = new HashMap<>();
                        map.put("email",newEmail);
                        RequestBody body = RequestBody.create(Constant.JSON, GsonUtil.fromMap2Json(map));
                        return APIManager.getInstance().getAPIService().verifyEmail(body);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BmobObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException){
                            HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,UpdateEmailActivity.this);
                            handle.handle();
                        }else {
                            ToastUtil.showToast(UpdateEmailActivity.this,"绑定失败");
                        }
                    }

                    @Override
                    public void onNext(BmobObject object) {
                        mAccount.setEmail(newEmail);
                        mAccount.setEmailVerified(false);
                        UserManager.getInstance().updateAccount(mAccount);
                        ToastUtil.showToast(UpdateEmailActivity.this,"验证邮件已发送到邮箱，请尽快进行验证");
                        setResult(RESULT_OK);
                        finish();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                confirm();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
