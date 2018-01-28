/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins;

import com.google.common.eventbus.EventBus;

import java.util.List;

/**
 *
 * @author johannes
 */
public interface AddressLoader {
    //Fetch results from the Loader
    List<AddressBookEntry> getResults(String ein, int n);

    //saves the settings if user clicks on save button also for the plugin
    void saved();

    //discards the setting if user clicks on discard
    void discarded();

    //get the DataSource
    DataSource getDataSource();

    //Sets the eventbus for asynch back propagation of results
    void setEventBus(EventBus eventBus);

}
