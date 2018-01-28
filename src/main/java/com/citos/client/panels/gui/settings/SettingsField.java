/*
 * Copyright (c) 2017. Johannes Engler
 */
package com.citos.client.panels.gui.settings;

import com.citos.client.panels.gui.plugins.TransparentImageButton;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * @author johannesengler
 */
public abstract class SettingsField extends VBox {
    private TransparentImageButton vUpDown;
    private boolean expanded;
    private String name;



    public SettingsField(String name) {
        this.name = name;
        createBox();
    }

    private void createBox() {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPadding(new Insets(12, 12, 12, 12));
        this.setSpacing(3);
        this.getStyleClass().clear();
        this.getStyleClass().add("setting-box");
        this.setFocusTraversable(true);

        HBox inner = new HBox();
        inner.setSpacing(5);
        inner.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(inner, Priority.ALWAYS);

        HBox innerChild = new HBox();
        innerChild.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(innerChild, Priority.ALWAYS);
        Label a = new Label(name);
        a.getStyleClass().clear();
        a.getStyleClass().add("setting-label");
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            SettingsField.this.requestFocus();
            event.consume();

        });
        inner.getChildren().add(a);
        this.getChildren().add(inner);
        inner.getChildren().add(innerChild);


        vUpDown = new TransparentImageButton("/pics/down-arrow.png");
        inner.getChildren().add(vUpDown);
        vUpDown.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            clickAction(false);
            event.consume();
        });

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            clickAction(true);
            event.consume();
        });
    }

    public void clickAction(boolean fromField) {
        if (expanded) {
            if(!fromField) {
                SettingsField.this.requestFocus();
                vUpDown.setDown();
                collapse();
            }
        } else {
            SettingsField.this.requestFocus();
            vUpDown.setUp();
            expand();
        }
    }

    public void refresh() {
        if(expanded) {
            collapse();
            vUpDown.setUp();
            expand();
        }
    }

    public void expand() {
        expanded = true;
    }

    public void collapse() {
        expanded = false;
    }

    public boolean isExpanded() {return expanded;}

    public abstract boolean hasChanged();
}
