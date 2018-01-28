/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

/**
 * Created by johannes on 28.04.17.
 */
public class OrderCDRsEvent {
    private int start;
    private int amount;

    public OrderCDRsEvent(int start, int amount) {
        this.start = start;
        this.amount = amount;
    }

    public int getStart() {
        return start;
    }

    public int getAmount() {
        return amount;
    }
}
