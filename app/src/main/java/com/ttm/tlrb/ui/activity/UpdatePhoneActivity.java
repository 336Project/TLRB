package com.ttm.tlrb.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.BaseSubscriber;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.GsonUtil;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.utils.VerifyUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class UpdatePhoneActivity extends TitlebarActivity implements View.OnClickListener{

    private EditText mEditTextPhone;
    private EditText mEditTextCode;
    private Button mButtonGetCode;
    private CountDownTimer mCountDownTimer;
    private Account mAccount;
    private Subscriber<BmobObject> mSubscriberGetCode;
    private Subscriber<BmobObject> mSubscriberVerify;
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
            case R.id.btn_confirm://绑定手机
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

                if(mSubscriberVerify == null || mSubscriberVerify.isUnsubscribed()){
                    mSubscriberVerify = new BaseSubscriber<BmobObject>(this) {
                        @Override
                        public void atNext(BmobObject object) {
                            Account account = UserManager.getInstance().getAccount();
                            account.setMobilePhoneNumberVerified(true);
                            account.setMobilePhoneNumber(phone);
                            UserManager.getInstance().updateAccount(account);
                            ToastUtil.showToast(UpdatePhoneActivity.this,"绑定成功");
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void atError(Throwable e) {
                            ToastUtil.showToast(UpdatePhoneActivity.this,"验证失败");
                        }
                    };
                }
                Map<String,String> map = new HashMap<>();
                map.put("mobilePhoneNumber",phone);
                RequestBody body = RequestBody.create(Constant.JSON, GsonUtil.fromMap2Json(map));
                APIManager.getInstance().getAPIService().verifySmsCode(code,body)
                        .flatMap(new Func1<BmobObject, Observable<BmobObject>>() {
                            @Override
                            public Observable<BmobObject> call(BmobObject object) {
                                Account updateAccount = new Account();
                                updateAccount.setMobilePhoneNumber(phone);
                                updateAccount.setMobilePhoneNumberVerified(true);
                                RequestBody body = RequestBody.create(Constant.JSON, updateAccount.toString());
                                return APIManager.getInstance().getAPIService().putUser(mAccount.getObjectId(),body);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mSubscriberVerify);
                break;
            case R.id.btn_get_code://获取验证码
                String phoneNum = mEditTextPhone.getText().toString().trim();
                if(VerifyUtil.checkMobileNumber(phoneNum)){
                    mCountDownTimer.start();
                    if(mSubscriberGetCode == null || mSubscriberGetCode.isUnsubscribed()){
                        mSubscriberGetCode = new BaseSubscriber<BmobObject>(this) {
                            @Override
                            public void atNext(BmobObject object) {
                                ToastUtil.showToast(UpdatePhoneActivity.this,"验证码已发送，请注意查收");
                            }

                            @Override
                            public void atError(Throwable e) {
                                ToastUtil.showToast(UpdatePhoneActivity.this,"获取验证码失败");
                            }
                        };
                    }
                    APIManager.getInstance().getSmsCode(phoneNum, mSubscriberGetCode);
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
        if(mSubscriberGetCode != null && !mSubscriberGetCode.isUnsubscribed()){
            mSubscriberGetCode.unsubscribe();
        }
        if(mSubscriberVerify != null && !mSubscriberVerify.isUnsubscribed()){
            mSubscriberVerify.unsubscribe();
        }
        super.onDestroy();
    }
}
