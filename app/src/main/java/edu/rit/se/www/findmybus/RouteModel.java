package edu.rit.se.www.findmybus;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 2/28/2018.
 */

public class RouteModel {
    public String routeNumber;

    @SerializedName("bus_number")
        public String busNumber;
}
