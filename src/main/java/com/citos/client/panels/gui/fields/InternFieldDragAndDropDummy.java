/*
 * Copyright (c) 2017. Johannes Engler
 */
package com.citos.client.panels.gui.fields;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author johannesengler
 */
public class InternFieldDragAndDropDummy extends HBox {

    public InternFieldDragAndDropDummy(String name,double width) {
        this.setOpacity(0.6);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPadding(new Insets(12, 12, 12, 12));
        this.getStyleClass().clear();
        this.getStyleClass().add("intern-box");
        this.setFocusTraversable(true);
        this.setWidth(width);

        Label label = new Label(name);
        label.setPrefWidth(width);
        label.getStyleClass().clear();
        label.getStyleClass().add("fields-label-notfound");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(label);
    }
}
