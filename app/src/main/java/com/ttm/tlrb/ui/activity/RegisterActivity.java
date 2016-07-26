package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.utils.VerifyUtil;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        String userName = mEditTextUsername.getText().toString().trim();
        String pwd = mEditTextPassword.getText().toString().trim();


        if(TextUtils.isEmpty(userName)){
            ToastUtil.showToast(RegisterActivity.this,"账号不能为空");
            return;
        }
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher m = p.matcher(userName);
        if(m.find()){
            ToastUtil.showToast(RegisterActivity.this,"账号有非法字符,请使用字母或数字");
            return;
        }

        if(VerifyUtil.checkPassword(this,pwd)) {
            Account account = new Account();
            account.setUsername(userName);
            account.setPassword(pwd);
            account.setType(0);
            APIManager.getInstance().register(account, new Subscriber<Account>() {

                @Override
                public void onStart() {
                    super.onStart();
                    showLoadingDialog();
                }

                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    hideLoadingDialog();
                    if (e instanceof HttpException) {
                        HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e, RegisterActivity.this);
                        handle.handle();
                    } else {
                        ToastUtil.showToast(RegisterActivity.this, "注册失败");
                    }
                }

                @Override
                public void onNext(Account account) {
                    hideLoadingDialog();
                    ToastUtil.showToast(RegisterActivity.this, "注册成功");
                    finish();
                }
            });

        }
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
