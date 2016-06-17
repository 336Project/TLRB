package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.utils.ToastUtil;

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

        APIManager.getInstance().register(account, new Subscriber<Account>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                ToastUtil.showToast(RegisterActivity.this,"注册错误，请重试");
            }
            @Override
            public void onNext(Account account) {
                ToastUtil.showToast(RegisterActivity.this,"onNext");
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                input();
                break;
            case R.id.imageView_close:
                finish();
                break;
        }
    }
}
