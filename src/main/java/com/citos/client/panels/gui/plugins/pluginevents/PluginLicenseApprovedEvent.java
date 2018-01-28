/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.pluginevents;

/**
 * Created by johannes on 01.05.17.
 */
public class PluginLicenseApprovedEvent {
    private String pluginname;
    private int daysLeft;
    private boolean trial;

    public PluginLicenseApprovedEvent(String pluginname, int daysLeft, boolean trial) {
        this.pluginname = pluginname;
        this.daysLeft = daysLeft;
        this.trial = trial;
    }

    public String getPluginname() {
        return pluginname;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public boolean isTrial() {
        return trial;
    }
}
