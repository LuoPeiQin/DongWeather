package com.dong.dongweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/4/18.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public class update {
        //本地时间
        public String loc;
    }
//    "basic": {
//        "city": "青岛",
//                "cnty": "中国",
//                "id": "CN101120201",
//                "lat": "36.088000",
//                "lon": "120.343000",
//                "prov": "山东"  //城市所属省份（仅限国内城市）
//        "update": {
//            "loc": "2016-08-30 11:52",
//                    "utc": "2016-08-30 03:52"
//        }
//    },
}
