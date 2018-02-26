package edu.rit.se.www.findmybus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        populateTestData();
        ArrayList<String> routeNumbers = retrieveAllRoutes();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routeNumbers);
        busList.setAdapter(adapter);



    }

    private void populateTestData() {
        ContentValues values = new ContentValues();
        values.put("routeID", 142);
        Uri uri = getContentResolver().insert(RouteProvider.CONTENT_URI, values);
    }

    private ArrayList<String> retrieveAllRoutes() {

        String URL = "content://edu.rit.se.www.findmybus.RouteProvider/routes";
        Uri Routes = Uri.parse(URL);
        ArrayList<String> routeNumbers = new ArrayList<String>();
        Cursor cursor = getContentResolver().query(Routes, null, null, null, "routeID");
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(RouteProvider.NAME);
            String res = cursor.getString(column_index);
            routeNumbers.add(res);
        }

        return routeNumbers;

    }

}
