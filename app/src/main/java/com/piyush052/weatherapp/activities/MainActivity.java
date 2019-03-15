package com.piyush052.weatherapp.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.piyush052.weatherapp.Dialog.Loader;
import com.piyush052.weatherapp.R;
import com.piyush052.weatherapp.network.NetworkException;
import com.piyush052.weatherapp.network.NetworkResponse;
import com.piyush052.weatherapp.network.NetworkService;
import com.piyush052.weatherapp.network.Request;
import com.piyush052.weatherapp.response.combined.CombinedResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NetworkResponse {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int GEOLOCATION_REQUEST = 101;
    private LinearLayout errorLayout;
    private Loader loader;

    private MainActivity context;
    private LatLng currentLatLng;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        hide the action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        loader = new Loader(this);
        context = this;

        errorLayout = findViewById(R.id.errorLayout);
        Button retryButton = findViewById(R.id.retryButton);

        retryButton.setOnClickListener(v -> getLocation());

        // fetchData(currentLatLng);

        getLocation();
    }


    public void showAnimation() {

        FrameLayout view = findViewById(R.id.frameLayout);

        final int TRANSLATION_Y = view.getHeight();
        view.setTranslationY(TRANSLATION_Y);
        view.setVisibility(View.GONE);
        view.animate()
                .translationYBy(-TRANSLATION_Y)
                .setDuration(500)
                .setStartDelay(200)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(final Animator animation) {

                        view.setVisibility(View.VISIBLE);
                    }
                })
                .start();


    }

    private void getLocation() {
        showLoader();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GEOLOCATION_REQUEST);
        } else {
            if (currentLatLng != null) {
                // fetch API
                fetchData(currentLatLng);
            } else {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                fetchData(currentLatLng);
                            } else {
                                showLoader();
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationRequest.setInterval(10 * 1000); // 10 seconds
                                locationRequest.setFastestInterval(5 * 1000); // 5 seconds
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

                                //showErrorLayout();
                                Toast.makeText(context, R.string.failed_fetch_location, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    fetchData(currentLatLng);

                    if (mFusedLocationClient != null) {
                        mFusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        }
    };

    private String getDay(String input) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = inFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        if (date != null) {
            return outFormat.format(date);
        } else return input;
    }

    /**
     * Fetch data from server
     *
     * @param currentLatLng
     */
    private void fetchData(LatLng currentLatLng) {

        NetworkService.getInstance().callBatchApi(currentLatLng, new Request(), this);
    }

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

            showAnimation();

            ((TextView) findViewById(R.id.day1)).setText(getDay(combinedResult.forecastResponse.getForecast().getForecastday().get(1).getDate()) + "");
            ((TextView) findViewById(R.id.day1Value)).setText(Math.round(combinedResult.forecastResponse.getForecast().getForecastday().get(1).getDay().getAvgtempC()) + " C");

            ((TextView) findViewById(R.id.day2)).setText(getDay(combinedResult.forecastResponse.getForecast().getForecastday().get(2).getDate()) + "");
            ((TextView) findViewById(R.id.day2Value)).setText(Math.round(combinedResult.forecastResponse.getForecast().getForecastday().get(2).getDay().getAvgtempC()) + " C");

            ((TextView) findViewById(R.id.day3)).setText(getDay(combinedResult.forecastResponse.getForecast().getForecastday().get(3).getDate()) + "");
            ((TextView) findViewById(R.id.day3Value)).setText(Math.round(combinedResult.forecastResponse.getForecast().getForecastday().get(3).getDay().getAvgtempC()) + " C");

            ((TextView) findViewById(R.id.day4)).setText(getDay(combinedResult.forecastResponse.getForecast().getForecastday().get(4).getDate()) + "");
            ((TextView) findViewById(R.id.day4Value)).setText(Math.round(combinedResult.forecastResponse.getForecast().getForecastday().get(4).getDay().getAvgtempC()) + " C");


        } else {
            // Something went wrong
            showErrorLayout();
        }
    }

    @Override
    public void onNetworkError(Request request, NetworkException ex) {
        Log.e(TAG, "onNetworkError: ", ex);
        hideLoader();
        showErrorLayout();
        Toast.makeText(MainActivity.this, ex.getKind() + "", Toast.LENGTH_SHORT).show();
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
        if (loader != null && loader.isShowing()) loader.dismiss();
        errorLayout.setVisibility(View.VISIBLE);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GEOLOCATION_REQUEST) {
            // if user is not providing permission then ask again for permission of go for the location
            getLocation();
        }
    }

}
