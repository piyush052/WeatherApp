package com.piyush052.weatherapp;

import android.support.annotation.NonNull;

import com.piyush052.weatherapp.network.NetworkException;
import com.piyush052.weatherapp.network.NetworkResponse;
import com.piyush052.weatherapp.network.NetworkService;
import com.piyush052.weatherapp.network.Request;
import com.piyush052.weatherapp.response.combined.CombinedResult;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Scheduler;
import rx.subscriptions.CompositeSubscription;

import static org.mockito.Mockito.when;

public class MainPresenterTest implements NetworkResponse {


    @Mock
    CombinedResult combinedResult;

    @NonNull
    private Scheduler backgroundScheduler;

    @NonNull
    private Scheduler mainScheduler;

    @NonNull
    private CompositeSubscription subscriptions;

    public MainPresenterTest(
            @NonNull CombinedResult combinedResult,
            @NonNull Scheduler backgroundScheduler,
            @NonNull Scheduler mainScheduler
            ) {
        this.combinedResult = combinedResult;
        this.backgroundScheduler = backgroundScheduler;
        this.mainScheduler = mainScheduler;
        subscriptions = new CompositeSubscription();
    }


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);


    }


    @Test
    public void fetchValidDataShouldLoadIntoView() {
//        when(NetworkService.getInstance().callBatchApi(null,new Request(),this))
//                .thenReturn(Observable.just(combinedResult));

    }

    @Override
    public void onNetworkResponse(Request request) {

    }

    @Override
    public void onNetworkError(Request request, NetworkException ex) {

    }
}
