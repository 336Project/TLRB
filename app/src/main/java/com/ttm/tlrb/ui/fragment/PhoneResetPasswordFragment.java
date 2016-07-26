package com.ttm.tlrb.ui.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.utils.VerifyUtil;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Helen on 2016/7/26.
 * 短信重置
 */
public class PhoneResetPasswordFragment extends Fragment implements View.OnClickListener{
    private EditText mEditTextPhone;
    private EditText mEditTextCode;
    private EditText mEditTextPwd;
    private Button mButtonGetCode;
    private CountDownTimer mCountDownTimer;
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
                    APIManager.getInstance().getSmsCode(phoneNum, new Subscriber<BmobObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if(e instanceof HttpException){
                                HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,getActivity());
                                handle.handle();
                            }else {
                                ToastUtil.showToast(getActivity(),"获取验证码失败");
                            }
                        }

                        @Override
                        public void onNext(BmobObject object) {
                            ToastUtil.showToast(getActivity(),"验证码已发送，请注意查收");
                        }
                    });
                }else {
                    ToastUtil.showToast(getActivity(),"请输入正确的手机号码");
                }
                break;
            case R.id.btn_confirm://确定重置
                String newPwd = mEditTextPwd.getText().toString();
                String code = mEditTextCode.getText().toString();
                if(!VerifyUtil.checkPassword(getActivity(),newPwd)){
                    return;
                }
                if(TextUtils.isEmpty(code)){
                    ToastUtil.showToast(getActivity(),"请输入验证码");
                    return;
                }
                APIManager.getInstance().resetPasswordBySmsCode(newPwd, code, new Subscriber<BmobObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException){
                            HttpException httpException = (HttpException) e;
                            int code = httpException.code();
                            if(code == 500){
                                ToastUtil.showToast(getActivity(),"该手机未绑定账号");
                            }else {
                                HttpExceptionHandle handle = new HttpExceptionHandle(httpException, getActivity());
                                handle.handle();
                            }
                        }else {
                            ToastUtil.showToast(getActivity(),"该手机未绑定账号");
                        }
                    }

                    @Override
                    public void onNext(BmobObject object) {
                        ToastUtil.showToast(getActivity(),"修改成功");
                        getActivity().finish();
                    }
                });
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCountDownTimer.cancel();
    }
}
