package edu.rit.se.www.findmybus.API;

/**
 * Created by Alex on 2/28/2018.
 */

public interface MainContract {

    interface View {

        void onFetchDataStarted();

        void onFetchDataCompleted();

        void onFetchDataSuccess(RouteResponseModel routeResponseModel);

        void onFetchDataError();
    }

    interface Presenter {

        void loadData();

        void subscribe();

        void unsubscribe();

        void onDestroy();

    }
}
