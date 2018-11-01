package com.dong.dongweather.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dong.dongweather.LogUtil;
import com.dong.dongweather.R;
import com.dong.dongweather.WeatherActivity;
import com.dong.dongweather.WidgetProvider;
import com.dong.dongweather.db.CountyChanged;
import com.dong.dongweather.db.SelectedCounty;
import com.dong.dongweather.gson.HeWeather5;
import com.dong.dongweather.http.OkHttp;
import com.dong.dongweather.json.WeatherJson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListViewService extends RemoteViewsService {

    private static final String TAG = "WEATHER_WIDGET";
    //获取和风天气的key--自己的
    public static final String KEY = "a0187789a4424bc89254728acd4a08ed";
    //获取和风天气的key--作者的
    //public static final String KEY = "bc0418b57b2d4918819d3974ac1285d9";

    public static final String INITENT_DATA = "extra_data";

    //"com.dong.dongweather.WEATHER_WIDGET_UPDATE"
    private List<HeWeather5> heWeather5List;

    private List<SelectedCounty> selectedCountyList;

    private int count = 0;
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;

        private static final String U_COUNTYCHANGED = "content://com.dong.dongweather.WidgetListviewContentProvider/CountyChanged";
        private static final String U_SELECTEDCOUNTY = "content://com.dong.dongweather.WidgetListviewContentProvider/SelectedCounty";

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {
            heWeather5List = new ArrayList<>();
            selectedCountyList = new ArrayList<>();
            selectedCountyList = DataSupport.findAll(SelectedCounty.class);
            Log.d(TAG, "onCreate: selectedCountyList size: " + selectedCountyList.size());
            //查看是否能定位,能定位则添加定位城市到第一位

            //从网上获取相应的天气信息进行保存
            int i = 0;
            for (SelectedCounty selectedCounty : selectedCountyList) {
                requestWeatherAsync(selectedCounty.getWeatherId());
                ++i;
                while (i != count);
            }
            //判断selectedCountyList和heWeather5List长度是否相同
            //这是下策，只是为了保证不出现异常
            //避免网络获取出错的情况
            while (selectedCountyList.size() > heWeather5List.size()) {
                selectedCountyList.remove(selectedCountyList.size()-1);
            }
            //这个时候heWeather5List已经添加完成了
            //清楚数据库数据，以免被影响
            ContentResolver cr = getContentResolver();
            Uri uri = Uri.parse(U_COUNTYCHANGED);
            cr.delete(uri, null, null);
        }

        @Override
        public void onDataSetChanged() {
            //先判断网络状态，如果无网络连接，直接退出
            ConnectivityManager connectivityManager;//用于判断是否有网络
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (null == info) {
                Log.d(TAG, "onDataSetChanged: connect failed");
                return;
             }
            Log.d(TAG, "onDataSetChanged: ");
            //获取contentProvider中的数据
            ContentResolver cr = getContentResolver();
            Uri uri = Uri.parse(U_COUNTYCHANGED);
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    String addWeatherId = cursor.getString(cursor.getColumnIndex("addWeatherID"));
                    String delCountyPosition = cursor.getString(cursor.getColumnIndex("delCountyPosition"));
                    String isSwapCounty = cursor.getString(cursor.getColumnIndex("isSwapCounty"));
                    LogUtil.d(TAG, "onDataSetChanged: isSwapCounty:" + isSwapCounty);
                    if ("changed".equals(isSwapCounty)){
                        Log.d(TAG, "onDataSetChanged: ");
                        selectedCountyList.clear();
                        uri = Uri.parse(U_SELECTEDCOUNTY);
                        Cursor selectedCursor = cr.query(uri, null, null, null, null);
                        if (null != selectedCursor) {
                            for (selectedCursor.moveToFirst(); !selectedCursor.isAfterLast(); selectedCursor.moveToNext()) {
                                SelectedCounty selectedCounty = new SelectedCounty();
                                selectedCounty.setCountyName(selectedCursor.getString(selectedCursor.getColumnIndex("countyName")));
                                selectedCounty.setWeatherId(selectedCursor.getString(selectedCursor.getColumnIndex("weatherId")));
                                selectedCountyList.add(selectedCounty);
                                Log.d(TAG, "onDataSetChanged: selectedCountyList add succeed :" + selectedCounty.getCountyName());
                            }

                            //更新heWeather5List的数据
                            heWeather5List.clear();
                            count = 0;
                            //从网上获取相应的天气信息进行保存
                            int i = 0;
                            for (SelectedCounty selectedCounty : selectedCountyList) {
                                requestWeatherAsync(selectedCounty.getWeatherId());
                                ++i;
                                while (i != count);
                            }
                            //判断selectedCountyList和heWeather5List长度是否相同
                            //这是下策，只是为了保证不出现异常
                            //避免网络获取出错的情况
                            while (selectedCountyList.size() > heWeather5List.size()) {
                                selectedCountyList.remove(selectedCountyList.size()-1);
                            }
                            Log.d(TAG, "onDataSetChanged: heWeather5List Change succeed");
                            //这个时候heWeather5List已经添加完成了
                        }
                    }else if (null != addWeatherId) {
                        //添加城市
                        SelectedCounty selectenCounty = new SelectedCounty();
                        requestWeatherAsync(addWeatherId);
                        selectenCounty.setWeatherId(addWeatherId);
                        selectedCountyList.add(selectenCounty);
                        Log.d(TAG, "onDataSetChanged: add succeed");
                    } else if (null != delCountyPosition) {
                        //删除城市
                        int[] intDelPosition = stringToInt(delCountyPosition);
                        for (int i : intDelPosition) {
                            selectedCountyList.remove(i);
                            heWeather5List.remove(i);
                            Log.d(TAG, "onDataSetChanged: del succeed:" + i);
                        }
                    } else {
                        Log.d(TAG, "onDataSetChanged: cursor size is zero");
                    }
                }
            } else {
                Log.d(TAG, "onDataSetChanged: no Change");
            }
            cr.delete(uri, null, null);
            //用sharedPreference不能跨进程
            //这里改用数据库
