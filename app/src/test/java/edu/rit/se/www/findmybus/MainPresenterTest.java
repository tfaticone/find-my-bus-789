package edu.rit.se.www.findmybus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;

import edu.rit.se.www.findmybus.API.MainContract;
import edu.rit.se.www.findmybus.API.MainPresenter;
import edu.rit.se.www.findmybus.API.RouteDataSource;
import edu.rit.se.www.findmybus.API.RouteModel;
import edu.rit.se.www.findmybus.API.RouteResponseModel;
import edu.rit.se.www.findmybus.API.RouteStopModel;
import edu.rit.se.www.findmybus.API.RouteStopResponseModel;
import rx.Observable;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
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

    @Test
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

    @Test
    public void validateGetRoutes() {
        RouteModel data = new RouteModel("1", "1 - Lake / Park", "1", "Lake / Park");
        ArrayList<RouteModel> results = new ArrayList<>();
        results.add(data);

        RouteResponseModel routeResponseModel = new RouteResponseModel(0, null, null, results);

        when(routeDataSource.getRoutes())
                .thenReturn(Observable.just(routeResponseModel));

        assertEquals(0, routeResponseModel.count);
        assertEquals("1", routeResponseModel.results.get(0).routeId);
        assertEquals("1 - Lake / Park", routeResponseModel.results.get(0).routeName);
        assertEquals("1", routeResponseModel.results.get(0).routeNum);
        assertEquals("Lake / Park", routeResponseModel.results.get(0).routeText);
    }

    @Test
    public void validateGetRouteStops() {
        RouteStopModel data = new RouteStopModel("93", "Beach & 411 Beach", "43.262171", "-77.614512");
        ArrayList<RouteStopModel> results = new ArrayList<>();
        results.add(data);

        RouteStopResponseModel routeStopResponseModel = new RouteStopResponseModel(1, null, null, results);

        when(routeDataSource.getRouteStops())
                .thenReturn(Observable.just(routeStopResponseModel));

        assertEquals(1, routeStopResponseModel.count);
        assertEquals("93", routeStopResponseModel.results.get(0).stopId);
        assertEquals("Beach & 411 Beach", routeStopResponseModel.results.get(0).stopName);
        assertEquals("43.262171", routeStopResponseModel.results.get(0).lat);
        assertEquals("-77.614512", routeStopResponseModel.results.get(0).lon);
    }

    @Test
    public void validateMultipleGetRouteStops() {
        RouteStopModel stop1 = new RouteStopModel("93", "Beach & 411 Beach", "43.262171", "-77.614512");
        RouteStopModel stop2 = new RouteStopModel("94", "Beach & 412 Beach", "44.262171", "-76.614512");

        ArrayList<RouteStopModel> results = new ArrayList<>();
        results.add(stop1);
        results.add(stop2);

        RouteStopResponseModel routeStopResponseModel = new RouteStopResponseModel(2, null, null, results);

        when(routeDataSource.getRouteStops())
                .thenReturn(Observable.just(routeStopResponseModel));

        assertEquals(2, routeStopResponseModel.count);
        assertEquals("93", routeStopResponseModel.results.get(0).stopId);
        assertEquals("Beach & 411 Beach", routeStopResponseModel.results.get(0).stopName);
        assertEquals("43.262171", routeStopResponseModel.results.get(0).lat);
        assertEquals("-77.614512", routeStopResponseModel.results.get(0).lon);
        assertEquals("94", routeStopResponseModel.results.get(1).stopId);
        assertEquals("Beach & 412 Beach", routeStopResponseModel.results.get(1).stopName);
        assertEquals("44.262171", routeStopResponseModel.results.get(1).lat);
        assertEquals("-76.614512", routeStopResponseModel.results.get(1).lon);
    }
}
