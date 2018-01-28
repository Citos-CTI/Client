/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.internevents;

import com.citos.client.PhoneNumber;
import com.citos.client.PhoneNumber;

/**
 * Created by johannes on 07.04.2017.
 */
public class AddInternEvent {
    private PhoneNumber phoneNumber;

    public AddInternEvent(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneNumber getPhoneNumber(){
        return phoneNumber;
    }
}
