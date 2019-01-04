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
    private int start;
    private long timestamp;
    private boolean strict;

    // case if we search for an incomplete number (strict search disabled)
    public SearchCdrInDatabaseEvent(String number, int amount, int start) {
        this.number = number;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.strict = false;
        this.start = start;
    }

    // case if number (-> to name) was found in datasource -> strict search enabled (we know the exact number)
    public SearchCdrInDatabaseEvent(String number, int amount, int start, long timestamp) {
        this.number = number;
        this.amount = amount;
        this.timestamp = timestamp;
        this.strict = true;
        this.start = start;
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

    public int getStart() {
        return start;
    }
}
