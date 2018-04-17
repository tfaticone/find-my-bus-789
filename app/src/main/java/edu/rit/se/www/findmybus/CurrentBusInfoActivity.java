package edu.rit.se.www.findmybus;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import java.net.URI;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Vibrator;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class CurrentBusInfoActivity extends AppCompatActivity {
    TextToSpeech talker;
    Integer routeID = null;
    private float distance = 0;
    private float heading = 0;
    Timer timer;
    Timer vibTimer = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private static Location savedLocation = null;
    private Vibrator myVib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        talker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    talker.setLanguage(Locale.US);
                    startTimedUpdates();
                }
            }
        });

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        setContentView(R.layout.activity_current_bus_info);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Bundle bundle = getIntent().getExtras();

        if(bundle.getString("routeID")!=null) {
            TextView routeIDText = (TextView) findViewById(R.id.routeID);
            routeID = Integer.parseInt(bundle.getString("routeID"));
            routeIDText.setText(bundle.getString("routeID"));
        }

        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                savedLocation = location;
                            }
                        }
                    });
        } catch (SecurityException se) {

        }

        Button setLocationBtn = (Button) findViewById(R.id.currentLocation);
        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCurrentLocation();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        updateDisplayValues();
    }

    protected void onPause(){
        super.onPause();
        timer.cancel();
    }

    protected void onResume(){
        super.onResume();
        startTimedUpdates();
    }

    private void updateDisplayValues(){
        final TextView busDistanceLabel = (TextView) findViewById(R.id.busDistance);
        final TextView busHeadingLabel = (TextView) findViewById(R.id.busHeading);

        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                distance = (float)(((double)location.distanceTo(savedLocation)) * 0.000621371);
                                heading = location.bearingTo(savedLocation);
                            }
                        }
                    });
        } catch (SecurityException se) {

        }

        CurrentBusInfoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                busDistanceLabel.setText(Float.toString(distance) + " miles");
                busHeadingLabel.setText(Float.toString(heading) + " degrees");
                if(0.00094697 < distance && distance < 0.0189394) { //Rougly between 5 and 100 feet ( in miles )
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
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                savedLocation = location;
                            }
                        }
                    });
        } catch (SecurityException se) {
        }
    }

    private void voiceUpdates() {
        if(!talker.isSpeaking()) {
            talker.speak("Bus" + Integer.toString(routeID) + " is " + Float.toString(distance)
                    + " miles away. It has a heading of " + Float.toString(heading) + " degrees.", TextToSpeech.QUEUE_FLUSH, null);
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

        Float time = (float)((10 * (distance * 0.000189394)) + 2);
        Float strength = (float)((-.009 * (distance * 0.000189394)) + 1);
        final VibrationEffect effect = VibrationEffect.createOneShot(Math.round(time), Math.round(strength * VibrationEffect.DEFAULT_AMPLITUDE));

        TimerTask vibrateTask = new TimerTask() {
            @Override
            public void run() {
                if(myVib.hasVibrator()) {
                    myVib.vibrate(effect);
                }
            }
        };

        vibTimer.schedule(vibrateTask, 0, 2 * Math.round(time));
    }

    private void startTimedUpdates(){
        timer = new Timer();
        TimerTask updateData = new TimerTask() {
            @Override
            public void run() {
                updateDisplayValues();
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
}
