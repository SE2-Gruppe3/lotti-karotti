package com.example.lottikarotti;

public class Rabbit {

    private int id;
    private float xCor;
    private float yCor;
    private int field;

    private boolean inUse;

    public Rabbit(int id,float xCor, float yCor) {
        this.id=id;
        this.xCor = xCor;
        this.yCor = yCor;
        this.field = 0;
        this.inUse= false;
    }

    public boolean isInUse() {
        return inUse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public float getxCor() {
        return xCor;
    }

    public void setxCor(float xCor) {
        this.xCor = xCor;
    }

    public float getyCor() {
        return yCor;
    }

    public void setyCor(float yCor) {
        this.yCor = yCor;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }
}
