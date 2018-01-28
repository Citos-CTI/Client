/*
 * Copyright (c) 2017. Johannes Engler
 */
package com.citos.client;

import com.citos.client.messagestage.ErrorMessage;
import com.citos.client.messagestage.SuccessMessage;
import com.citos.client.panels.gui.plugins.PluginDataField;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlLiteConnection {

    private static final String JDBC = "jdbc:sqlite:";
    private String database;

    /**
     * Beispiel database: "settingsAndData.db"
     *
     * @param database
     */
    public SqlLiteConnection(String database) {
        this.database = database;
        String[] createLines2 = {"create table settings (id integer, setting string, description string)",
            "create table internfields (id integer  Primary Key AUTOINCREMENT, number varchar, name varchar, callcount integer, position integer)"};
        createDatabase(database, createLines2);
    }

    private void createDatabase(String database, String[] createLines) {
        File f = new File(database);
        if (f.exists() && !f.isDirectory()) {
            try(Connection connection = DriverManager.getConnection(JDBC + database)) {
                String query = "SELECT name FROM sqlite_master WHERE type=?";
                try (PreparedStatement ptsm = connection.prepareStatement(query)) {
                    ptsm.setString(1, "table");
                    ResultSet table = ptsm.executeQuery();
                    ArrayList<String> check = new ArrayList<>();
                    while(table.next()) {
                        check.add(table.getString("name"));
                    }
                    if(!(check.contains("settings") && check.contains("internfields") || (check.contains("callhistory") && check.contains("phonebook")))){
                        throw new SQLException("Database seems not to have the right scheme");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(SqlLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
                new ErrorMessage("Database seems to exist but is not readable. Please check database integrity and if necessary delete it");
            }

        } else {
            try (Connection connection = DriverManager.getConnection(JDBC + database)) {
                try( Statement statement = connection.createStatement()) {
                    // Erstelle die Datenbank für das Programm
                    statement.setQueryTimeout(30);
                    //Asterisk Optionen
                    for (String create : createLines) {
                        statement.executeUpdate(create);
                    }
                }
                new SuccessMessage("Created new database.");
            } catch (SQLException e) {
                new ErrorMessage("Could not create local database in folder " + f.getAbsolutePath());
                Logger.getLogger(SqlLiteConnection.class.getName()).log(Level.SEVERE, null, e);
            }
        }

    }

    /**
     * Performs an insert or update statement on the settings table. Used to update or insert any settings
     *
     * @param description
     * @param settingValue
     */
    public void buildUpdateOrInsertStatementForSetting(String description, String settingValue) {
        int currentId = getMaxIdValueOfTable("Settings") + 1;
        try (Connection con = DriverManager.getConnection(JDBC + database); Statement statement = con.createStatement()) {
            statement.setQueryTimeout(10);
            con.setAutoCommit(false);
            final String query = "UPDATE Settings SET setting='" + settingValue + "' WHERE description='" + description + "'";
            final String query2 = "INSERT INTO Settings (id, setting , description) SELECT " + currentId + ",'" + settingValue + "','" + description + "' WHERE (Select Changes() = 0)";
            statement.addBatch(query);
            statement.addBatch(query2);
            statement.executeBatch();
            con.commit();
            statement.closeOnCompletion();
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
        }
    }

    /**
     * Returns the current max index in the table (since sqlite not has serial like pg)
     * @param table
     * @return
     */
    private int getMaxIdValueOfTable(String table) {
        final String query = "Select max(id) from " + table;
        try (Connection con = DriverManager.getConnection(JDBC + database); Statement statement = con.createStatement();
             ResultSet set = statement.executeQuery(query)) {
            statement.setQueryTimeout(10);
            return set.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
        }
        return 0;
    }


    /**
     * Runs a SQL query and returns the results
     *
     * @param query
     * @return resultset
     */
    public String query(String query) {
        try(Connection connection = DriverManager.getConnection(JDBC + database); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            statement.setQueryTimeout(10);
            return resultSet.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(SqlLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
            new ErrorMessage("There was a database error with the query: \""+query+"\"");
            return null;
        }
    }

    /**
     * Updates one attribute
     *
     * @param table
     * @param whereAttribut
     * @param whereValue
     * @param updateAttribut
     * @param updateValue
     */
    public void updateOneAttribute(String table, String whereAttribut, String whereValue, String updateAttribut, String updateValue) {
        try(Connection connection = DriverManager.getConnection(JDBC + database); Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(10);
            statement.executeUpdate("UPDATE " + table + " SET " + updateAttribut + " = '" + updateValue + "' WHERE " + whereAttribut + " = '" + whereValue+"'");
        } catch (SQLException ex) {
            Logger.getLogger(SqlLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
            new ErrorMessage("There was a database error with the query");
        }
    }

    /**
     * Returns a list with options (mappings) the user set in the plugin settings (the mapping between showed field value and internal plugin name)
     * @param datasource
     * @return
     */
    public ArrayList<PluginDataField> getFieldsForDataSource(String datasource) {
        ArrayList<PluginDataField> dataSourceFields = new ArrayList<>();
        int i = 0;
        String quField = datasource + "Field";
        while (true) {
            try (Connection connection = DriverManager.getConnection(JDBC + database); PreparedStatement statement = connection.prepareStatement("select setting from settings where description = ?")) {
                statement.setString(1, quField + i);
                try (ResultSet fieldRS = statement.executeQuery()) {
                    if (fieldRS.next()) {
                        String field = fieldRS.getString("setting");
                        String[] parameter = field.split(";");
                        dataSourceFields.add(new PluginDataField(parameter[0],parameter[1]));
                        ++i;
                    } else {
                        break;
                    }
                } catch (SQLException e) {
                    throw e;
                }
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
            try (Connection connection = DriverManager.getConnection(JDBC + database); PreparedStatement statement = connection.prepareStatement("select setting from settings where description = ?")) {
                statement.setString(1, quField + "Tel");
                try (ResultSet fieldRS = statement.executeQuery()) {
                    if (fieldRS.next()) {
                        int field = fieldRS.getInt("setting");
                        dataSourceFields.get(field).setTelephone(true);
                    }
                } catch (SQLException e) {
                    throw e;
                }
                statement.setString(1, quField + "Mob");
                try (ResultSet fieldRS = statement.executeQuery()) {
                    if (fieldRS.next()) {
                        int field = fieldRS.getInt("setting");
                        dataSourceFields.get(field).setMobile(true);
                    }
                } catch (SQLException e) {
                    throw e;
                }
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }

        return dataSourceFields;
    }


    /**
     * Loads settings as a String for a specific plugin plugin developer knows how to handle
     * @param name
     * @return
     */
    public ArrayList<String> getOptionsForDataSource(String name) {
        String quField = name + "Setting";
        return getMultipleStringsFromDatabase(quField);
    }

    /**
     * Loads multiple settings from database
     * @param name
     * @return
     */
    public ArrayList<String> getMultipleStringsFromDatabase(String name) {
        ArrayList<String> options = new ArrayList<>();
        int i = 0;
        while (true) {
            try (Connection connection = DriverManager.getConnection(JDBC + database); PreparedStatement statement = connection.prepareStatement("select setting from settings where description = ?")) {
                statement.setString(1, name + i);
                try (ResultSet fieldRS = statement.executeQuery()) {
                    if (fieldRS.next()) {
                        String field = fieldRS.getString("setting");
                        options.add(field);
                        ++i;
                    } else {
                        break;
                    }
                } catch (SQLException e) {
                    throw e;
                }
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
        return options;
    }


    /**
     * Writes the plugin fields and the options to the settigs database
     * @param name
     * @param options
     * @param linkFields
     */
    public void writePluginSettingsToDatabase(String name, ArrayList<String> options, ArrayList<PluginDataField> linkFields) {
        queryNoReturn("Delete from settings where description LIKE '" + name + "Field_%%%%%%%%%%%%';");
        int i = 0;
        for (PluginDataField fields: linkFields) {
            buildUpdateOrInsertStatementForSetting(name + "Field" + i, fields.getFieldname() + ";" + fields.getFieldvalue());
            if(fields.isMobile()) {
                buildUpdateOrInsertStatementForSetting(name+ "FieldMob", String.valueOf(i));
            } else if (fields.isTelephone()) {
                buildUpdateOrInsertStatementForSetting(name+ "FieldTel", String.valueOf(i));
            }
            ++i;
        }
        i = 0;
        for (String option : options) {
            // Except in case of an update the Setting amount stays the same etc.
            buildUpdateOrInsertStatementForSetting(name + "Setting" + i, option);
            ++i;
        }
    }


    /**
     *
     * @return Map with all interns
     */
    public Map<String, PhoneNumber> getInterns() {
        Map<String, PhoneNumber> internNumbers = new TreeMap<>();

        try(Connection connection = DriverManager.getConnection(JDBC + database); Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(10);
            try (ResultSet rs = statement.executeQuery("select * from internfields")) {
                while (rs.next()) {
                    internNumbers.put(rs.getString(2), new PhoneNumber(true, rs.getString(2), rs.getString(3), rs.getInt(4),rs.getInt(5)));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SqlLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
            new ErrorMessage("There was a database error with the query. The data may not be complete");
            return internNumbers;
        }
        return internNumbers;
    }

    /**
     * Only a query without a return (used for inserts/updates...)
     *
     * @param query
     */
    public void queryNoReturn(String query) {
        try(Connection connection = DriverManager.getConnection(JDBC + database); Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(10);
            statement.execute(query);
            statement.closeOnCompletion();
        } catch (SQLException ex) {
            Logger.getLogger(SqlLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
            new ErrorMessage("There was a database error with the query: \""+query+"\"");
        }

    }

    public String getConnection() {
        return JDBC + database;
    }


}
