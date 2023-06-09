package com.example.lottikarotti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.lottikarotti.Util.BGMusic;
import com.example.lottikarotti.Util.MusicService;

public class SettingsActivity extends AppCompatActivity {

    private static final String BRIGHTNESS_PREF = "brightness";

    private ImageButton settingsVolOn;
    private SeekBar barVolume;
    private ImageButton settingsVolMute;
    private Button exitSettings;
    private BGMusic bgMusicService;
    private boolean isBound = false;
    private static float volume;
    private static boolean muted = false;
    private static boolean isBarZero = false;
    private SeekBar barBrightness;
    private SharedPreferences preferences;

    // constructor that allows dependency injection
    public SettingsActivity(BGMusic bgMusicService, SharedPreferences preferences) {
        this.bgMusicService = bgMusicService;
        this.preferences = preferences;
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //**finding the buttons and the seekbars**//

        setSettingsVolMute((ImageButton) findViewById(R.id.settingsVolMute));
        setSettingsVolOn((ImageButton) findViewById(R.id.settingVolOn));
        setBarVolume((SeekBar) findViewById(R.id.seekBar));
        setBarBrightness((SeekBar) findViewById(R.id.seekBar2));
        setExitSettings((Button) findViewById(R.id.exitMenu));

        //--------------------------------------------------------------------------------------------//
        //-------------------------------Volume Settings--------------------------------------------------------//

        Intent intent = new Intent(this, BGMusic.class);
        bindService(intent, getServiceConnection(), Context.BIND_AUTO_CREATE);


        Log.d("Muted", String.valueOf(isMuted()));




        //MuteButton-Logic
        getSettingsVolOn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getSettingsVolOn().setVisibility(View.INVISIBLE);
//                getSettingsVolMute().setVisibility(View.VISIBLE);
                if (isBound()) {
                    getBgMusicService().setMuted(true);
                    getBarVolume().setEnabled(false);
//                    getSettingsVolMute().setEnabled(true);
                }
                updateVolButtons();
                Log.d("Muted", String.valueOf(getBgMusicService().isMuted()));
            }
        });

        //Un-muteButton-Logic
        getSettingsVolMute().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getSettingsVolMute().setVisibility(View.INVISIBLE);
