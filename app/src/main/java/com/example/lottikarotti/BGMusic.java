package com.example.lottikarotti;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class BGMusic extends Service {
    MediaPlayer player;
    private final IBinder musicBind = new MusicBinder();

    @Override
public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.bgmusic2);
        player.setLooping(true);
        player.setVolume(1f, 1f);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        player.start();
        return START_STICKY;
    }

    public class MusicBinder extends Binder {
        public BGMusic getService() {
            return BGMusic.this;
        }
    }
    public void setVolume(float volume) {
        player.setVolume(volume, volume);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }
}