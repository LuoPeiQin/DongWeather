package com.dong.dongweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/4/18.
 */
    //当天每小时天气预报
    //因为未付费，只有三个小时的数据
public class HourlyForecast {
    //天气状况
    @SerializedName("cond")
    public WeatherRegime weatherRegime;
    public class WeatherRegime {
        public String code;
        public String txt;
    }
    //时间：
    public String date;
    public String tmp;

//    "hourly_forecast": [ //当天每小时天气预报
//    {
//        "cond": { //天气状况
//        "code": "100",  //天气状况代码
//                "txt": "晴"  //天气状况描述
//    },
//        "date": "2016-08-31 12:00",  //时间
//            "hum": "21",  //相对湿度（%）
//            "pop": "0",  //降水概率
//            "pres": "998",  //气压
//            "tmp": "33",  //温度
//            "wind": {  //风力风向
//        "deg": "40",  //风向（360度）
//                "dir": "东北风",  //风向
//                "sc": "4-5",  //风力
//                "spd": "24"  //风速（kmph）
//    }
//    }
//            ],
}
