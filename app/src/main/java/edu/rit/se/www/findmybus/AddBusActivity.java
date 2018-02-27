package edu.rit.se.www.findmybus;

import android.content.ContentValues;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;

public class AddBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button addBusSubmit = (Button)findViewById(R.id.addBusSubmit);
        final EditText addBusInput   = (EditText)findViewById(R.id.addBusInput);

        addBusSubmit.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Integer routeID = Integer.parseInt(addBusInput.getText().toString());
                        addRoute(routeID);

                        Intent changetoTrack = new Intent(AddBusActivity.this, TrackedBusesActivity.class);
                        startActivity(changetoTrack);
                    }
                });

    }

    private void addRoute(int routeID) {
        ContentValues values = new ContentValues();
        values.put("routeID", routeID);
        Log.e("New Value entered", Integer.toString(routeID));
        Uri uri = getContentResolver().insert(RouteProvider.CONTENT_URI, values);
    }

}
