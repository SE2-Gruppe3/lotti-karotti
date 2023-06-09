package com.example.lottikarotti.Util;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

public interface MusicService {
    MediaPlayer getPlayer();
    void setPlayer(MediaPlayer player);
    SharedPreferences getPreferences();
    void setPreferences(SharedPreferences preferences);
    IBinder getMusicBind();
    boolean isMuted();
    void setVolume(float volume);
    float getVolume();
    void setMuted(boolean mute);
}
