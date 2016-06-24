package com.ttm.tlrb.ui.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李晓伟 on 2016/6/24.
 *
 */
public class AuthData extends BaseEn{
    //用户相关信息
    private String userNickname;
    private String userPortrait;

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }
    //平台授权信息
    private String uid;
    private String access_token;
    private long expires_in;
    private Platform platform;

    public Platform getPlatform() {
        return platform;
    }


    public AuthData(Platform platform){
        this.platform = platform;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public Map<String,Object> getAuthData(){
        Map<String,Object> platMap = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        map.put("uid",getUid());
        map.put("access_token",getAccess_token());
        map.put("expires_in",getExpires_in());
        platMap.put(platform.getName(),map);
        return platMap;
    }

    public enum Platform{
        PLATFORM_WB("weibo"),PLATFORM_WX("weixin"),PLATFORM_QQ("qq");
        private String name;
        Platform(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
