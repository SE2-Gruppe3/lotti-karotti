package com.example.lottikarotti.Util;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.lottikarotti.R;

public class BGMusic extends Service implements MusicService {
    private MediaPlayer player;
    private SharedPreferences preferences;
    private final IBinder musicBind = new MusicBinder();
    private float volume = 1f;
    private static boolean muted;

    @Override
    public void onCreate() {
        super.onCreate();
        setPlayer(MediaPlayer.create(this, R.raw.bgmusic2));
        setPreferences(getSharedPreferences("settings", MODE_PRIVATE));
        setVolume(getPreferences().getFloat("volume", 1f));
        setMuted(getPreferences().getBoolean("muted", false));
        getPlayer().setLooping(true);
        updatePlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return getMusicBind();
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        getPlayer().start();
        return START_STICKY;
    }


    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public IBinder getMusicBind() {
        return musicBind;
    }

    public boolean isMuted() {
        return muted;
    }

    public class MusicBinder extends Binder {
        public BGMusic getService() {
            return BGMusic.this;
        }
    }
    public void setVolume(float volume) {
        this.volume = volume;
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putFloat("volume", volume);
        editor.apply();
        updatePlayer();
    }
    public float getVolume() {
        return this.volume;
    }
    public void setMuted(boolean mute) {
        muted = mute;
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean("muted", mute);
        editor.apply();
        updatePlayer();
    }
    private void updatePlayer() {
        if (isMuted()) {
            getPlayer().setVolume(0f, 0f);
        } else {
            getPlayer().setVolume(getVolume(), getVolume());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPlayer().stop();
        getPlayer().release();
    }
}