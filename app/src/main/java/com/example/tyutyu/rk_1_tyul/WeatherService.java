package com.example.tyutyu.rk_1_tyul;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.lang.UnsupportedOperationException;

import ru.mail.weather.lib.*;

public class WeatherService extends IntentService {

    public final static String WEATHER_LOAD_ACTION = "ru.mail.park.WEATHER_LOAD_ACTION";
    public final static String WEATHER_ERROR_ACTION = "ru.mail.park.WEATHER_ERROR_ACTION";
    public final static String WEATHER_CHANGED_ACTION = "ru.mail.park.WEATHER_CHANGED_ACTION";


    public WeatherService() {
        super("WeatherService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        if (intent != null && WEATHER_LOAD_ACTION.equals(intent.getAction())) {
            try {
                City city = WeatherStorage.getInstance(WeatherService.this).getCurrentCity();
                Weather weather = WeatherUtils.getInstance().loadWeather(city);
                WeatherStorage.getInstance(this).saveWeather(city, weather);
                broadcastManager.sendBroadcast(new Intent(WEATHER_CHANGED_ACTION));
            } catch (IOException e) {
                broadcastManager.sendBroadcast(new Intent(WEATHER_ERROR_ACTION));
            }
        }
    }
}