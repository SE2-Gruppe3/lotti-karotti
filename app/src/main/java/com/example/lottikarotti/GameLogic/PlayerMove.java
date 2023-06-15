package com.example.lottikarotti.GameLogic;

import com.example.lottikarotti.MainActivity;
import com.example.lottikarotti.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class PlayerMove {

    public static List<Player> handleMoveFromServer(String json) throws JsonProcessingException {
        System.out.println("Received move from server!");
        ObjectMapper mapper = new ObjectMapper();
        List<Player> players = Arrays.asList(mapper.readValue(json, Player[].class));
        return players;

    }
    public static void rabbitToggle(MainActivity context, boolean isMyTurn){
        context.runOnUiThread(() -> {
            context.rabbit1.setEnabled(isMyTurn);
            context.rabbit2.setEnabled(isMyTurn);
            context.rabbit3.setEnabled(isMyTurn);
            context.rabbit4.setEnabled(isMyTurn);
            context.drawButton.setEnabled(!isMyTurn);
        });
    }
}
