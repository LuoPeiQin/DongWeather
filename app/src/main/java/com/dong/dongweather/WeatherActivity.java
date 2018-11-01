package com.dong.dongweather;

import android.Manifest;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.dong.dongweather.db.SelectedCounty;
import com.dong.dongweather.gson.DailyForecast;
import com.dong.dongweather.gson.HeWeather5;
import com.dong.dongweather.gson.HourlyForecast;
import com.dong.dongweather.http.MyCallBack;
import com.dong.dongweather.http.MyHttp;
import com.dong.dongweather.http.OkHttp;
import com.dong.dongweather.json.WeatherJson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final boolean DEBUG = true;

    private static final String TAG = "WeatherActivity";

    //获取和风天气的key---自己的
    public static final String KEY = "a0187789a4424bc89254728acd4a08ed";
    //获取和风天气的key---作者的
    //public static final String KEY = "bc0418b57b2d4918819d3974ac1285d9";

    //提供给AddCountyActivity活动判断是以哪种方式进入它的
    //这边的实现是直接用静态的方式跨文件使用的，有点像全局变量了，
    // 不太好，以后些这些的时候使用intent，sharedPreference传数据
    public static final int INMODE_DIRECT = 1;
    public static final int INMODE_INDIRECT = 2;
    public static int INMODE = 0;

    //定位成功Toast提示msg
    private final int TOAST_LOCATION_SUCCEED = 1;

    //定位失败提醒次数，提醒两次后就不再提醒
    private static int attentionTimes = 1;

    //是否第一次加载的标志，用于第一次触碰屏幕时的加载天气数据和viewPager页数
    //为了使得开启app时响应速度更快
    private static boolean keyForFirstIn = true;

    //刷新界面的控件
    public SwipeRefreshLayout swipeRefreshLayout;

    //当前城市天气ID，实现下拉刷新功能
    private String currentWeatherId;

    //已经选择的城市列表
    public static List<SelectedCounty> selectedCountisList;

    //定位城市的天气Id和城市名
    public static String locationCountyWeatherId = null;
    public static String locationCountyWeatherName = null;

    //ui切换的提示，进度加载
    private ProgressDialog progressDialog;

    //图片更新
    private ImageView bingPicIv;
    //viewPager切换时保存当前天气view
    private View currentView;
    /**
     * 天气的整体信息
     */
    //title中的控件
    private Button manageCityBtn;
    private TextView titleText;
    //now_weather中的控件
    private TextView nowTemperatureTV;
    private TextView nowDayWeatherQltyTV;
    private TextView nowToady;
    private TextView nowMinMaxTemperature;
    //hourlyWeather中的控件的声明
    private RecyclerView hourlyRecycler;
    private LinearLayoutManager layoutManager;
    private HourlyWeatherAdapter hourlyWeatherAdapter;
    private List<HourlyWeather> hourlyWeatherList;
    //dailyWeather中的控件的声明
    private LinearLayout dailyForecastLayout;
    private TextView dailyDate;
    private TextView dailyWeather;
    private ImageView dailyWeatherImage;
    private TextView dailyTemperature;
    //weather_index中的控件的声明
    private TextView weatherSendibleTemperatureTv;
    private TextView weatherHumitidyTv;
    private TextView weatherVisibilityTv;
    private TextView weatherRiskLevelTv;
    private TextView weatherPrecipitationTv;
    private TextView weatherPressureTv;
    //suggestion中的控件声明
    private TextView suggestionComfort;
    private TextView suggestionCarwash;
    private TextView suggestionSport;
    private TextView suggestionDressingIndex;

    //导航栏设置（圆点导航栏）
    private List<ImageView> guideShapeViewArrayList;
    private ImageView guideShapeViewIv;

    //请求返回结果的代码的常量定义，相当于全局变量，以后少用
    public static final int ADDCOUNTYACTIVITY_RETURN = 1;
    public static final int CHOOSEAREAACTIVITY_RETURN = 2;

    //判断百度是否完成调用，因为百度定位是异步实现的，
    // 主线程用不能停下来等他，所以这个暂时没有处理好解决
    public static boolean isBDLocationOk = false;

    //定位返回指针
    BaiduLocation baiduLocation;

    //定位城市是否被删除
    public static boolean isLocationCountyRemove = false;
    //定位城市页面是否已经生成
    private boolean isAddLocationView = false;

    /**
     * 和viewpager有关的控件声明
     */
    private ViewPager vp;
    private List<View> viewList;
    PagerAdapter pagerAdapter;

    //天气缓存数据，用于断网时的显示，以免断网时界面很丑
    private Set<String> weatherBufferSet;
    private List<HeWeather5> HeatherBufferList;

    //滑动切换时记录当前的页面的position，相当于下标
    private int currentPosition;

    //记录已经保存的个数（用于实现主线程和异步线程的同步）
    private int iCount = 0;

    //从窗口插件传来的position，由于为了使打开app的响应速度更快，这个功能暂未实现
    private int widgetStartPosition;

    //是否第一次进入onReceiveLocation函数
    private boolean isFirstonReceiveLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Log.d(TAG, "onCreate: ");
        //从窗口程序点击进入到天气程序时获取点击的城市信息，已升级到
        //为了加快开始程序时的响应速度，这个功能暂时未实现，选择直接定位在第一页
        Intent getStartIntent = getIntent();
        widgetStartPosition = getStartIntent.getIntExtra("skipPosition", -1);

        viewList = new ArrayList<>();
        bingPicIv = (ImageView) findViewById(R.id.weather_backgroud_imageview);
        guideShapeViewArrayList = new ArrayList<>();
        //天气缓存数据的定义
        HeatherBufferList = new ArrayList<>();
        weatherBufferSet = new HashSet();
        hourlyWeatherList = new ArrayList<HourlyWeather>();
        vp = (ViewPager) findViewById(R.id.viewpager);

        initView();
    }

    /**
     * 界面初始化
     */
    public void initView() {
        //先申请定位需要用到的权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //没有权限则申请
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            //有权限开启定位功能，异步
            baiduLocation = new BaiduLocation(getApplicationContext());
            //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
            baiduLocation.registerListener(mListener);
            //注册监听
            baiduLocation.setLocationOption(baiduLocation.getDefaultLocationClientOption());
            baiduLocation.start();
            //显示进度条
            LogUtil.d(TAG, "initView: baiduLocation worked");
        }
        //防止界面无响应，显示进度圈
        showProgressDialog();
        //测试需要，防止出现异常之后无法恢复
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.clear();

        //从数据库获取已经选择了的城市列表
        selectedCountisList = DataSupport.findAll(SelectedCounty.class);
        //如果定位城市成功，则先显示定位城市
        //因为百度定位有延迟回调的时间，所以这里先读上一次的位置信息
        //每次启动百度定位都会更新定位信息
        SharedPreferences sp = getSharedPreferences("location", MODE_PRIVATE);
        locationCountyWeatherId = sp.getString("locationWeatherId", null);
        if (DEBUG) LogUtil.d(TAG, "initView: locationCountyWeatherName:" + locationCountyWeatherName);
        if (isNetworkConnected(WeatherActivity.this) && null != locationCountyWeatherId && null != locationCountyWeatherName) {
            SelectedCounty selectedCounty = new SelectedCounty();
            selectedCounty.setWeatherId(locationCountyWeatherId);
            selectedCounty.setCountyName(locationCountyWeatherName);
            selectedCountisList.add(0, selectedCounty);
        }
        if (selectedCountisList.size() > 0) {
            //如果存在已选择的城市，则显示已选择城市的信息
            LayoutInflater layoutInflater = getLayoutInflater();
            //为了加快打开app的响应速度，只先加载一个界面
            viewList.add(viewList.size(), (View) layoutInflater.inflate(R.layout.weather_fragment, null));
            isAddLocationView = true;
        } else {
            //如果以选择城市列表无信息，则跳转到城市添加界面
            INMODE = INMODE_DIRECT;
            Intent intent = new Intent(this, AddCountyActivity.class);
            startActivityForResult(intent, ADDCOUNTYACTIVITY_RETURN);
        }
        //启动图片更新,异步
        updateBingPic();

        //初始化导航页圆点
        initGuideView();

        pagerAdapter = new PagerAdapter() {
            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
                //super.destroyItem(container, position, object);
                //container.removeView(viewList.get(position));
                //container.removeViewAt(position);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Log.d(TAG, "instantiateItem: position = " + position);
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };

        //设置初始坐标
        currentPosition = 0;
        vp.addOnPageChangeListener(this);
        vp.setAdapter(pagerAdapter);

        //为了使得开始的响应速度较快，先只显示一个界面
        if (!isNetworkConnected(WeatherActivity.this)) {
            //无网络情况
            SharedPreferences sp1 = getSharedPreferences("weather_buffer", MODE_PRIVATE);
            weatherBufferSet = sp1.getStringSet("weatherBuffer", null);
            if (null != weatherBufferSet) {
                Log.d( TAG, "initView: weatherBufferSet.size = " + weatherBufferSet.size() );
                for (String response : weatherBufferSet) {
                    final HeWeather5 heWeather5 = WeatherJson.getWeatherResponse(response);
                    HeatherBufferList.add(heWeather5);
                }
                closeProgressDialog();
                if (null != HeatherBufferList && HeatherBufferList.size() > 0)
                showWeatherInfo(HeatherBufferList.get(0));
            }
        } else if (selectedCountisList.size() > 0) {
            //有网的情况
            requestWeatherAsync(currentWeatherId = selectedCountisList.get(0).getWeatherId());
        } else {
            if (DEBUG) LogUtil.d(TAG, "initView: selectedCountistList.size error");
        }
        //有网络图片Uri就加载，无网络图片Uri就显示本地图片
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(WeatherActivity.this).load(bingPic).into(bingPicIv);
        } else {
            bingPicIv.setImageResource(R.drawable.bg);
        }
    }

    /**
     * 百度定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (DEBUG) LogUtil.d(TAG, "onReceiveLocation: start");
            if (isFirstonReceiveLocation) {
                isFirstonReceiveLocation = false;
            } else {
                return;
            }
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Log.v(TAG, "latitude " + location.getLatitude() + "  longitude:" + location.getLongitude());
                String tempString1 = String.valueOf(location.getLatitude()); // 经度
                String tempString2 = String.valueOf(location.getLongitude()); // 纬度
                if (DEBUG) LogUtil.d(TAG, "onReceiveLocation: 经度: " + tempString1);
                if (DEBUG) LogUtil.d(TAG, "onReceiveLocation: 纬度: " + tempString2);

                /**
                 * 先判断得到的天气ID能不能返回正确的天气数据再确定要不要保存
                 * 以免定位到一些比较奇怪的地方时获取不到数据
                 */
                locationCountyWeatherId = tempString2.substring(0,tempString2.indexOf('.') + 4)
                        + "," + tempString1.substring(0, tempString1.indexOf('.') + 4);
                String weatherUrl = "https://free-api.heweather.com/v5/weather?city="
                        + locationCountyWeatherId + "&key=" + KEY;
                MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
                            @Override
                            public void onFailure(IOException e) {
                                if (DEBUG) LogUtil.d(TAG, "onFailure: netWork error");
                            }

                            @Override
                            public void onResponse(String response) throws IOException {
                                final String responseText = response;
                                if (DEBUG) LogUtil.d(TAG, "onResponse: responseText: " + responseText);
                                final HeWeather5 heWeather5 = WeatherJson.getWeatherResponse(responseText);
                                SharedPreferences.Editor edit = getSharedPreferences("location", MODE_PRIVATE).edit();
                                  if (heWeather5 != null && "ok".equals(heWeather5.status)) {
                                      //获取的地理位置信息有效，保存定位结果，等下次Oncreate的时候直接调用
                                      edit.putString("locationWeatherId", locationCountyWeatherId);
                                      locationCountyWeatherName = heWeather5.basic.cityName;
                                      edit.apply();
                                      Message msg = new Message();
                                      msg.what = TOAST_LOCATION_SUCCEED;
                                      myHandler.sendMessage(msg);
                                  } else {
                                      //定位的城市信息无效，保存为空
                                      locationCountyWeatherId = null;
                                      locationCountyWeatherName = null;
                                      edit.putString("locationWeatherId", locationCountyWeatherId);
                                      edit.apply();
                                  }
                            }
                        });
                        Log.d(TAG, "locationCountyWeatherId: " + locationCountyWeatherId);

                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                Log.d(TAG, "onReceiveLocation: " + sb.toString() );
            } else {
                //网络定位失败时把数据置零
                SharedPreferences.Editor edit = getSharedPreferences("location", MODE_PRIVATE).edit();
                edit.putString("locationWeatherId", locationCountyWeatherId = null);
                edit.apply();
            }
        }

        public void onConnectHotSpotMessage(String s, int i){
        }
    };

    /**
     * 请求权限的回调函数，不管用户有没有允许，都会调用该函数
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "由于权限原因，定位失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TOAST_LOCATION_SUCCEED) {
                Toast.makeText(WeatherActivity.this, "城市定位成功", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        //这里也可以写在onActivityResult活动回调函数里面
        if (INMODE == INMODE_DIRECT) {
            //直接进入城市添加的，重新初始化导航页
            initGuideView();
        }
        if (!ChooseAreaActivity.isBackFromOnItem){
            if (!ChooseAreaActivity.isBackFormBackBtn && INMODE == INMODE_INDIRECT) {
                //判断是否是从添加城市间接返回的，是的话执行下列操作
                Log.d(TAG, "onStart: in");
                SharedPreferences shared = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                //从城市管理间接添加城市时返回执行这里
                String weatherId = shared.getString("weatherID", "");
                if (weatherId != null) {
                    if (weatherId.equals("")) {
                        Log.d(TAG, "onStart: data is null");
                    } else {
                        selectedCountisList = DataSupport.findAll(SelectedCounty.class);
                        if (!isLocationCountyRemove && null != locationCountyWeatherId) {
                            //如果定位城市存在且未删除，则添加定位城市
                            SelectedCounty selectedCounty = new SelectedCounty();
                            selectedCounty.setWeatherId(locationCountyWeatherId);
                            selectedCountisList.add(0, selectedCounty);
                        }
                        initGuideView();
                        LayoutInflater layoutInflater = getLayoutInflater();
                        viewList.add(viewList.size(), (View) layoutInflater.inflate(R.layout.weather_fragment, null));
                        pagerAdapter.notifyDataSetChanged();
                        vp.setCurrentItem(viewList.size() - 1);
                        currentPosition = viewList.size() - 1;
                        requestWeatherAsync(weatherId);
                        //使页面切换时不重复加载
                        vp.setOffscreenPageLimit(viewList.size());
                    }
                }
                editor.clear();
            }
        }
        ChooseAreaActivity.isBackFormBackBtn = false;
        ChooseAreaActivity.isBackFromOnItem = false;
        //待解决的问题，因为create之后必须点击才会加载后面的页数，
        //但是传来的position又不能放到后面加载，所以出现问题了
        //设置从窗口传来的定位
        if (widgetStartPosition >= 0 && widgetStartPosition < viewList.size()) {
            //判断是否定位成功
            if (locationCountyWeatherId != null && locationCountyWeatherName != null){
                vp.setCurrentItem(widgetStartPosition + 1);
                currentPosition = widgetStartPosition + 1;
            } else {
                vp.setCurrentItem(widgetStartPosition);
                currentPosition = widgetStartPosition;
            }
            //防止被多次定页
            widgetStartPosition = -1;
        }
    }

    /**
     * 导航栏shape初始化，动态生成匹配数量的shape
     */
    public void initGuideView() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.vp_guide_layout);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(20, 20);
        mParams.setMargins(0, 0, 0, 0);//设置小圆点左右之间的间隔

        guideShapeViewArrayList.clear();
        layout.removeAllViews();
        for(int i = 0; i < selectedCountisList.size(); i++)
        {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(mParams);
            imageView.setImageResource(R.drawable.guide_shape_select);
            if(i == currentPosition)
            {
                imageView.setSelected(true);//默认启动时，选中第一个小圆点
            }
            else {
                imageView.setSelected(false);
            }
            guideShapeViewArrayList.add(i, imageView);//得到每个小圆点的引用，用于滑动页面时，（onPageSelected方法中）更改它们的状态。
            layout.addView(imageView);//添加到布局里面显示
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop: ");
        //keyForFirstIn = true;
    }

    /**
     *退出且有网络时缓存天气数据
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: ");
        keyForFirstIn = true;
        //有网退出的时候缓存天气的数据，防止断网
        if (isNetworkConnected(WeatherActivity.this)) {
            iCount = 0;
            for (int i = 0; i < selectedCountisList.size(); ++i) {
                requestWeatherBufferAsync(selectedCountisList.get(i).getWeatherId());
            }
            while (selectedCountisList.size() > iCount){}
            SharedPreferences.Editor spEdit = getSharedPreferences("weather_buffer", MODE_PRIVATE).edit();
            spEdit.putStringSet("weatherBuffer", weatherBufferSet);

            Log.d(TAG, "onDestroy: weatherBufferSet.size = " + weatherBufferSet.size());
            Log.d(TAG, "onDestroy: icount" + iCount);
            Log.d(TAG, "onDestroy: selectedCountisList.size = " +selectedCountisList.size());
            if (weatherBufferSet.size() != iCount || selectedCountisList.size() != iCount) {
                //以上数据不相等，说明数据有问题
                if (DEBUG) LogUtil.d(TAG, "onDestroy: weatherBuffer eror");
                finish();
            }
            spEdit.apply();
        }
        finish();
    }

    /**
     * 根据天气id请求城市天气信息，专为天气数据缓存使用
     */
    public void requestWeatherBufferAsync(final String weateherId) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city="
                + weateherId + "&key=" + KEY;
        Log.d(TAG, "Url: " + weatherUrl);
        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                ++iCount;
                Log.d(TAG, "onFailure: WeatherBuffer error");
            }

            @Override
            public void onResponse(String response) throws IOException {
                final String responseText = response;
                //final HeWeather5 heWeather5 = WeatherJson.getWeatherResponse(responseText);
                weatherBufferSet.add(responseText);
                ++iCount;
                Log.d(TAG, "onResponse: WeatherBuffer save succeed");
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(TAG, "onRestart: ");
//        getIntent().getBundleExtra();
//        Bundle;
    }

    /**
     * 第一次进入没有城市时的回调函数
     * 编辑城市之后返回的回调函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        switch (requestCode) {
            //获取从AddCountyActivity活动返回的城市ID
            case ADDCOUNTYACTIVITY_RETURN:
                Log.d(TAG, "ADDCOUNTYACTIVITY_RETURN: in");
                if (resultCode == RESULT_OK){
                    selectedCountisList = DataSupport.findAll(SelectedCounty.class);
                    if (!isLocationCountyRemove && null != locationCountyWeatherId ) {
                        SelectedCounty selectedCounty = new SelectedCounty();
                        selectedCounty.setWeatherId(locationCountyWeatherId);
                        selectedCountisList.add(0, selectedCounty);
                        if (isAddLocationView) {
                            LayoutInflater layoutInflater = getLayoutInflater();
                            viewList.add(viewList.size(), (View) layoutInflater.inflate(R.layout.weather_fragment, null));
                        }
                    }
                    String returnWeaherId = data.getStringExtra(AddCountyActivity.WEATHERID);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    viewList.add(viewList.size(), (View) layoutInflater.inflate(R.layout.weather_fragment, null));
                    pagerAdapter.notifyDataSetChanged();
                    //使页面切换时不重复加载
                    vp.setCurrentItem(viewList.size()-1);
                    currentPosition = viewList.size() - 1;
                    requestWeatherAsync(returnWeaherId);
                    vp.setOffscreenPageLimit(viewList.size()-1);
                }
                break;
            case CHOOSEAREAACTIVITY_RETURN:
                if (resultCode == RESULT_OK) {
                    int returnPosition = data.getIntExtra("position", -1);
                    int isSwapCounty = data.getIntExtra("isSwapCounty", -1);
                    ArrayList<Integer> delList = data.getIntegerArrayListExtra("delCountyIndex");
                    if (returnPosition == -1) {
                        Log.d(TAG, "onActivityResult: get position error");
                    } else {
                        if (1 == isSwapCounty) {
                            selectedCountisList = DataSupport.findAll(SelectedCounty.class);
                            if (!isLocationCountyRemove && null != locationCountyWeatherId) {
                                SelectedCounty selectedCounty = new SelectedCounty();
                                selectedCounty.setWeatherId(locationCountyWeatherId);
                                selectedCountisList.add(0, selectedCounty);
                            }
                            pagerAdapter.notifyDataSetChanged();
                            vp.setCurrentItem(returnPosition);
                            currentPosition = returnPosition;
                        }
                        if (ChooseAreaActivity.isDeletedCounties) {
                            //如果在编辑阶段删除了城市,那么重新载入城市
                            ChooseAreaActivity.isDeletedCounties = false;
                            selectedCountisList = DataSupport.findAll(SelectedCounty.class);
                            if (!isLocationCountyRemove && null != locationCountyWeatherId) {
                                SelectedCounty selectedCounty = new SelectedCounty();
                                selectedCounty.setWeatherId(locationCountyWeatherId);
                                selectedCountisList.add(0, selectedCounty);
                            }
                            if (selectedCountisList.size() > 0) {
                                //如果有城市，就显示
                                if (delList.size() > 0) {
                                    for (Integer integer : delList) {
                                        //pagerAdapter.destroyItem((ViewGroup) viewList.get(integer).getParent(), integer, integer);
                                        if (viewList.size() > integer.intValue()) {
                                            viewList.remove(integer.intValue());
                                        } else {
                                            viewList.remove(viewList.size() - 1);
                                        }

                                        if (integer.intValue() == 0 && null != locationCountyWeatherId) {
                                            isLocationCountyRemove = true;
                                            selectedCountisList.remove(0);
                                        }
                                    }
                                }
                                pagerAdapter.notifyDataSetChanged();
                                vp.setCurrentItem(returnPosition);
                                currentPosition = returnPosition;
                            } else {
                                //跳转到添加活动
                                INMODE = INMODE_DIRECT;
                                Intent intent = new Intent(this, AddCountyActivity.class);
                                startActivityForResult(intent, ADDCOUNTYACTIVITY_RETURN);
                            }
                            initGuideView();
                        } else {
                            //如果没有发生删除，则直接定位到选择的城市
                            vp.setCurrentItem(returnPosition);
                            currentPosition = returnPosition;
                        }
                    }
                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String bingPic = prefs.getString("bing_pic", null);
                if (bingPic != null) {
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicIv);
                } else {
                    bingPicIv.setImageResource(R.drawable.bg);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 滑动页面时的偏移量，会不断的在调用
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Log.d(TAG, "postion: " + position + "Offest" + positionOffset + "pixels" + positionOffsetPixels);
    }

    /**
     * 页面发生切换的时候调用的函数（准确的说是即将切换的时候调用）
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "position " + position);
        if (currentPosition != position) {
            guideShapeViewArrayList.get(currentPosition).setSelected(false);
            currentPosition = position;
            guideShapeViewArrayList.get(currentPosition).setSelected(true);
            //实现界面切换,更新数据
            currentWeatherId = selectedCountisList.get(position).getWeatherId();
            //有网络从网上更新数据，没有网络显示缓存
            //判断网络状态，无网络显示上次缓存数据并提示无网络
            if (!isNetworkConnected(WeatherActivity.this)) {
                //无网络情况
                HeWeather5 heWeather5 = HeatherBufferList.get(position);
                if (heWeather5 != null && "ok".equals(heWeather5.status)) {
                    //显示天气数据
                    showWeatherInfo(heWeather5);
                }
                Toast.makeText(WeatherActivity.this, "无网络连接", Toast.LENGTH_SHORT).show();
            } else {
                //有网络情况
                requestWeatherAsync(selectedCountisList.get(position).getWeatherId());
            }
        }
    }

    /**
     * 点击和松开的时候会调用，总共有三种状态
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        //判断是不是第一次进入该函数，是的话进行加载
        if( keyForFirstIn ) {
                    keyForFirstIn = false;
                    LayoutInflater layoutInflater = getLayoutInflater();
                    for (int i = viewList.size(); i < selectedCountisList.size(); ++i) {
                        viewList.add(i, (View) layoutInflater.inflate(R.layout.weather_fragment, null));
                    }

                    pagerAdapter.notifyDataSetChanged();
                    //使页面切换时不重复加载
                    vp.setOffscreenPageLimit(viewList.size() - 1);
                }
        }


    /**
     * 根据天气id请求城市天气信息，用开源的OKHTTP实现
     */
