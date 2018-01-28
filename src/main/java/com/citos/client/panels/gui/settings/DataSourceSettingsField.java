/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.settings;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author johannes
 */
public class DataSourceSettingsField extends SettingsField {

    private ArrayList<CheckBox> checkBoxes;
    private ToolTipTextField pluginFolderTextField;
    private boolean changed;

    public DataSourceSettingsField() {
        super("Datasource");
        checkBoxes = new ArrayList<>();
        pluginFolderTextField = new ToolTipTextField("Plugin Folder");
        changed = false;
    }

    public void setCheckBoxes(List<String> foundList, List<String> activatedList, List<String> loadedPluginsNames) {
        checkBoxes.clear();
        for (String found : foundList) {
            CheckBox b = new CheckBox(found);
            b.setOnAction((event)->changed = true);
            if(activatedList.contains(found)) {
                b.selectedProperty().setValue(true);
            } else {
                b.selectedProperty().setValue(false);
            }
            checkBoxes.add(b);
        }
        ArrayList<String> activatedNotFound = new ArrayList<>();
        for(String activated: activatedList) {
            if(!foundList.contains(activated)) {
                activatedNotFound.add(activated);
            }
        }
        for (String found : activatedNotFound) {
            CheckBox b = new CheckBox(found);
            b.setOnAction((event)->changed = true);
            b.selectedProperty().setValue(true);
            checkBoxes.add(b);
        }
        for(CheckBox b : checkBoxes) {
            if(!loadedPluginsNames.contains(b.getText()) && !foundList.contains(b.getText())) {
                b.setStyle("-fx-text-fill: darkgray");
            }
        }
    }

    public List<String> getChecked() {
        ArrayList<String> checked = new ArrayList<>();
        for(CheckBox checkBox: checkBoxes){
            if(checkBox.selectedProperty().getValue()) {
                checked.add(checkBox.getText());
            }
        }
        return checked;
    }

    @Override
    public void expand() {
        VBox v = new VBox();
        VBox.setMargin(v, new Insets(6, 0, 3, 0));
        v.getChildren().addAll(checkBoxes);
        v.setSpacing(10);
        v.getChildren().add(pluginFolderTextField);
        this.getChildren().add(v);
        super.expand();
    }


    @Override
    public void collapse() {
        this.getChildren().remove(this.getChildren().size() - 1);
        super.collapse();
    }

    public void setPluginFolder(String pathTextPath) {
        pluginFolderTextField.setText(pathTextPath);
    }

    public String getPluginFolder(String pluginFolder) {
        if(!(pluginFolder.equals(pluginFolderTextField.getText()))) {
            changed = true;
        }
        return pluginFolderTextField.getText();
    }

    public boolean hasChanged(){
        boolean out = changed;
        changed = false;
        return out;
    }

    public boolean isExpanded() {return super.isExpanded();}

    public void refresh() {super.refresh();}


}
