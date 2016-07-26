package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.ttm.tlrb.R;
import com.ttm.tlrb.ui.fragment.EmailResetPasswordFragment;
import com.ttm.tlrb.ui.fragment.PhoneResetPasswordFragment;

/**
 * Created by Helen on 2016/7/26.
 * 重置密码
 */
public class ResetPasswordActivity extends TitlebarActivity implements View.OnClickListener{
    private TextView mTextViewEmail;
    private TextView mTextViewPhone;
    private Fragment currentFragment;
    private Fragment mPhoneFragment;
    private Fragment mEmailFragment;

    public static void launcher(Context context){
        context.startActivity(new Intent(context,ResetPasswordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle("密码重置");
        mTextViewEmail = (TextView) findViewById(R.id.tv_use_email);
        mTextViewEmail.setOnClickListener(this);
        mTextViewPhone = (TextView) findViewById(R.id.tv_use_phone);
        mTextViewPhone.setOnClickListener(this);
        mTextViewEmail.setSelected(false);
        mTextViewPhone.setSelected(true);
        currentFragment = mPhoneFragment = new PhoneResetPasswordFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_reset_content, mPhoneFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switchContent(v.getId());
    }

    private void switchContent(int id){
        switch (id){
            case R.id.tv_use_email:
                mTextViewEmail.setSelected(true);
                mTextViewPhone.setSelected(false);
                if(mEmailFragment == null){
                    mEmailFragment = new EmailResetPasswordFragment();
                }
                switchFragment(mPhoneFragment, mEmailFragment);
                break;
            case R.id.tv_use_phone:
                mTextViewEmail.setSelected(false);
                mTextViewPhone.setSelected(true);
                if(mPhoneFragment == null){
                    mPhoneFragment = new PhoneResetPasswordFragment();
                }
                switchFragment(mEmailFragment,mPhoneFragment);
                break;
            default:break;
        }
    }

    private void switchFragment(Fragment from, Fragment to) {
        if (currentFragment != to) {
            currentFragment = to;
            try {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (!to.isAdded()) { // 先判断是否被add过
                    transaction.hide(from)
                            .add(R.id.layout_reset_content, to)
                            .commit(); // 隐藏当前的fragment，add下一个到Activity中
                } else {
                    transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
