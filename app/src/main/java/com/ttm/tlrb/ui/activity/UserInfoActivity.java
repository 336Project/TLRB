package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.ui.entity.Account;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.util.List;

import rx.Subscriber;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private SimpleDraweeView mHeaderView;
    private TextView mTextViewNick;
    private TextView mTextViewPhone;
    private ImageConfig mImageConfig;
    private final int REQUEST_NICK = 0x001;
    private final int REQUEST_PASSWORD = 0x001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        initImageConfig();
    }

    private void initImageConfig() {

         mImageConfig
                = new ImageConfig.Builder(new FrescoLoad())
                .steepToolBarColor(getResources().getColor(R.color.blue))
                .titleBgColor(getResources().getColor(R.color.blue))
                .titleSubmitTextColor(getResources().getColor(R.color.white))
                .titleTextColor(getResources().getColor(R.color.white))
                 .crop() // 默认截图
                // 开启单选   （默认为多选）
                .singleSelect()
                // 开启拍照功能 （默认关闭）
                .showCamera()
                // 拍照后存放的图片路径（默认 /temp/picture） （会自动创建）
                .filePath("/ImageSelector/Pictures")
                .build();
    }
    class FrescoLoad implements com.yancy.imageselector.ImageLoader {

        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context)
                    .load(path)
                    .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                    .centerCrop()
                    .into(imageView);
        }

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
    private void inputPicture(String filePath){
        File file = new File(filePath);
        Subscriber<String> mUpdatePictureSubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(String s) {
                Log.e("next","next");
                mHeaderView.setImageURI(Uri.parse(s));
            }
        };
        Account account = UserManager.getInstance().getAccount();
        Account newAccount = new Account();
        newAccount.setObjectId(account.getObjectId());
        APIManager.getInstance().updatePicture(file,mUpdatePictureSubscriber,newAccount);
//        APIManager.getInstance().uploadFile(file,mUpdatePictureSubscriber);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == this.REQUEST_NICK){
            mTextViewNick.setText(UserManager.getInstance().getAccount().getNickname());
        }
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Log.e("hhh","hhh");
            // Get Image Path List
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            int i=0;
            for (String path : pathList) {
                Log.e("path","path:"+path);
                if(i==0){
                    inputPicture(path);
                    i++;
                }
            }
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
                ImageSelector.open(UserInfoActivity.this, mImageConfig);   // 开启图片选择器
                break;
        }
    }
}
