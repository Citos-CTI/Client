/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.johannes.lsctic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;


/**
 *
 * @author johannesengler
 */
public class SqlLiteConnection {

    private Connection connection;
    private Connection localConnection;

    // Beispiel database: "settingsAndData.db"
    public SqlLiteConnection(String database, String localDatabase) {
        File f = new File(database);
        if (f.exists() && !f.isDirectory()) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:"+database);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
          
        } else {
            try {
                // Erstelle die Datenbank für das Programm
                connection = DriverManager.getConnection("jdbc:sqlite:"+database);
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                //Asterisk Optionen
                statement.executeUpdate("create table settings (id integer, setting string, description string)");
     
                
                statement.executeUpdate("create table internfields (id integer  Primary Key AUTOINCREMENT, number string, name string, callcount integer, favorit boolean)");
               
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            } 
        }
        
          File f2 = new File("localDatabase");
        if (f2.exists() && !f2.isDirectory()) {
            try {
                localConnection = DriverManager.getConnection("jdbc:sqlite:"+localDatabase);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }else {
            try {
                // Erstelle die Datenbank für das Programm
                localConnection = DriverManager.getConnection("jdbc:sqlite:"+localDatabase);
                Statement statement = localConnection.createStatement();
                statement.setQueryTimeout(30);
                
                statement.executeUpdate("create table callhistory (id integer, number string, outgoing boolean)");
                statement.executeUpdate("create table phonebook (id integer, number string, name string, callcount integer, favorit boolean)");

           
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            } 
        }
    }
    public void closeConnections() {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        if(localConnection != null) {
            try {
                localConnection.close();
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
    }
    public ResultSet query(String query) {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery(query);
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return null;
           }
    }
    public void update(String update) {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    public ResultSet selectWhere(String attribut, String table, String whereAttribut, String whereValue) {
         Statement statement;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery("select "+attribut+" from "+table+" where "+whereAttribut+"="+whereValue+"");
        return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }
    public ResultSet select(String attribut, String table) {
         Statement statement;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery("select "+attribut+" from "+table);
        return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }
    // value beispiel: "1, 'leo', 'test'"
    public void insert(String table , String value) {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            statement.executeUpdate("insert into "+table+" values("+value+")");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    public void updateOneAttribute(String table ,String whereAttribut ,String whereValue, String updateAttribut, String updateValue) {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            statement.executeUpdate("UPDATE "+table+" SET "+updateAttribut+" = "+updateValue+" WHERE "+whereAttribut+" = "+whereValue);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    public Connection getConnection(){
        return connection;
    }

    Map<String, PhoneNumber> getInterns() {
        Map<String, PhoneNumber> internNumbers = new TreeMap<>();
        
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery("select * from internfields");
       
            while(rs.next()) {
                internNumbers.put(rs.getString(2), new PhoneNumber(true, rs.getString(2), rs.getString(3), rs.getInt(4)));
            }
            
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return internNumbers;
        }
        
        return internNumbers;
    }

    void queryNoReturn(String query) {
       
         Statement statement;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery(query);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
           }
    }
}