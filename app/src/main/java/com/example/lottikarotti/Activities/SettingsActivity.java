package com.example.lottikarotti.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.example.lottikarotti.R;

public class SettingsActivity extends AppCompatActivity {

    //**Relevant variables for Volume**//
    private ImageButton settingsVolOn;
    private SeekBar barVolume;
    private ImageButton settingsVolMute;
    private Button exitSettings;
    private BGMusic bgMusicService;
    private boolean isBound = false;
    private static float volume = 1f;
    private float volumeBuffer;
    private static boolean muted = false;
    private static boolean isBarZero = false;

    //**Relevant variables for Brightness**//
    private SeekBar barBrightness;
    private SharedPreferences preferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //**finding the buttons and the seekbars**//

        settingsVolMute = (ImageButton) findViewById(R.id.settingsVolMute);
        settingsVolOn = (ImageButton) findViewById(R.id.settingVolOn);
        barVolume = (SeekBar) findViewById(R.id.seekBar);
        barBrightness = (SeekBar) findViewById(R.id.seekBar2);
        exitSettings = (Button) findViewById(R.id.exitMenu);

        //--------------------------------------------------------------------------------------------//
        //-------------------------------Volume Settings--------------------------------------------------------//

        Intent intent = new Intent(this, BGMusic.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        barVolume.setMax(100);
        barVolume.setProgress((int)(volume*100));

        //Checking whether the volume is muted or not
        if (muted) {
            settingsVolOn.setVisibility(View.INVISIBLE);
            settingsVolMute.setVisibility(View.VISIBLE);
            barVolume.setEnabled(false);
        }else if (isBarZero) {
            settingsVolOn.setVisibility(View.INVISIBLE);
            settingsVolMute.setVisibility(View.VISIBLE);
            settingsVolMute.setEnabled(false);
            barVolume.setEnabled(true);
        } else {
            settingsVolMute.setVisibility(View.INVISIBLE);
            settingsVolOn.setVisibility(View.VISIBLE);
            settingsVolMute.setEnabled(false);
            barVolume.setEnabled(true);
        }

        settingsVolOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsVolOn.setVisibility(View.INVISIBLE);
                settingsVolMute.setVisibility(View.VISIBLE);
                if (isBound) {
                    muted = true;
                    bgMusicService.setVolume(0f);
                    barVolume.setEnabled(false);
                    settingsVolMute.setEnabled(true);
                }
            }
        });

        settingsVolMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsVolMute.setVisibility(View.INVISIBLE);
                settingsVolOn.setVisibility(View.VISIBLE);
                if (isBound) {
                    muted = false;
                    bgMusicService.setVolume(volume);
                    barVolume.setEnabled(true);
                }
            }
        });



        barVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int vol, boolean isUser) {
                if (isBound) {
                    volume = (float) vol / 100;
                    bgMusicService.setVolume(volume);
                    if(volume == 0f){
                        settingsVolOn.setVisibility(View.INVISIBLE);
                        settingsVolMute.setVisibility(View.VISIBLE);
                        settingsVolMute.setEnabled(false);
                        isBarZero = true;
                    } else {
                        settingsVolMute.setVisibility(View.INVISIBLE);
                        settingsVolOn.setVisibility(View.VISIBLE);
                        settingsVolMute.setEnabled(true);
                        isBarZero = false;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //--------------------------------------------------------------------------------------------//
        //-------------------------------Brightness Settings--------------------------------------------------------//

        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        barBrightness.setProgress(preferences.getInt("brightness", 100));
        barBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar barBrightness, int amount, boolean isUser) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("brightness", amount);
                editor.apply();
                setBrightness(amount);
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
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName compName, IBinder service) {
            BGMusic.MusicBinder binder = (BGMusic.MusicBinder) service;
            bgMusicService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName compName) {
            isBound = false;
        }
    };

    private void setBrightness(int brightness) {
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
    }


    @Override
    protected void onDestroy() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }

        //Can be removed in case we decide to storage the volume changes-information
//        isBarZero = false;
//        muted = false;
//        volume = 1f;

        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateBrightness();
    }

    private void updateBrightness() {
        SharedPreferences sharedBrightness = getSharedPreferences("settings", MODE_PRIVATE);
        int brightness = sharedBrightness.getInt("brightness", 100);
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
    }
}