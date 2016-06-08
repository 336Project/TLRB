package com.ttm.tlrb.api.e;

/**
 * Created by Helen on 2016/6/8.
 * 分组已存在异常
 */
public class CategoryExistException extends BusinessException{

    public CategoryExistException(String categoryName){
        super(String.format("the %s category has exist,can't add again",categoryName));
    }
}
