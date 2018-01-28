/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins;

/**
 * Created by johannes on 23.04.17.
 */
public class PluginDataField {
    private String fieldname;
    private String fieldvalue;
    private boolean telephone;
    private boolean mobile;

    public PluginDataField(String fieldname, String fieldvalue) {
        this.fieldname = fieldname;
        this.fieldvalue = fieldvalue;
        this.telephone = false;
        this.mobile = false;
    }

    public PluginDataField(String fieldname, String fieldvalue, boolean telephone, boolean mobile) {
        this.fieldname = fieldname;
        this.fieldvalue = fieldvalue;
        this.telephone = telephone;
        this.mobile = mobile;
    }

    public String getFieldname() {
        return fieldname;
    }

    public String getFieldvalue() {
        return fieldvalue;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public void setFieldvalue(String fieldvalue) {
        this.fieldvalue = fieldvalue;
    }

    public boolean isTelephone() {
        return telephone;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setTelephone(boolean telephone) {
        this.telephone = telephone;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginDataField dataField = (PluginDataField) o;

        if (telephone != dataField.telephone) return false;
        if (mobile != dataField.mobile) return false;
        if (!fieldname.equals(dataField.fieldname)) return false;
        return fieldvalue.equals(dataField.fieldvalue);
    }


}

