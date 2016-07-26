package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/6/2.
 *
 */
public class BmobUser extends BmobObject{
    private String username;//登录账号
    private String password;//密码
    private String mobilePhoneNumber;
    private Boolean mobilePhoneNumberVerified;
    private String email;
    private Boolean emailVerified;
    private String sessionToken;//token

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public Boolean isMobilePhoneNumberVerified() {
        if(mobilePhoneNumberVerified == null){
            return Boolean.FALSE;
        }
        return mobilePhoneNumberVerified;
    }

    public void setMobilePhoneNumberVerified(Boolean mobilePhoneNumberVerified) {
        this.mobilePhoneNumberVerified = mobilePhoneNumberVerified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isEmailVerified() {
        if(emailVerified == null){
            return Boolean.FALSE;
        }
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

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
        if(sessionToken == null){
            return "";
        }
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
