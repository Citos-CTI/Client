package com.citos.client.panels.gui.fields.otherevents;

public class WindowPositionLoadedEvent {
    private double x;
    private double y;

    public WindowPositionLoadedEvent(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public WindowPositionLoadedEvent(String str) {
        x = 0.0;
        y = 0.0;
        if (str.split(";").length == 2) {
            String[] s = str.split(";");
            x = Double.valueOf(s[0]);
            y = Double.valueOf(s[1]);
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
