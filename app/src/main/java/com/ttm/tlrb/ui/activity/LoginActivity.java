package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.view.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private MaterialDialog mMaterialDialog;

    public static void launcher(Context context){
        context.startActivity(new Intent(context,LoginActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initDialog();
    }

    private void initDialog() {
        mMaterialDialog =new MaterialDialog(LoginActivity.this).setContentView(R.layout.material_dialog_login);
        mMaterialDialog.setCanceledOnTouchOutside(true);
    }

    private void initView() {
        findViewById(R.id.textView_register).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        mEditTextUserName = (EditText) findViewById(R.id.editText_username);
        mEditTextPassword = (EditText) findViewById(R.id.editText_password);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.textView_register:
                intent.setClass(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                MobclickAgent.onEvent(LoginActivity.this, Constant.Event.EVENT_ID_LOGIN_NORMAL);
                login();
                break;
        }
    }

    /**
     *
     */
    private void login() {
        String userName = mEditTextUserName.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        if(userName.equals("")){
            ToastUtil.showToast(this,"账号不能为空");
            return;
        }
        if(password.equals("")){
            ToastUtil.showToast(this,"密码不能为空");
            return;
        }
        APIManager.getInstance().login(userName, password, new Subscriber<Account>() {
            @Override
            public void onStart() {
                super.onStart();
                mMaterialDialog.show();
            }

            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException){
                    HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,LoginActivity.this);
                    handle.handle();
                }
                mMaterialDialog.dismiss();
            }
            @Override
            public void onNext(Account account) {
                mMaterialDialog.dismiss();
                MainActivity.launcher(LoginActivity.this);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("keyCode","keyCode");
        if(keyCode ==KeyEvent.KEYCODE_BACK){
            if(mMaterialDialog!=null){
                mMaterialDialog.dismiss();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
