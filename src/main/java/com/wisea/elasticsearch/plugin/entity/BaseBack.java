package com.wisea.elasticsearch.plugin.entity;

import com.wisea.elasticsearch.plugin.exception.BackInfoException;

/**
 * 备份基础信息
 * 
 * @author XuDL(Wisea)
 *
 *         2019年1月30日 下午1:44:02
 */
public class BaseBack {
    /** id */
    private String id;
    /** name */
    private String name;

    public BaseBack(String str) {
        if (null != str) {
            String[] arr = str.split("#");
            if (arr.length != 2) {
                throw new BackInfoException("BaseBack " + str + " formate must be [id#name]");
            }
            this.id = arr[0];
            this.name = arr[1];
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
