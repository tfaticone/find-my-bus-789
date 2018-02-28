package edu.rit.se.www.findmybus;


import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Alex on 2/28/2018.
 */

public interface RouteDataSource {

    @GET("route/")
    Observable<RouteResponseModel> getRoutes();

}
