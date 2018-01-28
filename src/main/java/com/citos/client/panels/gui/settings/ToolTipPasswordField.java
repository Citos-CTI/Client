/*
 * Copyright (c) 2017. Johannes Engler
 */
package com.citos.client.panels.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;

/**
 * Created by johannesengler on 05.05.17.
 */
public class ToolTipPasswordField extends PasswordField {
    public ToolTipPasswordField(String description) {
        super();
        Tooltip fieldToolTip = new Tooltip();
        this.getStyleClass().add("textfield-search");

        this.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    fieldToolTip.hide();
                    fieldToolTip.setText(description);
                    //fieldToolTip.setPrefWidth(ToolTipTextField.super.getWidth());
                    ToolTipPasswordField.this.setTooltip(fieldToolTip);
                    fieldToolTip.setAutoHide(true);
                    Point2D p = ToolTipPasswordField.this.localToScene(0.0, 0.0);
                    fieldToolTip.show(ToolTipPasswordField.this, p.getX()
                            + ToolTipPasswordField.this.getScene().getX() + ToolTipPasswordField.this.getScene().getWindow().getX(), p.getY()
                            + ToolTipPasswordField.this.getScene().getY() + ToolTipPasswordField.this.getScene().getWindow().getY() + ToolTipPasswordField.this.getHeight());
                } else {
                    fieldToolTip.hide();
                }
            }
        });
    }
}
