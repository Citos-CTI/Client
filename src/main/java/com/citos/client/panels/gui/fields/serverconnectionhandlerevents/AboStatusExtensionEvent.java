/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.serverconnectionhandlerevents;

/**
 * Created by johannes on 07.04.2017.
 */
public class AboStatusExtensionEvent {
    private String phonenumber;

    public AboStatusExtensionEvent(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
}
