/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.MysqlPlugin;

import com.citos.client.panels.gui.plugins.PluginDataField;

import java.util.ArrayList;

/**
 * Created by johannes on 31.03.2017.
 */
public class MysqlLoaderStorage {

    private String serverAddress = "localhost";
    private int serverPort = 3306;
    private String database = "database";
    private int mobile = -1;
    private int telephone = -1;
    // the fields that match database value name and view name eg. tel in database would be showed as telephone in the program
    private ArrayList<PluginDataField> mysqlFields;

    /**
     * Copy Constructor, deep Copy is required for the safe discard functionality
     * @param old
     */
    public MysqlLoaderStorage(MysqlLoaderStorage old) {
        this.serverAddress = old.getServerAddress();
        this.serverPort = old.getServerPort();
        this.database = old.getDatabase();
        this.telephone = old.getTelephone();
        this.mobile = old.getMobile();
        mysqlFields = new ArrayList<>();
        for(PluginDataField oldFields : old.getMysqlFields()) {
            mysqlFields.add(new PluginDataField(oldFields.getFieldname(),oldFields.getFieldvalue(), oldFields.isTelephone(),oldFields.isMobile()));
        }
    }

    public MysqlLoaderStorage() {
        mysqlFields = new ArrayList<>();
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public ArrayList<PluginDataField> getMysqlFields() {
        int i = 0;
        for(PluginDataField field: mysqlFields) {
            field.setMobile(false);
            field.setTelephone(false);
            if(telephone == i) {
                field.setTelephone(true);
            } else if(mobile == i) {
                field.setMobile(true);
            }
            ++i;
        }
        return mysqlFields;
    }

    public void setMysqlFields(ArrayList<PluginDataField> mysqlFields) {
        this.mysqlFields = mysqlFields;
    }

    public int removeFromMysqlFields(String text, String text1) {
        int i =0;
        for (PluginDataField entry : mysqlFields) {
            if (entry.getFieldname().equals(text) && entry.getFieldvalue().equals(text1)) {
                if(entry.isTelephone()) {
                    unsetTelephone();
                } else if(entry.isMobile()) {
                    unsetMobile();
                }
                mysqlFields.remove(entry);
                return i;
            }
            ++i;
        }
        return 0;
    }

    public void alterMysqlFields(String text, String text1, String textNex, String text1New) {
        mysqlFields.forEach((field)->{
            if(field.getFieldname().equals(text) && field.getFieldvalue().equals(text1)) {
                field.setFieldname(textNex);
                field.setFieldvalue(text1New);
            }
        });
    }

    public boolean addToMysqlFields(String text, String text1) {
        for (PluginDataField entry : mysqlFields) {
            if ((entry.getFieldname().equals(text) || entry.getFieldvalue().equals(text1)) ||
                    (text.length()==0 || text1.length()==0)) {
                //One of the entries is already available
                return false;
            }
        }
        //String[] g = {text, text1};
        PluginDataField g = new PluginDataField(text, text1);
        //implement here status
        mysqlFields.add(g);
        return true;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public void unsetMobile() {this.mobile = -1;}

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public void unsetTelephone() {this.telephone =-1;}
}