//                getSettingsVolOn().setVisibility(View.VISIBLE);
                if (isBound()) {
                    getBgMusicService().setMuted(false);
                    getBgMusicService().setVolume(getVolume());
                    getBarVolume().setEnabled(true);
                }
                updateVolButtons();
                Log.d("Muted", String.valueOf(getBgMusicService().isMuted()));
            }
        });



        getBarVolume().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int vol, boolean isUser) {
                if (isBound()) {
                    setVolume((float) vol / 100);
                    getBgMusicService().setVolume(getVolume());
                    if(getVolume() == 0f){
                        getSettingsVolOn().setVisibility(View.INVISIBLE);
                        getSettingsVolMute().setVisibility(View.VISIBLE);
                        getSettingsVolMute().setEnabled(false);
                        getSettingsVolOn().setEnabled(true);
                        setIsBarZero(true);
                    } else {
                        getSettingsVolMute().setVisibility(View.INVISIBLE);
                        getSettingsVolOn().setVisibility(View.VISIBLE);
                        getSettingsVolMute().setEnabled(true);
                        getSettingsVolOn().setEnabled(true);
                        setIsBarZero(false);
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

        setPreferences(getSharedPreferences("settings", MODE_PRIVATE));
        getBarBrightness().setProgress(getPreferences().getInt("brightness", 100));
        getBarBrightness().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar barBrightness, int amount, boolean isUser) {
                SharedPreferences.Editor editor = getPreferences().edit();

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

        getExitSettings().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName compName, IBinder service) {
            BGMusic.MusicBinder binder = (BGMusic.MusicBinder) service;
            setBgMusicService(binder.getService());
            setBound(true);
            muted = getBgMusicService().isMuted();
            checkingMuteBar();
        }

   @Override
    public void onServiceDisconnected(ComponentName compName) {
            setBound(false);
        }
    };

    public void setBrightness(int brightness) {
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
    }


    @Override
    protected void onDestroy() {
        if (isBound()) {
            unbindService(getServiceConnection());
            setBound(false);
        }
      
        super.onDestroy();
    }
  
    @Override
    protected void onResume() {
        super.onResume();
        updateBrightness();
      
        if (muted) {
            getSettingsVolOn().setVisibility(View.INVISIBLE);
            getSettingsVolMute().setVisibility(View.VISIBLE);
            getBarVolume().setEnabled(false);
            getBarVolume().setProgress((int)(getVolume() *100));
            getSettingsVolMute().setEnabled(true);
            getSettingsVolOn().setEnabled(false);
        } else {
            getSettingsVolMute().setVisibility(View.VISIBLE);
            getSettingsVolOn().setVisibility(View.INVISIBLE);
            getBarVolume().setEnabled(true);
            getBarVolume().setProgress((int)(getVolume() *100));
            getSettingsVolMute().setEnabled(false);
            getSettingsVolOn().setEnabled(true);
        }
    }

    private void updateBrightness() {
        SharedPreferences sharedBrightness = getSharedPreferences("settings", MODE_PRIVATE);
        int brightness = sharedBrightness.getInt("brightness", 100);
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
    }
  
    private void checkingMuteBar() {
        if (getBgMusicService().isMuted()) {
            Log.d("Muted?", "It works?");
            getSettingsVolOn().setVisibility(View.INVISIBLE);
            getSettingsVolMute().setVisibility(View.VISIBLE);
            getSettingsVolMute().setEnabled(true);
            getSettingsVolOn().setEnabled(false);
            getBarVolume().setEnabled(false);
            getBarVolume().setProgress((int)(getVolume() *100));
        } else if (isIsBarZero()) {
            getSettingsVolOn().setVisibility(View.INVISIBLE);
            getSettingsVolMute().setVisibility(View.VISIBLE);
            getSettingsVolMute().setEnabled(true);
            getSettingsVolOn().setEnabled(true);
            getBarVolume().setEnabled(true);
            getBarVolume().setProgress((int)(getVolume() *100));
        } else {
            getSettingsVolMute().setVisibility(View.INVISIBLE);
            getSettingsVolOn().setVisibility(View.VISIBLE);
            getSettingsVolMute().setEnabled(false);
            getSettingsVolOn().setEnabled(true);
            getBarVolume().setEnabled(true);
            getBarVolume().setProgress((int)(getVolume() *100));
        }
    }
    private void updateVolButtons() {
        if (getBgMusicService().isMuted() || isIsBarZero()) {
            getSettingsVolOn().setVisibility(View.INVISIBLE);
            getSettingsVolMute().setVisibility(View.VISIBLE);
            getSettingsVolOn().setEnabled(false);
            getSettingsVolMute().setEnabled(true);
            getSettingsVolMute().setEnabled(!isIsBarZero());
        } else {
            Log.d("Muted", String.valueOf("oops"));
            getSettingsVolOn().setEnabled(true);
            getSettingsVolMute().setEnabled(false);
            getSettingsVolOn().setVisibility(View.VISIBLE);
            getSettingsVolMute().setVisibility(View.INVISIBLE);
        }
    }

    public ImageButton getSettingsVolOn() {
        return settingsVolOn;
    }

    public void setSettingsVolOn(ImageButton settingsVolOn) {
        this.settingsVolOn = settingsVolOn;
    }

    public SeekBar getBarVolume() {
        return barVolume;
    }

    public void setBarVolume(SeekBar barVolume) {
        this.barVolume = barVolume;
    }

    public ImageButton getSettingsVolMute() {
        return settingsVolMute;
    }

    public void setSettingsVolMute(ImageButton settingsVolMute) {
        this.settingsVolMute = settingsVolMute;
    }

    public Button getExitSettings() {
        return exitSettings;
    }

    public void setExitSettings(Button exitSettings) {
        this.exitSettings = exitSettings;
    }

    public BGMusic getBgMusicService() {
        return bgMusicService;
    }

    public void setBgMusicService(BGMusic bgMusicService) {
        this.bgMusicService = bgMusicService;
    }

    public boolean isBound() {
        return isBound;
    }

    public void setBound(boolean bound) {
        isBound = bound;
    }

    public SeekBar getBarBrightness() {
        return barBrightness;
    }

    public void setBarBrightness(SeekBar barBrightness) {
        this.barBrightness = barBrightness;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }
    public static float getVolume() {
        return volume;
    }

    public static void setVolume(float volume) {
        SettingsActivity.volume = volume;
    }

    public static boolean isMuted() {
        return muted;
    }

    public static void setMuted(boolean muted) {
        SettingsActivity.muted = muted;
    }

    public static boolean isIsBarZero() {
        return isBarZero;
    }

    public static void setIsBarZero(boolean isBarZero) {
        SettingsActivity.isBarZero = isBarZero;
    }

    public void setServiceConnection(ServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

    public MusicService getMusicService() {
        return bgMusicService;
    }
}