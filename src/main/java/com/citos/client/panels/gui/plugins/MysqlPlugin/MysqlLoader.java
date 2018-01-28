/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.MysqlPlugin;

import com.google.common.eventbus.EventBus;
import com.citos.client.panels.gui.fields.callrecordevents.FoundCdrNameInDataSourceEvent;
import com.citos.client.panels.gui.fields.callrecordevents.NotFoundCdrNameInDataSourceEvent;
import com.citos.client.panels.gui.fields.callrecordevents.SearchCdrInDatabaseEvent;
import com.citos.client.panels.gui.fields.callrecordevents.SearchDataSourcesForCdrEvent;
import com.citos.client.panels.gui.plugins.AddressBookEntry;
import com.citos.client.panels.gui.plugins.AddressLoader;
import com.citos.client.panels.gui.plugins.DataSource;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


/**
 * @author johannes
 */
public class MysqlLoader implements AddressLoader {
    //TODO: DELTE only for Test
    private ArrayList<AddressBookEntry> en = new ArrayList<>();

    private String name = "MysqlPlugin";
    private MysqlLoaderStorage storageTemp;
    private MysqlLoaderStorage storage;
    private DataSource source;
    private EventBus eventBus;

    public MysqlLoader(DataSource source) {
        this.source = source;

        ArrayList<String> infos = new ArrayList<>();
        ArrayList<String> test = new ArrayList<>();

        //Test Dummy Data
        test.add("Wilhelm");
        test.add("Meier");
        test.add("Berlin");
        test.add("0132323");
        test.add("018923123");
        en.add(new AddressBookEntry(test, "Wilhelm Meier", source));
        infos.add("Johannes");
        infos.add("Engler");
        infos.add("Bad Krozingen");
        infos.add("076641234");
        infos.add("0123123");
        en.add(new AddressBookEntry(infos, "Johannes Minister Prinz und König von Swasiland", source));

        storage = new MysqlLoaderStorage();
        storageTemp = new MysqlLoaderStorage(storage);
    }

    @Override
    public ArrayList<AddressBookEntry> getResults(String query, int number) {
        // TODO: Implement function
        ArrayList<AddressBookEntry> found = new ArrayList<>();
        for(AddressBookEntry entry : en) {
            if(entry.getName().contains(query)) {
                found.add(entry);
               // eventBus.post();
            }
        }
        return found;
    }

    // given a name we want to return the number for the database query
    public void numberQuery(String name, AtomicInteger left,long searchTimestamp) {
        // Experimental implementation -> will in that fashion not work on actual data
        en.stream()
                .filter(addressBookEntry ->
                addressBookEntry.getSource().getMobile()>-1 || addressBookEntry.getSource().getTelephone()>-1)
                .filter(addressBookEntry -> addressBookEntry.getName().toLowerCase().contains(name.toLowerCase()))
                .forEach(addressBookEntry -> {
                    if(addressBookEntry.getSource().getTelephone()>-1 && left.decrementAndGet()>0){
                            this.eventBus.post(new SearchCdrInDatabaseEvent(
                                    addressBookEntry.get(addressBookEntry.getSource().getTelephone()), 10,searchTimestamp));

                    }
                    if(addressBookEntry.getSource().getMobile()>-1 && left.decrementAndGet()>0){
                        this.eventBus.post(new SearchCdrInDatabaseEvent(
                                addressBookEntry.get(addressBookEntry.getSource().getMobile()), 10,searchTimestamp));

                    }

                });
    }

    public void resolveNameForNumber(SearchDataSourcesForCdrEvent event, AtomicInteger terminated, AtomicBoolean found) {
        int mobile = en.get(0).getSource().getMobile();
        int telephone = en.get(0).getSource().getTelephone();
        for(AddressBookEntry entry : en) {

            if(entry.get(mobile).equals(event.getWho())) {
                found.set(true);
                Logger.getLogger(getClass().getName()).info("Found: "+entry.getName());
                eventBus.post(new FoundCdrNameInDataSourceEvent(event, entry.getName()));
                break;
            } else if( entry.get(telephone).equals(event.getWho())) {
                found.set(true);
                Logger.getLogger(getClass().getName()).info("Found: "+entry.getName());
                eventBus.post(new FoundCdrNameInDataSourceEvent(event, entry.getName()));
                break;
            }

        }
        if(!found.get()) {
            eventBus.post(new NotFoundCdrNameInDataSourceEvent(event));
        }
        terminated.decrementAndGet();
    }

    public void saved() {
        this.storage = new MysqlLoaderStorage(this.storageTemp);
        // Update also the datasource -> without that Addressfields wouldnt update by accept
        this.getDataSource().setAvailableFields(this.storage.getMysqlFields());
    }

    public void discarded() {
        this.storageTemp = new MysqlLoaderStorage(this.storage);
    }

    public MysqlLoaderStorage getStorageTemp() {
        return storageTemp;
    }

    public MysqlLoaderStorage getStorage() {
        return storage;
    }

    public DataSource getDataSource() {
        return source;
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }


    protected void setTag(String tag) {

    }

}
