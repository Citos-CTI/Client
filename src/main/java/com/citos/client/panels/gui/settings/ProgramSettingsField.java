/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.settings;

import com.citos.client.OptionTuple;
import com.citos.client.OptionTuple;
import javafx.geometry.Insets;
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

    public ProgramSettingsField() {
        super("Other");
        checkBoxes = new ArrayList<>();
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
        v.getChildren().addAll(checkBoxes);
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
