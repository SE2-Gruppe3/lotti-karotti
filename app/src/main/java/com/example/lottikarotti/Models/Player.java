package com.example.lottikarotti.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class represents a Player object in the Lotti Karotti game.
 */
public class Player {

    // Instance variables
    private final String sid; // Session ID of the player
    private final String lobbycode; // Lobby code of the game
    private final String color; // Color of the player's rabbits
    private final List<Rabbit> rabbits; // List of the player's rabbits

    /**
     * Constructor for Player class.
     *
     * @param sid       the session ID of the player.
     * @param lobbycode the lobby code of the game.
     * @param color     the color of the player's rabbits.
     * @param rabbits   a list of the player's rabbits.
     */
    @JsonCreator
    public Player(@JsonProperty("sid") String sid,
                  @JsonProperty("lobbycode") String lobbycode,
                  @JsonProperty("color") String color,
                  @JsonProperty("rabbits") List<Rabbit> rabbits) {
        this.sid = sid;
        this.lobbycode = lobbycode;
        this.color = color;
        this.rabbits = rabbits;
    }

    /**
     * Getter for session ID.
     *
     * @return the session ID of the player.
     */
    public String getSid() {
        return sid;
    }

    /**
     * Getter for lobby code.
     *
     * @return the lobby code of the game.
     */
    public String getLobbycode() {
        return lobbycode;
    }

    /**
     * Getter for player color.
     *
     * @return the color of the player's rabbits.
     */
    public String getColor() {
        return color;
    }

    /**
     * Getter for player's rabbits.
     *
     * @return a list of the player's rabbits.
     */
    public List<Rabbit> getRabbits() {
        return rabbits;
    }
}
