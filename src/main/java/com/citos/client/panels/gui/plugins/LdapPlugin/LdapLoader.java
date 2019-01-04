/*
 * Copyright (c) 2017. Johannes Engler
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.citos.client.panels.gui.plugins.LdapPlugin;

import com.citos.client.panels.gui.fields.callrecordevents.FoundCdrNameInDataSourceEvent;
import com.citos.client.panels.gui.fields.callrecordevents.NotFoundCdrNameInDataSourceEvent;
import com.citos.client.panels.gui.fields.callrecordevents.SearchCdrInDatabaseEvent;
import com.citos.client.panels.gui.fields.callrecordevents.SearchDataSourcesForCdrEvent;
import com.citos.client.panels.gui.plugins.AddressBookEntry;
import com.citos.client.panels.gui.plugins.AddressLoader;
import com.citos.client.panels.gui.plugins.DataSource;
import com.citos.client.panels.gui.plugins.PluginDataField;
import com.google.common.eventbus.EventBus;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author johannesengler
 */
public class LdapLoader implements AddressLoader {
    private static final String SETTING = "setting";

    private LdapLoaderStorage storage;
    private LdapLoaderStorage storageTemp;

    private DataSource source;
    private EventBus eventBus;


    public LdapLoader(DataSource source) {
        this.source = source;
        this.storage = new LdapLoaderStorage();
        this.storageTemp = new LdapLoaderStorage(storage);
    }

    @Override
    public ArrayList<AddressBookEntry> getResults(String ein, int n) {
        this.storage.initLdap();

        ArrayList<AddressBookEntry> aus = new ArrayList<>();
        SearchControls sc = new SearchControls();
        String[] attributeFilter = new String[storage.getLdapFields().size()];

        int i = 0;
        StringBuilder builder = new StringBuilder();
        if(storage.getLdapType().length()>0 && storage.getLdapType().split("=").length==2) {
            builder.append("(&");
        }
        builder.append("(|");
        for (PluginDataField s : storage.getLdapFields()) {
            attributeFilter[i] = s.getFieldname();
            builder.append("(");
            builder.append(s.getFieldname());
            builder.append("=");
            // Search for occurence of sequence user typed (ex: without search fpr Mei wouldnt find user "Harald Meier")
            if (ein.length() > 0) {
                builder.append("*");
            }
            builder.append(ein);
            builder.append("*)");
            ++i;
        }
        builder.append(")");
        if(storage.getLdapType().length()>0 && storage.getLdapType().split("=").length==2) {
            builder.append("(" + storage.getLdapType() + ")");
            builder.append(")");
        }
        String filter = builder.toString();
        sc.setReturningAttributes(attributeFilter);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

        //String filter = "(|(sn="+ein+"*)(sn="+ein+"*)(cn="+ein+"*)(o="+ein+"*))";
        NamingEnumeration results = null;

        DirContext dctx = null;
        try {
            dctx = new InitialDirContext(storage.getEnv());
        } catch (NamingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try {
            results = dctx.search("", filter, sc);
        } catch (NamingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try {
            i = 0;
            while (results != null && results.hasMore() && i < n) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();

                ArrayList<String> data = new ArrayList<>();

                for (PluginDataField field : storage.getLdapFields()) {
                    // catch if a ldap field will be not available

                        try {
                            Attribute attr = (Attribute) attrs.get(field.getFieldname());
                            if(attr != null) {
                                data.add((String) attr.get());
                            }
                        } catch (Exception e) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                            return null;
                        }

                }
                aus.add(new AddressBookEntry(data, data.get(0), source));
                ++i;
            }
        } catch (NamingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        try {
            dctx.close();
        } catch (NamingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return aus;
    }


    public void resolveNameForNumber(SearchDataSourcesForCdrEvent event, AtomicInteger terminated, AtomicBoolean found) {
        // TODO: observe if this is a safe way to search only for numbers
        ArrayList<AddressBookEntry> results = getResults(event.getWho(), 1);
        if (results == null) {
            found.set(false);
        } else if (!results.isEmpty()) {
            found.set(true);
            eventBus.post(new FoundCdrNameInDataSourceEvent(event, results.get(0).getName()));
        }

        if (!found.get()) {
            eventBus.post(new NotFoundCdrNameInDataSourceEvent(event));
        }
        terminated.decrementAndGet();
    }

    // given a name we want to return the number for the database query
    public void numberQuery(String name, AtomicInteger left, long searchTimestamp, int start) {

        ArrayList<AddressBookEntry> results = getResults(name, 1);

        if (!results.isEmpty()) {
            if (storage.getTelephone() > -1) {
                this.eventBus.post(new SearchCdrInDatabaseEvent(results.get(0).get(storage.getTelephone()), 10, start, searchTimestamp));
            } else if (storage.getMobile() > -1) {
                this.eventBus.post(new SearchCdrInDatabaseEvent(results.get(0).get(storage.getMobile()), 10, start, searchTimestamp));
            }
        }
    }

    @Override
    public void saved() {
        this.storage = new LdapLoaderStorage(this.storageTemp);
        // Update also the datasource -> without that Addressfields wouldnt update by accept
        this.getDataSource().setAvailableFields(this.storage.getLdapFields());
    }

    @Override
    public void discarded() {
        this.storageTemp = new LdapLoaderStorage(this.storage);
    }

    public LdapLoaderStorage getStorageTemp() {
        return storageTemp;
    }

    public LdapLoaderStorage getStorage() {
        return storage;
    }

    @Override
    public DataSource getDataSource() {
        return source;
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

}
