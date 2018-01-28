/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.pluginevents;

/**
 * Created by johannes on 01.05.17.
 */
public class CheckLicenseForPluginEvent {
    private String pluginLicense;
    private String pluginName;

    public CheckLicenseForPluginEvent(String pluginName, String pluginLicense) {
        this.pluginLicense = pluginLicense;
        this.pluginName = pluginName;
    }

    public String getPluginLicense() {
        return pluginLicense;
    }

    public String getPluginName() {
        return pluginName;
    }
}
