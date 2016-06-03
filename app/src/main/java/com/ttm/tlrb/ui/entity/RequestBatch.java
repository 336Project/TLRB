package com.ttm.tlrb.ui.entity;

/**
 * Created by Helen on 2016/5/26.
 *
 */
@Deprecated
public class RequestBatch extends BaseEn{
    private String method;
    private String path;
    private String body;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
