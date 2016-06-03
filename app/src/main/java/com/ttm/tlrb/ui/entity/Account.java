package com.ttm.tlrb.ui.entity;

import com.google.gson.Gson;

/**
 * Created by Helen on 2016/5/19.
 * 账户
 */
public class Account extends BmobUser{
    private String nickname;//昵称，显示名称
    private Integer type;//注册类型：0自注册，1新浪微博，2微信，3qq
    private String portrait;//头像地址


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
