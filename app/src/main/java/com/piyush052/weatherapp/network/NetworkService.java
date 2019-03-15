package com.piyush052.weatherapp.network;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.piyush052.weatherapp.response.combined.CombinedResult;
import com.piyush052.weatherapp.response.forecast.ForecastResponse;
import com.piyush052.weatherapp.response.weather.WeatherResponse;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class NetworkService {

    private static final String TAG = "Trax_NetworkService";
    private static final Object lock = new Object();
    private static NetworkService mInstance;
    private NetworkEndPoints mRequestInterface;
    private static String BASE_URL = "http://api.apixu.com/v1/";

    private String WEATHER_API_KEY = "89c817e449d74d578ac80831191403";

    private NetworkService() {
        mRequestInterface = getRetrofitService(NetworkEndPoints.class);
    }

    public static NetworkService getInstance() {
        if (mInstance == null) {
            synchronized (lock) {
                if (mInstance == null) {
                    mInstance = new NetworkService();
                }
            }
        }
        return mInstance;
    }

    private <S> S getRetrofitService(Class<S> tClass) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(tClass);
    }

    private OkHttpClient getHttpClient() {

        return new OkHttpClient.Builder()
                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder().headers(getHeaders()).build();
                    return chain.proceed(request);
                })
                .build();
    }

    private HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    private Headers getHeaders() {
        Headers.Builder headers = new Headers.Builder();
//            headers.add("someParam", ""));
        return headers.build();
    }


    public void callWeatherApi(final Request request, final NetworkResponse responseCallback) {
        Call<WeatherResponse> devicesObservable = mRequestInterface
                .getWeatherData(WEATHER_API_KEY, "Bengaluru");

        devicesObservable.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                request.setResponse(response.body());
                responseCallback.onNetworkResponse(request);

            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable throwable) {
                if (throwable instanceof HttpException) {
                    HttpException httpException = (HttpException) throwable;
                    retrofit2.Response response = httpException.response();
                    responseCallback.onNetworkError(request, NetworkException.httpError(response.raw().request().url().toString(), response, null));
                } else
                    // A network error happened
                    if (throwable instanceof IOException) {
                        responseCallback.onNetworkError(request, NetworkException.networkError((IOException) throwable));
                    }
                responseCallback.onNetworkError(request, NetworkException.unexpectedError(throwable));
            }
        });


    }

    public void callBatchApi(LatLng currentLatLng, Request request, NetworkResponse responseCallback) {
        Observable<WeatherResponse> weatherObservable = mRequestInterface
                .getWeatherDataObserver(WEATHER_API_KEY, currentLatLng.latitude+","+currentLatLng.longitude)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());


        Observable<ForecastResponse> forecastObservable = mRequestInterface
                .forecastDataObserver(WEATHER_API_KEY, currentLatLng.latitude+","+currentLatLng.longitude, 5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());


        Observable<CombinedResult> combined = Observable.zip(weatherObservable, forecastObservable,
                (weatherResponse, forecastResponse) -> new CombinedResult(forecastResponse, weatherResponse));

        combined.subscribe(new Subscriber<CombinedResult>() {
            @Override
            public void onCompleted() {
                Log.v("Piyush", "Combined onCompleted");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.v("Piyush", "Combined onError : " + throwable.getMessage());
                //handle errors
                if (throwable instanceof HttpException) {
                    HttpException httpException = (HttpException) throwable;
                    retrofit2.Response response = httpException.response();
                    responseCallback.onNetworkError(null, NetworkException.httpError(response.raw().request().url().toString(), response, null));
                } else
                    // A network error happened
                    if (throwable instanceof IOException) {
                        responseCallback.onNetworkError(null, NetworkException.networkError((IOException) throwable));
                    } else
                        // We don't know what happened. We need to simply convert to an unknown error
                        responseCallback.onNetworkError(null, NetworkException.unexpectedError(throwable));
            }

            @Override
            public void onNext(CombinedResult combinedResult) {
                Log.v("Piyush", "Combined Next : ");

                request.setResponse(combinedResult);

                responseCallback.onNetworkResponse(request);
            }
        });


    }


}
