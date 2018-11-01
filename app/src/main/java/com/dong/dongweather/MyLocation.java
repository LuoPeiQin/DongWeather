package com.dong.dongweather;

import android.app.ActivityManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by 44607 on 2017/4/24.
 * 这个是Android自带API的定位实现
 * 问题：不知道为什么，有的时候能用，有的时候无法使用。
 * 应该和国产手机厂商搞的系统有关
 */

public class MyLocation {

    private static final String TAG = "MyLocation";
    private   LocationManager locationManager;
    private static Location location;
    //经度
    private static double latitude;
    //纬度
    private static double longitude;
    private String returnData = null;
    private Context context;

    public MyLocation (Context context){
        this.context = context;
    }

    public String getLocationInfo() {
        if(locationManager == null){
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 设置为最大精度
        criteria.setAltitudeRequired(false); // 不要求海拔信息
        criteria.setCostAllowed(true);//是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 对电量的要求
        criteria.setBearingRequired(false); // 不要求Bearing信息

        String bestProvider = locationManager.getBestProvider(criteria, true);
        Log.i(TAG, "bestProvider=" + bestProvider);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);//1秒，2米


        LocationProvider locationProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (null == location) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (null == location) {
        location = locationManager.getLastKnownLocation(bestProvider);
        }
        updateWithNewLocation(location);


        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
        return returnData;
    }


    LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(android.location.Location location) {

            if(locationManager != null){
                locationManager.removeUpdates(mLocationListener);
            }
            updateWithNewLocation(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged");

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled");
        }
    };

    private void updateWithNewLocation(Location location){
        if (location != null) {
            latitude = location.getLatitude(); // 经度
            longitude = location.getLongitude(); // 纬度
            //double altitude = location.getAltitude(); // 海拔
            Log.v(TAG, "latitude " + latitude + "  longitude:" + longitude);
            String tempString1 = String.valueOf(latitude);
            String tempString2 = String.valueOf(longitude);
//        returnData = tempString1.substring(0,tempString1.indexOf('.') + 4)
//        + "," + tempString2.substring(0, tempString2.indexOf('.') + 4);
            returnData = tempString2.substring(0,tempString2.indexOf('.') + 4)
                    + "," + tempString1.substring(0, tempString1.indexOf('.') + 4);
            Log.d(TAG, "retrunData :" + returnData);
        }else{
            Log.v(TAG, "don't know location info");
            //Toast.makeText(, "无法获取位置信息", Toast.LENGTH_SHORT).show();
        }

    }
}
