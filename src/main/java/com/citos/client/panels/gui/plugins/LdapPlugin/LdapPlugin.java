/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.LdapPlugin;

import com.citos.client.panels.gui.plugins.AddressLoader;
import com.citos.client.panels.gui.fields.callrecordevents.SearchDataSourcesForCdrEvent;
import com.citos.client.panels.gui.plugins.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by johannes on 22.03.2017.
 */
public class LdapPlugin implements AddressPlugin {
    private LdapLoader loader;
    private LDAPSettingsField settingsField;
    private String AUTHOR = "Johannes Engler";
    private String AUTHOR_CONTACT = "engler.johannes@posteo.de";
    private String PLUGIN_NAME= "LdapPlugin";
    private String PLUGIN_TAG = "LDAP";

    public LdapPlugin() {
        DataSource source = new DataSource(PLUGIN_NAME, PLUGIN_TAG);
        loader = new LdapLoader(source);
        settingsField = new LDAPSettingsField(loader, PLUGIN_NAME);
        this.setLoader(loader);
        this.setPluginSettingsField(settingsField);
    }

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public AddressLoader getLoader() {
        return this.loader;
    }

    @Override
    public void setLoader(AddressLoader loader) {
        this.loader = (LdapLoader) loader;
    }

    @Override
    public PluginSettingsField getPluginSettingsField() {
        return settingsField;
    }

    @Override
    public void setPluginSettingsField(PluginSettingsField settingsField) {
        this.settingsField = (LDAPSettingsField) settingsField;
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
        return loader.getResults(query,number);
    }

    @Override
    public void searchPossibleNumbers(String name, AtomicInteger left, long searchTimestamp) {
        loader.numberQuery(name, left, searchTimestamp);
    }

    @Override
    public void resolveNameForNumber(SearchDataSourcesForCdrEvent event, AtomicInteger terminated, AtomicBoolean found) {
        loader.resolveNameForNumber(event, terminated, found);
    }

    @Override
    public ArrayList<PluginDataField> getDataFields() {
        return loader.getStorage().getLdapFields();
    }

    @Override
    public void setDataFields(ArrayList<PluginDataField> datasourceFields) {
        loader.getDataSource().setAvailableFields(datasourceFields);
        loader.getStorage().setLdapFields(datasourceFields);
        ArrayList<PluginDataField> datasourceFieldsTemp = new ArrayList<>();
        // Deep Copy for being able to reset
        for(PluginDataField plug : datasourceFields) {
            datasourceFieldsTemp.add(new PluginDataField(plug.getFieldname(),plug.getFieldvalue(),plug.isTelephone(),plug.isMobile()));
        }
        loader.getStorageTemp().setLdapFields(datasourceFieldsTemp);
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
        options.add(loader.getStorage().getLdapAddress());
        options.add(String.valueOf(loader.getStorage().getLdapServerPort()));
        options.add(loader.getStorage().getLdapSearchBase());
        options.add(loader.getStorage().getLdapType());

        return options;
    }

    @Override
    public void setOptions(ArrayList<String> options) {
        if(options.isEmpty()) {
            options.add("localhost");
            options.add("389");
            options.add("dc=test,dc=de");
            options.add("ou=addressbook");

        }
        loader.getStorage().setLdapAddress(options.get(0));
        loader.getStorage().setLdapServerPort(Integer.valueOf(options.get(1)));
        loader.getStorage().setLdapSearchBase(options.get(2));
        loader.getStorage().setLdapBase(options.get(3));

        //to force storageTemp = storage
        loader.discarded();
    }

}
