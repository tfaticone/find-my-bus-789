package edu.rit.se.www.findmybus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;


public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button addbus = (Button) findViewById(R.id.addbus);
        addbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changetoAdd = new Intent(HomepageActivity.this, AddBusActivity.class);
                startActivity(changetoAdd);
            }
        });
    }
}
