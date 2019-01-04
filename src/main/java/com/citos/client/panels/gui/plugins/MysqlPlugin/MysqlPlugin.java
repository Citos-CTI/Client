/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.MysqlPlugin;

import com.citos.client.panels.gui.fields.callrecordevents.SearchDataSourcesForCdrEvent;
import com.citos.client.panels.gui.plugins.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by johannes on 22.03.2017.
 */
public class MysqlPlugin implements AddressPlugin {
    private MysqlLoader loader;
    private MysqlSettingsField settingsField;
    private String AUTHOR = "Johannes Engler";
    private String AUTHOR_CONTACT = "engler.johannes@posteo.de";
    private String PLUGIN_NAME= "MysqlPlugin";
    private String PLUGIN_TAG = "MySql";

    public MysqlPlugin() {

        DataSource source = new DataSource(PLUGIN_NAME, PLUGIN_TAG);
        loader = new MysqlLoader(source);
        settingsField = new MysqlSettingsField(loader, PLUGIN_NAME);

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
        this.loader = (MysqlLoader) loader;
    }

    @Override
    public PluginSettingsField getPluginSettingsField() {
        return settingsField;
    }

    @Override
    public void setPluginSettingsField(PluginSettingsField settingsField) {
        this.settingsField = (MysqlSettingsField) settingsField;
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
    public void searchPossibleNumbers(String name, AtomicInteger left, long searchTimestamp, int start) {
        loader.numberQuery(name, left, searchTimestamp, start);
    }

    @Override
    public void resolveNameForNumber(SearchDataSourcesForCdrEvent event, AtomicInteger terminated, AtomicBoolean found) {
        loader.resolveNameForNumber(event, terminated, found);
    }

    @Override
    public ArrayList<PluginDataField> getDataFields() {
        return loader.getStorage().getMysqlFields();
    }

    @Override
    public void setDataFields(ArrayList<PluginDataField> datasourceFields) {
        loader.getDataSource().setAvailableFields(datasourceFields);
        loader.getStorage().setMysqlFields(datasourceFields);
        ArrayList<PluginDataField> datasourceFieldsTemp = new ArrayList<>();
        // Deep Copy for being able to reset
        for(PluginDataField plug : datasourceFields) {
            datasourceFieldsTemp.add(new PluginDataField(plug.getFieldname(),plug.getFieldvalue(),plug.isTelephone(),plug.isMobile()));
        }
        loader.getStorageTemp().setMysqlFields(datasourceFieldsTemp);
        int i = 0;
        for(PluginDataField dataField : datasourceFields) {
            if(dataField.isTelephone()) {
                loader.getStorageTemp().setTelephone(i);
                loader.getStorage().setTelephone(i);
            } else if(dataField.isMobile()) {
                loader.getStorageTemp().setMobile(i);
                loader.getStorage().setMobile(i);
            }
            ++i;
        }
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
        if(options.isEmpty()) {
            options.add("localhost");
            options.add("2323");
            options.add("database");
        }
        loader.getStorage().setServerAddress(options.get(0));
        loader.getStorage().setServerPort(Integer.valueOf(options.get(1)));
        loader.getStorage().setDatabase(options.get(2));

        //to force storageTemp = storage
        loader.discarded();
    }

}
