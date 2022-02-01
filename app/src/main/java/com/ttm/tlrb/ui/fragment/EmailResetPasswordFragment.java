package com.ttm.tlrb.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.BaseSubscriber;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.utils.VerifyUtil;

/**
 * Created by Helen on 2016/7/26.
 *
 */
public class EmailResetPasswordFragment extends BaseFragment implements View.OnClickListener{
    private EditText mEditTextEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_reset_password,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mEditTextEmail = (EditText) view.findViewById(R.id.editText_email);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String email = mEditTextEmail.getText().toString().trim();
        if(!VerifyUtil.checkEmail(email)){
            ToastUtil.showToast(getActivity(),"请输入正确的邮箱地址");
            return;
        }
        APIManager.getInstance().resetPasswordByEmail(email, new BaseSubscriber<BmobObject>(mContext) {
            @Override
            public void atNext(BmobObject object) {
                ToastUtil.showToast(getActivity(),"重置密码邮件已发送，请查收");
                finish();
            }
        });
    }
}
