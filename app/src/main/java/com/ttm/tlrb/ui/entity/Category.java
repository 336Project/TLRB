package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/5/19.
 * 组别
 */
public class Category extends BmobObject{
    private String name;//组别名称
    private String userName;//数据关联的用户名

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if(name != null){
            builder.append("\"name\":\"").append(name).append("\",");
        }else {
            throw new NullPointerException("name not be null");
        }
        if (getACL() != null){
            builder.append("\"ACL\":").append(getACL().toString()).append(",");
        }
        if(userName != null){
            builder.append("\"userName\":\"").append(userName).append("\"");
        }else {
            throw new NullPointerException("userName not be null");
        }
        builder.append("}");
        return builder.toString();
    }
}
