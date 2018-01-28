/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client;

public class OptionTuple {
    private boolean activated;
    private String name;

    public OptionTuple(boolean activated, String name) {
        this.activated = activated;
        this.name = name;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getName() {
        return name;
    }
}
