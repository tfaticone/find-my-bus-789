package edu.rit.se.www.findmybus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CurrentBusInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_bus_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("routeID")!=null) {
            Log.e("Bundle ID", bundle.getString("routeID"));

            TextView routeIDText = (TextView) findViewById(R.id.routeID);
            routeIDText.setText(bundle.getString("routeID"));
        }

    }

}
