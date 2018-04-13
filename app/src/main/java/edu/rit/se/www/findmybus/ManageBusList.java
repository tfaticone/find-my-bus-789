package edu.rit.se.www.findmybus;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;
import java.util.Locale;

public class ManageBusList extends AppCompatActivity {
    TextToSpeech talker;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        talker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    talker.setLanguage(Locale.UK);

                    if(getVoicePreference()) {
                        talker.speak("Manage Bus List", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

        Button addBusButton = (Button)findViewById(R.id.addbus);
        addBusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changetoAdd = new Intent(ManageBusList.this, AddBusActivity.class);
                startActivity(changetoAdd);

            }
        });

        Button removeBusButton = (Button)findViewById(R.id.removeBus);
        removeBusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changetoAdd = new Intent(ManageBusList.this, RemoveBusActivity.class);
                startActivity(changetoAdd);

            }
        });

        Button goBackButton = (Button)findViewById(R.id.goBack);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changetoAdd = new Intent(ManageBusList.this, HomepageActivity.class);
                startActivity(changetoAdd);

            }
        });


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
