package com.example.lottikarotti.mainactivity;

import com.example.lottikarotti.Util.DisectJSON;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PlayerListTest {

    static String namesJSON;

    @BeforeAll
    public static void setup() {
        namesJSON = "[{\"clientId\":\"cQYxc4SMNxFur1zsAAAP\",\"name\":\"testrabbit\"}]";
    }

    @Test
    public void inFromServerErrorHandling() {
        Assertions.assertThrows(Exception.class, () -> {
            DisectJSON.getNames("");
        });
    }
}
