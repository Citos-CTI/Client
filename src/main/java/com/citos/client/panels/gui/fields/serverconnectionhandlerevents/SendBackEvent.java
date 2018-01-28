/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.serverconnectionhandlerevents;

/**
 * Created by johannes on 07.04.2017.
 */
public class SendBackEvent {
    private String message;

    public SendBackEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
