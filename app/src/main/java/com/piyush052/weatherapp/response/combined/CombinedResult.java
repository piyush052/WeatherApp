package com.piyush052.weatherapp.response.combined;

import com.piyush052.weatherapp.response.forecast.ForecastResponse;
import com.piyush052.weatherapp.response.weather.WeatherResponse;

public class CombinedResult {
    public ForecastResponse forecastResponse;
   public WeatherResponse weatherResponse;

    public CombinedResult(ForecastResponse forecastResponse, WeatherResponse weatherResponse) {
        this.forecastResponse = forecastResponse;
        this.weatherResponse = weatherResponse;
    }
}
