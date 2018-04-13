package edu.rit.se.www.findmybus;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

public class CurrentBusInfoActivity extends AppCompatActivity {
    TextToSpeech talker;
    Integer routeID = null;
    private int distance = 0;
    private int heading = 0;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        talker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    talker.setLanguage(Locale.UK);
                    startTimedUpdates();
                }
            }
        });

        setContentView(R.layout.activity_current_bus_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Bundle bundle = getIntent().getExtras();

        if(bundle.getString("routeID")!=null) {
            TextView routeIDText = (TextView) findViewById(R.id.routeID);
            routeID = Integer.parseInt(bundle.getString("routeID"));
            routeIDText.setText(bundle.getString("routeID"));
        }

    }

    protected void onStart() {
        super.onStart();
        getValues();
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

    private void getValues() {
        Random r = new Random();
        distance = r.nextInt((10) + 1);

        heading = r.nextInt(360 + 1);
    }

    private void updateDisplayValues(){
        final TextView busDistanceLabel = (TextView) findViewById(R.id.busDistance);
        final TextView busHeadingLabel = (TextView) findViewById(R.id.busHeading);

        CurrentBusInfoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                busDistanceLabel.setText(Integer.toString(distance) + " miles");
                busHeadingLabel.setText(Integer.toString(heading) + " degrees");
                if(getVoicePreference()){
                    //voiceUpdates();
                }
            }
        });
    }

    private void voiceUpdates() {
        talker.speak("Bus" + Integer.toString(routeID) + " is " + Integer.toString(distance)
                + " miles away. It has a heading of " + Integer.toString(heading) + " degrees.", TextToSpeech.QUEUE_FLUSH, null);
    }

    private void startTimedUpdates(){
        timer = new Timer();
        TimerTask updateData = new TimerTask() {
            @Override
            public void run() {
                updateDisplayValues();
            }
        };

        timer.schedule(updateData, 20000, 20000);
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
