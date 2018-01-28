/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.otherevents;

/**
 * Created by johannes on 27.04.17.
 */
public class ViewOptionsChangedEvent {
    private boolean sortByCallCount;

    public ViewOptionsChangedEvent(boolean sortByCallCount) {
        this.sortByCallCount = sortByCallCount;
    }

    public boolean isSortByCallCount() {
        return sortByCallCount;
    }
}
