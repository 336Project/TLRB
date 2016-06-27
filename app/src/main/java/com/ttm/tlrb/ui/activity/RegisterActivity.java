package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private EditText mEditTextUsername;
    private  EditText mEditTextPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        mEditTextUsername =  (EditText)findViewById(R.id.editText_username);
        mEditTextPassword =  (EditText)findViewById(R.id.editText_password);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.imageView_close).setOnClickListener(this);
    }
    private void input(){
        Account account = new Account();
        account.setUsername(mEditTextUsername.getText().toString().trim());
        account.setPassword(mEditTextPassword.getText().toString().trim());

        String userName = mEditTextUsername.getText().toString();
        Pattern p = Pattern.compile("[a-zA-Z0-9]");
        Matcher m = p.matcher(userName);
        if(!m.matches()){
            Toast.makeText(RegisterActivity.this,"输入有中文", Toast.LENGTH_SHORT).show();
            return;
        }
        APIManager.getInstance().register(account, new Subscriber<Account>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException){
                    HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,RegisterActivity.this);
                    handle.handle();
                }
            }
            @Override
            public void onNext(Account account) {
                ToastUtil.showToast(RegisterActivity.this,"注册成功");
                finish();
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                MobclickAgent.onEvent(RegisterActivity.this, Constant.Event.EVENT_ID_REGISTER);
                input();
                break;
            case R.id.imageView_close:
                finish();
                break;
        }
    }
}
