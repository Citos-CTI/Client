/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins;


import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Created by johannes on 24.04.17.
 */
public class TransparentImageButton extends VBox {
    private ImageView view;

    //Used for the choosing of (mobile/telephone/nothing) by clicking through the options
    private int pos =0;


    public TransparentImageButton(String resource) {
        Image image = new Image(resource);
        this.view = new ImageView(image);
        view.setFitHeight(15);
        view.setFitWidth(15);
        this.setOpacity(0.2);
        this.addHover();
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(view);

    }

    public TransparentImageButton(String resource, boolean disabled) {
        Image image = new Image(resource);
        this.view = new ImageView(image);
        view.setFitHeight(15);
        view.setFitWidth(15);
        this.setOpacity(0.2);
        if(!disabled){
            this.addHover();
        }
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(view);

    }

    public void setImageFromResource(String resource) {
        this.view.setImage(new Image(resource));
    }

    public void setImage(Image image) {
        this.view.setImage(image);
    }

    public void setUp() {
        this.view.setRotate(180);
    }

    public void setDown() {
        this.view.setRotate(0);
    }

    private void addHover(){
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            VBox v12 = (VBox) event.getSource();
            v12.setOpacity(1);
            event.consume();
        });
        this.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            VBox v1 = (VBox) event.getSource();
            v1.setOpacity(0.2);
            event.consume();
        });
    }

    public int getPos() {return pos;}

    public void setPos(int pos) {this.pos = pos;}

    public void disable() {
        this.setEventHandler(MouseEvent.MOUSE_ENTERED, event -> {});
    }


}
