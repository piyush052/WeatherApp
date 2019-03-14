package com.piyush052.weatherapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.piyush052.weatherapp.R;
import com.piyush052.weatherapp.network.NetworkException;
import com.piyush052.weatherapp.network.NetworkResponse;
import com.piyush052.weatherapp.network.NetworkService;
import com.piyush052.weatherapp.network.Request;
import com.piyush052.weatherapp.response.combined.CombinedResult;

public class MainActivity extends AppCompatActivity {

    LinearLayout errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        hide the action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        errorLayout = findViewById(R.id.errorLayout);

        fetchData();
    }

    private void fetchData() {
        NetworkService.getInstance().callBatchApi(new Request(), new NetworkResponse() {
            @Override
            public void onNetworkResponse(Request request) {
                // set initial state
                if (request.getResponse() instanceof CombinedResult) {
                    errorLayout.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.text)).setText(new Gson().toJson(request.getResponse()));
                    ((TextView) findViewById(R.id.secondText)).setText(new Gson().toJson(request.getResponse()));
                } else {
                    // Something wrong
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNetworkError(Request request, NetworkException ex) {
                errorLayout.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, ex.getKind() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
