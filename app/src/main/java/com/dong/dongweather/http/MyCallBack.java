package com.dong.dongweather.http;

import java.io.IOException;

/**
 * Created by 44607 on 2017/4/29.
 */

public interface MyCallBack {

        void onFailure(IOException e);

        void onResponse(String response) throws IOException;
}
