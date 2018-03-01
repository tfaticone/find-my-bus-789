package edu.rit.se.www.findmybus.API;


import rx.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alex on 2/28/2018.
 *
 */

public class RouteRemoteDataSource implements RouteDataSource {
        private RouteDataSource api;
        private final String URL = "http://api.rgrta.com/"; //api URL

        public RouteRemoteDataSource() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            this.api = retrofit.create(RouteDataSource.class);
        }

        @Override
        public Observable<RouteResponseModel> getRoutes() {
            return this.api.getRoutes();
        }

        @Override
        public Observable<RouteStopResponseModel> getRouteStops() {
            return this.api.getRouteStops();
        }
}
