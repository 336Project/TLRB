package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/5/6.
 *
 */
public class VersionInfo extends BaseEn{
    private String version;//版本号
    private String updateContent;//更新内容
    private BmobFile file;//下载文件
    private Boolean isPatch;//是否是修复包
    private Boolean isForce;//是否强制更新

    public Boolean getForce() {
        return isForce;
    }

    public void setForce(Boolean force) {
        isForce = force;
    }

    public Boolean getPatch() {
        return isPatch;
    }

    public void setPatch(Boolean patch) {
        isPatch = patch;
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

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "version='" + version + '\'' +
                ", updateContent='" + updateContent + '\'' +
                ", file=" + file +
                ", isPatch=" + isPatch +
                ", isForce=" + isForce +
                '}';
    }
}
