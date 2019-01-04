/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client;

import com.citos.client.amiapi.netty.ServerConnectionHandler;
import com.citos.client.panels.gui.DataPanelsRegister;
import com.citos.client.panels.gui.fields.otherevents.StartConnectionEvent;
import com.citos.client.panels.gui.fields.serverconnectionhandlerevents.CallEvent;
import com.google.common.eventbus.EventBus;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FXMLController implements Initializable {

    @FXML
    private VBox panelA;
    @FXML
    private VBox panelB;
    @FXML
    private VBox panelC;
    @FXML
    private VBox panelD;
    @FXML
    private Button optionAccept;
    @FXML
    private Button optionReject;
    @FXML
    private TextField paneATextIn;
    @FXML
    private TextField paneBTextIn;
    @FXML
    private TextField paneCTextIn;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button btnlast;
    @FXML
    private Button btnnext;
    @FXML
    private ToggleButton dndToggle;
    @FXML
    private ToggleButton redirectToggle;
    @FXML
    private Text serverStatusText;


    private String quickfireString;
    private DataPanelsRegister dataPanelsRegister;
    private Stage stage;
    private EventBus eventBus;

    private Scene scene;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        /*stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                paneATextIn.requestFocus();
                paneATextIn.setFocusTraversable(true);
            } else {
                stage.setIconified(true);
                stage.toBack();
            }

        });*/
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void startApp(EventBus eventBus) {
        this.eventBus = eventBus;

        // Sqlite connection must be established before creating the optionsstorage, because he loads data from sqlite
        SqlLiteConnection sqlLiteConnection = new SqlLiteConnection("settingsAndData.db");

        // creates optionstorage which loads data from sqlite and triggers plugin loading
        OptionsStorage storage = new OptionsStorage(optionAccept, optionReject, panelD, eventBus, sqlLiteConnection);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            new ServerConnectionHandler(eventBus);
            eventBus.post(new StartConnectionEvent(storage.getAmiAddress(), storage.getAmiServerPort(), storage.getAmiLogIn(), storage.getAmiPasswordHash(), true,false));
        });

        //QuickCommandBox -> For DND, Server status, Redirect
        new QuickCommandBox(eventBus,dndToggle,redirectToggle,serverStatusText);

        //Build datapanelregister
        VBox[] panels = {panelA, panelB, panelC, panelD};
        Button[] buttons = {btnlast, btnnext};
        dataPanelsRegister = new DataPanelsRegister(eventBus, sqlLiteConnection, panels, buttons);

        //Initally show 10 first entries in the Addressbook View
        storage.getPluginRegister().getResultFromEveryPlugin("", 10);

        //Add listener for number enterd in search field of paneA which will be used as quickdial field for phonenumbers
        paneATextIn.addEventFilter(KeyEvent.KEY_PRESSED, (javafx.scene.input.KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (quickfireString.matches("^[0-9]*$")) {
                    eventBus.post(new CallEvent(quickfireString,false));
                }
                event.consume();
            }
        });


        // Tooltip that will be used to indicate options for the user input in the search field
        Tooltip customTooltip = new Tooltip();

        //listener to search the intern on new entry
        paneATextIn.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            quickfireString = newValue;
            if (quickfireString.matches("^[0-9]*$") && quickfireString.length() > 0) {
                customTooltip.hide();
                customTooltip.setText("Nummer erkannt. Enter zum wählen");
                paneATextIn.setTooltip(customTooltip);
                customTooltip.setAutoHide(true);
                Point2D p = paneATextIn.localToScene(0.0, 0.0);
                customTooltip.show(paneATextIn, p.getX()
                        + paneATextIn.getScene().getX() + paneATextIn.getScene().getWindow().getX(), p.getY()
                        + paneATextIn.getScene().getY() + paneATextIn.getScene().getWindow().getY() + paneATextIn.getHeight());

            } else {
                customTooltip.hide();
            }
            dataPanelsRegister.updateView(dataPanelsRegister.generateReducedInternSet(newValue));
        });

        //listener to search in the Address sources
        paneBTextIn.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // Todo might be better done async via Eventbus
            storage.getPluginRegister().getResultFromEveryPlugin(newValue, 10);
        });

        // Listener to search history data on the server
        paneCTextIn.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dataPanelsRegister.searchPaneC(newValue,false);
        });
        storage.repushMainWindowEvents();
    }
}
