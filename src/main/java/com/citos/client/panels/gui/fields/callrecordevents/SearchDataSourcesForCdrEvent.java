/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

/**
 * Created by johannes on 15.04.2017.
 */
public class SearchDataSourcesForCdrEvent {
    private AddCdrAndUpdateEvent event;

    public SearchDataSourcesForCdrEvent(AddCdrAndUpdateEvent event) {
        this.event = event;
    }

    public String getWho() {
        return event.getWho();
    }

    public String getWhen() {
        return event.getWhen();
    }

    public String getHowLong() {
        return event.getHowLong();
    }

    public boolean isOutgoing() {
        return event.isOutgoing();
    }

    public long getTimeStamp() {return  event.getTimeStamp();}

    public long getSearchInvokedTimestamp() {return  event.getSearchInvokedTimestamp();}

    public int getCountryCode() {
        return event.getCountryCode();
    }

    public int getPrefix() {
        return event.getPrefix();
    }

    public boolean isInternal() {
        return event.isInternal();
    }
}
