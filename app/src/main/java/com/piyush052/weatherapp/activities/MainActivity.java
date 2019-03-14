package com.piyush052.weatherapp.activities;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.piyush052.weatherapp.Dialog.Loader;
import com.piyush052.weatherapp.R;
import com.piyush052.weatherapp.network.NetworkException;
import com.piyush052.weatherapp.network.NetworkResponse;
import com.piyush052.weatherapp.network.NetworkService;
import com.piyush052.weatherapp.network.Request;
import com.piyush052.weatherapp.response.combined.CombinedResult;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    LinearLayout errorLayout;
    Button retryButton;

    Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        hide the action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        loader = new Loader(this);

        errorLayout = findViewById(R.id.errorLayout);
        retryButton = findViewById(R.id.retryButton);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });

        fetchData();
    }

    /**
     * Fetch data from server
     */
    private void fetchData() {
        hideLoader();
        showLoader();


        NetworkService.getInstance().callBatchApi(new Request(), new NetworkResponse() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onNetworkResponse(Request request) {
                hideLoader();
                // set initial state
                if (request.getResponse() instanceof CombinedResult) {
                    hideErrorLayout();
                    CombinedResult combinedResult = (CombinedResult) request.getResponse();

                    ((TextView) findViewById(R.id.tempValue)).setText(Math.round(combinedResult.weatherResponse.getCurrent().getTempC())+ "");
                    ((TextView) findViewById(R.id.secondText)).setText(new Gson().toJson(request.getResponse()));
                } else {
                    // Something wrong
                    showErrorLayout();
                }
            }

            @Override
            public void onNetworkError(Request request, NetworkException ex) {
                Log.e(TAG, "onNetworkError: " );
                hideLoader();
                showErrorLayout();
                Toast.makeText(MainActivity.this, ex.getKind() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoader(){
        loader.show();
    }
    private void hideLoader(){
        loader.dismiss();

    }

    private void showErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        errorLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loader!=null && loader.isShowing()){
            loader.dismiss();
            loader= null;
        }
    }
}
