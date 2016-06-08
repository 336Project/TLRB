package com.ttm.tlrb.api;

import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobACL;
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
        updateSessionToken(mAccount.getSessionToken());
    }

    public Account getAccount(){
        if(mAccount == null){
            mAccount = GsonUtil.fromJson(SPUtil.getInstance().getString("user_info"),Account.class);
        }
        return mAccount;
    }

    public void updateSessionToken(String sessionToken){
        SPUtil.getInstance().putString("session_token",sessionToken).commit();
    }

    public String getSessionToken(){
        if(mAccount != null){
            return mAccount.getSessionToken();
        }
        return SPUtil.getInstance().getString("session_token");
    }

    public BmobACL getUserACL(){
        BmobACL acl = new BmobACL();
        Account account = getAccount();
        String objectId = account.getObjectId();
        acl.setReadAccess(objectId,true);
        acl.setWriteAccess(objectId,true);
        return acl;
    }
}
