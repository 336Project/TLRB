package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.BaseSubscriber;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.utils.VerifyUtil;

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
        if(VerifyUtil.checkPassword(this,newPassword)) {
            Account mAccount = UserManager.getInstance().getAccount();
            if (mUpdatePasswordSubscriber == null || mUpdatePasswordSubscriber.isUnsubscribed()) {
                mUpdatePasswordSubscriber = new BaseSubscriber<BmobObject>(this) {
                    @Override
                    public void atNext(BmobObject object) {
                        ToastUtil.showToast(UpdatePasswordActivity.this, "修改成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                };
            }

            APIManager.getInstance().updateUserPassword(mAccount.getObjectId(), oldPassword, newPassword, mUpdatePasswordSubscriber);
        }

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
