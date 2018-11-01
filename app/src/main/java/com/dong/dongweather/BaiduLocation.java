package com.dong.dongweather;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by 44607 on 2017/4/25.
 */

public class BaiduLocation {
    private LocationClient client = null;
    private LocationClientOption mOption,DIYoption;
    private Object  objLock = new Object();

    /***
     *
     * @param locationContext
     */
    public BaiduLocation(Context locationContext){
        synchronized (objLock) {
            if(client == null){
                client = new LocationClient(locationContext);
                client.setLocOption(getDefaultLocationClientOption());
            }
        }
    }

    /***
     *
     * @param listener
     * @return
     */

    public boolean registerListener(BDLocationListener listener){
        boolean isSuccess = false;
        if(listener != null){
            client.registerLocationListener(listener);
            isSuccess = true;
        }
        return  isSuccess;
    }

    public void unregisterListener(BDLocationListener listener){
        if(listener != null){
            client.unRegisterLocationListener(listener);
        }
    }

    /***
     *
     * @param option
     * @return isSuccessSetOption
     */
    public boolean setLocationOption(LocationClientOption option){
        boolean isSuccess = false;
        if(option != null){
            if(client.isStarted())
                client.stop();
            DIYoption = option;
            client.setLocOption(option);
        }
        return isSuccess;
    }

    public LocationClientOption getOption(){
        return DIYoption;
    }
    /***
     *
     * @return DefaultLocationClientOption
     */
    public LocationClientOption getDefaultLocationClientOption(){
        if(mOption == null){
            mOption = new LocationClientOption();
            mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            //mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(60*60*1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//            mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
//            mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
//            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//            mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//
//            mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        }
        return mOption;
    }

    public void start(){
        synchronized (objLock) {
            if(client != null && !client.isStarted()){
                client.start();
            }
        }
    }
    public void stop(){
        synchronized (objLock) {
            if(client != null && client.isStarted()){
                client.stop();
            }
        }
    }

    public boolean requestHotSpotState(){

        return client.requestHotSpotState();

    }
//    public static final int BAIDU_LOCATION_EVENT = 1;
//    private final static boolean DEBUG = true;
//    private final static String TAG = "BaiduLocation";
//    private static BaiduLocation mInstance;
//    private BDLocation mLocation = null;
//    public CurrentLocation  currentLocation = new CurrentLocation();
//    public BDLocationListener myListener = new MyLocationListener();
//    private LocationClient mLocationClient;
//
//    public static BaiduLocation getInstance(Context context) {
//        if (mInstance == null) {
//            mInstance = new BaiduLocation(context);
//        }
//        return mInstance;
//    }
//
//    private BaiduLocation(Context context) {
//        mLocationClient = new LocationClient(context);
//        initParams();
//        mLocationClient.registerLocationListener(myListener);
//        startMonitor();
//    }
//
//    public void startMonitor() {
//        if (DEBUG) Log.d(TAG, "start monitor location");
//        if (!mLocationClient.isStarted()) {
//            mLocationClient.start();
//        }
//        while (mLocationClient != null && mLocationClient.isStarted()) {
//            mLocationClient.requestLocation();
//        }
////        else {
////            Log.d("LocSDK3", "locClient is null or not started");
////        }
//    }
//
//    public void stopMonitor() {
//        if (DEBUG) Log.d(TAG, "stop monitor location");
//        if (mLocationClient != null && mLocationClient.isStarted()) {
//            mLocationClient.stop();
//        }
//    }
//
//    public BDLocation getLocation() {
//        if (DEBUG) Log.d(TAG, "get location");
//        return mLocation;
//    }
//
//    public CurrentLocation getBaseLocation() {
//        if (DEBUG) Log.d(TAG, "get location");
//        return currentLocation;
//    }
//
//    private void initParams() {
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true);
//        option.setPriority(LocationClientOption.NetWorkFirst);
//        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
//        option.setScanSpan(60*60*1000);//设置发起定位请求的间隔时间为5000ms
//        option.disableCache(false);//禁止启用缓存定位
//        mLocationClient.setLocOption(option);
//    }
//
//
//    public class MyLocationListener implements BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            if (location == null) {
//                return ;
//            }
//            mLocation = location;
//            currentLocation.latitude = mLocation.getLatitude();
//            currentLocation.longitude = mLocation.getLongitude();
//            String tempString1 = String.valueOf(currentLocation.latitude);
//            String tempString2 = String.valueOf(currentLocation.longitude);
////        returnData = tempString1.substring(0,tempString1.indexOf('.') + 4)
////        + "," + tempString2.substring(0, tempString2.indexOf('.') + 4);
//            currentLocation.weatherId = tempString2.substring(0,tempString2.indexOf('.') + 4)
//                    + "," + tempString1.substring(0, tempString1.indexOf('.') + 4);
////            Message message = new Message();
////            message.what = BAIDU_LOCATION_EVENT;
////            WeatherActivity.handler.sendMessage(message);
////            WeatherActivity.isBDLocationOk = true;
//
//            Log.d(TAG, "retrunData :" + currentLocation.weatherId);
//
//            StringBuffer sb = new StringBuffer(256);
//            sb.append("time : ");
//            sb.append(location.getTime());
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());
//            sb.append("\nlatitude : ");
//            sb.append(location.getLatitude());
//            sb.append("\nlontitude : ");
//            sb.append(location.getLongitude());
//            sb.append("\nradius : ");
//            sb.append(location.getRadius());
//            sb.append("\ncity : ");
//            sb.append(location.getCity());
//            if (location.getLocType() == BDLocation.TypeGpsLocation){
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
//            }
//            if (DEBUG) Log.d(TAG, "" + sb);
//            //只需要定位一次就可以了
//            closeLocationClient();
//        }
//
//        @Override
//        public void onConnectHotSpotMessage(String s, int i) {
//
//        }
//
//    }
//
//    public class CurrentLocation {
//        public double latitude;
//        public double longitude;
//        public String weatherId;
//    }
//
//    public void closeLocationClient()
//    {
//        mLocationClient.stop();
//        mLocationClient.unRegisterLocationListener(myListener);
//        mLocationClient = null;
//        myListener = null;
//    }
}
