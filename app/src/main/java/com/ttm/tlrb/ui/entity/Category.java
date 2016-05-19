package com.ttm.tlrb.ui.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by 李晓伟 on 2016/5/19.
 *
 */
public class Category extends DataSupport{
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
