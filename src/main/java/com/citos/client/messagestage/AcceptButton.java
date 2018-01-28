/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.messagestage;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
/**
 * Created by johannes on 12.04.2017.
 */
public class AcceptButton extends Button {
    public AcceptButton(String name) {
        super(name);
        this.getStyleClass().add("button-accept");
        this.setFont(Font.font("Segoe UI"));

    }
}
