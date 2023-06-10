//package com.example.lottikarotti;
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//
//import static org.hamcrest.CoreMatchers.not;
//import static org.junit.Assert.assertEquals;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.test.runner.AndroidJUnit4;
//import androidx.test.rule.ActivityTestRule;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class MainActivityTest {
//
//    @Rule
//    public ActivityTestRule<MainActivity> activityRule
//            = new ActivityTestRule<>(MainActivity.class);
//
//
//    @Test
//    public void testInitializeIntent() {
//        MainActivity activity = activityRule.getActivity();
//
//        Intent mockIntent = new Intent();
//        mockIntent.putExtra("lobbyId", "123456");
//        mockIntent.putExtra("username", "Dummy");
//        mockIntent.putExtra("info", "start");
//
//        activityRule.launchActivity(mockIntent);
//
//        activity.initializeIntent();
//
//
//        TextView lobbyID = activity.findViewById(R.id.lobbyID);
//        assertEquals("Lobby ID: 123456", lobbyID.getText().toString());
//
//        // Verify the interactions with ServerConnection based on the info value
//        if ("join".equals(mockIntent.getStringExtra("info"))) {
//
//        } else {
//
//        }
//    }
//    @Test
//    public void testPutHolesOnBoard() {
//        MainActivity activity = activityRule.getActivity();
//
//        Intent mockIntent = new Intent();
//        mockIntent.putExtra("lobbyId", "123");
//        mockIntent.putExtra("username", "John");
//        mockIntent.putExtra("info", "start");
//
//        // Set the mock intent on the activity
//        activityRule.launchActivity(mockIntent);
//        int hole1 = 1;
//        int hole2 = 2;
//        activity.runOnUiThread(() -> activity.putHolesOnBoard(hole1, hole2));
//
//        for (int h : activity.holes) {
//            if (h != hole1 && h != hole2) {
//                onView(withId(h)).check(matches(not(isDisplayed())));
//            } else {
//                onView(withId(h)).check(matches(isDisplayed()));
//            }
//        }
//    }
//}
