/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

/**
 * Created by johannesengler on 12.05.17.
 */
public class CdrNotFoundOnServerEvent {
    private final long timestamp;

    public CdrNotFoundOnServerEvent(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
