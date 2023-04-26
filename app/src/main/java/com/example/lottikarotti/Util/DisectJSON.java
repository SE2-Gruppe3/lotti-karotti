package com.example.lottikarotti.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class DisectJSON {

    public static String[] getNames(String jsonstring){
        String[] names;
        try {
            // Create a JSONArray from the input string
            JSONArray jsonArray = new JSONArray(jsonstring);

            // Create a new array to store the names
            names = new String[jsonArray.length()];

            // Loop through the JSON array and extract the name values
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                names[i] = name;
            }

            // Print the array of names

        } catch (JSONException e) {
            System.out.println("Invalid JSON string.");
            names = new String[0];
        }
        return names;
    }
}
