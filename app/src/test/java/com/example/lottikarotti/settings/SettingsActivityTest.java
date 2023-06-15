/*package com.example.lottikarotti.settings;

import static org.junit.jupiter.api.Assertions.*;

import com.example.lottikarotti.SettingsActivity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettingsActivityTest {

    SettingsActivity settingsActivity;

    @BeforeEach
    void setUp() {
        settingsActivity = new SettingsActivity();
    }

    @AfterEach
    void tearDown() {
        settingsActivity = null;
    }

    @Test
    void onCreate() {

    }

    @Test
    public void testSetBrightnessMax() {
        int desiredBrightness = 100;
        float expected = desiredBrightness / 255f;
        settingsActivity.setBrightness(desiredBrightness);
        float current = settingsActivity.getWindow().getAttributes().screenBrightness;
        assertEquals(expected, current, 0.01);
    }
    @Test
    public void testSetBrightnessMin() {
        int desiredBrightness = 0;
        float expected = desiredBrightness / 255f;
        settingsActivity.setBrightness(desiredBrightness);
        float current = settingsActivity.getWindow().getAttributes().screenBrightness;
        assertEquals(expected, current, 0.01);
    }
    @Test
    public void testSetBrightnessAverage() {
        int desiredBrightness = 50;
        float expected = desiredBrightness / 255f;
        settingsActivity.setBrightness(desiredBrightness);
        float current = settingsActivity.getWindow().getAttributes().screenBrightness;
        assertEquals(expected, current, 0.01);
    }
}*/