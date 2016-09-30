package com.example.tyutyu.rk_1_tyul;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import ru.mail.weather.lib.*;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        City city = WeatherStorage.getInstance(MainActivity.this).getCurrentCity();

        Button cityActivity = (Button) findViewById(R.id.buttonCity);
        cityActivity.setText(city.name());
        cityActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCityActivity();
            }
        });

        Button back = (Button) findViewById(R.id.buttonBackG);
        back.setText("Обновлять в фоне");
        back.setOnClickListener(new View.OnClickListener() {
            //schedule
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeatherService.class);
                intent.setAction(WeatherService.WEATHER_LOAD_ACTION);
                WeatherUtils.getInstance().schedule(MainActivity.this, intent);
            }
        });

        Button noBack = (Button) findViewById(R.id.buttonNoBackG);
        noBack.setText("Не обновлять в фоне");
        noBack.setOnClickListener(new View.OnClickListener() {
            //unSchedule
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeatherService.class);
                intent.setAction(WeatherService.WEATHER_LOAD_ACTION);
                WeatherUtils.getInstance().unschedule(MainActivity.this, intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        final IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherService.WEATHER_CHANGED_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                MainActivity.this.onUpdate();
            }
        };

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (receiver != null) {
            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        Button cityActivity = (Button) findViewById(R.id.buttonCity);
        cityActivity.setText(WeatherStorage.getInstance(MainActivity.this).getCurrentCity().name());
        Intent intent = new Intent(MainActivity.this, WeatherService.class);
        intent.setAction(WeatherService.WEATHER_LOAD_ACTION);
        startService(intent);
        onUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    private void startCityActivity() {
        Intent intent = new Intent(this, CityActivity.class);
        startActivity(intent);
    }

    protected void onUpdate() {
        Weather weather = WeatherStorage.getInstance(MainActivity.this).getLastSavedWeather(WeatherStorage.getInstance(MainActivity.this).getCurrentCity());
        TextView weatherText = (TextView)findViewById(R.id.textD);
        String text;
        if (weather == null) {
            text = "Error";
        } else {
            text = Integer.toString(weather.getTemperature()) + " " + weather.getDescription();
        }
        weatherText.setText(text);
    }
}
