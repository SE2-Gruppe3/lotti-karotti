package com.example.lottikarotti;

public class Rabbit {

    private int xCor;
    private int yCor;
    private int field;

    private boolean inUse;

    public Rabbit(int xCor, int yCor) {
        this.xCor = xCor;
        this.yCor = yCor;
        this.field = 0;
        this.inUse= false;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public int getxCor() {
        return xCor;
    }

    public void setxCor(int xCor) {
        this.xCor = xCor;
    }

    public int getyCor() {
        return yCor;
    }

    public void setyCor(int yCor) {
        this.yCor = yCor;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }
}