//            List<CountyChanged> tempList = DataSupport.findAll(CountyChanged.class);
//            if (tempList.size() > 0) {
//                CountyChanged countyChanged = tempList.get(0);
//                String addWeatherID = countyChanged.getAddWeatherID();
//                int[] delCountyPosition = countyChanged.getDelCountyPosition();
//                if (null != addWeatherID) {
//                    //添加城市
//                    SelectedCounty selectenCounty = new SelectedCounty();
//                    requestWeatherAsync(selectenCounty.getWeatherId());
//                    selectedCountyList.add(selectenCounty);
//                    DataSupport.deleteAll(CountyChanged.class);
//                    Log.d(TAG, "onDataSetChanged: add succeed");
//                } else if (null != delCountyPosition) {
//                    //删除城市
//                    for (int i : delCountyPosition) {
//                        selectedCountyList.remove(i);
//                        heWeather5List.remove(i);
//                        Log.d(TAG, "onDataSetChanged: del succeed:" + i);
//                    }
//                    DataSupport.deleteAll(CountyChanged.class);
//                } else {
//                    Log.d(TAG, "onDataSetChanged: no Change");
//                }
//            }
//            //获取添加或删除信息
//            SharedPreferences.Editor ditor = getSharedPreferences("countyChange", MODE_MULTI_PROCESS).edit();
//            SharedPreferences sp = getSharedPreferences("countyChange", MODE_MULTI_PROCESS);
//            String delPositions = sp.getString("positions", null);
//            String addWeatherId = sp.getString("weatherId", null);
//            if (null != delPositions) {
//                //传过来的参数是删除
//                int[] delIntPostion = StringToInt(delPositions);
//                for (int i : delIntPostion) {
//                    selectedCountyList.remove(i);
//                }
//            } else if (null != addWeatherId) {
//                //传过来的参数是添加
//                SelectedCounty addCounty = new SelectedCounty();
//                addCounty.setWeatherId(addWeatherId);
//                selectedCountyList.add(addCounty);
//            }
//            ditor.clear();
//            //测试
//            SharedPreferences test = getSharedPreferences("countyChange", MODE_PRIVATE);
//            String test1 = test.getString("positions", null);
//            String test2 = test.getString("weatherId", null);
        }

        private int[] stringToInt(String src) {
            int[] returnInt = new int[src.length()];
            for (int i = 0; i < src.length(); ++i) {
                returnInt[i] = src.charAt(i) - 48;
            }
            return  returnInt;
        }
        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy: ");
            selectedCountyList.clear();
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount: " + selectedCountyList.size());
            return selectedCountyList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(TAG, "getViewAt: " + position);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            Log.d(TAG, "getViewAt: RemoteViews Succeed");
            //设置viewlist中的单项的值
            HeWeather5 heWeather5 = heWeather5List.get(position);
            if (null != heWeather5.basic) {
                views.setTextViewText(R.id.widget_countyname_tv, heWeather5.basic.cityName);
            }
            try {
                String filename = heWeather5.now.weatherRegime.code + ".png";
                views.setImageViewBitmap(R.id.widget_weather_im, BitmapFactory.decodeStream(getAssets().open(filename)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            views.setTextViewText(R.id.widget_temperature_tv, heWeather5.now.tmp + "º");
            Log.d(TAG, "getViewAt: add succeed");
            Bundle extras = new Bundle();
            extras.putInt(ListViewService.INITENT_DATA, position);
            Intent skipIntent = new Intent();
            skipIntent.setAction(WidgetProvider.SKIP_COUNTY_WEATHER);
            skipIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widget_item_id, skipIntent);
            return views;

        }

        /**
         *  在更新界面的时候如果耗时就会显示 正在加载... 的默认字样，但是你可以更改这个界面
         * 如果返回null 显示默认界面
         * 否则 加载自定义的，返回RemoteViews
         */
        @Override
        public RemoteViews getLoadingView() {
            Log.d(TAG, "getLoadingView: ");
            return null;
        }

        @Override
        public int getViewTypeCount() {
            Log.d(TAG, "getViewTypeCount: ");
            return 1;
        }

        @Override
        public long getItemId(int position) {
            Log.d(TAG, "getItemId: ");
            return position;
        }

        @Override
        public boolean hasStableIds() {
            Log.d(TAG, "hasStableIds: ");
            return false;
        }
    }

    public void requestWeatherAsync(final String weateherId) {
        Log.d(TAG, "requestWeatherAsync: ");
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city="
                + weateherId + "&key=" + KEY;
        OkHttp.sendRequestOkHttpForGet(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ++count;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                HeWeather5 heWeather5 = WeatherJson.getWeatherResponse(responseText);
                heWeather5List.add(heWeather5);
                if (null != heWeather5.basic) {
                    Log.d(TAG, "onResponse: " + heWeather5.basic.cityName);
                } else {
                    Log.d(TAG, "onResponse: cityName is Null" );
                }

                ++count;
            }
        });

    }

}
