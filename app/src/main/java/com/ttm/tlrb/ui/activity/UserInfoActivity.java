package com.ttm.tlrb.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.ui.entity.Account;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private SimpleDraweeView mHeaderView;
    private TextView mTextViewNick;
    private TextView mTextViewPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
    }

    private void initView() {
        findViewById(R.id.textView_back).setOnClickListener(this);
        findViewById(R.id.linearLayout_nick).setOnClickListener(this);
        findViewById(R.id.linearLayout_password).setOnClickListener(this);
        findViewById(R.id.linearLayout_phone).setOnClickListener(this);
        mHeaderView = (SimpleDraweeView) findViewById(R.id.iv_portrait);
        mTextViewNick = (TextView) findViewById(R.id.textView_nick);
        mTextViewPhone = (TextView) findViewById(R.id.textView_phone);

        Account account = UserManager.getInstance().getAccount();
        if(account != null){
            mHeaderView.setImageURI(Uri.parse(account.getPortrait()));
            if(account.getNickname().equals("")){
                mTextViewNick.setText("您还没有昵称，快去设置吧");
            }
            else{
                mTextViewNick.setText(account.getNickname());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_back:
                finish();
                break;
            case R.id.linearLayout_nick:

                break;
            case R.id.linearLayout_password:
                break;
            case R.id.linearLayout_phone:

                break;

        }
    }
}
