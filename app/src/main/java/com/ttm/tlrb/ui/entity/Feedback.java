package com.ttm.tlrb.ui.entity;


import com.google.gson.Gson;

/**
 * Created by Helen on 2016/5/5.
 * 用户反馈
 */
public class Feedback extends BaseEn{
    private String content;//反馈内容
    private String contact;//联系方式
    private BmobFile fileLog;//崩溃日志文件

    public BmobFile getFileLog() {
        return fileLog;
    }

    public void setFileLog(BmobFile fileLog) {
        this.fileLog = fileLog;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
