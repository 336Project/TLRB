package com.ttm.tlrb.ui.entity;

import com.google.gson.Gson;

/**
 * Created by 李晓伟 on 2016/5/19.
 * 账户
 */
public class Account extends BaseEn{
    private String userName;//登录账号
    private String password;//密码
    private String nickname;//昵称，显示名称
    private Integer type;//注册类型：0自注册，1新浪微博，2微信，3qq
    private String phoneNumber;//绑定的手机号,用于找回密码?
    private String portrait;//头像地址

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
