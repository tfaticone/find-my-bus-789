package edu.rit.se.www.findmybus.API;


import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Alex on 2/28/2018.
 */

public interface RouteDataSource {

    @GET("RTRoutes/")
    Observable<RouteResponseModel> getRoutes();

    @GET("RTRouteStops")
    Observable<RouteStopResponseModel> getRouteStops();

}
