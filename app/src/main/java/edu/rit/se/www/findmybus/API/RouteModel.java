package edu.rit.se.www.findmybus.API;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 2/28/2018.
 * Model for our route information
 */

public class RouteModel {
    //Serialzed Name converts the JSON into our variable name
    @SerializedName("RouteId")
        public final String routeId;

    @SerializedName("RouteName")
        public final String routeName;

    @SerializedName("RouteNum")
    public final String routeNum;

    @SerializedName("RouteText")
    public final String routeText;

    public RouteModel(String routeId, String routeName, String routeNum, String routeText) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.routeNum = routeNum;
        this.routeText = routeText;
    }
}
