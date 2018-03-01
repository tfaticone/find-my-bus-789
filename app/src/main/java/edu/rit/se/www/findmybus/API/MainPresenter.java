package edu.rit.se.www.findmybus.API;

/**
 * Created by Alex on 2/28/2018.
 */

import android.support.annotation.NonNull;

import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainPresenter{
    @NonNull
    private RouteDataSource routeDataSource;

    @NonNull
    private Scheduler backgroundScheduler;

    @NonNull
    private Scheduler mainScheduler;

    @NonNull
    private CompositeSubscription subscriptions;

    private MainContract.View view;

    public MainPresenter(
        @NonNull RouteDataSource routeDataSource,
        @NonNull Scheduler backgroundScheduler,
        @NonNull Scheduler mainScheduler,
        MainContract.View view) {
            this.routeDataSource = routeDataSource;
            this.backgroundScheduler = backgroundScheduler;
            this.mainScheduler = mainScheduler;
            this.view = view;
            subscriptions = new CompositeSubscription();
        }

        public void subscribe() {
            loadData();
        }

        public void unsubscribe() {
            subscriptions.clear();
        }

        public void onDestroy() {
            this.view = null;
        }

        public void loadData() {
            view.onFetchDataStarted();
            subscriptions.clear();

            Subscription subscription = routeDataSource
                    .getRoutes()
                    .subscribeOn(backgroundScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(new Observer<RouteResponseModel>() {
                        @Override
                        public void onCompleted() {
                            view.onFetchDataCompleted();
                        }

                        @Override
                        public void onError(Throwable e) {
                            view.onFetchDataError();
                        }

                        @Override
                        public void onNext(RouteResponseModel rootModel) {
                            view.onFetchDataSuccess(rootModel);
                        }
                    });

            subscriptions.add(subscription);
        }
    }
