/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.SqlitePlugin;

import com.citos.client.panels.gui.fields.callrecordevents.SearchDataSourcesForCdrEvent;
import com.citos.client.panels.gui.plugins.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by johannes on 22.03.2017.
 */
public class SqlitePlugin implements AddressPlugin {
    private SqliteLoader loader;
    private SqliteSettingsField settingsField;
    private String AUTHOR = "Johannes Engler";
    private String AUTHOR_CONTACT = "engler.johannes@posteo.de";
    private String PLUGIN_NAME = "SqlitePlugin";
    private String PLUGIN_TAG = "SQLite";

    public SqlitePlugin() {

        DataSource source = new DataSource(PLUGIN_NAME, PLUGIN_TAG);
        loader = new SqliteLoader(source);
        settingsField = new SqliteSettingsField(loader, "SQLite", PLUGIN_NAME);

    }

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public AddressLoader getLoader() {
        return loader;
    }

    @Override
    public void setLoader(AddressLoader loader) {
        this.loader = (SqliteLoader) loader;
    }

    @Override
    public PluginSettingsField getPluginSettingsField() {
        return settingsField;
    }

    @Override
    public void setPluginSettingsField(PluginSettingsField settingsField) {
        this.settingsField = (SqliteSettingsField) settingsField;
    }

    @Override
    public String getAuthor() {
        return AUTHOR;
    }

    @Override
    public String getAuthorContact() {
        return AUTHOR_CONTACT;
    }

    @Override
    public ArrayList<AddressBookEntry> getResults(String query, int number) {
        return loader.getResults(query, number);
    }

    @Override
    public void searchPossibleNumbers(String name, AtomicInteger left, long timestamp) {

    }

    @Override
    public void resolveNameForNumber(SearchDataSourcesForCdrEvent event, AtomicInteger terminated, AtomicBoolean found) {

    }

    @Override
    public ArrayList<PluginDataField> getDataFields() {
        return loader.getStorage().getMysqlFields();
    }

    @Override
    public void setDataFields(ArrayList<PluginDataField> datasourceFields) {
        loader.getDataSource().setAvailableFields(datasourceFields);
        loader.getStorage().setMysqlFields(datasourceFields);
        loader.getStorageTemp().setMysqlFields(datasourceFields);
    }

    @Override
    public ArrayList<String> getOptions() {
        ArrayList<String> options = new ArrayList<>();
        options.add(loader.getStorage().getServerAddress());
        options.add(String.valueOf(loader.getStorage().getServerPort()));
        options.add(loader.getStorage().getDatabase());
        return options;
    }

    @Override
    public void setOptions(ArrayList<String> options) {
        loader.getStorage().setServerAddress(options.get(0));
        loader.getStorage().setServerPort(Integer.valueOf(options.get(1)));
        loader.getStorage().setDatabase(options.get(2));

        //to force storageTemp = storage
        loader.discarded();
    }

}
