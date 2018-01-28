/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

/**
 * Created by johannes on 15.04.2017.
 */
public class FoundCdrNameInDataSourceEvent {
    private final String name;
    private final String who;
    private final String when;
    private final String howLong;
    private final boolean outgoing;
    private final int countryCode;
    private final int prefix;
    private final boolean internal;
    private final long timeStamp;

    public FoundCdrNameInDataSourceEvent(SearchDataSourcesForCdrEvent event, String name) {
        this.name = name;
        this.who = event.getWho();
        this.when = event.getWhen();
        this.howLong = event.getHowLong();
        this.outgoing = event.isOutgoing();
        this.timeStamp = event.getTimeStamp();

        this.countryCode = event.getCountryCode();
        this.prefix = event.getPrefix();
        this.internal = event.isInternal();
    }

    public String getName() {
        return name;
    }

    public String getWho() {
        return who;
    }

    public String getWhen() {
        return when;
    }

    public String getHowLong() {
        return howLong;
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public int getPrefix() {
        return prefix;
    }

    public boolean isInternal() {
        return internal;
    }
}
