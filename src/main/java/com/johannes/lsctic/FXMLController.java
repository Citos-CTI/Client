package com.johannes.lsctic;

import com.google.common.eventbus.EventBus;
import com.johannes.lsctic.amiapi.netty.ServerConnectionHandler;
import com.johannes.lsctic.panels.gui.DataPanelsRegister;
import com.johannes.lsctic.panels.gui.fields.serverconnectionhandlerevents.AboCdrExtensionEvent;
import com.johannes.lsctic.panels.gui.plugins.AddressBookEntry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    private TabPane tabPane;


    private String ownExtension;
    private OptionsStorage storage;
    private String quickdialString;
    private DataPanelsRegister dataPanelsRegister;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {


        // Sqlite connection must be established before creating the optionsstorage, because he loads data from sqlite
        SqlLiteConnection sqlLiteConnection = new SqlLiteConnection("settingsAndData.db", "dataLocal.db");

        // creates optionstorage which loads data from sqlite and triggers plugin loading
        storage = new OptionsStorage(optionAccept, optionReject, panelD);

        // set Ownextension
        ownExtension = storage.getOwnExtension();

        //Hard Coded plugins must be registered
        EventBus bus = new EventBus();
        ServerConnectionHandler serverConnectionHandler;
        try {
            serverConnectionHandler = new ServerConnectionHandler(bus,ownExtension);
            VBox[] panels = {panelA, panelB, panelC, panelD};
            dataPanelsRegister = new DataPanelsRegister(bus, sqlLiteConnection, panels);
            bus.post(new AboCdrExtensionEvent(ownExtension));
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Initally show 10 first entries in the Addressbook View
        List<AddressBookEntry> ld = storage.getLoaderRegister().getResultFromEveryPlugin("", 10);
        dataPanelsRegister.updateAddressFields((ArrayList<AddressBookEntry>) ld);

        //Add listener for number enterd in search field of paneA which will be used as quickdial field for phonenumbers
        paneATextIn.addEventFilter(KeyEvent.KEY_PRESSED, (javafx.scene.input.KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    long l = Long.parseLong(quickdialString);
                    Logger.getLogger(getClass().getName()).log(Level.INFO, null, "Dial: " + l);
                    //this.getServerConnectionHandler().sendBack("003"+getOwnExtension()+":"+quickdialString);
                    // USE EVENTBUS
                } catch (NumberFormatException e) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, null, e);
                }

                event.consume();
            }
        });


        // Tooltip that will be used to indicate options for the user input in the search field
        Tooltip customTooltip = new Tooltip();

        //listener to search the intern on new entry
        paneATextIn.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                quickdialString = newValue;
                long quickdial = Long.parseLong(newValue);
                customTooltip.hide();
                customTooltip.setText("Nummer erkannt. Enter zum wählen");
                paneATextIn.setTooltip(customTooltip);
                customTooltip.setAutoHide(true);
                Point2D p = paneATextIn.localToScene(0.0, 0.0);
                customTooltip.show(paneATextIn, p.getX()
                        + paneATextIn.getScene().getX() + paneATextIn.getScene().getWindow().getX(), p.getY()
                        + paneATextIn.getScene().getY() + paneATextIn.getScene().getWindow().getY() + paneATextIn.getHeight());

            } catch (NumberFormatException e) {
                customTooltip.hide();
            }
            dataPanelsRegister.updateView(dataPanelsRegister.generateReducedSet(newValue));
        });

        //listener to search in the Address sources
        paneBTextIn.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            List<AddressBookEntry> ld1 = storage.getLoaderRegister().getResultFromEveryPlugin(newValue, 10);
            dataPanelsRegister.updateAddressFields((ArrayList<AddressBookEntry>)ld1);
        });


        /*   tabPane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {   NICHT LÖSCHEN ERSTER ANSATZ FÜR WEITERE BESCHLEUNIGUNG DES ARBEITENS
        

         @Override
         public void handle(javafx.scene.input.KeyEvent event) {
         if (event.getCode() == KeyCode.I && event.isControlDown()) {
         selectTab(0);
         System.out.println(0);
         event.consume(); 
         } if(event.getCode() == KeyCode.L && event.isControlDown()) {
         selectTab(1);
         System.out.println(1);

         event.consume();
         }  if(event.getCode() == KeyCode.A && event.isControlDown()) {
         selectTab(2);
         System.out.println(2);

         event.consume();
         }  if (event.getCode() == KeyCode.O && event.isControlDown()) {
         selectTab(3);
         System.out.println(3);

         event.consume();
         }
         }
         });*/


    }

    private void selectTab(int i) {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(i); //select by index starting with 0
        tabPane.setSelectionModel(selectionModel);
        selectionModel.clearSelection(); //clear your selection
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    paneATextIn.requestFocus();
                    paneATextIn.setFocusTraversable(true);
                } else {
                    stage.setIconified(true);
                    stage.toBack();
                }

            }
        });
    }
}
