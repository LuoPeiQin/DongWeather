package com.dong.dongweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.dong.dongweather.http.OkHttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService{
//    public AutoUpdateService() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        updateBingPic();
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        int anTime = 8 * 60 * 60 *1000;
//        long triggerAtTime = SystemClock.elapsedRealtime() + anTime;
//        Intent intentSelf = new Intent(this, AutoUpdateService.class);
//        PendingIntent pi = PendingIntent.getService(this, 0, intentSelf, 0);
//        alarmManager.cancel(pi);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        return super.onStartCommand(intent, flags, startId);
//    }

}
