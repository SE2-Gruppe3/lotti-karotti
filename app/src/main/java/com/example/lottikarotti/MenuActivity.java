package com.example.lottikarotti;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import com.example.lottikarotti.Highscore.HighscoreActivity;
import com.example.lottikarotti.Util.BGMusic;

import java.util.Random;

public class MenuActivity extends AppCompatActivity {

    private Button playGame;
    private Button openSettings;
    private Button openAbout;
    private Button exitSettings;
    private Button highscore;

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
                Intent intent = new Intent(MenuActivity.this, LobbyActivity.class);
                startActivity(intent);


            }
        });
        startShakeAnimation();

        openSettings = findViewById(R.id.button_Settings);
        openSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        exitSettings =  findViewById(R.id.exitMenu);

        exitSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        highscore = findViewById(R.id.button_Highscore);
        highscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, HighscoreActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
    private AnimatorSet animatorSet;
    private void startShakeAnimation() {
        float shakeDistance = 20f; // Adjust the shake distance as needed
        long shakeDuration = 800; // Adjust the shake duration as needed
        Random rand = new Random();
        int pauseDuration = rand.nextInt(10000); // Adjust the pause duration between shakes as needed

        ObjectAnimator shakeAnimator = ObjectAnimator.ofFloat(playGame, "translationX", -shakeDistance, shakeDistance);
        shakeAnimator.setDuration(shakeDuration / 2);
        shakeAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator resetAnimator = ObjectAnimator.ofFloat(playGame, "translationX", 0f);
        resetAnimator.setDuration(shakeDuration / 2);
        resetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(shakeAnimator, ValueAnimator.ofFloat(0f, 0f).setDuration(pauseDuration), resetAnimator);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Animation ended, start next iteration
                startShakeAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Animation cancelled
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Animation repeated
            }
        });

        animatorSet.start();
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, BGMusic.class);
        stopService(intent);
        super.onDestroy();
        // Cancel the animation when the activity is destroyed
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.cancel();
        }

    }
}
