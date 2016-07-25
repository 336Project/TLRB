package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.ToastUtil;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public class UpdatePasswordActivity extends TitlebarActivity implements View.OnClickListener {
    private EditText mEditTextOldPassword;
    private EditText mEditTextNewPassword;
    private EditText mEditTextAgainPassword;
    private Subscriber<BmobObject> mUpdatePasswordSubscriber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        setTitle("修改密码");
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mEditTextOldPassword = (EditText) findViewById(R.id.editText_oldPassword);
        mEditTextNewPassword = (EditText) findViewById(R.id.editText_newPassword);
        mEditTextAgainPassword = (EditText) findViewById(R.id.editText_againPassword);
    }

    private void updatePassword() {
        String oldPassword = mEditTextOldPassword.getText().toString().trim();
        String newPassword = mEditTextNewPassword.getText().toString().trim();
        String againPassword = mEditTextAgainPassword.getText().toString().trim();
        if (oldPassword.equals("")) {
            ToastUtil.showToast(UpdatePasswordActivity.this, "当前密码不能为空");
            return;
        }
        if (newPassword.equals("")) {
            ToastUtil.showToast(UpdatePasswordActivity.this, "新密码不能为空");
            return;
        }
        if (againPassword.equals("")) {
            ToastUtil.showToast(UpdatePasswordActivity.this, "确认密码不能为空");
            return;
        }
        if(!newPassword.equals(againPassword)){
            ToastUtil.showToast(UpdatePasswordActivity.this,"新密码与确认密码需要一致");
            return;
        }
        /*Pattern p = Pattern.compile("[A-Za-z0-9_]+");
        Matcher m = p.matcher(newPassword);
        if(!m.matches()){
            ToastUtil.showToast(UpdatePasswordActivity.this,"新密码含有特殊字符，建议使用数字，字母，下划线组成密码");
            return;
        }*/
        int wordCount = newPassword.length();
        if(!(wordCount>=6&&wordCount<=32)){
            ToastUtil.showToast(UpdatePasswordActivity.this,"密码长度至少6个字符，最多32个字符");
            return;
        }

        Account mAccount = UserManager.getInstance().getAccount();
        if(mUpdatePasswordSubscriber == null || mUpdatePasswordSubscriber.isUnsubscribed()){
            mUpdatePasswordSubscriber = new Subscriber<BmobObject>() {

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
                    if(e instanceof HttpException){
                        HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,UpdatePasswordActivity.this);
                        handle.handle();
                    }
                }
                @Override
                public void onNext(BmobObject bmobObject) {
                    hideLoadingDialog();
                    ToastUtil.showToast(UpdatePasswordActivity.this,"更新成功");
                    setResult(RESULT_OK);
                    finish();
                }
            };
        }

        APIManager.getInstance().updateUserPassword(mAccount.getObjectId(),oldPassword,newPassword,mUpdatePasswordSubscriber);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                updatePassword();
                break;
        }
    }
}
