/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.messagestage;

import javafx.scene.control.Button;
import javafx.scene.text.Font;

/**
 * Created by johannes on 12.04.2017.
 */
public class CancelButton extends Button {
    public CancelButton(String name) {
        super(name);
        this.getStyleClass().add("button-error");
        this.setFont(Font.font("Segoe UI"));

    }
}
