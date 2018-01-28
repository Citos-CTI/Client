/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.otherevents;

/**
 * Created by johannes on 07.04.2017.
 */
public class SetStatusEvent {
    private int status;
    private String intern;

    public SetStatusEvent(int status, String intern) {
        this.status = status;
        this.intern = intern;
    }

    public String getIntern() {
        return intern;
    }

    public int getStatus() {
        return status;
    }
}
