package com.example.lottikarotti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lottikarotti.Network.ServerConnection;
import com.example.lottikarotti.databinding.ActivityLobbyBinding;

import java.util.Random;

public class LobbyActivity extends AppCompatActivity {
    private ActivityLobbyBinding binding;
    private boolean setUsername;
    private boolean setLobbyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLobbyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.btnStartGame.setEnabled(false);
        binding.btnJoinGame.setEnabled(false);
        setLobbyId = false;
        setUsername = false;

        binding.etLobbyId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    binding.btnStartGame.setEnabled(false);
                }else{
                    binding.btnStartGame.setEnabled(true);
                }
                if (s.toString().length() < 6) {
                    binding.btnJoinGame.setEnabled(false);
                }
                if (s.toString().length() == 6) {
                    setLobbyId = true;
                    if (setLobbyId && setUsername) {
                        binding.btnJoinGame.setEnabled(true);
                    }
                }
            }
        });

        binding.usernameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().isEmpty()) {
                    binding.usernameTextView.setError("username must be set");
                    binding.btnStartGame.setEnabled(false);
                    binding.btnJoinGame.setEnabled(false);
                } else {
                    setUsername = true;
                    if (binding.etLobbyId.getText().toString().length() ==6 && setUsername) {
                        binding.btnJoinGame.setEnabled(true);
                        binding.btnStartGame.setEnabled(false);
                    }else {
                        binding.btnStartGame.setEnabled(true);
                    }
                }
            }
        });

        binding.btnStartGame.setOnClickListener(v -> {
            int lobbyCode = new Random().nextInt(900000) + 100000;
            startGameActivity(lobbyCode, binding.usernameTextView.getText().toString(), "start");
        });

        binding.btnJoinGame.setOnClickListener(v -> {
            try {
                int id = Integer.parseInt(binding.etLobbyId.getText().toString());
                startGameActivity(id, binding.usernameTextView.getText().toString(), "join");
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Please type valid number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startGameActivity(Integer lobbyId, String username, String info) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("lobbyId", String.valueOf(lobbyId));
        intent.putExtra("username", String.valueOf(username));
        intent.putExtra("info", String.valueOf(info));
        startActivity(intent);
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
