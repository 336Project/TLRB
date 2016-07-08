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
        if(nickname == null){
            return "";
        }
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
        if(portrait == null){
            return "";
        }
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /*public String getUpdateString(){//该方法用于更新用户时，上传服务器的json body
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        if(nickname != null){
            builder.append("\"nickname\":\"").append(nickname).append("\",");
        }
        if(portrait != null){
            builder.append("\"portrait\":\"").append(portrait).append("\",");
        }
        if(type != null){
            builder.append("\"type\":").append(type).append(",");
        }
        if(getUsername() != null){
            builder.append("\"username\":\"").append(getUsername()).append("\",");
        }

        if(getACL() != null){
            builder.append("\"ACL\":").append(getACL()).append(",");
        }
        //去掉最后一个逗号
        int index = builder.lastIndexOf(",");
        builder = builder.delete(index,builder.length());

        builder.append("}");
        return builder.toString();
    }*/
}
