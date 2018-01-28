/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

/**
 * Created by johannes on 07.04.2017.
 */
public class AddCdrAndUpdateEvent {
    private final String who;
    private final String when;
    private final String howLong;
    private final boolean outgoing;
    private final long timeStamp;
    private final long searchInvokedTimestamp;
    private final boolean ordered;
    private final String searchText;
    private final boolean searched;
    private final int disposition;
    private final int countryCode;
    private final int prefix;
    private final boolean internal;


    public AddCdrAndUpdateEvent(String who, String when, String howLong, int disposition,
                                boolean outgoing, long timeStamp, boolean ordered, String searched,
                                long searchInvokedTimestamp, boolean internal, int countryCode, int prefix) {
        this.who = who;
        this.when = when;
        this.howLong = howLong;
        this.outgoing = outgoing;
        this.timeStamp = timeStamp;
        this.ordered = ordered;
        this.searchText = searched;
        this.searchInvokedTimestamp = searchInvokedTimestamp;
        this.searched = searched.length() > 0;
        this.disposition = disposition;
        this.internal = internal;
        this.countryCode = countryCode;
        this.prefix = prefix;
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

    public boolean isOrdered() {
        return ordered;
    }

    public String getSearchText() {
        return searchText;
    }

    public boolean isSearched() {
        return searched;
    }

    public long getSearchInvokedTimestamp() {
        return searchInvokedTimestamp;
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

    public int getDisposition() {
        return disposition;
    }
}
