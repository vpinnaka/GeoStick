package com.androidprojects.vinaydatta.geostick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

public class Settings extends AppCompatActivity {


    private SeekBar radius;
    private TextView progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        radius  = (SeekBar)findViewById(R.id.radius);
        progress = (TextView) findViewById(R.id.radiustext);

        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                int value  = 100 + seekBar.getProgress()*10;
                setradiusshared(value);
                progress.setText(value+" meters");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    private void setradiusshared(int value){
        SharedPreferences mPrefs = getSharedPreferences("radius",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        String radius = ""+value;
        prefsEditor.putString("radiusdata", radius);
        prefsEditor.commit();
    }


    public void home(View view){

        startActivity(new Intent(getApplicationContext(),homescreen.class));
        finish();
    }
}
