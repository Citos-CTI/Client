package com.citos.client.panels.gui.fields.otherevents;

public class EndWindowPositioningEvent {
    private double x;
    private double y;

    public EndWindowPositioningEvent(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
