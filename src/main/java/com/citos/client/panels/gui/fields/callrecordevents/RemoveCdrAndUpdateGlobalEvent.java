/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

import com.citos.client.panels.gui.fields.HistoryField;
import com.citos.client.panels.gui.fields.HistoryField;

public class RemoveCdrAndUpdateGlobalEvent {
    private HistoryField historyField;
    private int historyFieldCount;
    private int amount;

    public RemoveCdrAndUpdateGlobalEvent(HistoryField historyField, int historyFieldCount, int amount) {
        this.historyField = historyField;
        this.historyFieldCount = historyFieldCount;
        this.amount = amount;
    }

    public HistoryField getHistoryField() {
        return historyField;
    }

    public int getHistoryFieldCount() {
        return historyFieldCount;
    }

    public int getAmount() {
        return amount;
    }
}
