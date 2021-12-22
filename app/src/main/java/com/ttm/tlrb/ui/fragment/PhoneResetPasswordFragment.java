package com.ttm.tlrb.ui.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.BaseSubscriber;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.utils.VerifyUtil;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Helen on 2016/7/26.
 * 短信重置
 */
public class PhoneResetPasswordFragment extends BaseFragment implements View.OnClickListener{
    private EditText mEditTextPhone;
    private EditText mEditTextCode;
    private EditText mEditTextPwd;
    private Button mButtonGetCode;
    private CountDownTimer mCountDownTimer;
    private BaseSubscriber<BmobObject> mSubscriberGetCode;
    private BaseSubscriber<BmobObject> mSubscriber;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_reset_password,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mEditTextPhone = (EditText) view.findViewById(R.id.editText_phone);
        mEditTextCode = (EditText) view.findViewById(R.id.et_code);
        mButtonGetCode = (Button) view.findViewById(R.id.btn_get_code);
        mEditTextPwd = (EditText) view.findViewById(R.id.editText_newPassword);
        mButtonGetCode.setOnClickListener(this);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.btn_get_code://获取验证码
                String phoneNum = mEditTextPhone.getText().toString().trim();
                if(VerifyUtil.checkMobileNumber(phoneNum)){
                    mCountDownTimer.start();
                    if(mSubscriberGetCode == null || mSubscriberGetCode.isUnsubscribed()){
                        mSubscriberGetCode = new BaseSubscriber<BmobObject>(mContext) {

                            @Override
                            public void atError(Throwable e) {
                                ToastUtil.showToast(mContext,"获取验证码失败");
                            }

                            @Override
                            public void atNext(BmobObject object) {
                                ToastUtil.showToast(mContext,"验证码已发送，请注意查收");
                            }
                        };
                    }
                    APIManager.getInstance().getSmsCode(phoneNum, mSubscriberGetCode);
                }else {
                    ToastUtil.showToast(mContext,"请输入正确的手机号码");
                }
                break;
            case R.id.btn_confirm://确定重置
                String newPwd = mEditTextPwd.getText().toString();
                String code = mEditTextCode.getText().toString();
                if(!VerifyUtil.checkPassword(mContext,newPwd)){
                    return;
                }
                if(TextUtils.isEmpty(code)){
                    ToastUtil.showToast(mContext,"请输入验证码");
                    return;
                }
                if(mSubscriber == null || mSubscriber.isUnsubscribed()){
                    mSubscriber = new BaseSubscriber<BmobObject>(mContext) {

                        @Override
                        public void atNext(BmobObject object) {
                            ToastUtil.showToast(mContext,"修改成功");
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dismiss();
                            if(e instanceof HttpException){
                                HttpException httpException = (HttpException) e;
                                int code = httpException.code();
                                if(code == 500){
                                    ToastUtil.showToast(mContext,"该手机未绑定账号");
                                }else {
                                    HttpExceptionHandle handle = new HttpExceptionHandle(httpException, mContext);
                                    handle.handle();
                                }
                            }else {
                                ToastUtil.showToast(mContext,"该手机未绑定账号");
                            }
                        }
                    };
                }
                APIManager.getInstance().resetPasswordBySmsCode(newPwd, code,mSubscriber);
                break;
        }
    }

    @Override
    public void onDestroy() {
        mCountDownTimer.cancel();
        if(mSubscriberGetCode != null && !mSubscriberGetCode.isUnsubscribed()){
            mSubscriberGetCode.unsubscribe();
        }
        if(mSubscriber != null && !mSubscriber.isUnsubscribed()){
            mSubscriber.unsubscribe();
        }
        super.onDestroy();
    }
}
