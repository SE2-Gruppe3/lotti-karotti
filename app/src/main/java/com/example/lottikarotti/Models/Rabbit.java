package com.example.lottikarotti.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


/**
 * This class represents a Rabbit object in the Lotti Karotti game.
 */
public class Rabbit {

    // Instance variables
    private String name; // Name of the rabbit
    private int position; // Position of the rabbit on the game board

    /**
     Default constructor for Rabbit class.
     */
    public Rabbit() {
    }

    /**
     Constructor for Rabbit class.
     @param name the name of the rabbit.
     @param position the position of the rabbit on the game board.
     */
    @JsonCreator
    public Rabbit(@JsonProperty("name") String name, @JsonProperty("position") int position) {
        this.name = name;
        this.position = position;
    }

    /**
     Getter for rabbit name.
     @return the name of the rabbit.
     */
    public String getName() {
        return name;
    }

    /**
     Getter for rabbit position.
     @return the position of the rabbit on the game board.
     */
    public int getPosition() {
        return position;
    }
}

