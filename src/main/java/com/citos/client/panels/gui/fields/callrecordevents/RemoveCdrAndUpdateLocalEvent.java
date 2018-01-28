/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.callrecordevents;

import com.citos.client.panels.gui.fields.HistoryField;
import com.citos.client.panels.gui.fields.HistoryField;


// Used to trigger DataPanelsRegister cdr remove
// -> DataPanelsRegister will then send delete order to server and fetch current cdr fields
public class RemoveCdrAndUpdateLocalEvent {
    private HistoryField historyField;

    public RemoveCdrAndUpdateLocalEvent(HistoryField historyField) {
        this.historyField = historyField;
    }

    public HistoryField getHistoryField() {
        return historyField;
    }
}
