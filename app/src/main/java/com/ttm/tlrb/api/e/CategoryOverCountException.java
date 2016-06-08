package com.ttm.tlrb.api.e;

import com.ttm.tlrb.ui.entity.Category;

import java.util.Locale;

/**
 * Created by Helen on 2016/6/8.
 * 分组超出个数限制异常
 */
public class CategoryOverCountException extends BusinessException{

    public CategoryOverCountException() {
        super(String.format(Locale.getDefault(),"the category has out of limit [limit count = %d]", Category.LIMIT_COUNT));
    }
}
