/*
 * Copyright (c) 2017. Johannes Engler
 */
package com.citos.client.panels.gui.fields;

import com.citos.client.messagestage.ErrorMessage;
import com.citos.client.panels.gui.fields.serverconnectionhandlerevents.CallEvent;
import com.citos.client.panels.gui.plugins.AddressBookEntry;
import com.citos.client.panels.gui.plugins.DataSource;
import com.citos.client.panels.gui.plugins.PluginDataField;
import com.citos.client.panels.gui.plugins.TransparentImageButton;
import com.google.common.eventbus.EventBus;
import com.citos.client.messagestage.ErrorMessage;
import com.citos.client.panels.gui.fields.serverconnectionhandlerevents.CallEvent;
import com.citos.client.panels.gui.plugins.AddressBookEntry;
import com.citos.client.panels.gui.plugins.DataSource;
import com.citos.client.panels.gui.plugins.PluginDataField;
import com.citos.client.panels.gui.plugins.TransparentImageButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author johannesengler
 */
public class AddressField extends VBox {
    private final AddressBookEntry addressBookEntry;
    private final ArrayList<String> fieldNames;
    private final EventBus eventBus;
    private String name;
    private String tag;
    private TransparentImageButton vUpDown;
    private boolean expanded;
    private int mobile = -1;
    private int telephone = -1;

    public AddressField(int count, AddressBookEntry addressBookEntry, EventBus eventBus) {
        this.name = addressBookEntry.getName();
        this.tag = addressBookEntry.getSource().getTag();
        this.eventBus = eventBus;
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPadding(new Insets(12, 12, 12, 12));
        this.setSpacing(3);
        this.setFocusTraversable(true);
        this.expanded = false;
        this.addressBookEntry = addressBookEntry;
        DataSource source = addressBookEntry.getSource();
        this.fieldNames = new ArrayList<>();
        this.getStyleClass().clear();
        this.getStyleClass().add("address-box");

        for (PluginDataField s : source.getAvailableFields()) {
            fieldNames.add(s.getFieldvalue());
        }
        if(source.getMobile()>-1) {
            mobile = source.getMobile();
        }
        if(source.getTelephone()>-1) {
            telephone = source.getTelephone();
        }

        HBox inner = new HBox();
        inner.setSpacing(5);
        inner.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(inner, Priority.ALWAYS);

        HBox innerinner = new HBox();
        HBox.setHgrow(innerinner, Priority.ALWAYS);

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                handleCollapseExpand();
            }
            AddressField.this.requestFocus();
            event.consume();
        });

        Label a = new Label(name);
        a.getStyleClass().clear();
        a.getStyleClass().add("address-label-big");
        inner.getChildren().add(a);
        this.getChildren().add(inner);
        inner.getChildren().add(innerinner);
        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (telephone != -1) {
                    this.eventBus.post(new CallEvent(addressBookEntry.get(telephone),false));
                } else if (mobile != -1) {
                    this.eventBus.post(new CallEvent(addressBookEntry.get(mobile),false));
                }
                event.consume();
            }
        });

        if (telephone>-1) {
            TransparentImageButton v = new TransparentImageButton("/pics/telephone-of-old-design.png");
            inner.getChildren().add(v);
            v.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if(addressBookEntry.getDataSize()>telephone) {
                    this.eventBus.post(new CallEvent(String.valueOf(addressBookEntry.get(telephone)),false));
                }
                event.consume();
            });

        }
        if (mobile>-1) {
            TransparentImageButton v = new TransparentImageButton("/pics/smartphone-call.png");
            inner.getChildren().add(v);
            v.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        if(addressBookEntry.getDataSize()>mobile) {
                            this.eventBus.post(new CallEvent(addressBookEntry.get(mobile),false));
                        }
                event.consume();
            });
        }

        Label tagLabel = new Label("(" + this.tag + ")");
        tagLabel.setMinWidth(Region.USE_PREF_SIZE);
        tagLabel.getStyleClass().add("address-label-datasource");
        inner.setAlignment(Pos.CENTER);
        inner.getChildren().add(tagLabel);

        vUpDown = new TransparentImageButton("/pics/down-arrow.png");
        inner.getChildren().add(vUpDown);
        vUpDown.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            handleCollapseExpand();
            event.consume();
        });
    }

    private void handleCollapseExpand() {
        if (expanded) {
            AddressField.this.requestFocus();
            vUpDown.setDown();
            collapse();
        } else {
            AddressField.this.requestFocus();
            vUpDown.setUp();
            expand();
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void expand() {
        VBox v = new VBox();
        v.setSpacing(3);
        int i = 0;
        for (String fieldName : fieldNames) {
            HBox h = new HBox();
            h.setSpacing(0);
            Label field = new Label(fieldName + ": ");
            field.getStyleClass().clear();
            field.getStyleClass().add("address-label");
            field.setWrapText(true);
            h.getChildren().add(field);
            HBox space = new HBox();
            space.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(space, Priority.ALWAYS);
            h.getChildren().add(space);
            Label value = new Label("-");
            try {
                value.setText(addressBookEntry.get(i));
            } catch (IndexOutOfBoundsException ex) {
               break;
            }
            value.getStyleClass().clear();
            value.getStyleClass().add("address-label");
            value.setWrapText(true);
            value.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Label l = (Label) event.getSource();
                StringSelection stringSelection = new StringSelection(l.getText());
                java.awt.datatransfer.Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
                event.consume();
            });
            value.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                Label l = (Label) event.getSource();
                l.setStyle("-fx-font-weight: bold;");
                event.consume();
            });
            value.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
                Label l = (Label) event.getSource();
                l.setStyle("-fx-font-weight: normal;");
                event.consume();
            });
            h.getChildren().add(value);
            v.getChildren().add(h);
            ++i;
        }
        if(fieldNames.size() != addressBookEntry.getDataSize()) {
            new ErrorMessage("Please correct your plugin settings. Values for fields are missed. " +
                    "Some data fields might be shifted and can not be displayed properly");
        }

        this.getChildren().add(v);
        expanded = true;
    }

    public void collapse() {
        this.getChildren().remove(this.getChildren().size() - 1);
        expanded = false;
    }



}
