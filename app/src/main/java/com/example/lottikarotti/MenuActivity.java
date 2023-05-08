package com.example.lottikarotti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;

public class MenuActivity extends AppCompatActivity {

    private Button playGame;

    private Button openSettings;

    private Button openAbout;
    private Button exitSettings;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Connecting the Service to the MainActivity
        Intent intent = new Intent(this, BGMusic.class);
        startService(intent);

        playGame = findViewById(R.id.button_Play);
        playGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        openSettings = findViewById(R.id.button_Settings);
        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        openAbout =  findViewById(R.id.button_About);

        exitSettings =  findViewById(R.id.exitMenu);

        exitSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, BGMusic.class);
        stopService(intent);
        super.onDestroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        updateBrightness();
    }

    private void updateBrightness() {
        SharedPreferences sharedBrightness = getSharedPreferences("settings", MODE_PRIVATE);
        int brightness = sharedBrightness.getInt("brightness", 100);
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
    }
}
