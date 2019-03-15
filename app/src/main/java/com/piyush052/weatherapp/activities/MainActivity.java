package com.piyush052.weatherapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.piyush052.weatherapp.Dialog.Loader;
import com.piyush052.weatherapp.R;
import com.piyush052.weatherapp.network.NetworkException;
import com.piyush052.weatherapp.network.NetworkResponse;
import com.piyush052.weatherapp.network.NetworkService;
import com.piyush052.weatherapp.network.Request;
import com.piyush052.weatherapp.response.combined.CombinedResult;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int GEOLOCATION_REQUEST = 101;
    private LinearLayout errorLayout;
    private Loader loader;

    private MainActivity context;
    private LatLng currentLatLng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        hide the action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        loader = new Loader(this);
        context= this;

        errorLayout = findViewById(R.id.errorLayout);
        Button retryButton = findViewById(R.id.retryButton);

        retryButton.setOnClickListener(v -> getLocation());

       // fetchData(currentLatLng);

        getLocation();
    }


    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GEOLOCATION_REQUEST);
        } else {
            if (currentLatLng != null) {
               // fetch API
                fetchData(currentLatLng);
            } else {

                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                fetchData(currentLatLng);
                            }else {
                                showErrorLayout();
                                Toast.makeText(context, R.string.failed_fetch_location, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Error trying to get last GPS location");
                            e.printStackTrace();
                            Toast.makeText(context, R.string.failed_fetch_location, Toast.LENGTH_SHORT).show();
                        });

            }
        }
    }


    /**
     * Fetch data from server
     * @param currentLatLng
     */
    private void fetchData(LatLng currentLatLng) {
        hideLoader();
        showLoader();


        NetworkService.getInstance().callBatchApi(currentLatLng,new Request(), new NetworkResponse() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onNetworkResponse(Request request) {
                hideLoader();
                // set initial state
                if (request.getResponse() instanceof CombinedResult) {
                    hideErrorLayout();
                    CombinedResult combinedResult = (CombinedResult) request.getResponse();

                    ((TextView) findViewById(R.id.tempValue)).setText(Math.round(combinedResult.weatherResponse.getCurrent().getTempC()) + "");
                    ((TextView) findViewById(R.id.place)).setText(combinedResult.weatherResponse.getLocation().getName() + "");
                } else {
                    // Something went wrong
                    showErrorLayout();
                }
            }

            @Override
            public void onNetworkError(Request request, NetworkException ex) {
                Log.e(TAG, "onNetworkError: ");
                hideLoader();
                showErrorLayout();
                Toast.makeText(MainActivity.this, ex.getKind() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoader() {
        if (loader != null && !loader.isShowing())
            loader.show();
    }

    private void hideLoader() {
        if (loader != null)
            loader.dismiss();

    }

    private void showErrorLayout() {
        showLoader();
       // errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        errorLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
            loader = null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (requestCode == GEOLOCATION_REQUEST) {
            getLocation();
        }
    }
}
