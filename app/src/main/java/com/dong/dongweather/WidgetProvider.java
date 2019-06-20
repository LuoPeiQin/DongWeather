package com.dong.dongweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.dong.dongweather.http.OkHttp;
import com.dong.dongweather.service.ListViewService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by 44607 on 2017/4/24.
 */

public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WEATHER_WIDGET";
    
    public static final String SKIP_COUNTY_WEATHER = "com.dong.dongweather.action.SKIP_COUNTY_WEATHER";

    private RemoteViews mRemoteViews;
    private ComponentName mComponentName;

    private Bitmap pngBM;

    private Context mContent;
    private boolean isMark = false;
    private String bingPic = null;
    private AppWidgetTarget appWidgetTarget;


    private Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length /1024 > 256) {//判断如果图片大于256kb,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        Log.d(TAG, "onResponse: change bitmap size=" + bitmap.getByteCount());
        return bitmap;
    }

    /**
     * 更新背景图片
     */
    public void updateBingPic(final Context context) {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        OkHttp.sendRequestOkHttpForGet(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                isMark = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bingPic = response.body().string();

//                URL picUrl = new URL(bingPic);
// //               URL picUrl = new URL("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
//                pngBM = BitmapFactory.decodeStream(picUrl.openStream());
//                Log.d(TAG, "onUpdate: bingPic = " + pngBM);
//                Log.d(TAG, "onResponse: bitmap size=" + pngBM.getByteCount());
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
                SharedPreferences.Editor spEdit = context.getSharedPreferences("ListViewService", MODE_PRIVATE).edit();
                spEdit.putString("bing_pic", bingPic);
                spEdit.commit();
                Log.d(TAG, "onUpdate: Uri:" + Uri.parse(bingPic));
                isMark = true;
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (null != bingPic) {
                        Log.d(TAG, "handleMessage: receive");
//                        mRemoteViews.setImageViewBitmap(R.id.widget_bg_iv, comp(pngBM));
//                        AppWidgetManager manager = AppWidgetManager.getInstance(mContent);
//                        mComponentName = new ComponentName(mContent, WidgetProvider.class);
//                        manager.updateAppWidget(mComponentName, mRemoteViews);
                        LogUtil.d(TAG, "onUpdate: bingPic = " + bingPic);
                        if (null != bingPic) {
//                            Glide.with( mContent.getApplicationContext() ) // safer!
//                                    .load(bingPic)
//                                    .asBitmap()
//                                    .override(600, 200)
//                                    .into(appWidgetTarget);
                        }
                        AppWidgetManager manager = AppWidgetManager.getInstance(mContent);
                        mComponentName = new ComponentName(mContent, WidgetProvider.class);
                        manager.updateAppWidget(mComponentName, mRemoteViews);

                    }
                default:
                    break;
            }
        }
    };
    //  在更新 widget 时，被执行
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: ");
        updateBingPic(context);
        mContent = context;
        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_provider);

        //点击跳到天气活动
        Intent skipIntent = new Intent(context, WeatherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, skipIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.widget_skip_tv, pi);

        SharedPreferences sp = context.getSharedPreferences("ListViewService", MODE_PRIVATE);
        String bingPic = sp.getString("bing_pic", null);
        LogUtil.d(TAG, "onUpdate: bingPic = " + bingPic);

        //绑定Widget和ListViewService
        Intent lvIntent = new Intent(context, ListViewService.class);

        //mRemoteViews.setImageViewUri(R.id.widget_bg_iv, Uri.parse(bingPic));
        //等待异步线程处理完毕

        mRemoteViews.setRemoteAdapter(R.id.widget_listview, lvIntent);
        mRemoteViews.setEmptyView(R.id.widget_listview,android.R.id.empty);
        mComponentName = new ComponentName(context, WidgetProvider.class);

        // 设置响应 ListView 的intent模板
        // 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
        // 它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
        // (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
        // (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
        Intent toIntent = new Intent(SKIP_COUNTY_WEATHER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 200, toIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setPendingIntentTemplate(R.id.widget_listview, pendingIntent);

        appWidgetTarget = new AppWidgetTarget( mContent, R.id.widget_bg_iv, mRemoteViews, appWidgetIds );
        if (null != bingPic) {
//            Glide.with( mContent.getApplicationContext() ) // safer!
//                    .load(bingPic)
//                    .override(400, 280)
//                    .into(appWidgetTarget);
        }
//        while (!isMark){}
//        if (null != pngBM) {
//            Log.d(TAG, "handleMessage: receive 111");
//            mRemoteViews.setImageViewBitmap(R.id.widget_bg_iv, pngBM);
//        }

        mComponentName = new ComponentName(context, WidgetProvider.class);
        appWidgetManager.updateAppWidget(mComponentName, mRemoteViews);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetId = manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        manager.notifyAppWidgetViewDataChanged(appWidgetId[0], R.id.widget_listview);

    }
        // 设置 ListView 的adapter。
        // (01) intent: 对应启动 ListViewService(RemoteViewsService) 的intent
        // (02) setRemoteAdapter: 设置 ListView 的适配器
        // 通过setRemoteAdapter将 ListView 和ListViewService关联起来，
        // 以达到通过 GridWidgetService 更新 gridview 的目的

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: ");
        if (TextUtils.equals(SKIP_COUNTY_WEATHER, intent.getAction())) {
            Bundle extras = intent.getExtras();
            int position = extras.getInt(ListViewService.INITENT_DATA);
            Log.d(TAG, "onReceive: position : " + position);
            Intent skipIntent = new Intent(context, WeatherActivity.class);
            skipIntent.putExtra("skipPosition", position);
            skipIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(skipIntent);
            //AppWidgetManager.getInstance(context).updateAppWidget(mComponentName, mRemoteViews);
        }
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] appWidgetId = manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
            if (appWidgetId.length > 0) {
                manager.notifyAppWidgetViewDataChanged(appWidgetId[0], R.id.widget_listview);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "onDeleted: ");
    }

    // 最后一个widget被删除时调用
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    // 第一个widget被创建时调用
    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled: ");
//        // 在第一个 widget 被创建时，开启服务
//          做法：可以在manifest中
//        <service android:name=".ExampleAppWidgetService" >
//            <intent-filter>
//                <action android:name="android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE" />
//            </intent-filter>
//        </service>
//        context.startService(EXAMPLE_SERVICE_INTENT);
        super.onEnabled(context);
    }
}
