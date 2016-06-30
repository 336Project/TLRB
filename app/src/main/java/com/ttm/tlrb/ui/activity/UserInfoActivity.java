package com.ttm.tlrb.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.ui.entity.Account;

import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.engine.GlideEngine;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private SimpleDraweeView mHeaderView;
    private TextView mTextViewNick;
    private TextView mTextViewPhone;
    private final int REQUEST_NICK = 0x001;
    private final int REQUEST_PASSWORD = 0x001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
    }

    private void initView() {
        findViewById(R.id.textView_back).setOnClickListener(this);
        findViewById(R.id.linearLayout_portrait).setOnClickListener(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == this.REQUEST_NICK){
            mTextViewNick.setText(UserManager.getInstance().getAccount().getNickname());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.textView_back:
                finish();
                break;
            case R.id.linearLayout_nick:
                intent.setClass(UserInfoActivity.this,UpdateNickNameActivity.class);
                startActivityForResult(intent,this.REQUEST_NICK);
                break;
            case R.id.linearLayout_password:
                intent.setClass(UserInfoActivity.this,UpdatePasswordActivity.class);
                startActivityForResult(intent,this.REQUEST_PASSWORD);
                break;
            case R.id.linearLayout_portrait:
                Log.e("sdsd","sdsd");
                Picker.from(this)
                        .count(1)
                        .enableCamera(true)
                        .setEngine(new GlideEngine())
                        .forResult(6464);
                break;

        }
    }
}
