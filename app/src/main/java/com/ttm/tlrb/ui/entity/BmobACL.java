package com.ttm.tlrb.ui.entity;

import android.text.TextUtils;

import com.ttm.tlrb.utils.GsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李晓伟 on 2016/6/2.
 *
 */
public class BmobACL extends BaseEn{
    private Map<String, Object> aclMap = new HashMap<>();

    public Map<String, Object> getAclMap() {
        return aclMap;
    }

    public void setAclMap(Map<String, Object> aclMap) {
        this.aclMap = aclMap;
    }

    private void code(String userId, String key, boolean isAllow) {

        if(aclMap.containsKey(userId)){
            Map<String,Object> m = (Map<String, Object>) aclMap.get(userId);
            m.put(key,isAllow);
            aclMap.put(userId,m);
        }else {
            Map<String,Object> v = new HashMap<>();
            v.put(key, isAllow);
            aclMap.put(userId,v);
        }

    }

    public void setReadAccess(String userId, boolean allowed) {
        if(TextUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("cannot setReadAccess for null userId");
        } else {
            if(allowed) {
                this.code(userId,"read", true);
            }

        }
    }

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
        return GsonUtil.fromMap2Json(aclMap);
    }
}
