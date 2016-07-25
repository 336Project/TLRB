package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.utils.VerifyUtil;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public class UpdatePhoneActivity extends TitlebarActivity implements View.OnClickListener{

    private EditText mEditTextPhone;
    private EditText mEditTextCode;
    private Button mButtonGetCode;
    private CountDownTimer mCountDownTimer;
    private Account mAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone);
        setTitle("修改手机号码");
        initView();
    }

    private void initView() {
        mAccount = UserManager.getInstance().getAccount();
        String oldPhone = "";
        if(mAccount != null){
            oldPhone = mAccount.getMobilePhoneNumber();
        }
        mEditTextPhone = (EditText) findViewById(R.id.editText_phone);
        mEditTextCode = (EditText) findViewById(R.id.et_code);
        mButtonGetCode = (Button) findViewById(R.id.btn_get_code);
        mButtonGetCode.setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mEditTextPhone.setText(oldPhone);

        mCountDownTimer = new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long count) {
                mButtonGetCode.setEnabled(false);
                mButtonGetCode.setText((count/1000)+"s");
            }

            @Override
            public void onFinish() {
                mButtonGetCode.setEnabled(true);
                mButtonGetCode.setText(R.string.get_code);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                final String phone = mEditTextPhone.getText().toString().trim();
                String code = mEditTextCode.getText().toString().trim();
                if(!VerifyUtil.checkMobileNumber(phone)){
                    ToastUtil.showToast(UpdatePhoneActivity.this,"请输入正确的手机号码");
                    return;
                }
                if(TextUtils.isEmpty(code)){
                    ToastUtil.showToast(UpdatePhoneActivity.this,"请输入验证码");
                    return;
                }
                APIManager.getInstance().verifySmsCode(phone, code, new Subscriber<BmobObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException){
                            HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,UpdatePhoneActivity.this);
                            handle.handle();
                        }else {
                            ToastUtil.showToast(UpdatePhoneActivity.this,"验证失败");
                        }
                    }

                    @Override
                    public void onNext(BmobObject object) {
                        //验证成功，更新用户信息
                        Account account = UserManager.getInstance().getAccount();
                        account.setMobilePhoneNumberVerified(true);
                        account.setMobilePhoneNumber(phone);
                        UserManager.getInstance().updateAccount(account);

                        Account updateAccount = new Account();
                        updateAccount.setObjectId(account.getObjectId());
                        updateAccount.setMobilePhoneNumber(phone);
                        updateAccount.setMobilePhoneNumberVerified(true);
                        APIManager.getInstance().updateUser(updateAccount, new Subscriber<BmobObject>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if(e instanceof HttpException){
                                    HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,UpdatePhoneActivity.this);
                                    handle.handle();
                                }else {
                                    ToastUtil.showToast(UpdatePhoneActivity.this,"验证失败");
                                }
                            }

                            @Override
                            public void onNext(BmobObject object) {
                                ToastUtil.showToast(UpdatePhoneActivity.this,"绑定成功");
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                });
                break;
            case R.id.btn_get_code:
                String phoneNum = mEditTextPhone.getText().toString().trim();
                if(VerifyUtil.checkMobileNumber(phoneNum)){
                    mCountDownTimer.start();
                    APIManager.getInstance().getSmsCode(phoneNum, new Subscriber<BmobObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if(e instanceof HttpException){
                                HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,UpdatePhoneActivity.this);
                                handle.handle();
                            }else {
                                ToastUtil.showToast(UpdatePhoneActivity.this,"获取验证码失败");
                            }
                        }

                        @Override
                        public void onNext(BmobObject object) {
                            ToastUtil.showToast(UpdatePhoneActivity.this,"验证码已发送，请注意查收");
                        }
                    });
                }else {
                    ToastUtil.showToast(UpdatePhoneActivity.this,"请输入正确的手机号码");
                }
                break;

            default:break;
        }
    }

    @Override
    protected void onDestroy() {
        mCountDownTimer.cancel();
        super.onDestroy();
    }
}
