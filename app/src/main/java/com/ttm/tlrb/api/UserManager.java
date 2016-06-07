package com.ttm.tlrb.api;

import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.utils.GsonUtil;
import com.ttm.tlrb.utils.SPUtil;

/**
 * Created by Helen on 2016/6/7.
 *
 */
public class UserManager {

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
        mAccount = account;
        SPUtil.getInstance().putString("user_info",account.toString()).commit();
    }

    public Account getAccount(){
        if(mAccount != null){
            return mAccount;
        }
        return GsonUtil.fromJson(SPUtil.getInstance().getString("user_info"),Account.class);
    }

    public void updateSessionToken(String sessionToken){
        SPUtil.getInstance().putString("session_token",sessionToken).commit();
    }

    public String getSessionToken(){
        return SPUtil.getInstance().getString("session_token");
    }
}
