package edu.rit.se.www.findmybus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Alex on 2/28/2018.
 */

public class MainPresenterTest {
    @Mock
    private RouteDataSource routeDataSource;

    @Mock
    private MainContract.View view;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    MainPresenter mainPresenter = new MainPresenter(
            this.routeDataSource,
            Schedulers.immediate(),
            Schedulers.immediate(),
            this.view
    );

    public void fetchValidDataShouldLoadIntoView() {

        RouteResponseModel routeResponseModel = new RouteResponseModel(0, null, null, null);

        when(routeDataSource.getRoutes())
                .thenReturn(Observable.just(routeResponseModel));

        MainPresenter mainPresenter = new MainPresenter(
                this.routeDataSource,
                Schedulers.immediate(),
                Schedulers.immediate(),
                this.view
        );

        mainPresenter.loadData();

        InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view, times(1)).onFetchDataStarted();
        inOrder.verify(view, times(1)).onFetchDataSuccess(routeResponseModel);
        inOrder.verify(view, times(1)).onFetchDataCompleted();

    }

    @Test
    public void fetchErrorShouldReturnErrorToView() {

        Exception exception = new Exception();

        when(routeDataSource.getRoutes())
                .thenReturn(Observable.<RouteResponseModel>error(exception));

        MainPresenter mainPresenter = new MainPresenter(
                this.routeDataSource,
                Schedulers.immediate(),
                Schedulers.immediate(),
                this.view
        );

        mainPresenter.loadData();

        InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view, times(1)).onFetchDataStarted();
        inOrder.verify(view, times(1)).onFetchDataError();
        verify(view, never()).onFetchDataCompleted();
    }
}
