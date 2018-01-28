/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.otherevents;

/**
 * Created by johannesengler on 14.05.17.
 */
public class PluginLoadedEvent {
    private String name;

    public PluginLoadedEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
