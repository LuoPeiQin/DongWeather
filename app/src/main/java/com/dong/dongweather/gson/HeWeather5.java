package com.dong.dongweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HeWeather5 {

    /**
     * aqi : {"city":{"aqi":"15","qlty":"优","pm25":"6","pm10":"15","no2":"11","so2":"5","co":"0.4","o3":"47"}}
     * basic : {"city":"深圳","cnty":"中国","id":"CN101280601","lat":"22.54700089","lon":"114.08594513","update":{"loc":"2019-06-20 16:54","utc":"2019-06-20 08:54"}}
     * daily_forecast : [{"astro":{"mr":"21:40","ms":"07:59","sr":"05:39","ss":"19:10"},"cond":{"code_d":"101","code_n":"101","txt_d":"多云","txt_n":"多云"},"date":"2019-06-20","hum":"82","pcpn":"1.1","pop":"55","pres":"992","tmp":{"max":"32","min":"27"},"uv":"5","vis":"24","wind":{"deg":"-1","dir":"无持续风向","sc":"1-2","spd":"8"}},{"astro":{"mr":"22:23","ms":"08:51","sr":"05:40","ss":"19:11"},"cond":{"code_d":"101","code_n":"101","txt_d":"多云","txt_n":"多云"},"date":"2019-06-21","hum":"82","pcpn":"2.6","pop":"57","pres":"992","tmp":{"max":"33","min":"28"},"uv":"6","vis":"20","wind":{"deg":"-1","dir":"无持续风向","sc":"1-2","spd":"6"}},{"astro":{"mr":"23:02","ms":"09:44","sr":"05:40","ss":"19:11"},"cond":{"code_d":"101","code_n":"101","txt_d":"多云","txt_n":"多云"},"date":"2019-06-22","hum":"77","pcpn":"2.4","pop":"56","pres":"995","tmp":{"max":"33","min":"28"},"uv":"6","vis":"12","wind":{"deg":"-1","dir":"无持续风向","sc":"1-2","spd":"6"}}]
     * now : {"cond":{"code":"101","txt":"多云"},"fl":"33","hum":"75","pcpn":"0.0","pres":"998","tmp":"31","vis":"16","wind":{"deg":"242","dir":"西南风","sc":"3","spd":"19"}}
     * status : ok
     * suggestion : {"air":{"brf":"中","txt":"气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。"},"comf":{"brf":"较不舒适","txt":"白天天气多云，并且空气湿度偏大，在这种天气条件下，您会感到有些闷热，不很舒适。"},"cw":{"brf":"较适宜","txt":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"},"drsg":{"brf":"炎热","txt":"天气炎热，建议着短衫、短裙、短裤、薄型T恤衫等清凉夏季服装。"},"flu":{"brf":"少发","txt":"各项气象条件适宜，发生感冒机率较低。但请避免长期处于空调房间中，以防感冒。"},"sport":{"brf":"较适宜","txt":"天气较好，较适宜进行各种运动，但考虑气温较高且湿度较大，请适当降低运动强度，并及时补充水分。"},"trav":{"brf":"适宜","txt":"天气较好，但丝毫不会影响您的心情。微风，虽天气稍热，却仍适宜旅游，不要错过机会呦！"},"uv":{"brf":"中等","txt":"属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。"}}
     */

    public AqiBean aqi;
    public BasicBean basic;
    public NowBean now;
    public String status;
    public SuggestionBean suggestion;
    @SerializedName("daily_forecast")
    public List<DailyForecastBean> dailyForecastList;

    @SerializedName("hourly_forecast")
    public List<HourlyForecast> hourlyForecastList;

    public List<DailyForecastBean> getDailyForecastList() {
        return dailyForecastList;
    }

    public void setDailyForecastList(List<DailyForecastBean> dailyForecastList) {
        this.dailyForecastList = dailyForecastList;
    }

    public AqiBean getAqi() {
        return aqi;
    }

    public void setAqi(AqiBean aqi) {
        this.aqi = aqi;
    }

    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
    }

    public NowBean getNow() {
        return now;
    }

    public void setNow(NowBean now) {
        this.now = now;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuggestionBean getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(SuggestionBean suggestion) {
        this.suggestion = suggestion;
    }

    public static class AqiBean {
        /**
         * city : {"aqi":"15","qlty":"优","pm25":"6","pm10":"15","no2":"11","so2":"5","co":"0.4","o3":"47"}
         */

        public CityBean city;

        public CityBean getCity() {
            return city;
        }

        public void setCity(CityBean city) {
            this.city = city;
        }

        public static class CityBean {
            /**
             * aqi : 15
             * qlty : 优
             * pm25 : 6
             * pm10 : 15
             * no2 : 11
             * so2 : 5
             * co : 0.4
             * o3 : 47
             */

            public String aqi;
            public String qlty;
            public String pm25;
            public String pm10;
            public String no2;
            public String so2;
            public String co;
            public String o3;

            public String getAqi() {
                return aqi;
            }

            public void setAqi(String aqi) {
                this.aqi = aqi;
            }

            public String getQlty() {
                return qlty;
            }

            public void setQlty(String qlty) {
                this.qlty = qlty;
            }

            public String getPm25() {
                return pm25;
            }

            public void setPm25(String pm25) {
                this.pm25 = pm25;
            }

            public String getPm10() {
                return pm10;
            }

            public void setPm10(String pm10) {
                this.pm10 = pm10;
            }

            public String getNo2() {
                return no2;
            }

            public void setNo2(String no2) {
                this.no2 = no2;
            }

            public String getSo2() {
                return so2;
            }

            public void setSo2(String so2) {
                this.so2 = so2;
            }

            public String getCo() {
                return co;
            }

            public void setCo(String co) {
                this.co = co;
            }

            public String getO3() {
                return o3;
            }

            public void setO3(String o3) {
                this.o3 = o3;
            }
        }
    }

    public static class BasicBean {
        /**
         * city : 深圳
         * cnty : 中国
         * id : CN101280601
         * lat : 22.54700089
         * lon : 114.08594513
         * update : {"loc":"2019-06-20 16:54","utc":"2019-06-20 08:54"}
         */
        @SerializedName("city")
        public String cityName;

        @SerializedName("id")
        public String weatherId;
        public String cnty;
        public String lat;
        public String lon;
        public UpdateBean update;

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getWeatherId() {
            return weatherId;
        }

        public void setWeatherId(String weatherId) {
            this.weatherId = weatherId;
        }

        public String getCnty() {
            return cnty;
        }

        public void setCnty(String cnty) {
            this.cnty = cnty;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public UpdateBean getUpdate() {
            return update;
        }

        public void setUpdate(UpdateBean update) {
            this.update = update;
        }

        public static class UpdateBean {
            /**
             * loc : 2019-06-20 16:54
             * utc : 2019-06-20 08:54
             */

            public String loc;
            public String utc;

            public String getLoc() {
                return loc;
            }

            public void setLoc(String loc) {
                this.loc = loc;
            }

            public String getUtc() {
                return utc;
            }

            public void setUtc(String utc) {
                this.utc = utc;
            }
        }
    }

    public static class NowBean {
        /**
         * cond : {"code":"101","txt":"多云"}
         * fl : 33
         * hum : 75
         * pcpn : 0.0
         * pres : 998
         * tmp : 31
         * vis : 16
         * wind : {"deg":"242","dir":"西南风","sc":"3","spd":"19"}
         */

        public CondBean cond;
        public String fl;
        public String hum;
        public String pcpn;
        public String pres;
        public String tmp;
        public String vis;
        public WindBean wind;

        public CondBean getCond() {
            return cond;
        }

        public void setCond(CondBean cond) {
            this.cond = cond;
        }

        public String getFl() {
            return fl;
        }

        public void setFl(String fl) {
            this.fl = fl;
        }

        public String getHum() {
            return hum;
        }

        public void setHum(String hum) {
            this.hum = hum;
        }

        public String getPcpn() {
            return pcpn;
        }

        public void setPcpn(String pcpn) {
            this.pcpn = pcpn;
        }

        public String getPres() {
            return pres;
        }

        public void setPres(String pres) {
            this.pres = pres;
        }

        public String getTmp() {
            return tmp;
        }

        public void setTmp(String tmp) {
            this.tmp = tmp;
        }

        public String getVis() {
            return vis;
        }

        public void setVis(String vis) {
            this.vis = vis;
        }

        public WindBean getWind() {
            return wind;
        }

        public void setWind(WindBean wind) {
            this.wind = wind;
        }

        public static class CondBean {
            /**
             * code : 101
             * txt : 多云
             */

            public String code;
            public String txt;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }

        public static class WindBean {
            /**
             * deg : 242
             * dir : 西南风
             * sc : 3
             * spd : 19
             */

            public String deg;
            public String dir;
            public String sc;
            public String spd;

            public String getDeg() {
                return deg;
            }

            public void setDeg(String deg) {
                this.deg = deg;
            }

            public String getDir() {
                return dir;
            }

            public void setDir(String dir) {
                this.dir = dir;
            }

            public String getSc() {
                return sc;
            }

            public void setSc(String sc) {
                this.sc = sc;
            }

            public String getSpd() {
                return spd;
            }

            public void setSpd(String spd) {
                this.spd = spd;
            }
        }
    }

    public static class SuggestionBean {
        /**
         * air : {"brf":"中","txt":"气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。"}
         * comf : {"brf":"较不舒适","txt":"白天天气多云，并且空气湿度偏大，在这种天气条件下，您会感到有些闷热，不很舒适。"}
         * cw : {"brf":"较适宜","txt":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"}
         * drsg : {"brf":"炎热","txt":"天气炎热，建议着短衫、短裙、短裤、薄型T恤衫等清凉夏季服装。"}
         * flu : {"brf":"少发","txt":"各项气象条件适宜，发生感冒机率较低。但请避免长期处于空调房间中，以防感冒。"}
         * sport : {"brf":"较适宜","txt":"天气较好，较适宜进行各种运动，但考虑气温较高且湿度较大，请适当降低运动强度，并及时补充水分。"}
         * trav : {"brf":"适宜","txt":"天气较好，但丝毫不会影响您的心情。微风，虽天气稍热，却仍适宜旅游，不要错过机会呦！"}
         * uv : {"brf":"中等","txt":"属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。"}
         */

        public AirBean air;

        //天气适宜
        @SerializedName("comf")
        public Comfort comfort;
        public class Comfort {
            //天气大致适宜情况
            public String brf;
            //具体的天气信息内容
            public String txt;
        }
        // 洗车指数
        @SerializedName("cw")
        public CarWash carWash;
        public class CarWash {
            public String brf;
            public String txt;
        }
        //穿衣指数
        @SerializedName("drsg")
        public Hot hot;
        public class Hot {
            public String brf;
            public String txt;
        }
        //感冒指数
        @SerializedName("flu")
        public Flu flu;
        public class Flu {
            public String brf;
            public String txt;
        }
        //运动指数
        @SerializedName("sport")
        public Sport sport;
        public class Sport {
            public String brf;
            public String txt;
        }
        //旅游指数
        @SerializedName("trav")
        public Suitable suitableTrav;
        public class Suitable {
            public String brf;
            public String txt;
        }
        //紫外线指数
        @SerializedName("uv")
        public Radiation radiation;
        public class Radiation {
            public String brf;
            public String txt;
        }
        public AirBean getAir() {
            return air;
        }

        public void setAir(AirBean air) {
            this.air = air;
        }

        public static class AirBean {
            /**
             * brf : 中
             * txt : 气象条件对空气污染物稀释、扩散和清除无明显影响，易感人群应适当减少室外活动时间。
             */

            public String brf;
            public String txt;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }

        public static class ComfBean {
            /**
             * brf : 较不舒适
             * txt : 白天天气多云，并且空气湿度偏大，在这种天气条件下，您会感到有些闷热，不很舒适。
             */

            public String brf;
            public String txt;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }

        public static class CwBean {
            /**
             * brf : 较适宜
             * txt : 较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。
             */

            public String brf;
            public String txt;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }

        public static class DrsgBean {
            /**
             * brf : 炎热
             * txt : 天气炎热，建议着短衫、短裙、短裤、薄型T恤衫等清凉夏季服装。
             */

            public String brf;
            public String txt;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }

        public static class FluBean {
            /**
             * brf : 少发
             * txt : 各项气象条件适宜，发生感冒机率较低。但请避免长期处于空调房间中，以防感冒。
             */

            public String brf;
            public String txt;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }

        public static class SportBean {
            /**
             * brf : 较适宜
             * txt : 天气较好，较适宜进行各种运动，但考虑气温较高且湿度较大，请适当降低运动强度，并及时补充水分。
             */

            public String brf;
            public String txt;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }

        public static class TravBean {
            /**
             * brf : 适宜
             * txt : 天气较好，但丝毫不会影响您的心情。微风，虽天气稍热，却仍适宜旅游，不要错过机会呦！
             */

            public String brf;
            public String txt;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }

        public static class UvBean {
            /**
             * brf : 中等
             * txt : 属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。
             */

            public String brf;
            public String txt;

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }
        }
    }

    public static class DailyForecastBean {
        /**
         * astro : {"mr":"21:40","ms":"07:59","sr":"05:39","ss":"19:10"}
         * cond : {"code_d":"101","code_n":"101","txt_d":"多云","txt_n":"多云"}
         * date : 2019-06-20
         * hum : 82
         * pcpn : 1.1
         * pop : 55
         * pres : 992
         * tmp : {"max":"32","min":"27"}
         * uv : 5
         * vis : 24
         * wind : {"deg":"-1","dir":"无持续风向","sc":"1-2","spd":"8"}
         */

        public AstroBean astro;
        public CondBeanX cond;
        public String date;
        public String hum;
        public String pcpn;
        public String pop;
        public String pres;
        public TmpBean tmp;
        public String uv;
        public String vis;
        public WindBeanX wind;

        public AstroBean getAstro() {
            return astro;
        }

        public void setAstro(AstroBean astro) {
            this.astro = astro;
        }

        public CondBeanX getCond() {
            return cond;
        }

        public void setCond(CondBeanX cond) {
            this.cond = cond;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getHum() {
            return hum;
        }

        public void setHum(String hum) {
            this.hum = hum;
        }

        public String getPcpn() {
            return pcpn;
        }

        public void setPcpn(String pcpn) {
            this.pcpn = pcpn;
        }

        public String getPop() {
            return pop;
        }

        public void setPop(String pop) {
            this.pop = pop;
        }

        public String getPres() {
            return pres;
        }

        public void setPres(String pres) {
            this.pres = pres;
        }

        public TmpBean getTmp() {
            return tmp;
        }

        public void setTmp(TmpBean tmp) {
            this.tmp = tmp;
        }

        public String getUv() {
            return uv;
        }

        public void setUv(String uv) {
            this.uv = uv;
        }

        public String getVis() {
            return vis;
        }

        public void setVis(String vis) {
            this.vis = vis;
        }

        public WindBeanX getWind() {
            return wind;
        }

        public void setWind(WindBeanX wind) {
            this.wind = wind;
        }

        public static class AstroBean {
            /**
             * mr : 21:40
             * ms : 07:59
             * sr : 05:39
             * ss : 19:10
             */

            public String mr;
            public String ms;
            public String sr;
            public String ss;

            public String getMr() {
                return mr;
            }

            public void setMr(String mr) {
                this.mr = mr;
            }

            public String getMs() {
                return ms;
            }

            public void setMs(String ms) {
                this.ms = ms;
            }

            public String getSr() {
                return sr;
            }

            public void setSr(String sr) {
                this.sr = sr;
            }

            public String getSs() {
                return ss;
            }

            public void setSs(String ss) {
                this.ss = ss;
            }
        }

        public static class CondBeanX {
            /**
             * code_d : 101
             * code_n : 101
             * txt_d : 多云
             * txt_n : 多云
             */
            @SerializedName("txt_d")
            public String dayWeather;
            @SerializedName("txt_n")
            public String nightWeather;
            public String code_d;
            public String code_n;

            public String getCode_d() {
                return code_d;
            }

            public void setCode_d(String code_d) {
                this.code_d = code_d;
            }

            public String getCode_n() {
                return code_n;
            }

            public void setCode_n(String code_n) {
                this.code_n = code_n;
            }

            public String getDayWeather() {
                return dayWeather;
            }

            public void setDayWeather(String dayWeather) {
                this.dayWeather = dayWeather;
            }

            public String getNightWeather() {
                return nightWeather;
            }

            public void setNightWeather(String nightWeather) {
                this.nightWeather = nightWeather;
            }
        }

        public static class TmpBean {
            /**
             * max : 32
             * min : 27
             */

            public String max;
            public String min;

            public String getMax() {
                return max;
            }

            public void setMax(String max) {
                this.max = max;
            }

            public String getMin() {
                return min;
            }

            public void setMin(String min) {
                this.min = min;
            }
        }

        public static class WindBeanX {
            /**
             * deg : -1
             * dir : 无持续风向
             * sc : 1-2
             * spd : 8
             */

            public String deg;
            public String dir;
            public String sc;
            public String spd;

            public String getDeg() {
                return deg;
            }

            public void setDeg(String deg) {
                this.deg = deg;
            }

            public String getDir() {
                return dir;
            }

            public void setDir(String dir) {
                this.dir = dir;
            }

            public String getSc() {
                return sc;
            }

            public void setSc(String sc) {
                this.sc = sc;
            }

            public String getSpd() {
                return spd;
            }

            public void setSpd(String spd) {
                this.spd = spd;
            }
        }
    }
}
