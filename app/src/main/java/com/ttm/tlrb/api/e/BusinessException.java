package com.ttm.tlrb.api.e;

/**
 * Created by Helen on 2016/6/6.
 * 业务异常
 */
public class BusinessException extends RuntimeException{

    public BusinessException(String detailMessage) {
        super(detailMessage);
    }
}
