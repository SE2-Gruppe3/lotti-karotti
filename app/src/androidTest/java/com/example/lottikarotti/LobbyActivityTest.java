package com.example.lottikarotti;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lottikarotti.MainActivity;
import com.example.lottikarotti.R;
import com.example.lottikarotti.databinding.ActivityLobbyBinding;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LobbyActivityTest {

    private ActivityScenario<LobbyActivity> scenario;

    @Before
    public void setUp() {
        // Launch the activity
        scenario = ActivityScenario.launch(LobbyActivity.class);
    }

    @After
    public void tearDown() {
        // Close the activity after each test
        scenario.close();
    }

    @Test
    public void testStartGameButtonEnabledAfterEnteringUsername() {
        // Type a username in the username text view
        Espresso.onView(ViewMatchers.withId(R.id.usernameTextView))
                .perform(replaceText("JohnDoe"));

        // Check if the Start Game button is enabled
        Espresso.onView(ViewMatchers.withId(R.id.btnStartGame))
                .check(matches(isEnabled()));
    }

    @Test
    public void testJoinGameButtonEnabledAfterEnteringLobbyId() {
        // Type a valid lobby ID in the lobby ID text view
        Espresso.onView(ViewMatchers.withId(R.id.etLobbyId))
                .perform(replaceText("123456"));

        // Check if the Join Game button is enabled
        Espresso.onView(ViewMatchers.withId(R.id.btnJoinGame))
                .check(matches(isEnabled()));
    }

    @Test
    public void testStartGameButtonStartsMainActivityWithCorrectData() {
        // Mock the Intent and the startActivity method
        Intent mockIntent = Mockito.mock(Intent.class);
        LobbyActivity activity = Mockito.spy(LobbyActivity.class);

        // Set up the activity with a username
        Espresso.onView(ViewMatchers.withId(R.id.usernameTextView))
                .perform(replaceText("JohnDoe"));

        // Click the Start Game button
        Espresso.onView(ViewMatchers.withId(R.id.btnStartGame))
                .perform(click());

        // Verify that the startGameActivity method is called with the correct data
        verify(activity).startGameActivity(Mockito.anyInt(), Mockito.eq("JohnDoe"), Mockito.eq("start"));
        // Verify that startActivity is called with the correct intent
        verify(activity).startActivity(mockIntent);
    }

    @Test
    public void testJoinGameButtonStartsMainActivityWithCorrectData() {
        // Mock the Intent and the startActivity method
        Intent mockIntent = Mockito.mock(Intent.class);
        LobbyActivity activity = Mockito.spy(LobbyActivity.class);

        // Set up the activity with a lobby ID and username
        Espresso.onView(ViewMatchers.withId(R.id.etLobbyId))
                .perform(replaceText("123456"));
        Espresso.onView(ViewMatchers.withId(R.id.usernameTextView))
                .perform(replaceText("JohnDoe"));

        // Click the Join Game button
        Espresso.onView(ViewMatchers.withId(R.id.btnJoinGame))
                .perform(click());

        // Verify that the startGameActivity method is called with the correct data
        verify(activity).startGameActivity(Mockito.eq(123456), Mockito.eq("JohnDoe"), Mockito.eq("join"));
        // Verify that startActivity is called with the correct intent
        verify(activity).startActivity(mockIntent);
    }

    @Test
    public void testBrightnessUpdatedOnResume() {
        // Mock the SharedPreferences and WindowManager
        SharedPreferences mockSharedPreferences = Mockito.mock(SharedPreferences.class);
        WindowManager.LayoutParams mockLayoutParams = Mockito.mock(WindowManager.LayoutParams.class);

        // Mock the getSystemService method
        LobbyActivity activity = Mockito.spy(LobbyActivity.class);
        Mockito.doReturn(mockSharedPreferences).when(activity).getSharedPreferences(Mockito.anyString(), Mockito.anyInt());

        // Resume the activity
        scenario.onActivity((ActivityScenario.ActivityAction<LobbyActivity>) activity);

        // Verify that updateBrightness is called
        verify(activity).updateBrightness();
        // Verify that the screen brightness is updated with the correct value
        verify(mockLayoutParams).screenBrightness = 100 / 255f;
        // Verify that the updated attributes are set to the window
        verify(activity.getWindow()).setAttributes(mockLayoutParams);
    }
}
