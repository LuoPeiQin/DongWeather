package com.dong.dongweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/4/18.
 */

public class DailyForecast {
    @SerializedName("cond")
    public Cond cond;

    public class Cond{
        @SerializedName("txt_d")
        public String dayWeather;
        @SerializedName("txt_n")
        public String nightWeather;
        public String code_d;
        public String code_n;
    }


    public String date;

    @SerializedName("tmp")
    public Tmp tmp;
    public class Tmp{
        public String max;
        public String min;
    }
//    "daily_forecast": [
//    {
//        "astro": {
//        "mr": "03:09",
//                "ms": "17:06",
//                "sr": "05:28",
//                "ss": "18:29"
//    },
//        "cond": {
//        "code_d": "100",
//                "code_n": "100",
//                "txt_d": "晴",
//                "txt_n": "晴"
//    },
//        "date": "2016-08-30",
//            "hum": "45",
//            "pcpn": "0.0",
//            "pop": "8",
//            "pres": "1005",
//            "tmp": {
//        "max": "29",
//                "min": "22"
//    },
//        "vis": "10",
//            "wind": {
//        "deg": "339",
//                "dir": "北风",
//                "sc": "4-5",
//                "spd": "24"
//    }
//    }
//            ],
}
