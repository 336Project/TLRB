package com.ttm.tlrb.ui.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by 李晓伟 on 2016/5/19.
 *
 */
public class Category extends DataSupport{
    private int id;
    private String objectId;//服务端字段，只有当数据同步之后，该字段才有值
    private String name;//组别名称
    private String userName;//数据关联的用户名


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