//    public void requestWeatherAsync(final String weateherId) {
//        String weatherUrl = "https://free-api.heweather.com/v5/weather?city="
//                + weateherId + "&key=" + KEY;
//        //com.dong.dongweather.WeatherActivity.LogUtil.d("timeTest", "WeatherActivity requestWeatherAsync start");
//        OkHttp.sendRequestOkHttpForGet(weatherUrl, new Callback() {
//            @Override
//            public void onFailure(final Call call, IOException e) {
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        if (swipeRefreshLayout != null) {
//                            swipeRefreshLayout.setRefreshing(false);
//                        }
//                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                LogUtil.d("timeTest", "WeatherActivity onResponse start");
//                final String responseText = response.body().string();
//                final HeWeather5 heWeather5 = WeatherJson.getWeatherResponse(responseText);
//                LogUtil.d("timeTest", "WeatherActivity onResponse over");
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        if (heWeather5 != null && "ok".equals(heWeather5.status)) {
//                            //显示天气数据
//                            Log.d("timeTest", "WeatherActivity show start");
//                            showWeatherInfo(heWeather5);
//                            //Debug.stopMethodTracing();
//                            //com.dong.dongweather.WeatherActivity.LogUtil.d("timeTest", "WeatherActivity show over");
//                        }
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//            }
//        });
//    }

    /**
     * 根据天气id请求城市天气信息,用自己封装的网络线程实现异步
     */
    public void requestWeatherAsync(final String weateherId) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city="
                + weateherId + "&key=" + KEY;
        MyHttp.sendRequestOkHttpForGet(weatherUrl, new MyCallBack() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(String response) throws IOException {
                LogUtil.d("timeTest", "WeatherActivity onResponse start");
                final String responseText = response;
                final HeWeather5 heWeather5 = WeatherJson.getWeatherResponse(responseText);
                LogUtil.d("timeTest", "WeatherActivity onResponse over");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        if (heWeather5 != null && "ok".equals(heWeather5.status)) {
                            //显示天气数据
                            Log.d("timeTest", "WeatherActivity show start");
                            showWeatherInfo(heWeather5);
                        }
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

            }
        });
    }

    /**
     * 根据获取到的天气信息，显示天气
     */
    private void showWeatherInfo(HeWeather5 heWeather5) {
//        //如果城市天气id等于定位城市id,把值赋给定位城市名
//        if (locationCountyWeatherName == null && locationCountyWeatherId != null) {
//            locationCountyWeatherName = heWeather5.basic.cityName;
//        }
        currentView = viewList.get(currentPosition);
        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) currentView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeatherAsync(currentWeatherId);
            }
        });
        //标题栏定义
        titleText = (TextView) currentView.findViewById(R.id.weather_title_cityname);
        manageCityBtn = (Button) currentView.findViewById(R.id.manage_city_btn);
        manageCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始ChooseAreaActivity活动，等待返回的结果
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                startActivityForResult(intent, CHOOSEAREAACTIVITY_RETURN);
                //判断是不是第一次进入该函数，是的话进行加载
                if( keyForFirstIn ) {
                    keyForFirstIn = false;
                    LayoutInflater layoutInflater = getLayoutInflater();
                    for (int i = viewList.size(); i < selectedCountisList.size(); ++i) {
                        viewList.add(i, (View) layoutInflater.inflate(R.layout.weather_fragment, null));
                    }

                    pagerAdapter.notifyDataSetChanged();
                    //使页面切换时不重复加载
                    vp.setOffscreenPageLimit(viewList.size() - 1);
                }
