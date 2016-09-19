package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.BaseSubscriber;
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
    private TextView mTextViewEmail;
    private View layoutPwd;
    private TextView textViewType;

    private ImageConfig mImageConfig;
    private final int REQUEST_NICK = 0x001;
    private final int REQUEST_PHONE = 0x002;
    private final int REQUEST_EMAIL = 0x003;
    private final int REQUEST_PASSWORD = 0x004;
    private Account mAccount;

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
        findViewById(R.id.layout_phone).setOnClickListener(this);
        findViewById(R.id.layout_email).setOnClickListener(this);
        layoutPwd = findViewById(R.id.linearLayout_password);
        layoutPwd.setOnClickListener(this);
        layoutPwd.setVisibility(View.GONE);
        findViewById(R.id.line1).setVisibility(View.GONE);
        mHeaderView = (SimpleDraweeView) findViewById(R.id.iv_portrait);
        mTextViewNick = (TextView) findViewById(R.id.textView_nick);
        mTextViewPhone = (TextView) findViewById(R.id.tv_phone);
        mTextViewEmail = (TextView) findViewById(R.id.tv_email);

        textViewType = (TextView) findViewById(R.id.textView_type);
        mAccount = UserManager.getInstance().getAccount();
        setAccountInfo();
        refreshAccount();
    }

    private void refreshAccount() {
        if(mAccount == null ) return;

        APIManager.getInstance().getUser(mAccount.getObjectId(), new Subscriber<Account>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Account account) {
                mAccount = account;
                UserManager.getInstance().updateAccount(mAccount);
                setAccountInfo();
            }
        });
    }

    private void setAccountInfo(){
        if(mAccount != null){
            mHeaderView.setImageURI(Uri.parse(mAccount.getPortrait()));
            if(mAccount.getNickname().equals("")){
                mTextViewNick.setText("您还没有昵称，快去设置吧~");
            }
            else{
                mTextViewNick.setText(mAccount.getNickname());
            }
            int type = mAccount.getType();
            if(type == 0){
                layoutPwd.setVisibility(View.VISIBLE);
                findViewById(R.id.line1).setVisibility(View.VISIBLE);
                textViewType.setText("来自注册");
            }else if(type == 1){
                textViewType.setText("来自新浪");
            }else if(type == 2){
                textViewType.setText("来自微信");
            }else if(type == 3){
                textViewType.setText("来自QQ");
            }
            setPhone();
            setEmail();
        }
    }

    private void setPhone(){
        String phone = mAccount.getMobilePhoneNumber();
        boolean isVerify = mAccount.isMobilePhoneNumberVerified();
        if(!TextUtils.isEmpty(phone)){
            if(isVerify) {
                mTextViewPhone.setText(phone);
            }else {
                String phoneStr = phone+"(未验证)";
                SpannableString span = new SpannableString(phoneStr);
                span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Red_400)),phone.length(),phoneStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextViewPhone.setText(span);
            }
        }
    }

    private void setEmail(){
        String email = mAccount.getEmail();
        boolean isVerify = mAccount.isEmailVerified();
        if(!TextUtils.isEmpty(email)){
            if(isVerify) {
                mTextViewEmail.setText(email);
            }else {
                String emailStr = email+"(未验证)";
                SpannableString span = new SpannableString(emailStr);
                span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Red_400)),email.length(),emailStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTextViewEmail.setText(span);
            }
        }
    }

    private Subscriber<BmobObject> mUpdatePictureSubscriber;
    private void inputPicture(String filePath){
        File file = new File(filePath);
        if(mUpdatePictureSubscriber == null || mUpdatePictureSubscriber.isUnsubscribed()){
            mUpdatePictureSubscriber = new BaseSubscriber<BmobObject>(this) {
                @Override
                public void atNext(BmobObject object) {
                    Account account = UserManager.getInstance().getAccount();
                    account.setPortrait(APIManager.getInstance().getPictureUrl());
                    UserManager.getInstance().updateAccount(account);
                    mHeaderView.setImageURI(Uri.parse(APIManager.getInstance().getPictureUrl()));
                }
            };
        }
        Account account = UserManager.getInstance().getAccount();
        APIManager.getInstance().updatePicture(account.getObjectId(),file,mUpdatePictureSubscriber);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == this.REQUEST_NICK) {
                mTextViewNick.setText(UserManager.getInstance().getAccount().getNickname());
            } else if (requestCode == REQUEST_EMAIL) {
                mAccount = UserManager.getInstance().getAccount();
                setEmail();
            } else if (requestCode == REQUEST_PHONE) {
                mAccount = UserManager.getInstance().getAccount();
                setPhone();
            } else if (requestCode == ImageSelector.IMAGE_REQUEST_CODE) {
                if (data != null) {
                    List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                    if (pathList != null && !pathList.isEmpty()) {
                        inputPicture(pathList.get(0));
                    }
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
            case R.id.layout_phone:
                intent.setClass(UserInfoActivity.this,UpdatePhoneActivity.class);
                startActivityForResult(intent,this.REQUEST_PHONE);
                break;
            case R.id.layout_email:
                intent.setClass(UserInfoActivity.this,UpdateEmailActivity.class);
                startActivityForResult(intent,this.REQUEST_EMAIL);
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

    @Override
    protected void onDestroy() {
        if(mUpdatePictureSubscriber != null && !mUpdatePictureSubscriber.isUnsubscribed()){
            mUpdatePictureSubscriber.unsubscribe();
        }
        super.onDestroy();
    }
}
