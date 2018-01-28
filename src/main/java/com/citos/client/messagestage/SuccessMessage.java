/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.messagestage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Created by johannes on 12.04.2017.
 */
public class SuccessMessage extends Message {

    public SuccessMessage(String message) {
        super.messageText = message;
        start();
    }

    public void start() {
        super.pic = "/pics/success.png";
        super.background = new Background(new BackgroundFill(Color.TEAL, new CornerRadii(1),
                new Insets(0.0, 0.0, 0.0, 0.0)));
        super.headlineText = "Success!";
        HBox hBox = new HBox();
        AcceptButton buttonAccept = new AcceptButton("OK");
        buttonAccept.setOnMouseClicked(event -> SuccessMessage.super.dialog.close());
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(20));
        hBox.getChildren().addAll(buttonAccept);
        super.hBox = hBox;
        super.start();
    }
}
