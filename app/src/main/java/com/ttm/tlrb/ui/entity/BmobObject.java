package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/6/2.
 *
 */
public class BmobObject extends BaseEn{
    private String objectId;
    private String createdAt;
    private String updatedAt;
    private BmobACL ACL;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public BmobACL getACL() {
        return ACL;
    }

    public void setACL(BmobACL ACL) {
        this.ACL = ACL;
    }
}
