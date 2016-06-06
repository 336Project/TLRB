package com.ttm.tlrb.api.e;

/**
 * Created by Helen on 2016/6/6.
 * 用户已经存在 异常
 */
public class UserExistException extends BusinessException{

    public UserExistException(){
        super("user has already exist");
    }

    public UserExistException(String detailMessage) {
        super(detailMessage);
    }
}
