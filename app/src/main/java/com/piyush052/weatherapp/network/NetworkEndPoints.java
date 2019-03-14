package com.piyush052.weatherapp.network;

import com.piyush052.weatherapp.response.WeatherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetworkEndPoints {

    @GET("current.json")
    Call<WeatherResponse> getWeatherData(@Query("key") String key, @Query("q") String cityName);

}
