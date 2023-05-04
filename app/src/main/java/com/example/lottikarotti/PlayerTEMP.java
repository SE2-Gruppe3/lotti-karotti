package com.example.lottikarotti;

public class PlayerTEMP {
    private String sid;
    private int r1Field;
    private int r2Field;
    private int r3Field;
    private int r4Field;

    PlayerTEMP(String socketid){
        r1Field = 0;
        r2Field = 0;
        r3Field = 0;
        r4Field = 0;

        this.sid = socketid;
    }

    public String getSocketid(){
        return sid;
    }
    public void moveRabbit(int rabbitnumber, int fields){
        switch (rabbitnumber){
            case 1:
                r1Field += fields;
                break;
        }
        ///...
    }

    public int getR1Field() {
        return r1Field;
    }

    public int getR2Field() {
        return r2Field;
    }

    public int getR3Field() {
        return r3Field;
    }

    public void setR4Field(int r4Field) {
        this.r4Field = r4Field;
    }
}
