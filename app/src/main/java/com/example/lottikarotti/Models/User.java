package com.example.lottikarotti.Models;

public class User {
    private String username;
    private Rabbit rabbit1;
    private Rabbit rabbit2;
    private Rabbit rabbit3;
    private Rabbit rabbit4;
    private Rabbit currentRabbit;

    public User(String username, Rabbit rabbit1, Rabbit rabbit2, Rabbit rabbit3, Rabbit rabbit4) {
        this.username = username;
        this.rabbit1 = rabbit1;
        this.rabbit2 = rabbit2;
        this.rabbit3 = rabbit3;
        this.rabbit4 = rabbit4;
        this.currentRabbit = currentRabbit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Rabbit getRabbit1() {
        return rabbit1;
    }

    public void setRabbit1(Rabbit rabbit1) {
        this.rabbit1 = rabbit1;
    }

    public Rabbit getRabbit2() {
        return rabbit2;
    }

    public void setRabbit2(Rabbit rabbit2) {
        this.rabbit2 = rabbit2;
    }

    public Rabbit getRabbit3() {
        return rabbit3;
    }

    public void setRabbit3(Rabbit rabbit3) {
        this.rabbit3 = rabbit3;
    }

    public Rabbit getRabbit4() {
        return rabbit4;
    }

    public void setRabbit4(Rabbit rabbit4) {
        this.rabbit4 = rabbit4;
    }

    public Rabbit getCurrentRabbit() {
        return currentRabbit;
    }

    public void setCurrentRabbit(Rabbit currentRabbit) {
        this.currentRabbit = currentRabbit;
    }
}