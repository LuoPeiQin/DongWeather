package com.dong.dongweather;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.dongweather.db.City;
import com.dong.dongweather.db.County;
import com.dong.dongweather.db.CountyChanged;
import com.dong.dongweather.db.Province;
import com.dong.dongweather.db.SelectedCounty;
import com.dong.dongweather.http.OkHttp;
import com.dong.dongweather.json.AreaJson;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCountyActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ChooseAreaFragment";

    private final String baseAdress = "http://guolin.tech/api/china";
    //提供给其它活动的属性
    public static final String WEATHERID = "weather_id";
    /**
     * 城市等级
     */
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;
    private int currentLevel = 0;

    /**
     * 当前选中的省、市、县
     */
    private Province currentProvince;
    private City currentCity;
    private County currentCounty;

    private Button backButton;
    private Button delDatabaseButton;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List dataList = new ArrayList<>();
    /**
     * 省市县数据list列表
     */
    private List<Province> provinceList = null;
    private List<City> cityList = null;
    private List<County> countyList = null;

    //已添加的城市
    private List<SelectedCounty> selectedCountyList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_county);
        titleText = (TextView) findViewById(R.id.choose_city);
        backButton = (Button) findViewById(R.id.back_btn);
        backButton.setVisibility(View.GONE);
        delDatabaseButton = (Button) findViewById(R.id.clear_database_btn);
        listView = (ListView) findViewById(R.id.area_listview);
        adapter = new ArrayAdapter<>(AddCountyActivity.this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        //设置测试按钮delDatabaseButton不可用
        delDatabaseButton.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    currentProvince = provinceList.get(position);
                    backButton.setVisibility( View.VISIBLE);
                    queryCity();
                }else if (currentLevel == LEVEL_CITY){
                    currentCity = cityList.get(position);
                    queryCounty();
                }else if (currentLevel == LEVEL_COUNTY) {
                    currentCounty = countyList.get(position);
                    //判断该城市是否已经添加过
                    if ((selectedCountyList = LitePal.where("weatherId == ?",currentCounty.getWeatherId()).find(SelectedCounty.class)).size() == 0){
                        //把添加的城市加入到数据库selectedCounty
                        SelectedCounty selectedCounty = new SelectedCounty();
                        selectedCounty.setWeatherId(currentCounty.getWeatherId());
                        selectedCounty.setCountyName(currentCounty.getCountyName());
                        selectedCounty.save();
                        //提示添加城市成功
                        String prompt = "添加" + currentCounty.getCountyName() + "成功";
                        Toast.makeText(AddCountyActivity.this, prompt, Toast.LENGTH_SHORT).show();
                        //保存窗口更新数据到数据库
                        CountyChanged countyChanged = new CountyChanged();
                        countyChanged.setAddWeatherID(currentCounty.getWeatherId());
                        countyChanged.setDelCountyPosition(null);
                        countyChanged.setSwapCounty(null);
                        countyChanged.save();
                        //添加城市成功，返回天气显示界面，显示当前选择的天气
                        //判断是以哪种方式进入本活动的，以返回相应的结果
                        if (WeatherActivity.INMODE == WeatherActivity.INMODE_DIRECT) {
                            //直接进入的
                            Intent sintent = new Intent();
                            sintent.putExtra(WEATHERID, currentCounty.getWeatherId());
                            setResult(RESULT_OK, sintent);
                        } else {
                            //从城市管理间接进入的
                            //WeatherActivity.INMODE = WeatherActivity.INMODE_INDIRECT;
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("weatherID",currentCounty.getWeatherId());
                            editor.apply();
                        }
                        //发送广播通知listviewservice服务更新窗口
                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        sendBroadcast(intent);
                        finish();
                        //因为隔了一个Activity，所以不能使用Intent返回数据
//                    Intent intent = new Intent(AddCountyActivity.this, WeatherActivity.class);
//                    intent.putExtra(WEATHERID, currentCounty.getWeatherId());
//                    startActivity(intent);
                        //改用sharedpreference
                    } else {
                        Toast.makeText(AddCountyActivity.this, "该城市已添加",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        backButton.setOnClickListener(this);
        delDatabaseButton.setOnClickListener(this);
        queryProvinces();
    }

    /**
     * 显示中国所有省份的数据在listView中
     */
    public void queryProvinces() {
        titleText.setText("中国");
        //backButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 10) {
            dataList.clear();
            //如果数据库中已有省份数据，则直接读取数据库数据显示到listView上
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String adress = baseAdress;
            showProgressDialog();
            queryForService(adress, LEVEL_PROVINCE);
        }
    }

    /**
     * 显示当前选择的省的所有城市
     */
    public void queryCity(){
        titleText.setText(currentProvince.getProvinceName());
        cityList = LitePal.where("provinceId = ?", String.valueOf(currentProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = currentProvince.getProvinceCode();
            String adress = baseAdress + "/" + provinceCode;
            showProgressDialog();
            queryForService(adress, LEVEL_CITY);
        }
    }

    /**
     * 显示当前选择的市的所有县城
     */
    public void queryCounty() {
        titleText.setText(currentCity.getCityName());
        countyList = LitePal.where("cityId = ?",String.valueOf(currentCity.getCityCode()) ).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = currentProvince.getProvinceCode();
            int cityCode = currentCity.getCityCode();
            String adress = baseAdress + "/" + provinceCode + "/" + cityCode;
            showProgressDialog();
            queryForService(adress, LEVEL_COUNTY);
        }
    }

    /**
     * 从服务器上获取省、市、县数据
     */
    public void queryForService(String adress, final int typeLevel) {

        OkHttp.sendRequestOkHttpForGet(adress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(AddCountyActivity.this, "加载数据失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                if (null != responseText) {
                    boolean result = false;
                    if (typeLevel == LEVEL_PROVINCE){
                        result = AreaJson.getProvinceResponse(responseText);
                    }else if (typeLevel == LEVEL_CITY){
                        result = AreaJson.getCityJson(responseText, currentProvince.getProvinceCode());
                    }else if (typeLevel == LEVEL_COUNTY){
                        result = AreaJson.getCountyJson(responseText, currentCity.getCityCode());
                    }
                    if (result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                if (typeLevel == LEVEL_PROVINCE){
                                    queryProvinces();
                                }else if (typeLevel == LEVEL_CITY){
                                    queryCity();
                                }else if (typeLevel == LEVEL_COUNTY){
                                    queryCounty();
                                }
                            }
                        });
                    }
                }else {
                    LogUtil.d(TAG, "queryForService: error");
                    LogUtil.d(TAG, "response : error");
                }

            }
        });


    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_database_btn:
                LitePal.deleteAll(Province.class);
                LitePal.deleteAll(City.class);
                LitePal.deleteAll(County.class);
                LitePal.deleteAll(SelectedCounty.class);
                break;
            case R.id.back_btn:
                if (currentLevel == LEVEL_COUNTY) {
                    queryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    backButton.setVisibility(View.GONE);
                    queryProvinces();
                }
                break;
            default:
                break;
        }
    }

    //按返回键时
    @Override
    public void onBackPressed() {
        if (WeatherActivity.INMODE == WeatherActivity.INMODE_DIRECT) {
            Toast.makeText(this, "请添加城市", Toast.LENGTH_SHORT).show();
        } else if (WeatherActivity.INMODE == WeatherActivity.INMODE_INDIRECT) {
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putString("weatherID", null);
            editor.apply();
            finish();
        }
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
