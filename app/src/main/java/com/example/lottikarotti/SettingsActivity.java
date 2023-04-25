package com.example.lottikarotti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton settingsVolOn;
    private SeekBar barVolume;
    private Switch darkMode;
    private SeekBar barBrightness;
    private ImageButton settingsVolMute;
    private Button exitSettings;
    private BGMusic bgMusicService;
    private boolean isBound = false;

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







        barVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int volume, boolean isUser) {
                if (isBound) {
                    float volume2 = (float) volume / 100;
                    bgMusicService.setVolume(volume2);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });




        exitSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}