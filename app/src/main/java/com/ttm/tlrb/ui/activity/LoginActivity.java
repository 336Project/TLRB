package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.tencent.tauth.Tencent;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.BaseSubscriber;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.AuthData;
import com.ttm.tlrb.utils.HLog;
import com.ttm.tlrb.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import rx.Subscriber;

public class LoginActivity extends StatusBarActivity implements View.OnClickListener,UMAuthListener{

    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private Subscriber<Account> mSubscriberLogin;


    public static void launcher(Context context){
        context.startActivity(new Intent(context,LoginActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        Tencent.setIsPermissionGranted(true);
    }

    @Override
    protected int getStatusBarColor() {
        return android.R.color.transparent;
    }

    private void initView() {
        findViewById(R.id.textView_register).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.iv_sina).setOnClickListener(this);
        findViewById(R.id.iv_qq).setOnClickListener(this);
        findViewById(R.id.iv_weixin).setOnClickListener(this);
        findViewById(R.id.tv_retrieve_password).setOnClickListener(this);
        mEditTextUserName = (EditText) findViewById(R.id.editText_username);
        mEditTextPassword = (EditText) findViewById(R.id.editText_password);
    }
    UMShareAPI umShareAPI;
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.textView_register:
                intent.setClass(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                MobclickAgent.onEvent(LoginActivity.this, Constant.Event.EVENT_ID_LOGIN_NORMAL);
                login();
                break;
            case R.id.iv_sina:
                MobclickAgent.onEvent(LoginActivity.this, Constant.Event.EVENT_ID_LOGIN_WB);
                umShareAPI = UMShareAPI.get(RBApplication.getInstance());
                umShareAPI.doOauthVerify(this, SHARE_MEDIA.SINA, this);
                break;
            case R.id.iv_qq:
                MobclickAgent.onEvent(LoginActivity.this, Constant.Event.EVENT_ID_LOGIN_QQ);
                umShareAPI = UMShareAPI.get(RBApplication.getInstance());
                umShareAPI.doOauthVerify(this, SHARE_MEDIA.QQ, this);
                break;
            case R.id.iv_weixin:
                MobclickAgent.onEvent(LoginActivity.this, Constant.Event.EVENT_ID_LOGIN_WX);
                umShareAPI = UMShareAPI.get(RBApplication.getInstance());
                umShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, this);
                break;
            case R.id.tv_retrieve_password:
                ResetPasswordActivity.launcher(LoginActivity.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(umShareAPI != null) {
            umShareAPI.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     *
     */
    private void login() {
        String userName = mEditTextUserName.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        if(userName.equals("")){
            ToastUtil.showToast(this,"账号不能为空");
            return;
        }
        if(password.equals("")){
            ToastUtil.showToast(this,"密码不能为空");
            return;
        }
        if(mSubscriberLogin == null || mSubscriberLogin.isUnsubscribed()){
            mSubscriberLogin = new BaseSubscriber<Account>(this) {
                @Override
                public void atNext(Account account) {
                    loginSuccess();
                }
            };
        }
        APIManager.getInstance().login(userName, password, mSubscriberLogin);
    }

    private void loginSuccess(){
        ToastUtil.showToast(LoginActivity.this,getString(R.string.login_success));
        MainActivity.launcher(LoginActivity.this);
        finish();
    }

    private AuthData mAuthData;
    @Override
    public void onComplete(SHARE_MEDIA share_media, int action, Map<String, String> map) {
        if(share_media == SHARE_MEDIA.SINA) {//新浪微博
            if (action == 0) {//获取授权信息
                HLog.d("LoginActivity", "doOauthVerify--weibo--onComplete" + " action = " + action + " map = " + map.toString());

                String uid = map.get("uid");
                String expires_in = map.get("expires_in");
                String access_token = map.get("access_token");

                mAuthData = new AuthData(AuthData.Platform.PLATFORM_WB);
                mAuthData.setUid(uid);
                mAuthData.setAccess_token(access_token);
                mAuthData.setExpires_in(Long.valueOf(expires_in));

                umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, this);
            } else if (action == 2) {//获取用户信息
                HLog.d("LoginActivity", "getUser--weibo--onComplete" + " action = " + action + " map = " + map.toString());
                String resultJson = map.get("result");
                try {
                    JSONObject json = new JSONObject(resultJson);
                    String nickName = json.getString("screen_name");
                    String portrait = json.getString("avatar_large");

                    loginWithAuth(nickName,portrait);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else if(share_media == SHARE_MEDIA.QQ){//qq
            if(action == 0) {
                HLog.d("LoginActivity", "doOauthVerify--qq--onComplete" + " action = " + action + " map = " + map.toString());
                String openid = map.get("openid");
                String expires_in = map.get("expires_in");
                String access_token = map.get("access_token");

                mAuthData = new AuthData(AuthData.Platform.PLATFORM_QQ);
                mAuthData.setUid(openid);
                mAuthData.setAccess_token(access_token);
                mAuthData.setExpires_in(Long.valueOf(expires_in));

                umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, this);
            }else if(action == 2){
                HLog.d("LoginActivity", "getUser--qq--onComplete" + " action = " + action + " map = " + map.toString());
                String nickName = map.get("screen_name");
                String portrait = map.get("profile_image_url");
                loginWithAuth(nickName,portrait);
            }
        }else if(share_media == SHARE_MEDIA.WEIXIN){
            if(action == 0){
                HLog.d("LoginActivity", "doOauthVerify--weixin--onComplete" + " action = " + action + " map = " + map.toString());
                String openid = map.get("openid");
                String expires_in = map.get("expires_in");
                String access_token = map.get("access_token");

                mAuthData = new AuthData(AuthData.Platform.PLATFORM_WX);
                mAuthData.setUid(openid);
                mAuthData.setAccess_token(access_token);
                mAuthData.setExpires_in(Long.valueOf(expires_in));

                umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, this);
            }else if(action == 2){
                HLog.d("LoginActivity", "getUser--weixin--onComplete" + " action = " + action + " map = " + map.toString());
                String nickName = map.get("nickname");
                String portrait = map.get("headimgurl");
                loginWithAuth(nickName,portrait);
            }
        }
    }


    private Subscriber<Account> mAuthLoginSubscriber;
    /**
     * 授权登录
     * @param nickname 用户昵称
     * @param portrait 用户头像
     */
    private void loginWithAuth(String nickname,String portrait){
        if(mAuthData == null){
            ToastUtil.showToast(LoginActivity.this, getString(R.string.auth_fail));
            return;
        }
        if(mAuthLoginSubscriber == null || mAuthLoginSubscriber.isUnsubscribed()){
            mAuthLoginSubscriber = new BaseSubscriber<Account>(this) {

                @Override
                public void atError(Throwable e) {
                    ToastUtil.showToast(LoginActivity.this, getString(R.string.auth_fail));
                }

                @Override
                public void atNext(Account account) {
                    loginSuccess();
                }
            };
        }
        mAuthData.setUserNickname(nickname);
        mAuthData.setUserPortrait(portrait);
        APIManager.getInstance().loginWithAuthData(mAuthData,mAuthLoginSubscriber);
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        ToastUtil.showToast(LoginActivity.this,getString(R.string.auth_fail));
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        ToastUtil.showToast(LoginActivity.this, getString(R.string.cancel_auth));
    }

    @Override
    protected void onDestroy() {
        if(mAuthLoginSubscriber != null && !mAuthLoginSubscriber.isUnsubscribed()){
            mAuthLoginSubscriber.unsubscribe();
        }
        if(mSubscriberLogin != null && !mSubscriberLogin.isUnsubscribed()){
            mSubscriberLogin.unsubscribe();
        }
        super.onDestroy();
    }
}
