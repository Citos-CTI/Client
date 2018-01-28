/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.otherevents;

import com.citos.client.panels.gui.plugins.AddressBookEntry;
import com.citos.client.panels.gui.plugins.AddressBookEntry;

import java.util.List;


/**
 * Created by johannes on 14.04.2017.
 */
public class UpdateAddressFieldsEvent {
    private List<AddressBookEntry> address;

    public UpdateAddressFieldsEvent(List<AddressBookEntry> address) {
        this.address = address;
    }

    public List<AddressBookEntry> getAddressBookEntries() {
        return address;
    }
}
