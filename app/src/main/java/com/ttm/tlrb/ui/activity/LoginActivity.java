package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.AuthData;
import com.ttm.tlrb.utils.HLog;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.view.MaterialDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

public class LoginActivity extends BaseActivity implements View.OnClickListener,UMAuthListener{

    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private MaterialDialog mMaterialDialog;

    public static void launcher(Context context){
        context.startActivity(new Intent(context,LoginActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initDialog();
    }

    private void initDialog() {
        mMaterialDialog =new MaterialDialog(LoginActivity.this).setContentView(R.layout.material_dialog_login);
        mMaterialDialog.setCanceledOnTouchOutside(true);
    }

    private void initView() {
        findViewById(R.id.textView_register).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.iv_sina).setOnClickListener(this);
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
                umShareAPI = UMShareAPI.get(this);
                /*boolean isAuth = umShareAPI.isAuthorize(LoginActivity.this,SHARE_MEDIA.SINA);
                if(!isAuth) {*/
                    umShareAPI.doOauthVerify(this, SHARE_MEDIA.SINA, this);
                /*}else {
                    umShareAPI.getPlatformInfo(LoginActivity.this,SHARE_MEDIA.SINA,this);
                }*/
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
        APIManager.getInstance().login(userName, password, new Subscriber<Account>() {
            @Override
            public void onStart() {
                super.onStart();
                mMaterialDialog.show();
            }

            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException){
                    HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,LoginActivity.this);
                    handle.handle();
                }
                mMaterialDialog.dismiss();
            }
            @Override
            public void onNext(Account account) {
                Log.e("success","success");
                mMaterialDialog.dismiss();
                loginSuccess();
            }
        });
    }

    private void loginSuccess(){
        ToastUtil.showToast(LoginActivity.this,getString(R.string.login_success));
        MainActivity.launcher(LoginActivity.this);
        finish();
    }

    private AuthData mAuthData;
    @Override
    public void onComplete(SHARE_MEDIA share_media, int action, Map<String, String> map) {
        if(share_media == SHARE_MEDIA.SINA) {
            if (action == 0) {//获取授权信息
                HLog.d("LoginActivity", "doOauthVerify---onComplete" + " action = " + action + " map = " + map.toString());

                String uid = map.get("uid");
                String expires_in = map.get("expires_in");
                String access_token = map.get("access_token");

                mAuthData = new AuthData(AuthData.Platform.PLATFORM_WB);
                mAuthData.setUid(uid);
                mAuthData.setAccess_token(access_token);
                mAuthData.setExpires_in(Long.valueOf(expires_in));

                umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, this);
            } else if (action == 2) {//获取用户信息
                HLog.d("LoginActivity", "getFriend---onComplete" + " action = " + action + " map = " + map.toString());
                String resultJson = map.get("result");
                try {
                    JSONObject json = new JSONObject(resultJson);
                    String nickName = json.getString("screen_name");
                    String portrait = json.getString("avatar_large");
                    if(mAuthData != null){
                        mAuthData.setUserNickname(nickName);
                        mAuthData.setUserPortrait(portrait);
                        APIManager.getInstance().loginWithAuthData(mAuthData, new Subscriber<Account>() {

                            @Override
                            public void onStart() {
                                super.onStart();
                                mMaterialDialog.show();
                            }

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mMaterialDialog.dismiss();
                                ToastUtil.showToast(LoginActivity.this,getString(R.string.auth_fail));
                            }

                            @Override
                            public void onNext(Account account) {
                                mMaterialDialog.dismiss();
                                loginSuccess();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_BACK){
            if(mMaterialDialog!=null){
                mMaterialDialog.dismiss();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
