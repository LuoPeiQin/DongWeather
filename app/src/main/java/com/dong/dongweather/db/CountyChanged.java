package com.dong.dongweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 44607 on 2017/4/27.
 */

public class CountyChanged extends DataSupport {

    private int id;

    private String addWeatherID;

    private String delCountyPosition;

    private String isSwapCounty;

    public String isSwapCounty() {
        return isSwapCounty;
    }

    public void setSwapCounty(String swapCounty) {
        isSwapCounty = swapCounty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddWeatherID() {
        return addWeatherID;
    }

    public void setAddWeatherID(String addWeatherID) {
        this.addWeatherID = addWeatherID;
    }

    public String getDelCountyPosition() {
        return delCountyPosition;
    }

    public void setDelCountyPosition(String delCountyPosition) {
        this.delCountyPosition = delCountyPosition;
    }
}
