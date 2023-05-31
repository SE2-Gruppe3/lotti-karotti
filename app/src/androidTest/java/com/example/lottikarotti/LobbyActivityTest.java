package com.example.lottikarotti;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.lottikarotti.LobbyActivity;
import com.example.lottikarotti.MainActivity;
import com.example.lottikarotti.R;
import com.example.lottikarotti.databinding.ActivityLobbyBinding;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LobbyActivityTest {
    @Rule
    public ActivityTestRule<LobbyActivity> activityRule = new ActivityTestRule<>(LobbyActivity.class);

    private LobbyActivity lobbyActivity;

    @Before
    public void setUp() {
        lobbyActivity = activityRule.getActivity();
        Intents.init(); // Initialize Espresso Intents
    }

    @After
    public void tearDown() {
        Intents.release(); // Release Espresso Intents
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

        // Type a username in the username text view
        Espresso.onView(ViewMatchers.withId(R.id.usernameTextView))
                .perform(replaceText("JohnDoe"));

        // Check if the Join Game button is not enabled
        Espresso.onView(ViewMatchers.withId(R.id.btnJoinGame))
                .check(matches(isEnabled()));
    }

    @Test
    public void testStartGameButtonStartsMainActivityWithCorrectData() {
        // Set up the activity with a username
        Espresso.onView(ViewMatchers.withId(R.id.usernameTextView))
                .perform(replaceText("JohnDoe"));

        // Click the Start Game button
        Espresso.onView(ViewMatchers.withId(R.id.btnStartGame))
                .perform(click());

        // Verify that the startGameActivity method is called with the correct data
        Intent expectedIntent = new Intent(lobbyActivity, MainActivity.class);
        expectedIntent.putExtra("lobbyId", String.valueOf(lobbyActivity.getLobbyId()));
        expectedIntent.putExtra("username", "JohnDoe");
        expectedIntent.putExtra("info", "start");

        intended(hasComponent(MainActivity.class.getName()));
        intended(hasExtra("lobbyId", String.valueOf(lobbyActivity.getLobbyId())));
        intended(hasExtra("username", "JohnDoe"));
        intended(hasExtra("info", "start"));
    }

    @Test
    public void testJoinGameButtonStartsMainActivityWithCorrectData() {
        // Set up the activity with a lobby ID and username
        Espresso.onView(ViewMatchers.withId(R.id.etLobbyId))
                .perform(replaceText("123456"));
        Espresso.onView(ViewMatchers.withId(R.id.usernameTextView))
                .perform(replaceText("JohnDoe"));

        // Click the Join Game button
        Espresso.onView(ViewMatchers.withId(R.id.btnJoinGame))
                .perform(click());

        // Verify that the startGameActivity method is called with the correct data
        Intent expectedIntent = new Intent(lobbyActivity, MainActivity.class);
        expectedIntent.putExtra("lobbyId", "123456");
        expectedIntent.putExtra("username", "JohnDoe");
        expectedIntent.putExtra("info", "join");

        intended(hasComponent(MainActivity.class.getName()));
        intended(hasExtra("lobbyId", "123456"));
        intended(hasExtra("username", "JohnDoe"));
        intended(hasExtra("info", "join"));
    }
}
