package com.example.lottikarotti.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.example.lottikarotti.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton settingsVolOn;
    private SeekBar barVolume;
    private Switch darkMode;
    private SeekBar barBrightness;
    private ImageButton settingsVolMute;
    private Button exitSettings;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsVolMute = (ImageButton) findViewById(R.id.settingsVolMute);
        settingsVolOn = (ImageButton) findViewById(R.id.settingVolOn);
        darkMode = (Switch) findViewById(R.id.switch2);
        barVolume = (SeekBar) findViewById(R.id.seekBar);
        barBrightness = (SeekBar) findViewById(R.id.seekBar2);

        exitSettings = (Button) findViewById(R.id.exitMenu);

        exitSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}