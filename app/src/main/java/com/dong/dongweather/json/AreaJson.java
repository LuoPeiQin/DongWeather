package com.dong.dongweather.json;

import android.text.TextUtils;
import android.util.Log;

import com.dong.dongweather.db.City;
import com.dong.dongweather.db.County;
import com.dong.dongweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.LogUtil;

import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * Created by Administrator on 2017/4/17.
 */

/**
 * 获取省数据
 */
public class AreaJson {

    private static final String TAG = "AreaJson";

    public static boolean getProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.setProvinceName(jsonObject.getString("name"));
                    province.save();
                }
            }catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getProvinceResponse: ");
            }
            return true;
        }
        return false;
    }

    /**
     * 获取市数据
     */
    public static boolean getCityJson(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(jsonobject.getInt("id"));
                    city.setCityName(jsonobject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
            } catch (JSONException e){
                e.printStackTrace();
                LogUtil.d(TAG, "getCityJson: error");
            }
            return true;
        }
        return false;
    }

    /**
     * 获取县数据
     */
    public static boolean getCountyJson(String response, int cityId) {
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.save();
                }
            }catch (JSONException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "getCountyJson: error");
            }
            return true;
        }
        return false;
    }
}


