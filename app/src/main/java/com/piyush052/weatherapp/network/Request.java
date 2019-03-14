package com.piyush052.weatherapp.network;

public class Request<T> {
    private T response;

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
