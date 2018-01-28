/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.messagestage;

import com.google.common.eventbus.EventBus;
import com.citos.client.panels.gui.settings.PasswordChangeRequestEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 * Created by johannes on 12.04.2017.
 */
public class PasswordChange {

    private String pic;
    private Background background;
    private String headlineText;
    private String messageText;
    private HBox hBox;
    private Stage dialog;
    private EventBus bus;
    private Text t;
    private String user;

    public PasswordChange(EventBus bus, String user) {
        this.bus = bus;
        this.user = user;
        bus.register(this);
    }

    public void start() {
        this.dialog = new Stage();
        this.dialog.initStyle(StageStyle.UTILITY);
        this.pic = "/pics/information64.png";
        this.headlineText = "Change Passwort";
        this.messageText = "Please enter the new Password";
        this.background = new Background(new BackgroundFill(Color.TEAL, new CornerRadii(1),
                new Insets(0.0, 0.0, 0.0, 0.0)));

        dialog.initStyle(StageStyle.UNDECORATED);
        VBox box = new VBox();
        box.setSpacing(20);
        box.setAlignment(Pos.CENTER);
        Scene scene = new Scene(box);
        scene.getStylesheets().add("styles/Styles.css");

        Image image = new Image(pic);
        ImageView imageView = new ImageView(image);

        HBox colorHbox = new HBox();
        colorHbox.setAlignment(Pos.CENTER);
        colorHbox.getChildren().add(imageView);
        colorHbox.setBackground(background);
        colorHbox.setFillHeight(true);
        colorHbox.setMinHeight(64+64);
        colorHbox.setMinWidth(256+64);
        colorHbox.setFocusTraversable(false);


        Text headline = new Text(headlineText);
        headline.setFont(Font.font ("Roboto", 25));
        headline.setStyle("-fx-font-smoothing-type: gray;");

        VBox textFieldVbos = new VBox();
        textFieldVbos.setAlignment(Pos.CENTER);
        textFieldVbos.setSpacing(10);
        textFieldVbos.setPadding(new Insets(20,0,0,0));
        PasswordField textFieldOld = new PasswordField();
        textFieldOld.setStyle(" -fx-background-color: teal;");
        textFieldOld.setPromptText("old password");
        PasswordField textFieldNew  = new PasswordField();
        textFieldNew.setStyle(" -fx-background-color: teal;");
        textFieldNew.setPromptText("new password");
        PasswordField textFieldRetype = new PasswordField();
        textFieldRetype.setStyle(" -fx-background-color: teal;");
        textFieldRetype.setPromptText("new password retype");
        t = new Text("passwords don't match!");
        textFieldVbos.getChildren().addAll(textFieldOld, textFieldNew, textFieldRetype, t);

        VBox texts = new VBox();
        texts.setPadding(new Insets(0,20,0,20));
        texts.setSpacing(10);
        texts.setAlignment(Pos.CENTER);
        texts.getChildren().addAll(headline, textFieldVbos);
        HBox hBox = new HBox();
        AcceptButton buttonAccept = new AcceptButton("OK");
        buttonAccept.setDisable(true);
        buttonAccept.setOnMouseClicked(event ->{
            bus.post(new PasswordChangeRequestEvent(user,textFieldOld.getText(),textFieldNew.getText()));
            this.dialog.close();
        } );
        AcceptButton buttonRaise = new AcceptButton("Cancel");
        buttonRaise.setOnMouseClicked(event -> {
            this.dialog.close();
        });
        hBox.setFocusTraversable(false);
        HBox separatorBox = new HBox();
        HBox.setHgrow(separatorBox, Priority.ALWAYS);
        hBox.setPadding(new Insets(20));
        hBox.getChildren().addAll(buttonRaise, separatorBox, buttonAccept);
        box.getChildren().addAll(colorHbox,texts,hBox);

        textFieldNew.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue.equals(textFieldRetype.getText())) {
                buttonAccept.setDisable(false);
                t.setText("");
            } else {
                if(newValue.length()<6) {
                    t.setText("password is too short");
                } else {
                    buttonAccept.setDisable(true);
                    t.setText("passwords don't match!");
                }
            }
        });

        textFieldRetype.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue.equals(textFieldNew.getText())&&newValue.length()>5) {
                buttonAccept.setDisable(false);
                t.setText("");
            } else {
                if(newValue.length()<6) {
                    t.setText("password is too short");
                } else {
                    buttonAccept.setDisable(true);
                    t.setText("passwords don't match!");
                }
            }
        });
        dialog.setScene(scene);
        dialog.show();
        dialog.sizeToScene();
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
    }


}
