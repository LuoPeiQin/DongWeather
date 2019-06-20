package com.dong.dongweather;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dong.dongweather.db.County;
import com.dong.dongweather.db.CountyChanged;
import com.dong.dongweather.db.SelectedCounty;
import com.dong.dongweather.service.ListViewService;
import com.mobeta.android.dslv.DragSortListView;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChooseAreaActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ChooseAreaActivity";
    //控件声明
    private ListView areaListview;
    private Button areaBackBtn;
    private Button areaEditBtn;
    private Button areaAddBtn;
    private Button editSaveBtn;
    private Button editCancelBtn;
    //删除的城市的下标保存
    private ArrayList<Integer> delCountyId;
    //删除的城市的下标保存，返回给WeatherActivity的
    private ArrayList<Integer> delCountyIndex;
    //适配器
    private ArrayList<String> areaList;
    private ArrayAdapter<String> areaAdapter;
    private CountyAdapter countyAdapter;
    //以选中城市list声明
    private List<SelectedCounty> selectedCityList;
    //按钮指针保持
    private List<Button> buttonList;
    //可拖拽按钮删除和移动按钮的指针
    private List<ImageView> moveImageViewList;
    private List<ImageView> deleteImageViewList;
    //判断点击了编辑按钮
    private boolean isEditClick = false;

    //判断是否从返回键返回
    public static boolean isBackFormBackBtn = false;
    //判断是否从onItemClicklistener返回
    public static boolean isBackFromOnItem = false;

    //返回给天气活动的值
    //用于判断是否有进行删除过
    private int seletedCountyLength;
    //提供给WeatherActivity判断有没有删除过
    public static boolean isDeletedCounties = false;
    //判断添加按钮是否可用
    private boolean isAreaAddBtnUsed = true;
    //判断是否交换了位置
    private boolean isSwapCounty = false;
    private DragSortListView dragSortListView;
    private CountiesAdapter countiesAdapter;
    private Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);

        //控件初始化
        areaListview = (ListView) findViewById(R.id.area_listview);
        areaBackBtn = (Button) findViewById(R.id.area_back_btn);
        areaEditBtn = (Button) findViewById(R.id.area_edit_btn);
        areaAddBtn = (Button) findViewById(R.id.area_add_btn);
        //选择删除的CountyId
        delCountyId = new ArrayList<>();
        delCountyIndex = new ArrayList<>();
        //下面是点击编辑之后生效的按钮
        editSaveBtn = (Button) findViewById(R.id.edit_save_btn);
        editSaveBtn.setVisibility(View.GONE);
        editCancelBtn = (Button) findViewById(R.id.edit_cancel_btn);
        editCancelBtn.setVisibility(View.GONE);
        buttonList = new ArrayList<>();
        moveImageViewList = new ArrayList<>();
        deleteImageViewList = new ArrayList<>();
        //添加监听器
        areaBackBtn.setOnClickListener(this);
        areaEditBtn.setOnClickListener(this);
        areaAddBtn.setOnClickListener(this);

        //可滑动的适配器设置
        dragSortListView = (DragSortListView) findViewById(R.id.dslvList);

        initView();
    }

    private void initView() {
        //查询当前的以选中城市
        selectedCityList = new ArrayList<>();
        selectedCityList = LitePal.findAll(SelectedCounty.class);
        //如果定位城市成功，则先显示定位城市
        if (!WeatherActivity.isLocationCountyRemove && null != WeatherActivity.locationCountyWeatherId && null != WeatherActivity.locationCountyWeatherName) {
            SelectedCounty selectedCounty = new SelectedCounty();
            selectedCounty.setWeatherId(WeatherActivity.locationCountyWeatherId);
            selectedCounty.setCountyName(WeatherActivity.locationCountyWeatherName);
            selectedCityList.add(0, selectedCounty);
        }
        if (selectedCityList.size() > 0) {
            dragSortListView.setDropListener(onDrop);
            dragSortListView.setRemoveListener(onRemove);
            countiesAdapter = new CountiesAdapter(ChooseAreaActivity.this, selectedCityList);
            dragSortListView.setAdapter(countiesAdapter);
            dragSortListView.setDragEnabled(true); //设置是否可拖动。
            dragSortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //在编辑状态时，不做处理
                    if (isEditClick) {
                        return;
                    }
                    //发生了删除，返回天气时应该删除相应的城市页面
                    isBackFromOnItem = true;
                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    if (isDeletedCounties) {
                        intent.putExtra("delCountyIndex", delCountyIndex);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

//            //显示已选中的城市
//            countyAdapter = new CountyAdapter(this);
//            areaListview.setAdapter(countyAdapter);
//            if (buttonList.size() > 0) {
//                for (Button button : buttonList) {
//                    button.setVisibility(View.GONE);
//                }
//            }
//            areaListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    //发生了删除，返回天气时应该删除相应的城市页面
//                    isBackFromOnItem = true;
//                    Intent intent = new Intent();
//                    intent.putExtra("position", position);
//                    if (isDeletedCounties) {
//                        intent.putExtra("delCountyIndex", delCountyIndex);
//                    }
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            });
            //如果城市数量大于等于10的话，设置添加按钮为不可选，变换颜色
            if (selectedCityList.size() >= 10) {
                isAreaAddBtnUsed = false;
                areaAddBtn.setEnabled(false);
                areaAddBtn.setBackgroundColor(0xFF808080);
            }
        } else {
            //提示用户，并转到添加城市界面
            Toast.makeText(this, "请添加城市", Toast.LENGTH_SHORT).show();
            //areaList.remove()
            Intent intent = new Intent(this, AddCountyActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回天气界面
            case R.id.area_back_btn:
                if (areaBackBtn.getText().equals("取消")) {
                    /**
                     * 取消按钮
                     */
                    isEditClick = false;
                    areaEditBtn.setText("编辑");
                    areaBackBtn.setText("＜");
                    //如果有城市被删除或者交换了位置，则重新导入城市
                    if (delCountyId.size() > 0 || isSwapCounty) {
                        selectedCityList = LitePal.findAll(SelectedCounty.class);
                        //如果定位城市成功，则先显示定位城市
                        if (!WeatherActivity.isLocationCountyRemove && null != WeatherActivity.locationCountyWeatherId) {
                            SelectedCounty selectedCounty = new SelectedCounty();
                            selectedCounty.setWeatherId(WeatherActivity.locationCountyWeatherId);
                            selectedCounty.setCountyName(WeatherActivity.locationCountyWeatherName);
                            selectedCityList.add(0, selectedCounty);
                        }
                        countiesAdapter.change(selectedCityList);
                        //countiesAdapter.notifyDataSetChanged()；这里使用notifyDataSetChanged无效是因为selectedCityList指向的地址已经改变
                    }
                    //设置删除按钮和移动按钮不可用并隐藏
                    if (moveImageViewList.size() > 0) {
                        for (ImageView iv : moveImageViewList) {
                            iv.setVisibility(View.GONE);
                        }
                    }
                    if (deleteImageViewList.size() > 0) {
                        for (ImageView iv : deleteImageViewList) {
                            iv.setVisibility(View.GONE);
                        }
                    }
                } else {
                    /**
                     * 返回按钮
                     */
                    isBackFormBackBtn = true;
                    //如果只发生了删除，返回时要做的处理
                    if (isDeletedCounties && !isSwapCounty) {
                        Intent intent = new Intent();
                        intent.putExtra("position", 0);
                        intent.putExtra("delCountyIndex", delCountyIndex);
                        setResult(RESULT_OK, intent);
                        //通知桌面小插件发生相应的改变
                        String delPositon = new String();
                        for (Integer integer : delCountyIndex) {
                            delPositon += integer;
                        }
                        CountyChanged countyChanged = new CountyChanged();
                        countyChanged.setAddWeatherID(null);
                        countyChanged.setDelCountyPosition(delPositon);
                        countyChanged.setSwapCounty(null);
                        countyChanged.save();
                        Intent ListviewIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        sendBroadcast(ListviewIntent);
                    } else if (isSwapCounty && isDeletedCounties) {
                        //通知桌面小插件发生相应的改变
                        String delPositon = new String();
                        for (Integer integer : delCountyIndex) {
                            delPositon += integer;
                        }
                        CountyChanged countyChanged = new CountyChanged();
                        countyChanged.setAddWeatherID(null);
                        countyChanged.setDelCountyPosition(delPositon);
                        countyChanged.setSwapCounty("changed");
                        countyChanged.save();
                        Intent ListviewIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        sendBroadcast(ListviewIntent);
                        //如果发生了移动和删除或这发生了移动
                        Intent intent = new Intent();
                        intent.putExtra("position", 1);
                        intent.putExtra("delCountyIndex", delCountyIndex);
                        setResult(RESULT_OK, intent);
                    }  else if (!isDeletedCounties && isSwapCounty) {
                        CountyChanged countyChanged = new CountyChanged();
                        countyChanged.setAddWeatherID(null);
                        countyChanged.setDelCountyPosition(null);
                        countyChanged.setSwapCounty("changed");
                        countyChanged.save();
                        Intent ListviewIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        sendBroadcast(ListviewIntent);
                        //如果发生了移动和删除或这发生了移动
                        Intent intent = new Intent();
                        intent.putExtra("position", 1);
                        intent.putExtra("delCountyIndex", delCountyIndex);
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                }
                break;
            //地区编辑按钮
            case R.id.area_edit_btn:
                if (areaEditBtn.getText().equals("编辑") ) {
                    /**
                     * 编辑按钮
                     */
                    isEditClick = true;
                    //改变显示
                    areaEditBtn.setText("保存");
                    areaBackBtn.setText("取消");
                    //设置删除按钮和移动按钮可用并显示
                    if (moveImageViewList.size() > 0) {
                        for (ImageView iv : moveImageViewList) {
                            //如果定位城市存在，则去除定位城市的操作
                            if (!WeatherActivity.isLocationCountyRemove && null != WeatherActivity.locationCountyWeatherId && iv == moveImageViewList.get(0) ) {
                                iv.setVisibility(View.VISIBLE);
                                iv.setImageResource(R.drawable.location_mark);
                                iv.setEnabled(false);
                                continue;
                            }
                            iv.setVisibility(View.VISIBLE);
                        }
                    }
                    if (deleteImageViewList.size() > 0) {
                        int i = 0;
                        for (ImageView iv : deleteImageViewList) {
                            //如果定位城市存在，则去除定位城市的操作
                            if (!WeatherActivity.isLocationCountyRemove && null != WeatherActivity.locationCountyWeatherId &&  0 == i) {
                                iv.setVisibility(View.GONE);
                                ++i;
                                continue;
                            }
                            iv.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    /**
                     * 保存按钮
                     */
                    isEditClick = false;
                    areaEditBtn.setText("编辑");
                    areaBackBtn.setText("＜");
                    //分为四种情况处理
                    //1、没有删除，没有移动
                    if (delCountyId.size() <= 0 && !isSwapCounty) {
                        break;
                    } else if (delCountyId.size() <= 0 && isSwapCounty) {
                        //2、没有删除，但移动了
                        //如果城市之间交换了位置
                        LitePal.deleteAll(SelectedCounty.class);
                        int i = 0;
                        for (SelectedCounty selectedCounty : selectedCityList) {
                            if (!WeatherActivity.isLocationCountyRemove && null != WeatherActivity.locationCountyWeatherId && 0 == i ) {
                                ++i;
                                continue;
                            }
                            SelectedCounty sSave = new SelectedCounty();
                            sSave.setWeatherId(selectedCounty.getWeatherId());
                            sSave.setCountyName(selectedCounty.getCountyName());
                            sSave.save();
                        }
                    } else if (delCountyId.size() > 0 && isSwapCounty) {
                        //3、删除了，但没有移动
                        LitePal.deleteAll(SelectedCounty.class);
                        int i = 0;
                        for (SelectedCounty selectedCounty : selectedCityList) {
                            if (!WeatherActivity.isLocationCountyRemove && null != WeatherActivity.locationCountyWeatherId && 0 == i ) {
                                ++i;
                                continue;
                            }
                            SelectedCounty sSave = new SelectedCounty();
                            sSave.setWeatherId(selectedCounty.getWeatherId());
                            sSave.setCountyName(selectedCounty.getCountyName());
                            sSave.save();
                        }
                        isDeletedCounties = true;
                    }else if (!isSwapCounty && delCountyId.size() > 0) {
                        //4、只删除未移动
                        //删除标识变为真
                        isDeletedCounties = true;
                        for (Integer integer : delCountyId) {
                            LitePal.delete(SelectedCounty.class, integer);
                        }
//                        //保存已删除的城市对应的position
//                        /**
//                         * sharedPreference不能跨进程通信
//                         * 也不是说完全不能，android为了兼容低版本的系统，还是会提供这些API
//                         * 只不过是说效果不好，建议不使用，以后应该会彻底移除
//                         */
//                        SharedPreferences.Editor ditor = getSharedPreferences("countyChange", MODE_MULTI_PROCESS).edit();
//                        ditor.putString("positions",delPositon);
//                        ditor.apply();
//                        SharedPreferences sharedPreferences = getSharedPreferences("countyChange", MODE_MULTI_PROCESS);
//                        String test = sharedPreferences.getString("positions",null);
//                        //通知更新插件小窗口
                    }
//                    if (delCountyId.size() > 0) {
//                        //删除标识变为真
//                        isDeletedCounties = true;
////                        int[] tempInt = new int[delCountyId.size()];
////                        int iCount = 0;
//                        for (Integer integer : delCountyId) {
//                            LitePalSupport.delete(SelectedCounty.class, integer);
//                        }
////                        //保存已删除的城市对应的position
////                        /**
////                         * sharedPreference不能跨进程通信
////                         * 也不是说完全不能，android为了兼容低版本的系统，还是会提供这些API
////                         * 只不过是说效果不好，建议不使用，以后应该会彻底移除
////                         */
////                        SharedPreferences.Editor ditor = getSharedPreferences("countyChange", MODE_MULTI_PROCESS).edit();
////                        ditor.putString("positions",delPositon);
////                        ditor.apply();
////                        SharedPreferences sharedPreferences = getSharedPreferences("countyChange", MODE_MULTI_PROCESS);
////                        String test = sharedPreferences.getString("positions",null);
////                        //通知更新插件小窗口
//                    }
                    //设置删除按钮和移动按钮不可用并隐藏
                    if (moveImageViewList.size() > 0) {
                        for (ImageView iv : moveImageViewList) {
                            iv.setVisibility(View.GONE);
                        }
                    }
                    if (deleteImageViewList.size() > 0) {
                        for (ImageView iv : deleteImageViewList) {
                            iv.setVisibility(View.GONE);
                        }
                    }
                    if (!isAreaAddBtnUsed && selectedCityList.size() < 10) {
                        areaAddBtn.setEnabled(true);
                        areaAddBtn.setBackgroundColor(0xff000066);
                        isAreaAddBtnUsed = true;
                    }
                }
                break;
                //添加城市按钮
                case R.id.area_add_btn:
                    WeatherActivity.INMODE = WeatherActivity.INMODE_INDIRECT;
                    Intent intent = new Intent(this, AddCountyActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            default:
                LogUtil.d(TAG, "onClick:not find btnId");
                break;
        }
    }

    /**
     * 按钮触发事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            isBackFormBackBtn = true;
            //如果只发生了删除，返回时要做的处理
            if (isDeletedCounties && !isSwapCounty) {
                Intent intent = new Intent();
                intent.putExtra("position", 0);
                intent.putExtra("delCountyIndex", delCountyIndex);
                setResult(RESULT_OK, intent);
                //通知桌面小插件发生相应的改变
                String delPositon = new String();
                for (Integer integer : delCountyIndex) {
                    delPositon += integer;
                }
                CountyChanged countyChanged = new CountyChanged();
                countyChanged.setAddWeatherID(null);
                countyChanged.setDelCountyPosition(delPositon);
                countyChanged.setSwapCounty(null);
                countyChanged.save();
                Intent ListviewIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                sendBroadcast(ListviewIntent);
            } else if (isSwapCounty && isDeletedCounties) {
                //通知桌面小插件发生相应的改变
                String delPositon = new String();
                for (Integer integer : delCountyIndex) {
                    delPositon += integer;
                }
                CountyChanged countyChanged = new CountyChanged();
                countyChanged.setAddWeatherID(null);
                countyChanged.setDelCountyPosition(delPositon);
                countyChanged.setSwapCounty("changed");
                countyChanged.save();
                Intent ListviewIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                sendBroadcast(ListviewIntent);
                //如果发生了移动和删除或这发生了移动
                Intent intent = new Intent();
                intent.putExtra("position", 1);
                intent.putExtra("delCountyIndex", delCountyIndex);
                setResult(RESULT_OK, intent);
            }  else if (!isDeletedCounties && isSwapCounty) {
                CountyChanged countyChanged = new CountyChanged();
                countyChanged.setAddWeatherID(null);
                countyChanged.setDelCountyPosition(null);
                countyChanged.setSwapCounty("changed");
                countyChanged.save();
                Intent ListviewIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                sendBroadcast(ListviewIntent);
                //如果发生了移动和删除或这发生了移动
                Intent intent = new Intent();
                intent.putExtra("position", 1);
                intent.putExtra("delCountyIndex", delCountyIndex);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
        return true;
    }

//保存问题
//    /**
//     * Called when a view has been clicked.
//     *
//     * @param v The view that was clicked.
//     */
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            //返回天气界面
//            case R.id.area_back_btn:
//                finish();
//                break;
//            //地区编辑按钮
//            case R.id.area_edit_btn:
//                isEditClick = true;
//                //设置删除按钮可用并显示
//                if (buttonList.size() > 0){
//                    for (Button button : buttonList) {
//                        button.setVisibility(View.VISIBLE);
//                    }
//                }
//                //隐藏返回和编辑按钮，设为不可选
//                areaEditBtn.setVisibility(View.GONE);
//                areaBackBtn.setVisibility(View.GONE);
//                //显示出取消按钮和保存按钮
//                editSaveBtn.setVisibility(View.VISIBLE);
//                editCancelBtn.setVisibility(View.VISIBLE);
//                break;
//            //添加城市按钮
//            case R.id.area_add_btn:
//                Intent intent = new Intent(this, AddCountyActivity.class);
//                startActivity(intent);
//                finish();
//                break;
//            //保存编辑按钮
//            case R.id.edit_save_btn:
//                isEditClick = false;
//                if (delCountyId.size() > 0) {
//                    for (Integer integer : delCountyId) {
//                        LitePalSupport.delete(SelectedCounty.class, integer);
//                    }
//                }
//                //设置返回和编辑按钮为可选
//                areaEditBtn.setVisibility(View.VISIBLE);
//                areaBackBtn.setVisibility(View.VISIBLE);
//                //设置取消，保存，删除按钮为不可选，不可见
//                editSaveBtn.setVisibility(View.GONE);
//                editCancelBtn.setVisibility(View.GONE);
//                //设置删除按钮不可用并隐藏
//                if (buttonList.size() > 0){
//                    for (Button button : buttonList) {
//                        button.setVisibility(View.GONE);
//                    }
//                }
//                break;
//            //取消编辑按钮
//            case R.id.edit_cancel_btn:
//                isEditClick = false;
//                if (delCountyId.size() > 0) {
//                    selectedCityList = LitePalSupport.findAll(SelectedCounty.class);
//                    countyAdapter.notifyDataSetChanged();
//                }
//                //设置返回和编辑按钮为可选
//                areaEditBtn.setVisibility(View.VISIBLE);
//                areaBackBtn.setVisibility(View.VISIBLE);
//                //设置取消，保存，删除按钮为不可选，不可见
//                editSaveBtn.setVisibility(View.GONE);
//                editCancelBtn.setVisibility(View.GONE);
//                //设置删除按钮不可用并隐藏
//                if (buttonList.size() > 0){
//                    for (Button button : buttonList) {
//                        button.setVisibility(View.GONE);
//                    }
//                }
//                break;
//            default:
//                LogUtil.d(TAG, "onClick:not find btnId");
//                break;
//        }
//    }

//    public List<CountyManage> getCountiesData() {
//        List<CountyManage> list = new ArrayList<CountyManage>();
//        //为动态数组添加数据
//        for (int i = 0; i < selectedCityList.size(); ++i){
//            CountyManage county = new CountyManage();
//            county.countyName = selectedCityList;
//        }
//        areaAdapter = new ArrayAdapter<>(ChooseAreaActivity.this, android.R.layout.simple_list_item_1, areaList);
//        areaListview.setAdapter(areaAdapter);
//    }

    //监听器在手机拖动停下的时候触发
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {//from to 分别表示 被拖动控件原位置 和目标位置
                    //如果定位城市存在，则去除定位城市的操作
                    if (!WeatherActivity.isLocationCountyRemove && null != WeatherActivity.locationCountyWeatherId) {
                        if (0 == from || 0 == to) {
                            return;
                        }
                    }
                    if (from != to) {
                        SelectedCounty item = (SelectedCounty)countiesAdapter.getItem(from);//得到listview的适配器
                        countiesAdapter.remove(from);//在适配器中”原位置“的数据。
                        countiesAdapter.insert(item, to);//在目标位置中插入被拖动的控件。
                        //天气数据列表交换，如果有交换，则数据库数据需要重写
//                        SelectedCounty tempSwap = new SelectedCounty();
//                        tempSwap = selectedCityList.get(from);
//                        selectedCityList.add(from + 1, selectedCityList.get(to));
//                        selectedCityList.remove(from);
//                        selectedCityList.add(to + 1, tempSwap);
//                        selectedCityList.remove(to);
                        isSwapCounty = true;
                    }
                }
            };
    //删除监听器，点击左边差号就触发。删除item操作。
    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    delCountyId.add(selectedCityList.get(which).getId());
                    delCountyIndex.add(which);
                    countiesAdapter.remove(which);
                    Log.d(TAG, "onClick: remove position:" + which);
                }
            };

    /**
     * 可滑动适配器
     */
    public class CountiesAdapter extends BaseAdapter {
        private Context context;
        //适配器的数据源 selectedCityList
        private List<SelectedCounty> items;

        public CountiesAdapter(Context context,List<SelectedCounty> selectedCityList){
            this.context = context;
            this.items = selectedCityList;
            LogUtil.d(TAG, "CountiesAdapter: selectedCityList size:" + selectedCityList.size());
            LogUtil.d(TAG, "CountiesAdapter: selectedCityList items size:" + items.size());
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int arg0) {
            return items.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        public void remove(int arg0) {//删除指定位置的item
            items.remove(arg0);
            this.notifyDataSetChanged();//不要忘记更改适配器对象的数据源
        }

        public void insert(SelectedCounty item, int arg0) {
            items.add(arg0, item);
            this.notifyDataSetChanged();
        }

        public void change(List<SelectedCounty> selectedCityList) {
            items = selectedCityList;
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SelectedCounty item = (SelectedCounty)getItem(position);
            ViewHolder viewHolder;
            if(null == convertView){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.drag_listview_item, null);
                viewHolder.dragMoveIv = (ImageView) convertView.findViewById(R.id.drag_move_iv);
                viewHolder.dragCountyNameTv = (TextView) convertView.findViewById(R.id.drag_county_name_tv);
                viewHolder.drag_click_remove = (ImageView) convertView.findViewById(R.id.drag_click_remove);
                moveImageViewList.add(viewHolder.dragMoveIv);
                deleteImageViewList.add(viewHolder.drag_click_remove);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //是否点击了edit按钮，
            if (isEditClick) {
                if (!WeatherActivity.isLocationCountyRemove && null != WeatherActivity.locationCountyWeatherId && 0 == position ) {
                    viewHolder.drag_click_remove.setVisibility(View.GONE);
                } else {
                    viewHolder.dragMoveIv.setVisibility(View.VISIBLE);
                    viewHolder.drag_click_remove.setVisibility(View.VISIBLE);
                }
            }else {
                viewHolder.dragMoveIv.setVisibility(View.GONE);
                viewHolder.drag_click_remove.setVisibility(View.GONE);
            }
            viewHolder.dragCountyNameTv.setText(item.getCountyName());
            return convertView;
        }

        class ViewHolder {
            ImageView dragMoveIv;
            TextView dragCountyNameTv;
            ImageView drag_click_remove;
        }
    }

    /**
     * 基本适配器
     */
    public class CountyAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        public CountyAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return selectedCityList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = layoutInflater.inflate(R.layout.selected_county_item, null);
            TextView textView = (TextView) view.findViewById(R.id.listview_county_name_tv);
            Button button = (Button) view.findViewById(R.id.listview_del_county_btn);
            buttonList.add(button);
            textView.setText(selectedCityList.get(position).getCountyName());

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delCountyId.add(selectedCityList.get(position).getId());
                    delCountyIndex.add(position);
                    selectedCityList.remove(position);
                    Log.d(TAG, "onClick: remove position:" + position);
                    countyAdapter.notifyDataSetChanged();

                }
            });
            //是否点击了edit按钮，
            if (isEditClick) {
                button.setVisibility(View.VISIBLE);
            }else {
                button.setVisibility(View.GONE);
            }
            return view;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

