/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.settings;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * Created by johannes on 14.04.2017.
 */
public class ToolTipTextField extends TextField {
    public ToolTipTextField(String description) {
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
                    ToolTipTextField.this.setTooltip(fieldToolTip);
                    fieldToolTip.setAutoHide(true);
                    Point2D p = ToolTipTextField.this.localToScene(0.0, 0.0);
                    fieldToolTip.show(ToolTipTextField.this, p.getX()
                            + ToolTipTextField.this.getScene().getX() + ToolTipTextField.this.getScene().getWindow().getX(), p.getY()
                            + ToolTipTextField.this.getScene().getY() + ToolTipTextField.this.getScene().getWindow().getY() + ToolTipTextField.this.getHeight());
                } else {
                    fieldToolTip.hide();
                }
            }
        });
    }
}
