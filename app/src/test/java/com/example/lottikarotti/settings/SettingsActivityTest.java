package com.example.lottikarotti.settings;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.example.lottikarotti.SettingsActivity;
import com.example.lottikarotti.Util.BGMusic;
import com.example.lottikarotti.Util.MusicService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1)
public class SettingsActivityTest {

    private SettingsActivity activity;
    private MusicService bgMusicServiceMock;
    private BGMusic.MusicBinder binderSpy;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(SettingsActivity.class).create().get();

        // Create a mock for MusicService
        bgMusicServiceMock = Mockito.mock(MusicService.class);

        // Create a real instance of MusicBinder
        binderSpy = new BGMusic().new MusicBinder();

        // When getService() is called on the binder, return the mock MusicService
        Mockito.doReturn(bgMusicServiceMock).when(binderSpy).getService();
    }
    @Test
    public void testOnCreate_BindsMusicService() {
        activity.onCreate(null);

        // Verify that bindService was called
        ShadowApplication shadowApp = shadowOf(RuntimeEnvironment.application);
        Intent startedServiceIntent = shadowApp.getNextStartedService();
        assertNotNull(startedServiceIntent);
        assertEquals(MusicService.class.getName(), startedServiceIntent.getComponent().getClassName());

        // Verify the music service was set in the activity
        assertEquals(bgMusicServiceMock, activity.getMusicService());
    }
}
