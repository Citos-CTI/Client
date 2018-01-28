/*
 * Copyright (c) 2017. Johannes Engler
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.citos.client.panels.gui.plugins;

import java.util.ArrayList;

/**
 *
 * @author johannesengler
 */
public class AddressBookEntry {
    private String name;
    private ArrayList<String> data;
    private DataSource source;


    public AddressBookEntry(ArrayList<String> data, String name, DataSource source) {
        this.name = name;
        this.data = data;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public String get(int i) {
        return data.get(i);
    }

    public int getDataSize() {
        return data.size();
    }

    public DataSource getSource() {
        return source;
    }

}
