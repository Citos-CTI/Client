/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by johannesengler on 12.05.17.
 */
public class ResolveNumberFromNameEvent {
    private AtomicInteger left;
    private String name;
    private long timestamp;
    private int start;

    public ResolveNumberFromNameEvent(int left, String name, int start) {
        this.left = new AtomicInteger(left);
        this.name = name;
        this.timestamp = System.currentTimeMillis();
        this.start = start;
    }

    public AtomicInteger getLeft() {
        return left;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() { return  timestamp;}

    public int getStart() {
        return start;
    }
}
