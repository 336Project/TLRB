package com.ttm.tlrb.ui.entity;

import com.google.gson.Gson;

/**
 * 红包更新的时候使用到
 * user:wtw
 * time: 2016/5/19 0019.
 */
public class UpdateRedBomb {
    private String userName;//数据关联的数据账号
    private String name;//姓名
    private String time;//时间
    private Integer target;//男女方（1男方、2女方、3共同）
    private Integer type;//类型（1收入、2支出）
    private String categoryName = "默认";//组别名称（不设置，都为'默认'组别）
    private Double money;//金额
    private String gift;//随礼
    private String remark;//备注

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

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
