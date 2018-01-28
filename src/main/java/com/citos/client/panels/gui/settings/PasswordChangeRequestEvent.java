/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.settings;

/**
 * Created by johannes on 28.04.17.
 */
public class PasswordChangeRequestEvent {
    private String oldPw;
    private String newPw;
    private String user;

    public PasswordChangeRequestEvent(String user, String oldPw, String newPw) {
        this.user = user;
        this.oldPw = oldPw;
        this.newPw = newPw;
    }

    public String getOldPw() {
        return oldPw;
    }

    public String getNewPw() {
        return newPw;
    }

    public String getUser() {
        return user;
    }
}
