/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.serverconnectionhandlerevents;

/**
 * Created by johannes on 07.04.2017.
 */
public class DeAboStatusExtensionEvent {
    private String phonenumber;

    public DeAboStatusExtensionEvent(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
}
