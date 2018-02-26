package edu.rit.se.www.findmybus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import org.w3c.dom.Text;
import android.util.Log;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;

public class AddBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

}
