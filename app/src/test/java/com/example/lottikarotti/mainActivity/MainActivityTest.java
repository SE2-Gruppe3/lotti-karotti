//package com.example.lottikarotti.mainActivity;
//
//import androidx.test.ext.junit.rules.ActivityTestRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.espresso.matcher.ViewMatchers;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.not;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//
//import com.example.lottikarotti.MainActivity;
//
//
//@RunWith(AndroidJUnit4.class)
//public class MainActivityTest {
//
//    @Rule
//    public ActivityTestRule<MainActivity> activityRule
//            = new ActivityTestRule<>(MainActivity.class);
//
//    @Test
//    public void testPutHolesOnBoard() {
//        MainActivity activity = activityRule.getActivity();
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
//
