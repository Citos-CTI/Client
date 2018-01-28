/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.serverconnectionhandlerevents;

/**
 * Created by johannes on 07.04.2017.
 */
public class CallEvent {
    private String phoneNumber;
    private boolean intern;

    public CallEvent(String phoneNumber, boolean intern) {
        this.phoneNumber = phoneNumber;
        this.intern = intern;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isIntern() {
        return intern;
    }
}
