package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/5/6.
 *
 */
public class VersionInfo extends BaseEn{
    private String objectId;
    private String version;
    private String updateContent;
    private String apkUrl;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdateContent() {
        if(updateContent == null){
            return "";
        }
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "objectId='" + objectId + '\'' +
                ", version='" + version + '\'' +
                ", updateContent='" + updateContent + '\'' +
                ", apkUrl=" + apkUrl +
                '}';
    }
}
