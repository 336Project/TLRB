package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.ttm.tlrb.ui.entity.BmobObject;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.util.List;

import rx.Subscriber;

public class UserInfoActivity extends TitlebarActivity implements View.OnClickListener {
    private SimpleDraweeView mHeaderView;
    private TextView mTextViewNick;
    private TextView mTextViewPhone;
    private ImageConfig mImageConfig;
    private final int REQUEST_NICK = 0x001;
    private final int REQUEST_PASSWORD = 0x001;
    private String pictureUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setTitle("我的");
        initView();
        initImageConfig();
    }

    private void initImageConfig() {

         mImageConfig
                = new ImageConfig.Builder(new FrescoLoad())
                .steepToolBarColor(getResources().getColor(R.color.colorPrimary))
                .titleBgColor(getResources().getColor(R.color.colorPrimary))
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
        public void displayImage(Context context, String path, final ImageView imageView) {
            Glide.with(context)
                    .load(path)
                    .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                    .centerCrop()
                    .into(imageView);
            /*SimpleDraweeView image = (SimpleDraweeView) imageView;
            File file = new File(path);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(image.getController())
                    .setAutoPlayAnimations(false)
                    .setUri(Uri.fromFile(file))
                    .setControllerListener(new BaseControllerListener<ImageInfo>(){
                        @Override
                        public void onFailure(String id, Throwable throwable) {
                            super.onFailure(id, throwable);
                            System.out.println(Log.getStackTraceString(throwable));
                        }
                    }).build();

            image.setController(controller);*/
        }

    }
    private void initView() {
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
        Subscriber<BmobObject> mUpdatePictureSubscriber = new Subscriber<BmobObject>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(BmobObject bmobObject) {
                Account account = UserManager.getInstance().getAccount();
                account.setPortrait(APIManager.getInstance().getPictureUrl());
                UserManager.getInstance().updateAccount(account);
                mHeaderView.setImageURI(Uri.parse(APIManager.getInstance().getPictureUrl()));
            }
        };
        Account account = UserManager.getInstance().getAccount();
        Account newAccount = new Account();
        newAccount.setObjectId(account.getObjectId());
        APIManager.getInstance().updatePicture(account.getObjectId(),file,mUpdatePictureSubscriber);
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
            case R.id.linearLayout_nick:
                intent.setClass(UserInfoActivity.this,UpdateNickNameActivity.class);
                startActivityForResult(intent,this.REQUEST_NICK);
                break;
            case R.id.linearLayout_password:
                intent.setClass(UserInfoActivity.this,UpdatePasswordActivity.class);
                startActivityForResult(intent,this.REQUEST_PASSWORD);
                break;
            case R.id.linearLayout_portrait:
                ImageSelector.open(UserInfoActivity.this, mImageConfig);   // 开启图片选择器
                break;
        }
    }
}
