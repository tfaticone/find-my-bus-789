package edu.rit.se.www.findmybus;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.net.URI;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Vibrator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import edu.rit.se.www.findmybus.API.RouteConnection;

public class CurrentBusInfoActivity extends AppCompatActivity {
    TextToSpeech talker;
    Integer routeID = null;
    private float distance = 0;
    private float heading = 0;
    Timer timer;
    TextView routeListText = null;
    TextView etaText = null;
    TextView routeNameText = null;
    RequestQueue requestQueue;
    String nextStopID = null;
    String stopDescription = null;
    String eta = null;
    Location vehicle = null;
    String routeName = null;
    Timer vibTimer = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private static Location savedLocation = null;
    private Vibrator myVib;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestQueue = Volley.newRequestQueue(this);  // This setups up a new request queue which we will need to make HTTP requests.

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        setContentView(R.layout.activity_current_bus_info);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Bundle bundle = getIntent().getExtras();

        if(bundle.getString("routeID")!=null) {
            routeID = Integer.parseInt(bundle.getString("routeID"));
            routeListText = (TextView) findViewById(R.id.routeList);
            etaText = (TextView) findViewById(R.id.eta);
            routeNameText = (TextView) findViewById(R.id.routeName);
        }



        Button setLocationBtn = (Button) findViewById(R.id.currentLocation);
        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurrentLocation();
            }
        })  ;
    }

    @Override
    protected void onStop() {
        super.onStop();
        talker.stop();
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        if(vibTimer != null) {
            vibTimer.cancel();
            vibTimer = null;
        }
    }

    protected void onStart() {
        super.onStart();
        getRouteList(routeID, 1);
        getRouteList(routeID, 2);
        getRouteList(routeID, 3);
        getRouteList(routeID, 4);
        talker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    talker.setLanguage(Locale.US);
                    startTimedUpdates();
                }
            }
        });
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    private void updateDisplayValues(){
        final TextView busDistanceLabel = (TextView) findViewById(R.id.busDistance);
        final TextView busHeadingLabel = (TextView) findViewById(R.id.busHeading);
        try {
            Log.e("Display", Boolean.toString(mFusedLocationClient == null));
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                if(savedLocation != null){
                                    distance = (float)(((double)location.distanceTo(savedLocation)) * 0.000621371); //meters to miles
                                    heading = location.bearingTo(savedLocation);
                                } else {
                                    distance = (float)(((double)location.distanceTo(vehicle)) * 0.000621371); //meters to miles
                                    heading = location.bearingTo(vehicle);
                                }
                            } else {
                                Log.e("DISTANCE", "TIS FUCING EMPTY");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        } catch (SecurityException se) {
            Log.e("Display", "Security Exception");

        }


        CurrentBusInfoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Double DisplayDistance = Math.round(distance * 100) / 100.0;
                Double DisplayHeading = Math.round(heading * 100) / 100.0;
                busDistanceLabel.setText(Double.toString(DisplayDistance) + " miles");
                busHeadingLabel.setText(Double.toString(DisplayHeading) + " degrees");
                Log.e("DISTANCE", Float.toString(distance));
                if(distance < 0.0189394) { //Under 100 feet ( in miles )
                    startVibrationTimer(distance, heading);
                } else {
                    stopVibrationTimer();
                }
                if(getVoicePreference()){
                    voiceUpdates();
                }
            }
        });
    }

    private void saveCurrentLocation() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Location loc = new Location("");
                                loc.setBearing(location.getBearing());
                                loc.setLongitude(location.getLongitude());
                                loc.setLatitude(location.getLatitude());
                                savedLocation = loc;
                            }
                        }
                    });
        } catch (SecurityException se) {
        }
    }

    private void voiceUpdates() {
        if(!talker.isSpeaking()) {
            Double DisplayDistance = Math.round(distance * 100) / 100.0;
            Double DisplayHeading = Math.round(heading * 100) / 100.0;
            talker.speak("Bus" + Integer.toString(routeID) + " is " + Double.toString(DisplayDistance)
                    + " miles away and is" + Double.toString(DisplayHeading) + " degrees to the right.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void stopVibrationTimer() {
        if(vibTimer != null) {
            vibTimer.cancel();
            vibTimer = null;
        }
    }

    private void startVibrationTimer(Float distance, Float bearing) {
        stopVibrationTimer();

        vibTimer = new Timer();

        Float time = (float)(((distance * 5280) * 19) + 100);
        Float strength = (float)((-.681 * bearing) + 254);
        strength = (strength > 255) ? 255 : strength;
        Log.e("time", time.toString());
        Log.e("strength", strength.toString());
        final VibrationEffect effect = VibrationEffect.createOneShot(Math.round(time), Math.round(strength));

        TimerTask vibrateTask = new TimerTask() {
            @Override
            public void run() {
                if(myVib.hasVibrator()) {
                    myVib.vibrate(effect);
                }
            }
        };

        vibTimer.schedule(vibrateTask, 0, Math.round(time) * 2);
    }

    private void startTimedUpdates(){
        timer = new Timer();
        TimerTask updateData = new TimerTask() {
            @Override
            public void run() {
                getRouteList(routeID, 1);
                getRouteList(routeID, 2);
                getRouteList(routeID, 3);
                getRouteList(routeID, 4);
            }
        };

        timer.schedule(updateData, 1000, 10000);
    }

    private boolean getVoicePreference() {
        SharedPreferences sharedPref = getSharedPreferences("default_voice_assistance", Context.MODE_PRIVATE);
        String vAssistantBoolean = sharedPref.getString("default_voice_assistance", "false");
        return Boolean.parseBoolean(vAssistantBoolean);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getRouteList(Integer routeNumber, final Integer urlConfig) {
        // First, we insert the username into the repo url.
        // The repo url is defined in GitHubs API docs (https://developer.github.com/v3/repos/).
        String url = null;
        if(urlConfig == 1) {
            url = "http://api.rgrta.com/rtroutes?key=d0eac034-06b4-4c51-877a-3f21119b87e7&routeid=" + routeNumber;
        }
        if(urlConfig == 2){
            url = "http://api.rgrta.com/rtrouteStops?key=d0eac034-06b4-4c51-877a-3f21119b87e7&routeid=" + routeNumber;
        }
        if(urlConfig == 3){
            url = "http://api.rgrta.com/rtpredictions?key=d0eac034-06b4-4c51-877a-3f21119b87e7&routeid=" + routeNumber + "&stopid=" + nextStopID;
         }
        if(urlConfig == 4){
            url = "http://api.rgrta.com/rtvehicles?key=d0eac034-06b4-4c51-877a-3f21119b87e7&routeid=" + routeNumber;

        }
        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend readng the office docs: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check the length of our response
                        if (response.length() > 0) {
                            // We did get a response
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    if (urlConfig == 1){
                                        // For each response, add a new line to our route list.
                                        JSONObject jsonObj = response.getJSONObject(i);
                                        routeName = jsonObj.get("RouteName").toString();
                                    }
                                    if (urlConfig == 2){
                                        // For each response, add a new line to our route list.
                                        JSONObject jsonObj = response.getJSONObject(0);
                                        nextStopID = jsonObj.get("StopID").toString();
                                        stopDescription = jsonObj.get("StopName").toString();
                                    }
                                    if (urlConfig == 3){
                                        // For each response, add a new line to our route list.
                                        JSONObject jsonObj = response.getJSONObject(i);
                                        eta = jsonObj.get("ETA").toString();
                                    }
                                    if (urlConfig == 4){
                                        // For each response, add a new line to our route list.
                                        JSONObject jsonObj = response.getJSONObject(i);
                                        Location vehicleLocation = new Location("");
                                        vehicleLocation.setLatitude(Double.parseDouble(jsonObj.get("CurrentLat").toString()));
                                        vehicleLocation.setLongitude(Double.parseDouble(jsonObj.get("CurrentLon").toString()));
                                        vehicleLocation.setBearing(Float.parseFloat(jsonObj.get("Heading").toString()));
                                        Log.e("New Location", vehicleLocation.toString());
                                        vehicle = vehicleLocation;
                                        updateDisplayValues();
                                    }
                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                }
                            }
                            String currentText = routeNameText.getText().toString();
                            routeNameText.setText(routeName);
                            currentText = routeListText.getText().toString();
                            routeListText.setText(stopDescription);
                            currentText = etaText.getText().toString();
                            etaText.setText(eta);
                        } else {
                            // There are no buses found on this route
                            Log.e("Volley","No buses found on this route.");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our route list.
                        Log.e("Volley", "Unfortunately, we have encountered an error. ");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }

}
