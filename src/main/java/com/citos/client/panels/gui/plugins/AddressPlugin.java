/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins;

import com.citos.client.panels.gui.fields.callrecordevents.SearchDataSourcesForCdrEvent;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by johannes on 22.03.2017.
 */
public interface AddressPlugin {

    AddressLoader getLoader();

    void setLoader(AddressLoader loader);

    PluginSettingsField getPluginSettingsField();

    void setPluginSettingsField(PluginSettingsField settingsField);

    String getAuthor();

    String getName();

    String getAuthorContact();

    ArrayList<AddressBookEntry> getResults(String query, int number);

    void searchPossibleNumbers(String name, AtomicInteger left, long searchTimestamp);

    void resolveNameForNumber(SearchDataSourcesForCdrEvent event, AtomicInteger terminated, AtomicBoolean found);

    ArrayList<PluginDataField> getDataFields();

    void setDataFields(ArrayList<PluginDataField> datasourceValues);

    ArrayList<String> getOptions();

    void setOptions(ArrayList<String> options);

}
