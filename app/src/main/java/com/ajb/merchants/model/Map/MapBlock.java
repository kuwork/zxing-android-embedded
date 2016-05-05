package com.ajb.merchants.model.Map;

import java.io.Serializable;

public class MapBlock implements Serializable {
    private int startX;
    private int startY;
    private int w;
    private int h;

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public void setW(int w) {
        this.w = w;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}