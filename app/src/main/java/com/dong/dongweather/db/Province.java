package com.dong.dongweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/4/15.
 * 用于存放省份信息的表
 */

public class Province extends DataSupport {
    private int id;

    private String provinceName;

    private int provinceCode;

    public int getId() {
        return id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {

        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setId(int id) {
        this.id = id;

    }
}
