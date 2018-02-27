package edu.rit.se.www.findmybus;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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

import java.util.ArrayList;

public class TrackedBusesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracked_buses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ListView busList = (ListView) findViewById(R.id.tackedBusesLV);
        final ArrayList<String> routeNumbers = retrieveAllRoutes();
        Log.e("Array", routeNumbers.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routeNumbers);
        busList.setAdapter(adapter);

        busList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent i = new Intent(TrackedBusesActivity.this, CurrentBusInfoActivity.class);
                //If you wanna send any data to nextActicity.class you can use
                i.putExtra("routeID", routeNumbers.get(position));
                startActivity(i);
            }
        });



    }

    protected void onResume() {
        super.onResume();

        Log.e("STATE", "RESUMED");

        ListView busList = (ListView) findViewById(R.id.tackedBusesLV);
        ArrayList<String> routeNumbers = retrieveAllRoutes();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routeNumbers);
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
                Log.e("ID", res);
                routeNumbers.add(res);
                stillActive = cursor.moveToNext();
                Log.e("Is Active", Boolean.toString(stillActive));
            }
        }
        return routeNumbers;
    }

}
