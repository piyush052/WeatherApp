package com.piyush052.weatherapp.network;

import com.piyush052.weatherapp.response.forecast.ForecastResponse;
import com.piyush052.weatherapp.response.weather.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;

public interface NetworkEndPoints {

    @GET("current.json")
    Call<WeatherResponse> getWeatherData(@Query("key") String key, @Query("q") String cityName);

    @GET("current.json")
    Observable<WeatherResponse> getWeatherDataObserver(@Query("key") String key, @Query("q") String cityName);

    @GET("forecast.json")
    Call<ForecastResponse> forecastData(@Query("key") String key, @Query("q") String cityName, @Query("days") int days);

    @GET("forecast.json")
    Observable<ForecastResponse> forecastDataObserver(@Query("key") String key, @Query("q") String cityName, @Query("days") int days);

}
