package com.ttm.tlrb.ui.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * user:wtw
 * time: 2016/5/19 0019.
 */
public class RedBomb extends DataSupport {
    private long id;//本地数据库id
    @Column(unique = true, defaultValue = "",nullable = false)
    private String userName;//数据关联的数据账号
    private String name;//姓名
    private String time;//时间
    private int target;//男女方（1男方、2女方、3共同）
    private int type;//类型（1收入、2支出）
    private String categoryName;//组别名称（不设置，都为'默认'组别）
    private double money;//金额
    private String gift;//随礼
    private String remark;//备注
    private boolean isSync;//是否同步到服务器上面

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
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

    public boolean isSync() {
        return isSync;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }
}
