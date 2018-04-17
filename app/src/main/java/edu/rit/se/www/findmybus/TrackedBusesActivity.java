package edu.rit.se.www.findmybus;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.net.Uri;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class TrackedBusesActivity extends AppCompatActivity {
    TextToSpeech talker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracked_buses);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView busList = (ListView) findViewById(R.id.tackedBusesLV);
        final ArrayList<String> routeNumbers = retrieveAllRoutes();
        final ArrayList<String> routeNumber_string = new ArrayList<>(routeNumbers);

        for(int i = 0; i < routeNumber_string.size(); i++) {
            routeNumber_string.set(i, "Route " + routeNumber_string.get(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routeNumber_string){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextSize(32);

                // Generate ListView Item using TextView
                return view;
            }
        };
        busList.setAdapter(adapter);

        busList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent i = new Intent(TrackedBusesActivity.this, CurrentBusInfoActivity.class);
                //If you wanna send any data to nextActicity.class you can use
                i.putExtra("routeID", routeNumbers.get(position));
                startActivity(i);
            }
        });

        talker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    talker.setLanguage(Locale.US);

                    if(getVoicePreference()) {
                        talker.speak("Tracked Bus Page. Please select a bus from the list to get more details.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });


    }

    protected void onResume() {
        super.onResume();

        ListView busList = (ListView) findViewById(R.id.tackedBusesLV);
        ArrayList<String> routeNumbers = retrieveAllRoutes();
        final ArrayList<String> routeNumber_string = new ArrayList<>(routeNumbers);

        for(int i = 0; i < routeNumber_string.size(); i++) {
            routeNumber_string.set(i, "Route " + routeNumber_string.get(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routeNumber_string){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextSize(32);

                // Generate ListView Item using TextView
                return view;
            }
        };
        busList.setAdapter(adapter);
    }

    private ArrayList<String> retrieveAllRoutes() {
        String URL = "content://edu.rit.se.www.findmybus.RouteProvider/routes";
        Uri Routes = Uri.parse(URL);
        ArrayList<String> routeNumbers = new ArrayList<String>();
        Cursor cursor = getContentResolver().query(Routes, null, null, null, "routeID");
        if(cursor.moveToFirst()){
            Boolean stillActive = true;
            while(stillActive) {
                int column_index = cursor.getColumnIndexOrThrow(RouteProvider.NAME);
                String res = cursor.getString(column_index);
                routeNumbers.add(res);
                stillActive = cursor.moveToNext();
            }
        }
        return routeNumbers;
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
