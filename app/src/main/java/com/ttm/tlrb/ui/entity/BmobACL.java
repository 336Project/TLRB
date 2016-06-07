package com.ttm.tlrb.ui.entity;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Helen on 2016/6/2.
 *
 */
public class BmobACL extends HashMap<String,Object>{

    private void code(String userId, String key, boolean isAllow) {

        if(containsKey(userId)){
            Map<String,Object> m = (Map<String, Object>) get(userId);
            m.put(key,isAllow);
            put(userId,m);
        }else {
            Map<String,Object> v = new HashMap<>();
            v.put(key, isAllow);
            put(userId,v);
        }

    }

    /**
     * 设置是否可读
     * @param userId 当前用户的objectId
     * @param allowed true 可读
     */
    public void setReadAccess(String userId, boolean allowed) {
        if(TextUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("cannot setReadAccess for null userId");
        } else {
            if(allowed) {
                this.code(userId,"read", true);
            }

        }
    }
    /**
     * 设置是否可写
     * @param userId 当前用户的objectId
     * @param allowed true 可写
     */
    public void setWriteAccess(String userId, boolean allowed) {
        if(TextUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("cannot setReadAccess for null userId");
        } else {
            if(allowed) {
                this.code(userId,"write", true);
            }

        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
