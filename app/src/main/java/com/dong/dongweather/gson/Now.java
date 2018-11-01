package com.dong.dongweather.gson;


import com.google.gson.annotations.SerializedName;


/**
 * Created by Administrator on 2017/4/18.
 */

public class Now {

    //天气状况
    @SerializedName("cond")
    public WeatherRegime weatherRegime;
    public class WeatherRegime {
        public String code;
        public String txt;
    }
    //体感温度
    public String fl;
    //相对湿度
    public String hum;
    //降水量
    @SerializedName("pcpn")
    public String mypcpn;
    //气压
    public String pres;
    //温度
    public String tmp;
    //能见度
    public String vis;

    public Wind wind;
    public class Wind {

        //风向（360）度
        public String deg;
        //风向，八个大方向（西北风）
        public String dir;
        //风力等级
        public String sc;
        //风速
        public String spd;
    }
//
//    "now": {  //实况天气
//        "cond": {  //天气状况
//            "code": "104",  //天气状况代码
//                    "txt": "阴"  //天气状况描述
//        },
//        "fl": "11",  //体感温度
//                "hum": "31",  //相对湿度（%）
//                "pcpn": "0",  //降水量（mm）
//                "pres": "1025",  //气压
//                "tmp": "13",  //温度
//                "vis": "10",  //能见度（km）
//                "wind": {  //风力风向
//            "deg": "40",  //风向（360度）
//                    "dir": "东北风",  //风向
//                    "sc": "4-5",  //风力
//                    "spd": "24"  //风速（kmph）
//        }
//    },
}
