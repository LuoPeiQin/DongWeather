package com.dong.dongweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/4/18.
 */

public class HeWeather5 {
    public String status;

    public Basic basic;

    public Now now;

    public AQI aqi;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecastList;

    @SerializedName("hourly_forecast")
    public List<HourlyForecast> hourlyForecastList;
}
