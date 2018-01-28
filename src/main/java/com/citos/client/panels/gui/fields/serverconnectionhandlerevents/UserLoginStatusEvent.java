/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.serverconnectionhandlerevents;

/**
 * Created by johannes on 17.04.17.
 */
public class UserLoginStatusEvent {
    private boolean isLoggedIn;
    private String hashedPw;

    public UserLoginStatusEvent(boolean isLoggedIn, String hashedPw) {
        this.isLoggedIn = isLoggedIn;
        this.hashedPw = hashedPw;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getHashedPw() {
        return hashedPw;
    }
}
