package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.view.CleanableEditText;

public class RegisterActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        CleanableEditText mEditTextUsername =  (CleanableEditText)findViewById(R.id.editText_username);
        CleanableEditText mEditTextPassword =  (CleanableEditText)findViewById(R.id.editText_password);
        CleanableEditText mEditTextNickname =  (CleanableEditText)findViewById(R.id.editText_nickname);
        CleanableEditText mEditTextPhone =  (CleanableEditText)findViewById(R.id.editText_phone);
        CleanableEditText mEditTextPortrait =  (CleanableEditText)findViewById(R.id.editText_portrait);
    }
    private void input(){
        Account account = new Account();


//        APIManager.getInstance().register();

    }
}
