/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.messagestage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 * Created by johannes on 12.04.2017.
 */
public class Message {

    public String pic;
    public Background background;
    public String headlineText;
    public String messageText;
    public HBox hBox;
    public Stage dialog;

    public void start() {
        dialog = new Stage();
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
        headline.setFont(Font.font ("Segoe UI Semibold", 25));
        Text text = new Text(messageText);
        text.wrappingWidthProperty().set(256+64);
        headline.setStyle("-fx-font-smoothing-type: gray;");
        text.setStyle("-fx-font-smoothing-type: gray;");
        text.lineSpacingProperty().set(5);
        text.setFont(Font.font("Segoe UI",12));

        VBox texts = new VBox();
        texts.setPadding(new Insets(0,20,0,20));
        texts.setSpacing(10);
        texts.setAlignment(Pos.CENTER);
        texts.getChildren().addAll(headline,text);


        box.getChildren().addAll(colorHbox,texts,hBox);

        dialog.setScene(scene);
        dialog.show();
        dialog.sizeToScene();
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
    }
}
