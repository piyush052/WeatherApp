package com.piyush052.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.piyush052.weatherapp.network.NetworkException;
import com.piyush052.weatherapp.network.NetworkResponse;
import com.piyush052.weatherapp.network.NetworkService;
import com.piyush052.weatherapp.network.Request;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkService.getInstance().callWeatherApi(new Request(), new NetworkResponse() {
            @Override
            public void onNetworkResponse(Request request) {
                ((TextView)findViewById(R.id.text)).setText(new Gson().toJson(request.getResponse()));
            }

            @Override
            public void onNetworkError(Request request, NetworkException ex) {
                Toast.makeText(MainActivity.this, ex.getKind()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
