package edu.rit.se.www.findmybus;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.Text;
import android.util.Log;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;
import android.widget.Button;
import android.widget.EditText;

public class AddBusActivity extends AppCompatActivity {
    TextToSpeech talker;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Log.e("VOICE BOOLEAN", Boolean.toString(getVoicePreference()));

        talker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    talker.setLanguage(Locale.UK);

                    if(getVoicePreference()) {
                        startVoiceWalkthrough(talker);

                    }
                }
            }
        });

        Button addBusSubmit = (Button)findViewById(R.id.addBusSubmit);
        final EditText addBusInput   = (EditText)findViewById(R.id.addBusInput);

        addBusSubmit.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Integer routeID = Integer.parseInt(addBusInput.getText().toString());
                        //CHECK TO SEE ITS NOT EMPTY
                        addRoute(routeID);
                    }
                }
                );

    }

    private void addRoute(int routeID) {
        ContentValues values = new ContentValues();
        values.put("routeID", routeID);
        Log.e("New Value entered", Integer.toString(routeID));
        Uri uri = getContentResolver().insert(RouteProvider.CONTENT_URI, values);

        Intent changetoTrack = new Intent(AddBusActivity.this, TrackedBusesActivity.class);
        startActivity(changetoTrack);
    }

    private void startVoiceWalkthrough(TextToSpeech talker) {
        talker.speak("Initiating Add Bus Walkthrough. What is the route ID you want to add?", TextToSpeech.QUEUE_FLUSH, null);

        while(!talker.isSpeaking()){}
        while(talker.isSpeaking()){}
        promptSpeechInput();
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            try{
                Integer voicedrouteID = Integer.parseInt(spokenText);
                addRoute(voicedrouteID);
            } catch(NumberFormatException c) {
                talker.speak("Could not recognize number", TextToSpeech.QUEUE_FLUSH, null);
                while(!talker.isSpeaking()){}
                while(talker.isSpeaking()){}
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
