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

public class UpdateNickNameActivity extends TitlebarActivity implements View.OnClickListener{

    private EditText mEditTextNick;
    private Account mAccount;
    private BaseSubscriber<BmobObject> mUpdateUserSubscriber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick_name);
        setTitle("修改昵称");
        initView();
    }

    private void initView() {
        mAccount = UserManager.getInstance().getAccount();
        String oldNickName = "";
        if(mAccount!=null){
            oldNickName = mAccount.getNickname();
        }
        mEditTextNick = (EditText) findViewById(R.id.editText_nick);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mEditTextNick.setText(oldNickName);
    }
    private void confirm(){
        String newNickName = mEditTextNick.getText().toString().trim();
        if(mAccount ==null){
            ToastUtil.showToast(UpdateNickNameActivity.this,"登录异常，请重新登录");
            return;
        }
        if(newNickName.equals("")){
            ToastUtil.showToast(UpdateNickNameActivity.this,"用户名不能为空，请重新输入");
            return;
        }
        mAccount.setNickname(newNickName);
        Account newAccount = new Account();
        newAccount.setObjectId(mAccount.getObjectId());
        newAccount.setNickname(newNickName);
        if(mUpdateUserSubscriber == null || mUpdateUserSubscriber.isUnsubscribed()){
            mUpdateUserSubscriber = new BaseSubscriber<BmobObject>(this) {
                @Override
                public void atNext(BmobObject object) {
                    UserManager.getInstance().updateAccount(mAccount);
                    ToastUtil.showToast(UpdateNickNameActivity.this,"修改成功");
                    setResult(RESULT_OK);
                    finish();
                }

            };
        }
        APIManager.getInstance().updateUser(newAccount,mUpdateUserSubscriber);
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
        if(mUpdateUserSubscriber!=null&& !mUpdateUserSubscriber.isUnsubscribed()){
            mUpdateUserSubscriber.unsubscribe();
        }
    }
}
