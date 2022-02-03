package com.ttm.tlrb.ui.entity;

import com.ttm.tlrb.BuildConfig;

/**
 * Created by Helen on 2016/5/6.
 *
 */
public class VersionInfo extends BaseEn{
    private String version;//版本号
    private String updateContent;//更新内容
    private String path;//下载地址
    private BmobFile file;//下载文件
    private boolean isPatch;//是否是修复包
    private boolean isForce;//是否强制更新

    public boolean getForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public boolean getPatch() {
        return isPatch;
    }

    public void setPatch(boolean patch) {
        isPatch = patch;
    }

    public String getVersion() {
        if(version == null){
            return BuildConfig.VERSION_NAME;
        }
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "version='" + version + '\'' +
                ", updateContent='" + updateContent + '\'' +
                ", path='"+path+'\''+
                ", file=" + file +
                ", isPatch=" + isPatch +
                ", isForce=" + isForce +
                '}';
    }
}
