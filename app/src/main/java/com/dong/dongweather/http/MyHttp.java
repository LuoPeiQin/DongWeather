package com.dong.dongweather.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by 44607 on 2017/4/29.
 */

public class MyHttp {

    public static void sendRequestOkHttpForGet(final String adress,final MyCallBack myCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(adress);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    String response = convertStreamToString(in);
                    //回调接口函数，让主线程处理
                    //成功
                    myCallBack.onResponse(response);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    //失败
                    myCallBack.onFailure(e);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    myCallBack.onFailure(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    myCallBack.onFailure(e);
                } finally {
                    if (null != connection) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

//    public static void sendRequestOkHttpForPost(String adress, RequestBody requestBody, Response response){
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(adress)
//                .post(requestBody)
//                .build();
//        try {
//            response = client.newCall(request).execute();
//        }catch (IOException e){
//            e.printStackTrace();
//            LogUtil.d(TAG, "sendRequestOkHttp: error");
//        }
//    }
}

