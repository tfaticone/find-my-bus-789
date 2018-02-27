package edu.rit.se.www.findmybus;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import java.net.URI;

public class CurrentBusInfoActivity extends AppCompatActivity {
    TextToSpeech talker;
    Integer routeID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        talker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    talker.setLanguage(Locale.UK);
                    talker.speak("The route being tracked is route" + Integer.toString(routeID), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        setContentView(R.layout.activity_current_bus_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Bundle bundle = getIntent().getExtras();

        if(bundle.getString("routeID")!=null) {
            Log.e("Bundle ID", bundle.getString("routeID"));

            TextView routeIDText = (TextView) findViewById(R.id.routeID);
            routeID = Integer.parseInt(bundle.getString("routeID"));
            routeIDText.setText(bundle.getString("routeID"));
        }

        Button removeButton = (Button) findViewById(R.id.removeRoute);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                String[] selection = new String[1];
                selection[0] = bundle.getString("routeID");
                Integer rowCount = getContentResolver().delete(RouteProvider.CONTENT_URI, "routeID = ?", selection);
                Log.e("Deleted Rows", Integer.toString(rowCount));

                Intent changetoAdd = new Intent(CurrentBusInfoActivity.this, TrackedBusesActivity.class);
                startActivity(changetoAdd);
            }
        });

    }
}
