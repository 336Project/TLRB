package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/5/6.
 *
 */
public class VersionInfo extends BaseEn{
    private String version;//版本号
    private String updateContent;//更新内容
    private String apkUrl;//apk下载地址

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
                ", version='" + version + '\'' +
                ", updateContent='" + updateContent + '\'' +
                ", apkUrl=" + apkUrl +
                '}';
    }
}
