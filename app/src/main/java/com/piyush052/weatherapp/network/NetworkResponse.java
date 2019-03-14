package com.piyush052.weatherapp.network;


public interface NetworkResponse {

    public void onNetworkResponse(Request request);

    public void onNetworkError(Request request, NetworkException ex);
}
