/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.messagestage;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Created by johannes on 11.04.2017.
 */
public class ErrorMessage extends Message {

    public ErrorMessage(String message) {
        super.messageText = message;
        start();
    }

    public void start() {
        super.pic = "/pics/cancel64.png";
        super.background = new Background(new BackgroundFill(Color.INDIANRED, new CornerRadii(1),
                new Insets(0.0, 0.0, 0.0, 0.0)));
        super.headlineText = "Oh no!";
        HBox hBox = new HBox();
        CancelButton buttonAccept = new CancelButton("OK");
        buttonAccept.setOnMouseClicked(event -> ErrorMessage.super.dialog.close());
        CancelButton buttonRaise = new CancelButton("Report");
        buttonRaise.setOnMouseClicked(event -> {
            //TODO implement error raising in github or Email
        });
        hBox.setFocusTraversable(false);
        HBox separatorBox = new HBox();
        HBox.setHgrow(separatorBox, Priority.ALWAYS);
        hBox.setPadding(new Insets(20));
        hBox.getChildren().addAll(buttonRaise, separatorBox, buttonAccept);
        super.hBox = hBox;
        super.start();

    }
}
