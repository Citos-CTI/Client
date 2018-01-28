/*
 * Copyright (c) 2017. Johannes Engler
 */
package com.citos.client.panels.gui.fields;

import com.citos.client.PhoneNumber;
import com.citos.client.panels.gui.fields.internevents.AddInternEvent;
import com.citos.client.panels.gui.settings.SettingsFieldButton;
import com.google.common.eventbus.EventBus;
import com.citos.client.PhoneNumber;
import com.citos.client.panels.gui.fields.internevents.AddInternEvent;
import com.citos.client.panels.gui.settings.SettingsFieldButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author johannesengler
 */
public class NewInternField extends HBox{

    private Button button;
    public NewInternField(EventBus eventBus) {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPadding(new Insets(5,5, 5, 4));
        this.setSpacing(4);
        this.setAlignment(Pos.CENTER);
        HBox.setHgrow(this, Priority.ALWAYS);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            NewInternField.this.requestFocus();
            event.consume();
        });

        TextField name = new TextField();
        name.setPromptText("Name");

        TextField extension = new TextField();
        extension.setPromptText("Intern number");

        button = new SettingsFieldButton("Add");
        button.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if(extension.getText().length()>0 && name.getText().length()>0) {
                String number = extension.getText();
                PhoneNumber p = new PhoneNumber(true, number, name.getText(), 0, -1);
                eventBus.post(new AddInternEvent(p));
            }
           event.consume();
        });

        this.getChildren().addAll(name,extension,button);

    }


   
    

   
}
