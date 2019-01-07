/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client;

import com.citos.client.panels.gui.fields.otherevents.*;
import com.citos.client.panels.gui.fields.serverconnectionhandlerevents.UserLoginStatusEvent;
import com.citos.client.panels.gui.plugins.AddressPlugin;
import com.citos.client.panels.gui.plugins.PluginRegister;
import com.citos.client.panels.gui.plugins.pluginevents.CheckLicenseForPluginEvent;
import com.citos.client.panels.gui.plugins.pluginevents.PluginLicenseApprovedEvent;
import com.citos.client.panels.gui.settings.AsteriskSettingsField;
import com.citos.client.panels.gui.settings.DataSourceSettingsField;
import com.citos.client.panels.gui.settings.ProgramSettingsField;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class OptionsStorage {

    private static final String SETTING = "setting";
    private String amiAddress;        //AMI Server Address
    private int amiServerPort;       //AMI Server Port
    private String amiLogIn;         //AMI Login
    private String amiPasswordHash;
    private boolean sortByCount;
    private long time;               // TimeStamp
    private String pluginFolder;
    private PluginRegister pluginRegister;
    private ArrayList<String> activatedDataSources = new ArrayList<>();
    private AsteriskSettingsField asteriskSettingsField;
    private ProgramSettingsField programSettingsField;
    private DataSourceSettingsField dataSourceSettingsField;
    private EventBus bus;
    private VBox panelD;
    private SqlLiteConnection sqlLiteConnection;

    public OptionsStorage(Button accept, Button reject, VBox panelD, EventBus bus, SqlLiteConnection sqlLiteConnection) {
        this.asteriskSettingsField = new AsteriskSettingsField(bus);
        this.pluginRegister = new PluginRegister(bus);
        this.programSettingsField = new ProgramSettingsField(bus);

        this.dataSourceSettingsField = new DataSourceSettingsField();
        this.bus = bus;
        bus.register(this);
        this.panelD = panelD;
        this.sqlLiteConnection = sqlLiteConnection;

        readSettingsFromDatabase();

        this.pluginRegister.explorePluginFolder(this.pluginFolder);
        this.dataSourceSettingsField.setCheckBoxes(pluginRegister.getPluginsFound(), this.activatedDataSources, new ArrayList<String>() );

        addStandardFields();

        accept.setOnAction(event -> accept());
        reject.setOnAction(event -> setTempVariables());
    }


    private void setUpPlugins() {
        for(String g : activatedDataSources) {
            bus.post(new CheckLicenseForPluginEvent(g,this.pluginRegister.getPluginLicense(g, this.pluginFolder)));
        }
        this.pluginRegister.removeUnloadedPlugins(activatedDataSources);
        updateAddressFields();
    }

    private void addStandardFields() {
        panelD.getChildren().addAll(asteriskSettingsField,programSettingsField, dataSourceSettingsField);
    }

    @Subscribe
    public void loadApprovedPlugin(PluginLicenseApprovedEvent event) {
        this.pluginRegister.loadPlugin(event.getPluginname(), pluginFolder);
        this.pluginRegister.activateLicensedPlugins(sqlLiteConnection,event.getPluginname());
        panelD.getChildren().clear();
        addStandardFields();
        panelD.getChildren().addAll(this.getPluginRegister().getAllPluginSettingsFields());
        updateAddressFields();
    }

    /**
     * Updates the addressfields which are currently showed
     */
    private void updateAddressFields() {
       getPluginRegister().getResultFromEveryPlugin("", 10);
    }

    /**
     * Used to store the temp. vars.
     */
    public void setTempVariables() {
        pluginRegister.discardAllPlugins();
        String[] options = {amiAddress, Integer.toString(amiServerPort), amiLogIn};
        this.asteriskSettingsField.setOptions(options);
        dataSourceSettingsField.setCheckBoxes(pluginRegister.getPluginsFound(),activatedDataSources, this.pluginRegister.getLoadedPluginNames());
        dataSourceSettingsField.setPluginFolder(pluginFolder);
    }

    /**
     * User accepts the changes and wants to store the changes in database
     */
    public void accept() {
        pluginRegister.acceptAllPlugins();
        setVariables();
        writeSettingsToDatabase();
        refreshProgram();
    }

    /**
     * Refreshs all the settings fields
     */
    private void refreshProgram() {
        if(asteriskSettingsField.hasChanged() || asteriskSettingsField.passwordChanged()) {
            String[] options = asteriskSettingsField.getOptions();
            bus.post(new StartConnectionEvent(options[0], Integer.parseInt(options[1]), options[2], options[3], false,false));
        } else if(dataSourceSettingsField.hasChanged()) {
            Logger.getLogger(getClass().getName()).info("Has changed was triggered");
            // Check plugin path for plugins
            this.pluginRegister.explorePluginFolder(this.pluginFolder);
            this.dataSourceSettingsField.setCheckBoxes(pluginRegister.getPluginsFound(), this.activatedDataSources, new ArrayList<String>());
            this.dataSourceSettingsField.refresh();
            //starts the license check procedure
            setUpPlugins();
            writeActivatedDataSourcesToDatabase();
            // Delete all fields and add the standardfields + all loaded plugin fields
            panelD.getChildren().clear();
            addStandardFields();
        } else if(programSettingsField.hasChanged()) {
            bus.post(new ViewOptionsChangedEvent(sortByCount));
        }
        for (AddressPlugin plugin : this.getPluginRegister().getAllActivePlugins()) {
            if (plugin.getPluginSettingsField().hasChanged()) {
                sqlLiteConnection.writePluginSettingsToDatabase(plugin.getName(), plugin.getOptions(), plugin.getDataFields());
                getPluginRegister().getResultFromEveryPlugin("", 10);

            }
        }

    }

    // Since MainWindow is created later than optionstorage we have to repush the events
    public void repushMainWindowEvents() {
        bus.post(new ViewOptionsChangedEvent(sortByCount));
    }

    /**
     * Sets the real variables to the temps. That will happen if user clicks on
     * accept
     */
    public void setVariables() {
        // compare the old with the new values -> if no change happened its not necessary to restart server connection
        String[] optionsCompare = {amiAddress, Integer.toString(amiServerPort), amiLogIn};
        String[] options = asteriskSettingsField.getOptionsTriggerHasChanged(optionsCompare);
        amiAddress = options[0];
        amiServerPort = Integer.valueOf(options[1]);
        amiLogIn = options[2];

        boolean[] optionsOther = programSettingsField.getChecked();
        sortByCount = optionsOther[0];

        // compare the old with the new values -> if no change happened its not necessary to reload the plugins
        activatedDataSources = (ArrayList<String>) this.dataSourceSettingsField.getChecked();
        pluginFolder = this.dataSourceSettingsField.getPluginFolder(pluginFolder);
    }

    /**
     * Saves the settings in the sqlite database
     */
    private void writeSettingsToDatabase() {
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("amiAddress", amiAddress);
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("amiServerPort", String.valueOf(amiServerPort));
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("amiLogIn", amiLogIn);
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("pluginFolder", pluginFolder);
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("programSettingSortCallCount", String.valueOf(sortByCount));
    }

    /**
     * deletes all activated entries from the database and writes the currently activated
     */
    private void writeActivatedDataSourcesToDatabase() {
        sqlLiteConnection.queryNoReturn("Delete from settings where description LIKE 'datasource_%%%%%%%%%%%%';");
        int i = 0;
        for (String s : activatedDataSources) {
            sqlLiteConnection.buildUpdateOrInsertStatementForSetting("datasource" + i, s);
            ++i;
        }
    }

    /**
     * reads the settings saved in the sqlite database
     */
    public void readSettingsFromDatabase() {
        try (Connection con = DriverManager.getConnection(sqlLiteConnection.getConnection()); Statement statement = con.createStatement()) {
            statement.setQueryTimeout(10);
            //Safely read in all Settings. If a setting isnt found a default value will be taken
            final String query = "select setting from settings where description = ?";
            readAmiFields(query, con);

            readOtherOptions(query, con);

            readDatabaseForSources(con);


            try (PreparedStatement ptsm = con.prepareStatement(query)) {
                ptsm.setString(1, "time");
                ResultSet amiAddressRS = ptsm.executeQuery();
                time = Long.valueOf(!amiAddressRS.next() ? Long.toString(System.currentTimeMillis()) : amiAddressRS.getString(SETTING));
            }
            try (PreparedStatement ptsm = con.prepareStatement(query)) {
                ptsm.setString(1, "pluginFolder");
                ResultSet amiAddressRS = ptsm.executeQuery();
                pluginFolder = !amiAddressRS.next() ? "" : amiAddressRS.getString(SETTING);
            }

            setTempVariables();
        } catch (SQLException ex) {
            Logger.getLogger(OptionsStorage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Reads the AMI settings from the sqlite database
     *
     * @param query
     * @param con
     * @throws SQLException
     */
    private void readAmiFields(String query, Connection con) throws SQLException {
        try (PreparedStatement ptsm = con.prepareStatement(query)) {
            ptsm.setString(1, "amiAddress");
            ResultSet amiAddressRS = ptsm.executeQuery();
            amiAddress = !amiAddressRS.next() ? "localhost" : amiAddressRS.getString(SETTING);
        }
        try (PreparedStatement ptsm = con.prepareStatement(query)) {
            ptsm.setString(1, "amiServerPort");
            ResultSet amiAddressRS = ptsm.executeQuery();
            amiServerPort = Integer.valueOf(!amiAddressRS.next() ? "12350" : amiAddressRS.getString(SETTING));
        }
        try (PreparedStatement ptsm = con.prepareStatement(query)) {
            ptsm.setString(1, "amiLogIn");
            ResultSet amiAddressRS = ptsm.executeQuery();
            amiLogIn = !amiAddressRS.next() ? "admin" : amiAddressRS.getString(SETTING);
        }
        try (PreparedStatement ptsm = con.prepareStatement(query)) {
            ptsm.setString(1, "amiPasswordHash");
            ResultSet amiAddressRS = ptsm.executeQuery();
            amiPasswordHash = !amiAddressRS.next() ? "" : amiAddressRS.getString(SETTING);
        }
    }

    private void readOtherOptions(String query, Connection con) throws SQLException {
        ArrayList<OptionTuple> options = new ArrayList<>();
        try (PreparedStatement ptsm = con.prepareStatement(query)) {
            ptsm.setString(1, "programSettingSortCallCount");
            ResultSet amiAddressRS = ptsm.executeQuery();
            boolean value = !amiAddressRS.next() ? true : Boolean.valueOf(amiAddressRS.getString(SETTING));
            options.add(new OptionTuple(value, "Sort by call count"));
            sortByCount = value;
        }
        try (PreparedStatement ptsm = con.prepareStatement(query)) {
            ptsm.setString(1, "windowPosition");
            ResultSet amiAddressRS = ptsm.executeQuery();
            String windowPos = !amiAddressRS.next() ? "0;0" : amiAddressRS.getString(SETTING);
            bus.post(new WindowPositionLoadedEvent(windowPos));
        }
        programSettingsField.setCheckBoxes(options);
    }

    public void readDatabaseForSources(Connection con) throws SQLException {
        activatedDataSources.addAll(sqlLiteConnection.getMultipleStringsFromDatabase("datasource"));
    }

    @Subscribe
    public void closeApplication(CloseApplicationSafelyEvent event) {
        bus.unregister(this);
    }

    @Subscribe
    public void loginSuccessfulNewHash(UserLoginStatusEvent event) {
        if (event.isLoggedIn()) {
            this.amiPasswordHash = event.getHashedPw();
            sqlLiteConnection.buildUpdateOrInsertStatementForSetting("amiPasswordHash", this.amiPasswordHash);

            // Needs to login for the plugin check -> Trigger it if logged in successfully
            setUpPlugins();
        }
    }

    @Subscribe
    public void windowPositionChanged(EndWindowPositioningEvent ev) {
        sqlLiteConnection.buildUpdateOrInsertStatementForSetting("windowPosition", ev.getX() + ";" + ev.getY());
    }


    public String getAmiAddress() {
        return amiAddress;
    }

    public int getAmiServerPort() {
        return amiServerPort;
    }

    public String getAmiLogIn() {
        return amiLogIn;
    }

    public String getAmiPasswordHash() {
        return amiPasswordHash;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<String> getActivatedDataSources() {
        return this.activatedDataSources;
    }


    public PluginRegister getPluginRegister() {
        return pluginRegister;
    }

    public void setAsteriskSettingsField(AsteriskSettingsField asteriskSettingsField) {
        this.asteriskSettingsField = asteriskSettingsField;
        String[] options = {amiAddress, Integer.toString(amiServerPort), amiLogIn};
        this.asteriskSettingsField.setOptions(options);
    }

}
