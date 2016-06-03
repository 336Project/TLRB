package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/6/2.
 *
 */
public class BmobUser extends BmobObject{
    private String username;//登录账号
    private String password;//密码
    private String sessionToken;//token

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
