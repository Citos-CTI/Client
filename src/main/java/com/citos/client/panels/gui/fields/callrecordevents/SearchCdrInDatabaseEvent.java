/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

/**
 * Created by johannesengler on 12.05.17.
 */
public class SearchCdrInDatabaseEvent {
    private final String number;
    private int amount;
    private long timestamp;
    private boolean strict;

    // case if we search for an incomplete number (strict search disabled)
    public SearchCdrInDatabaseEvent(String number, int amount) {
        this.number = number;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.strict = false;
    }

    // case if number (-> to name) was found in datasource -> strict search enabled (we know the exact number)
    public SearchCdrInDatabaseEvent(String number, int amount, long timestamp) {
        this.number = number;
        this.amount = amount;
        this.timestamp = timestamp;
        this.strict = true;
    }

    public String getNumber() {
        return number;
    }

    public int getAmount() {
        return amount;
    }

    public long getTimestamp() {return timestamp;}

    public boolean isStrict() {
        return strict;
    }
}
