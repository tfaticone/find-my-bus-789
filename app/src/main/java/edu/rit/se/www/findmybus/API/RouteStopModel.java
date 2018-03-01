package edu.rit.se.www.findmybus.API;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 2/28/2018.
 * Model for our route stop information
 */

public class RouteStopModel {
    //Serialzed Name converts the JSON into our variable name
    @SerializedName("StopId")
    public final String stopId;

    @SerializedName("StopName")
    public final String stopName;

    @SerializedName("Lat")
    public final String lat;

    @SerializedName("Lon")
    public final String lon;

    public RouteStopModel(String stopId, String stopName, String lat, String lon) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.lat = lat;
        this.lon = lon;
    }
}
