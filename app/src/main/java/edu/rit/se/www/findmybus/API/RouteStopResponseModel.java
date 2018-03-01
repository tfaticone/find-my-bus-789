package edu.rit.se.www.findmybus.API;

import java.util.List;

/**
 * Created by Alex on 2/28/2018.
 */

public class RouteStopResponseModel {

    public final int count;
    public final String next;
    public final String previous;
    public final List<RouteStopModel> results;

    public RouteStopResponseModel(int count, String next, String previous, List<RouteStopModel> results){
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }
}
