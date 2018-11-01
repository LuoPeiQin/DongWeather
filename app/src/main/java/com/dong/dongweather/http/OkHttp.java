package com.dong.dongweather.http;

import android.net.Uri;
import android.util.Log;

import org.litepal.util.LogUtil;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/17.
 */

public class OkHttp {

    private static final String TAG = "OkHttp";

    public static void sendRequestOkHttpForGet(String adress,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(adress)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendRequestOkHttpForPost(String adress, RequestBody requestBody, Response response){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(adress)
                .post(requestBody)
                .build();
        try {
            response = client.newCall(request).execute();
        }catch (IOException e){
            e.printStackTrace();
            LogUtil.d(TAG, "sendRequestOkHttp: error");
        }
    }
}
