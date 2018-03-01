package edu.rit.se.www.findmybus;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import 	android.app.Fragment;


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

        Button trackbus = (Button) findViewById(R.id.trackbuses);
        trackbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changetoAdd = new Intent(HomepageActivity.this, TrackedBusesActivity.class);
                startActivity(changetoAdd);
            }
        });

        toggleSettingButton();
        Button toggleVoiceBtn = (Button) findViewById(R.id.toggleVoiceAssistant);
        toggleVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVoicePreference();
            }
        });
    }

    private boolean getVoicePreference() {
        SharedPreferences sharedPref = getSharedPreferences("default_voice_assistance", Context.MODE_PRIVATE);
        String vAssistantBoolean = sharedPref.getString("default_voice_assistance", "false");
        return Boolean.parseBoolean(vAssistantBoolean);
    }

    private void toggleVoicePreference() {
        SharedPreferences sharedPref = getSharedPreferences("default_voice_assistance", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean value = !getVoicePreference();
        editor.putString("default_voice_assistance", Boolean.toString(value));
        editor.commit();

        toggleSettingButton();
    }

    private void toggleSettingButton() {
        Button toggleVoiceBtn = (Button) findViewById(R.id.toggleVoiceAssistant);

        if(getVoicePreference()) {
            toggleVoiceBtn.setText("TOGGLE VOICE ASSISTANCE OFF");
        } else {
            toggleVoiceBtn.setText("TOGGLE VOICE ASSISTANCE ON");
        }

        Log.e("VOICE BOOLEAN", Boolean.toString(getVoicePreference()));
    }
}
