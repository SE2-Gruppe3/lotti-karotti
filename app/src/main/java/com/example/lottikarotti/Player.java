package com.example.lottikarotti;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Player {
    private String sid;
    private String lobbycode;
    private String color;
    private List<Rabbit> rabbits;

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

    public String getSid() {
        return sid;
    }

    public String getLobbycode() {
        return lobbycode;
    }

    public String getColor() {
        return color;
    }

    public List<Rabbit> getRabbits() {
        return rabbits;
    }
}

class Rabbit {
    private String name;
    private int position;

    public Rabbit() {}

    @JsonCreator
    public Rabbit(@JsonProperty("name") String name, @JsonProperty("position") int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}

