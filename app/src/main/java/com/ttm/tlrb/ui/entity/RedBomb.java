package com.ttm.tlrb.ui.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * user:wtw
 * time: 2016/5/19 0019.
 */
public class RedBomb extends DataSupport {
    @Column( defaultValue = "",nullable = true)
    private String userName;//数据关联的数据账号
    private String objectId;//服务端字段，只有当数据同步之后，该字段才有值
    private String name;//姓名
    private String time;//时间
    private String target;//男女方（1男方、2女方、3共同）
    private String type;//类型（1收入、2支出）
    private String categoryName;//组别名称（不设置，都为'默认'组别）
    private String money;//金额
    private String gift;//随礼
    private String remark;//备注
    private String isSync;//是否已经同步到服务器上


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }

    @Override
    public String toString() {
        return "RedBomb{" +
                ", userName='" + userName + '\'' +
                ", objectId='" + objectId + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", target=" + target +
                ", type=" + type +
                ", categoryName='" + categoryName + '\'' +
                ", money=" + money +
                ", gift='" + gift + '\'' +
                ", remark='" + remark + '\'' +
                ", isSync=" + isSync +
                '}';
    }
}
