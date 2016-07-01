package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public class UpdateNickNameActivity extends TitlebarActivity implements View.OnClickListener{

    private EditText mEditTextNick;
    private Account mAccount;
    private Subscriber<BmobObject> mUpdateUserSubscriber;
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
        Pattern p = Pattern.compile("[A-Za-z0-9_\\-\\u4e00-\\u9fa5]+");
        Matcher m = p.matcher(newNickName);
        if(!m.matches()){
            Toast.makeText(UpdateNickNameActivity.this,"昵称中有非法字符请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        int wordCount = getWordCount(newNickName);
        if(!(wordCount>=4&&wordCount<=16)){
            Toast.makeText(UpdateNickNameActivity.this,"昵称大小不符合请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        mAccount.setNickname(newNickName);
        Account newAccount = new Account();
        newAccount.setObjectId(mAccount.getObjectId());
        newAccount.setNickname(newNickName);
        if(mUpdateUserSubscriber == null || mUpdateUserSubscriber.isUnsubscribed()){
            mUpdateUserSubscriber = new Subscriber<BmobObject>() {
                @Override
                public void onCompleted() {
                }
                @Override
                public void onError(Throwable e) {
                    if(e instanceof HttpException){
                        HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,UpdateNickNameActivity.this);
                        handle.handle();
                    }
                }
                @Override
                public void onNext(BmobObject bmobObject) {
                    UserManager.getInstance().updateAccount(mAccount);
                    finish();
                    ToastUtil.showToast(UpdateNickNameActivity.this,"更新成功");
                }
            };
        }
        APIManager.getInstance().updateUser(newAccount,mUpdateUserSubscriber);
    }
    public  int getWordCount(String s)
    {
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
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
