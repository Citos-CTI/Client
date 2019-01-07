/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.settings;

import com.citos.client.OptionTuple;
import com.citos.client.panels.gui.fields.otherevents.StartWindowPositioningEvent;
import com.google.common.eventbus.EventBus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author johannes
 */
public class ProgramSettingsField extends SettingsField {

    private ArrayList<CheckBox> checkBoxes;
    private boolean changed;
    private Button change_position_window;

    public ProgramSettingsField(EventBus bus) {
        super("Other");
        checkBoxes = new ArrayList<>();
        change_position_window = new Button("Window position");
        change_position_window.setOnAction(event -> {
            bus.post(new StartWindowPositioningEvent());
        });
        change_position_window.getStyleClass().add("button-ui");

        changed = false;
    }

    public void setCheckBoxes(List<OptionTuple> foundList) {
        checkBoxes.clear();
        for (OptionTuple found : foundList) {
            CheckBox b = new CheckBox(found.getName());
            b.selectedProperty().setValue(found.isActivated());
            b.selectedProperty().addListener((ov, old_val, new_val) -> ProgramSettingsField.this.changed = true);
            checkBoxes.add(b);
        }

    }

    public boolean[] getChecked() {
        boolean[] options = new boolean[checkBoxes.size()];
        int i = 0;
        for(CheckBox b : checkBoxes) {
            options[i] = b.selectedProperty().getValue();
            ++i;
        }
        return options;
    }

    @Override
    public void expand() {
        VBox v = new VBox();
        setMargin(v, new Insets(6, 0, 3, 0));
        v.setAlignment(Pos.CENTER);
        v.setSpacing(6);
        v.getChildren().addAll(checkBoxes);
        v.getChildren().add(change_position_window);
        this.getChildren().add(v);
        super.expand();
    }


    @Override
    public void collapse() {
        this.getChildren().remove(this.getChildren().size() - 1);
        super.collapse();
    }


    public boolean hasChanged(){
        boolean out = changed;
        changed = false;
        return out;
    }

    public boolean isExpanded() {return super.isExpanded();}

    public void refresh() {super.refresh();}


}
