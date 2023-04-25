package com.example.lottikarotti;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BGMusic extends Service {
    MediaPlayer player; // MediaPlayer object

    @Override
public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.bgmusic);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        player.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }
}