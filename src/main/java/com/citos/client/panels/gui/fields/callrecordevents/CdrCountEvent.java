/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

/**
 * Created by johannesengler on 10.05.17.
 */
public class CdrCountEvent {
    private int currentAmount;

    public CdrCountEvent(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }
}
