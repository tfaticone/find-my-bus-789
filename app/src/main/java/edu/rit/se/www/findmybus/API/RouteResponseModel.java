package edu.rit.se.www.findmybus.API;

import java.util.List;

/**
 * Created by Alex on 2/28/2018.
 */

public class RouteResponseModel {

    public final int count;
    public final String next;
    public final String previous;
    public final List<RouteModel> results;

    public RouteResponseModel(int count, String next, String previous, List<RouteModel> results){
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }
}
