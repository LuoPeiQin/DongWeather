package com.dong.dongweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {

    private List<HourlyWeather> hourlyWeatherList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView hourlyTimeTV;
        ImageView hourlyWeatherImageV;
        TextView hourlyTemperatureTV;

        public ViewHolder(View view){
            super(view);
            hourlyTimeTV = (TextView) view.findViewById(R.id.hourly_time_tv);
            hourlyWeatherImageV = (ImageView) view.findViewById(R.id.hourly_weather_iv);
            hourlyTemperatureTV = (TextView) view.findViewById(R.id.hourly_temperature_tv);
        }
    }

    public HourlyWeatherAdapter(List<HourlyWeather> hourlyWeatherList){
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_forecast_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
        holder.hourlyTimeTV.setText(hourlyWeather.hourlyTime + "时");
        holder.hourlyWeatherImageV.setImageBitmap(hourlyWeather.hourlyImageBit);
        holder.hourlyTemperatureTV.setText(hourlyWeather.hourlyTemperature + "º");
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }


}
