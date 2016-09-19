package com.ttm.tlrb.api;

import android.text.TextUtils;

import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobACL;
import com.ttm.tlrb.utils.GsonUtil;
import com.ttm.tlrb.utils.SPUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Helen on 2016/6/7.
 *
 */
public class UserManager {
    public static final String KEY_SP_USER = "user_info";
    public static final String KEY_SP_TOKEN = "session_token";

    private static UserManager mInstance;
    private Account mAccount;

    private UserManager(){}

    public static synchronized UserManager getInstance(){
        if(mInstance == null){
            mInstance = new UserManager();
        }
        return mInstance;
    }

    public void updateAccount(Account account){
        if(account == null){
            return;
        }
        if(TextUtils.isEmpty(account.getSessionToken())){
            account.setSessionToken(mAccount.getSessionToken());
        }
        mAccount = account;
        SPUtil.getInstance().putString(KEY_SP_USER,mAccount.toString()).commit();
        updateSessionToken(mAccount.getSessionToken());
    }

    public Account getAccount(){
        if(mAccount == null){
            mAccount = GsonUtil.fromJson(SPUtil.getInstance().getString(KEY_SP_USER),Account.class);
        }
        if(mAccount == null){
            mAccount = new Account();
        }
        return mAccount;
    }

    public void updateSessionToken(String sessionToken){
        SPUtil.getInstance().putString(KEY_SP_TOKEN,sessionToken).commit();
    }

    public String getSessionToken(){
        if(mAccount != null){
            return mAccount.getSessionToken();
        }
        return SPUtil.getInstance().getString(KEY_SP_TOKEN,"");
    }

    public BmobACL getUserACL(){
        BmobACL acl = new BmobACL();
        Account account = getAccount();
        String objectId = account.getObjectId();
        acl.setReadAccess(objectId,true);
        acl.setWriteAccess(objectId,true);
        return acl;
    }

    /**
     * 登出
     */
    public void logout(){
        MobclickAgent.onProfileSignOff();
        mAccount = null;
        SPUtil.getInstance()
                .remove(KEY_SP_USER)
                .remove(KEY_SP_TOKEN)
                .commit();
    }
}
