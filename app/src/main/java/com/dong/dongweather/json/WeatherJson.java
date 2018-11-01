package com.dong.dongweather.json;

import android.text.TextUtils;
import android.util.Log;

import com.dong.dongweather.gson.HeWeather5;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.LogUtil;

/**
 * Created by Administrator on 2017/4/18.
 */

public class WeatherJson {
    private static final String TAG = "WeatherJson";
    public static HeWeather5 getWeatherResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
                String weateherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weateherContent, HeWeather5.class);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getWeatherResponse: ");
            }
        }
        return null;
    }
}
