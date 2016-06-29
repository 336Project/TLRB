package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.ui.entity.Account;

public class UpdatePasswordActivity extends AppCompatActivity {
    private EditText mEditTextOldPassword;
    private EditText mEditTextNewPassword;
    private EditText mEditTextAgainPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        initView();
    }

    private void initView() {
         mEditTextOldPassword = (EditText) findViewById(R.id.editText_oldPassword);
         mEditTextNewPassword = (EditText) findViewById(R.id.editText_newPassword);
         mEditTextAgainPassword = (EditText) findViewById(R.id.editText_againPassword);
        Account mAccount = UserManager.getInstance().getAccount();
        Log.e("e1","e1:"+mAccount.getPassword());


    }
}
