/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client;

import com.citos.client.panels.gui.plugins.PluginDataField;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by johannes on 11.04.2017.
 */
public class SqlLiteConnectionTest {
    private SqlLiteConnection sqlLiteConnection;

    public SqlLiteConnectionTest() {
        // delete possibly existing test-database -> could lead to wrong data to use the same over and over
        File file = new File("test.db");
        if(!file.delete()){
            Logger.getLogger(getClass().getName()).info("Delete operation is failed.");
        }

        // this can be seen as the createDatabase test
        try {
            sqlLiteConnection = new SqlLiteConnection("test.db");
        } catch (ExceptionInInitializerError | NoClassDefFoundError er) {
            // nothing to do just restart -> database is then created. In tests UI cant start, not a problem for tests
            sqlLiteConnection = new SqlLiteConnection("test.db");
        }
        // Insert some data and check directly if they are in
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("amiPassword", "Vogel");
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("amiTest", "Test123");
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("MysqlPluginDatasource2", "Field;Ranger");
    }

    @Test
    public void query() throws Exception {
        Assert.assertEquals("Test123",sqlLiteConnection.query("Select setting from settings where description='amiTest'"));
        Assert.assertEquals("Field;Ranger",sqlLiteConnection.query("Select setting from settings where description='MysqlPluginDatasource2'"));

    }


    @Test
    public void updateOneAttribute() throws Exception {
        sqlLiteConnection.updateOneAttribute("settings", "description", "amiTest", "setting", "Test1234");
        Assert.assertEquals("Test1234",sqlLiteConnection.query("Select setting from settings where description='amiTest'"));

    }

    @Test
    public void buildUpdateOrInsertStatementForSetting() {
        // check here for the data inserted in the constructor
        Assert.assertEquals("Vogel", sqlLiteConnection.query("Select setting from Settings where description='amiPassword'"));
        Assert.assertEquals("Test123", sqlLiteConnection.query("Select setting from Settings where description='amiTest'"));
        Assert.assertEquals("Field;Ranger", sqlLiteConnection.query("Select setting from Settings where description='MysqlPluginDatasource2'"));
    }

    @Test
    public void queryNoReturn() throws Exception {
        sqlLiteConnection.queryNoReturn("Update settings set setting='Test12345' where description='amiTest'");
        Assert.assertEquals("Test12345",sqlLiteConnection.query("Select setting from settings where description='amiTest'"));
    }

    @Test
    public void getInterns() throws Exception {
        sqlLiteConnection.queryNoReturn("Insert into internfields (id, number, name, callcount, position) values (1, 202, 'Johnny', 10, 1)");
        sqlLiteConnection.queryNoReturn("Insert into internfields (id, number, name, callcount, position) values (2, 203, 'Michi', 1, 4)");
        sqlLiteConnection.queryNoReturn("Insert into internfields (id, number, name, callcount, position) values (3, 204, 'Anna', 50, 3)");
        sqlLiteConnection.queryNoReturn("Insert into internfields (id, number, name, callcount, position) values (4, 205, 'Tom', 14, 2)");

        Map<String, PhoneNumber> testInterns = sqlLiteConnection.getInterns();

        Assert.assertEquals("Johnny",testInterns.get("202").getName());
        Assert.assertEquals(10,testInterns.get("202").getCount());
        Assert.assertEquals(2,testInterns.get("205").getPosition());
        Assert.assertEquals(1,testInterns.get("203").getCount());
        Assert.assertEquals("Anna",testInterns.get("204").getName());
    }

    @Test
    public void getFieldsForDataSource() {
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (4, 'name;Name', 'HammerPluginField0')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (5, 'num;Number', 'HammerPluginField1')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (6, 'ct;City', 'HammerPluginField2')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (7, 'add;Street', 'HammerPluginField3')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (8, '1', 'HammerPluginFieldTel')");

        ArrayList<PluginDataField> pluginDataFields = sqlLiteConnection.getFieldsForDataSource("HammerPlugin");

        Assert.assertEquals("Name",pluginDataFields.get(0).getFieldvalue());
        Assert.assertEquals("name",pluginDataFields.get(0).getFieldname());
        Assert.assertEquals(false, pluginDataFields.get(0).isTelephone());

        Assert.assertEquals("num",pluginDataFields.get(1).getFieldname());
        Assert.assertEquals(true, pluginDataFields.get(1).isTelephone());

        Assert.assertEquals("Street",pluginDataFields.get(3).getFieldvalue());
        Assert.assertEquals("add",pluginDataFields.get(3).getFieldname());
        Assert.assertEquals(false, pluginDataFields.get(3).isTelephone());
    }

    @Test
    public void getOptionsForDataSource() {
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (4, 'localhost', 'HammerPluginSetting0')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (5, '79189', 'HammerPluginSetting1')");

        ArrayList<String> options = sqlLiteConnection.getOptionsForDataSource("HammerPlugin");

        Assert.assertEquals("localhost", options.get(0));
        Assert.assertEquals("79189", options.get(1));
        Assert.assertEquals(79189, Integer.parseInt(options.get(1)));
    }

    @Test
    public void writePluginSettingsToDatabase() {
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (4, 'name;Name', 'HammerPluginField0')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (5, 'num;Number', 'HammerPluginField1')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (6, 'ct;City', 'HammerPluginField2')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (7, 'add;Street', 'HammerPluginField3')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (8, '1', 'HammerPluginFieldTel')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (9, 'localhost', 'HammerPluginSetting0')");
        sqlLiteConnection.queryNoReturn("Insert into settings (id, setting, description) values (10, '79189', 'HammerPluginSetting1')");

        ArrayList<String> optionsInsert = new ArrayList<>();
        optionsInsert.add("remote");
        optionsInsert.add("123");

        ArrayList<PluginDataField> linkFields = new ArrayList<>();
        linkFields.add(new PluginDataField("namus", "Nam"));
        linkFields.add(new PluginDataField("telus", "Tel",true,false));

        sqlLiteConnection.writePluginSettingsToDatabase("HammerPlugin", optionsInsert, linkFields);

        ArrayList<String> options = sqlLiteConnection.getOptionsForDataSource("HammerPlugin");
        Assert.assertEquals("remote", options.get(0));
        Assert.assertEquals("123", options.get(1));
        Assert.assertEquals(123, Integer.parseInt(options.get(1)));

        ArrayList<PluginDataField> pluginDataFields = sqlLiteConnection.getFieldsForDataSource("HammerPlugin");
        Assert.assertEquals("Nam",pluginDataFields.get(0).getFieldvalue());
        Assert.assertEquals("namus",pluginDataFields.get(0).getFieldname());
        Assert.assertEquals(false, pluginDataFields.get(0).isTelephone());
        Assert.assertEquals("telus",pluginDataFields.get(1).getFieldname());
        Assert.assertEquals(true, pluginDataFields.get(1).isTelephone());
    }

}