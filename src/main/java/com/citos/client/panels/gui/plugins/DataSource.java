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
 * @author johannes
 */
public class DataSource {
    private String source;
    private ArrayList<PluginDataField> availableFields = new ArrayList<>();
    private String tag;
    private int mobile = -1;
    private int telephone = -1;

    public DataSource(String source, String tag) {
        this.source = source;
        this.tag = tag;
    }

    public ArrayList<PluginDataField> getAvailableFields() {
        return availableFields;
    }

    public void setAvailableFields(ArrayList<PluginDataField> availableFields) {
        this.availableFields = availableFields;
        boolean telF = false;
        boolean mobF = false;
        int z=0;
        for (PluginDataField s : availableFields) {
            if(s.isMobile()) {
                mobile = z;
                mobF = true;
            } else if(s.isTelephone()) {
                telephone = z;
                telF = true;
            }
            ++z;
        }
        if(!telF)
            telephone = -1;
        if(!mobF)
            mobile  =-1;
    }

    public String getTag() {return this.tag;}

    public void setDataSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public int getMobile() {
        return mobile;
    }

    public int getTelephone() {
        return telephone;
    }
}