//                if( keyForFirstIn ) {
//                    keyForFirstIn = false;
//                    LayoutInflater layoutInflater = getLayoutInflater();
//                    for (int i = viewList.size(); i < selectedCountisList.size(); ++i) {
//                        viewList.add(i, (View) layoutInflater.inflate(R.layout.weather_fragment, null));
//                    }
//
//                    pagerAdapter.notifyDataSetChanged();
//                    //使页面切换时不重复加载
//                    vp.setOffscreenPageLimit(viewList.size());
//                }
            }
        });
        //title加载和显示(城市加载和显示)
        titleText.setText(heWeather5.basic.cityName);

        //now_weather加载和显示（当前温度，今天天气，空气质量，今天是周几，今天是的最低温和最高温）
        nowTemperatureTV = (TextView) currentView.findViewById(R.id.now_temperature);
        nowTemperatureTV.setText(heWeather5.now.tmp + "℃");
        nowDayWeatherQltyTV = (TextView) currentView.findViewById(R.id.now_dayweather_qlty);
        String nowWeather = heWeather5.dailyForecastList.get(0).cond.dayWeather;
        if (null != heWeather5.aqi){
            nowDayWeatherQltyTV.setText(nowWeather + "|空气" + heWeather5.aqi.city.qlty);
        } else {
            nowDayWeatherQltyTV.setText(nowWeather + "|空气" );
        }

        nowToady = (TextView) currentView.findViewById(R.id.now_today);
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        String tempToday = format.format(date) + "  今天";
        nowToady.setText(tempToday);
        nowMinMaxTemperature = (TextView) currentView.findViewById(R.id.now_min_max_temperature);
        nowMinMaxTemperature.setText(heWeather5.dailyForecastList.get(0).tmp.min + "º  "
                + heWeather5.dailyForecastList.get(0).tmp.max + "º");

        /**
         * 当前接下去的小时的数据，柱形图显示
         */
        //初始化hourlyWeatherList
        hourlyWeatherList.clear();
        HourlyWeather hourlyWeather;
        for (HourlyForecast hourlyForecast : heWeather5.hourlyForecastList) {
            hourlyWeather = new HourlyWeather();
            hourlyWeather.hourlyTime = hourlyForecast.date.substring(11, 13);
            try {
                hourlyWeather.hourlyImageBit = BitmapFactory.decodeStream(this.getAssets().open(hourlyForecast.weatherRegime.code + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            hourlyWeather.hourlyTemperature = hourlyForecast.tmp;
            hourlyWeatherList.add(hourlyWeather);
        }

        //水平滑动显示
        hourlyRecycler = (RecyclerView) currentView.findViewById(R.id.hourly_recycler);
        layoutManager = new LinearLayoutManager(currentView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hourlyRecycler.setLayoutManager(layoutManager);
        hourlyWeatherAdapter = new HourlyWeatherAdapter(hourlyWeatherList);
        hourlyRecycler.setAdapter(hourlyWeatherAdapter);

        //接下去几天的天气的显示
        //时间：2017-4-19  天气状况描述：  天气状况图标    最低温-最高温
        String filename;
        dailyForecastLayout = (LinearLayout) currentView.findViewById(R.id.daily_forecast_layout);
        dailyForecastLayout.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();
        for (DailyForecast dailyForecast : heWeather5.dailyForecastList) {
            View view = layoutInflater.from(this).inflate(R.layout.daily_forecast_item, dailyForecastLayout, false);
            dailyDate = (TextView) view.findViewById(R.id.daily_date);
            dailyWeather = (TextView) view.findViewById(R.id.daily_weather);
            dailyWeatherImage = (ImageView) view.findViewById(R.id.daily_weather_image);
            dailyTemperature = (TextView) view.findViewById(R.id.daily_temperature);

            dailyDate.setText(dailyForecast.date);
            dailyWeather.setText(dailyForecast.cond.dayWeather);
            try {
                filename = dailyForecast.cond.code_d + ".png";
                dailyWeatherImage.setImageBitmap(BitmapFactory.decodeStream(this.getAssets().open(filename)));
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.d(TAG, "showWeatherInfo: getAssets error");
            }
            dailyTemperature.setText(dailyForecast.tmp.min + "º  " + dailyForecast.tmp.max + "º");
            dailyForecastLayout.addView(view);
        }

        //生活指数
        //体感温度
        weatherSendibleTemperatureTv = (TextView) currentView.findViewById(R.id.weather_sendible_temperature_tv);
        weatherSendibleTemperatureTv.setText("体感温度" + heWeather5.now.fl + "");
        //湿度
        weatherHumitidyTv = (TextView) currentView.findViewById(R.id.weather_humitidy_tv);
        weatherHumitidyTv.setText("湿度" + heWeather5.now.hum + "%");
        //能见度
        weatherVisibilityTv = (TextView) viewList.get(currentPosition).findViewById(R.id.weather_visibility_tv);
        weatherVisibilityTv.setText("能见度" + heWeather5.now.vis + "千米");
        //风向等级
        weatherRiskLevelTv = (TextView) currentView.findViewById(R.id.weather_risk_level_tv);
        weatherRiskLevelTv.setText(heWeather5.now.wind.dir + heWeather5.now.wind.sc + "级");
        //降水量
        weatherPrecipitationTv = (TextView) currentView.findViewById(R.id.weather_precipitation_tv);
        weatherPrecipitationTv.setText("降水量" + heWeather5.now.mypcpn + "mm");
        //气压
        weatherPressureTv = (TextView) currentView.findViewById(R.id.weather_pressure_tv);
        weatherPressureTv.setText("气压" + heWeather5.now.pres + "百帕");

        //生活建议
        suggestionComfort = (TextView) currentView.findViewById(R.id.suggesstion_comfort);
        suggestionCarwash = (TextView) currentView.findViewById(R.id.suggesstion_carWash);
        suggestionSport = (TextView) currentView.findViewById(R.id.suggesstion_sport);
        suggestionDressingIndex = (TextView) currentView.findViewById(R.id.suggesstion_hot);

        if (heWeather5.suggestion != null) {
            suggestionComfort.setText("舒适度：" + heWeather5.suggestion.comfort.txt);
            suggestionCarwash.setText("洗车指数：" + heWeather5.suggestion.carWash.txt);
            suggestionSport.setText("运动指数：" + heWeather5.suggestion.sport.txt);
            suggestionDressingIndex.setText("穿衣指数：" + heWeather5.suggestion.hot.txt);
        }
        
        if (null == locationCountyWeatherId && attentionTimes < 3) {
            ++attentionTimes;
            Toast.makeText(WeatherActivity.this, "定位失败，请检查手机设置", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 用于判断是否有网络连接
     *
     * @param context 当前上下文
     * @return true 有连接 false 无连接
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 更新背景图片
     */
    public void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        OkHttp.sendRequestOkHttpForGet(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}